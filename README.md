# Social Media Big Data Analytics

## 📌 Project Overview

Social Media Big Data Analytics is a Big Data Analytics project that analyzes social media engagement data such as Likes, Comments, and Shares.

The project uses **Hadoop HDFS and MapReduce** to store and process large-scale social media data. A **Java backend** reads the processed Hadoop output and provides analytics through an API. The results are displayed using an interactive web dashboard built with **HTML, CSS, JavaScript, and Chart.js**.

---

## 🎯 Problem Statement

Social media platforms generate huge amounts of user engagement data every day.

Analyzing this data manually becomes difficult when the dataset grows large.

This project provides a Big Data-based solution to:

- Import social media engagement data
- Store large datasets using HDFS
- Process data using Hadoop MapReduce
- Calculate platform-wise engagement
- Analyze Likes, Comments, and Shares
- Identify engagement patterns
- Visualize analytics through an interactive dashboard

---

## 🚀 Objectives

- Process large-scale social media datasets
- Demonstrate distributed storage using Hadoop HDFS
- Perform distributed data processing using MapReduce
- Calculate platform-wise engagement metrics
- Provide meaningful engagement insights
- Display processed results through a web dashboard

---

## 🏗️ System Architecture

```text
                Social Media CSV
                       │
                       ▼
                  ┌─────────┐
                  │   HDFS  │
                  └────┬────┘
                       │
                       ▼
              ┌─────────────────┐
              │ Hadoop MapReduce│
              │                 │
              │ Mapper          │
              │      ↓          │
              │ Reducer         │
              └────────┬────────┘
                       │
                       ▼
                 HDFS Output
                       │
                       ▼
              ┌─────────────────┐
              │  Java Backend   │
              │     REST API    │
              └────────┬────────┘
                       │
                       ▼
              ┌─────────────────┐
              │ Web Dashboard   │
              │ HTML/CSS/JS     │
              │   Chart.js      │
              └─────────────────┘



##🛠️ Technologies Used
Technology	Purpose
Java -Backend and MapReduce programming
Hadoop - 3.4.3	Big Data processing
HDFS -	Distributed data storage
MapReduce -	Distributed data processing
Docker -	Running Hadoop containers
Maven	- Java project and dependency management
HTML -	Dashboard structure
CSS	- Dashboard design
JavaScript -	Frontend logic and API communication
Chart.js -	Data visualization
CSV	 - Input dataset
