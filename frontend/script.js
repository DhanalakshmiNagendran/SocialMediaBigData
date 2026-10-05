let platformChart = null;
let metricsChart = null;


/* =========================================
   LOAD ANALYTICS
========================================= */

async function loadAnalytics() {

    const insights = document.getElementById("insights");

    try {

        const response = await fetch("/api/analytics");

        if (!response.ok) {
            throw new Error("Backend API error");
        }

        const data = await response.json();

        if (data.status !== "success") {
            throw new Error("Analytics processing failed");
        }

        updateDashboard(data.platforms);

    } catch (error) {

        console.error(error);

        insights.innerHTML = `
            <div class="insight-item">
                <strong>Backend Connection Error</strong><br>
                Please make sure the Java backend and Hadoop are running.
            </div>
        `;
    }
}


/* =========================================
   UPDATE DASHBOARD
========================================= */

function updateDashboard(platforms) {

    if (!platforms || platforms.length === 0) {

        document.getElementById("insights").innerHTML = `
            <div class="insight-item">
                <strong>No analytics data available.</strong><br>
                Please upload and process a CSV file.
            </div>
        `;

        return;
    }


    let totalLikes = 0;
    let totalComments = 0;
    let totalShares = 0;
    let totalEngagement = 0;


    platforms.forEach(platform => {

        totalLikes += Number(platform.likes);
        totalComments += Number(platform.comments);
        totalShares += Number(platform.shares);
        totalEngagement += Number(platform.totalEngagement);

    });


    /* Summary cards */

    document.getElementById("totalLikes").textContent =
        totalLikes.toLocaleString();

    document.getElementById("totalComments").textContent =
        totalComments.toLocaleString();

    document.getElementById("totalShares").textContent =
        totalShares.toLocaleString();

    document.getElementById("totalEngagement").textContent =
        totalEngagement.toLocaleString();


    /*
       Current MapReduce output gives
       platform-level totals, not post count.
    */

    document.getElementById("totalPosts").textContent =
        "—";


    createPlatformChart(platforms);

    createMetricsChart(
        totalLikes,
        totalComments,
        totalShares
    );

    createPlatformTable(platforms);

    createInsights(
        platforms,
        totalLikes,
        totalComments,
        totalShares,
        totalEngagement
    );
}


/* =========================================
   PLATFORM ENGAGEMENT CHART
========================================= */

function createPlatformChart(platforms) {

    const labels = platforms.map(
        item => item.platform
    );

    const values = platforms.map(
        item => Number(item.totalEngagement)
    );


    if (platformChart) {
        platformChart.destroy();
    }


    platformChart = new Chart(
        document.getElementById("platformChart"),
        {
            type: "bar",

            data: {

                labels: labels,

                datasets: [
                    {
                        label: "Total Engagement",
                        data: values,
                        borderWidth: 1
                    }
                ]
            },

            options: {

                responsive: true,

                maintainAspectRatio: false,

                plugins: {

                    legend: {
                        display: false
                    }

                },

                scales: {

                    y: {
                        beginAtZero: true
                    }

                }
            }
        }
    );
}


/* =========================================
   ENGAGEMENT METRICS CHART
========================================= */

function createMetricsChart(
    likes,
    comments,
    shares
) {

    if (metricsChart) {
        metricsChart.destroy();
    }


    metricsChart = new Chart(
        document.getElementById("metricsChart"),
        {
            type: "doughnut",

            data: {

                labels: [
                    "Likes",
                    "Comments",
                    "Shares"
                ],

                datasets: [
                    {
                        data: [
                            likes,
                            comments,
                            shares
                        ],

                        borderWidth: 2
                    }
                ]
            },

            options: {

                responsive: true,

                maintainAspectRatio: false,

                plugins: {

                    legend: {
                        position: "bottom"
                    }

                }
            }
        }
    );
}


/* =========================================
   PLATFORM TABLE
========================================= */

function createPlatformTable(platforms) {

    const table =
        document.getElementById("platformTable");

    table.innerHTML = "";


    platforms.forEach(platform => {

        const row =
            document.createElement("tr");


        row.innerHTML = `

            <td>
                <strong>${platform.platform}</strong>
            </td>

            <td>
                ${Number(platform.likes).toLocaleString()}
            </td>

            <td>
                ${Number(platform.comments).toLocaleString()}
            </td>

            <td>
                ${Number(platform.shares).toLocaleString()}
            </td>

            <td>
                <strong>
                    ${Number(platform.totalEngagement)
                        .toLocaleString()}
                </strong>
            </td>

        `;


        table.appendChild(row);

    });
}


/* =========================================
   ENGAGEMENT INSIGHTS
========================================= */

function createInsights(
    platforms,
    likes,
    comments,
    shares,
    engagement
) {

    const insights =
        document.getElementById("insights");


    /* Highest platform */

    let highestPlatform = platforms[0];


    platforms.forEach(platform => {

        if (
            Number(platform.totalEngagement) >
            Number(highestPlatform.totalEngagement)
        ) {

            highestPlatform = platform;

        }

    });


    /* Dominant metric */

    let dominantMetric = "Likes";
    let highestMetricValue = likes;


    if (comments > highestMetricValue) {

        dominantMetric = "Comments";
        highestMetricValue = comments;

    }


    if (shares > highestMetricValue) {

        dominantMetric = "Shares";
        highestMetricValue = shares;

    }


    /* Percentages */

    let likePercentage = 0;
    let commentPercentage = 0;
    let sharePercentage = 0;


    if (engagement > 0) {

        likePercentage =
            ((likes / engagement) * 100).toFixed(1);

        commentPercentage =
            ((comments / engagement) * 100).toFixed(1);

        sharePercentage =
            ((shares / engagement) * 100).toFixed(1);

    }


    /* Generate insights */

    insights.innerHTML = `

        <div class="insight-item">

            <strong>
                Highest Engagement Platform
            </strong>

            <br>

            ${highestPlatform.platform}

            generated

            ${Number(
                highestPlatform.totalEngagement
            ).toLocaleString()}

            total engagements.

        </div>


        <div class="insight-item">

            <strong>
                Dominant Engagement Metric
            </strong>

            <br>

            ${dominantMetric}

            represents the highest number
            of interactions.

        </div>


        <div class="insight-item">

            <strong>
                Likes Contribution
            </strong>

            <br>

            ${likePercentage}%
            of total engagement.

        </div>


        <div class="insight-item">

            <strong>
                Comments Contribution
            </strong>

            <br>

            ${commentPercentage}%
            of total engagement.

        </div>


        <div class="insight-item">

            <strong>
                Shares Contribution
            </strong>

            <br>

            ${sharePercentage}%
            of total engagement.

        </div>


        <div class="insight-item">

            <strong>
                Platforms Analyzed
            </strong>

            <br>

            ${platforms.length}
            social media platforms.

        </div>

    `;
}


/* =========================================
   START DASHBOARD
========================================= */

loadAnalytics();