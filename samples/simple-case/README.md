# Power Jambda sample
The application can be deployed in an AWS account using the [Serverless Application Model](https://github.com/awslabs/serverless-application-model).
The `sam-template.yaml` file in the deploy folder contains the application definition

## Installation
To build and install the sample application you will need [Maven](https://maven.apache.org/) and
the [AWS CLI](https://aws.amazon.com/cli/) installed on your computer.

In a shell, navigate to the sample's folder and use maven to build a deployable jar.
```
$ mvn package
```

## Deployment
This command should generate a `power-jambda-sample.jar` in the `target` folder.
Now that we have generated the jar file, we can use the AWS CLI to package the template for deployment. 

You will need an S3 bucket to store the artifacts for deployment.
Once you have created the S3 bucket, run the following command from the sample's folder:

```
$ aws cloudformation package --template-file deploy/sam-template.yaml \
   --output-template-file packaged-template.yaml \
   --s3-bucket <YOUR S3 BUCKET NAME>
Uploading to 26cc027f12598d16742a503cad19dc12  7112932 / 7112932.0  (100.00%)
Successfully packaged artifacts and wrote output template to file packaged-template.yaml.
Execute the following command to deploy the packaged template
aws cloudformation deploy --template-file /path/to/packaged-template.yaml --stack-name <YOUR STACK NAME>
```

As the command output suggests, you can now use the cli to deploy the application.
Choose a stack name and run the `aws cloudformation deploy` command from the output of the package command.
 
```
$ aws cloudformation deploy --template-file packaged-template.yaml \
  --capabilities CAPABILITY_IAM \
  --stack-name <YOUR STACK NAME>
```

Once the application is deployed, you can describe the stack to show the API endpoint that was created.
The endpoint should be the `ApiUrl` key of the `Outputs` property:

```
$ aws cloudformation describe-stacks --stack-name <YOUR STACK NAME>
{
    "Stacks": [
        {
            "StackId": "arn:aws:cloudformation:us-west-2:xxxxxxxxxxxx:stack/new-sample-app/xxxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxx",
            "Description": "Sample API Endpoint configured using Swagger specified inline and backed by a Lambda function",
            "Tags": [],
            "Outputs": [
                {
                    "OutputKey": "ApiUrl",
                    "OutputValue": "https://xxxx.execute-api.us-west-2.amazonaws.com/dev"
                }
            ],
            "CreationTime": "2017-08-11T11:09:43.243Z",
            "Capabilities": [
                "CAPABILITY_IAM"
            ],
            "StackName": "new-sample-app",
            "NotificationARNs": [],
            "StackStatus": "CREATE_COMPLETE",
            "DisableRollback": false,
            "ChangeSetId": "arn:aws:cloudformation:us-west-2:xxxxxxxxxxxx:changeSet/awscli-cloudformation-package-deploy-xxxxxxxxxx/xxxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxx",
            "LastUpdatedTime": "2017-08-11T11:09:53.661Z"
        }
    ]
}

```

## Run
Copy the `OutputValue` into `API URL` in the following `curl` command to test a request.
```
curl -i -X POST -d '{"name":"Bob","message":"Hello"}' <API URL>/echo
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 32
Connection: keep-alive
Date: Thu, 20 Sep 2017 06:33:06 GMT
x-amzn-RequestId: e1cb4424-a416-11e7-bbce-b186d7a68ea7
Access-Control-Allow-Origin: *
X-Amzn-Trace-Id: sampled=0;root=1-59cc97a0-c5844e6e669804c49d80e9fe
X-Cache: Miss from cloudfront
Via: 1.1 xxxx.cloudfront.net (CloudFront)
X-Amz-Cf-Id: xxxx

{"name":"Bob","message":"Hello"}
```
