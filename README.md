# UserService

## What is this?
This is a public repository with code that deploys itself and pushes to a confluent topic. It uses my Confluent creds and
my AWS creds. If the pipeline is run, it deploys and runs as an AWS AppRunner application.

## Application
The application is built using Spring Boot, auto generating Users and pushing them to a Kafka topic in Confluent Cloud. It
uses the @Scheduled annotation to push a new user to the topic on a recurring basis.

On successfully pushing the data to the topic, the result is logged as a metric for consumption in Prometheus (although 
you can't actually use AWS Prometheus to scrape from a serverless service so a push gateway is needed).

There is also an API controller (2, versioned) that allows pushing data to the topic via an API. Swagger for each POST request
can be seen at http://<host>/swagger-ui/index.html where host is the DNS for the App Runner instance.

## Deployment
The project is built and deployed using GitHub workflows, found [here](.github/workflows/gradle.yml). The workflow builds the
project, including the dockerise task. 

The workflow then moves onto the deployment section and pushes the dockerised container to my private ECR. App Runner is deployed
using terraform and pulls the image from ECR to run.

## Design Flow
The flow looks like the below:

<img src="src/main/resources/UserService_flow.png">