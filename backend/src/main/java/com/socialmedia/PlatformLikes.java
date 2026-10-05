package com.socialmedia;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class PlatformLikes {

    // MAPPER
    public static class EngagementMapper
            extends Mapper<Object, Text, Text, Text> {

        private Text platform = new Text();
        private Text engagement = new Text();

        @Override
        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            String line = value.toString().trim();

            // Skip CSV header
            if (line.startsWith("user,platform")) {
                return;
            }

            String[] fields = line.split(",");

            // Expected:
            // user,platform,likes,comments,shares
            if (fields.length != 5) {
                return;
            }

            String platformName = fields[1].trim();

            try {
                int likes = Integer.parseInt(fields[2].trim());
                int comments = Integer.parseInt(fields[3].trim());
                int shares = Integer.parseInt(fields[4].trim());

                platform.set(platformName);

                // Send all engagement values to Reducer
                engagement.set(likes + "," + comments + "," + shares);

                context.write(platform, engagement);

            } catch (NumberFormatException e) {
                // Ignore invalid rows
            }
        }
    }


    // REDUCER
    public static class EngagementReducer
            extends Reducer<Text, Text, Text, Text> {

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

            int totalLikes = 0;
            int totalComments = 0;
            int totalShares = 0;

            for (Text value : values) {

                String[] data = value.toString().split(",");

                totalLikes += Integer.parseInt(data[0]);
                totalComments += Integer.parseInt(data[1]);
                totalShares += Integer.parseInt(data[2]);
            }

            int totalEngagement =
                    totalLikes + totalComments + totalShares;

            String result =
                    "Likes=" + totalLikes +
                    ", Comments=" + totalComments +
                    ", Shares=" + totalShares +
                    ", TotalEngagement=" + totalEngagement;

            context.write(key, new Text(result));
        }
    }


    // DRIVER
    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println(
                    "Usage: PlatformLikes <input path> <output path>");
            System.exit(2);
        }

        Configuration configuration = new Configuration();

        Job job = Job.getInstance(
                configuration,
                "Social Media Engagement Analysis");

        job.setJarByClass(PlatformLikes.class);

        job.setMapperClass(EngagementMapper.class);
        job.setReducerClass(EngagementReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(
                job,
                new Path(args[0]));

        FileOutputFormat.setOutputPath(
                job,
                new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}