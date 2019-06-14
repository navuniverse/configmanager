# configmanager
Uploading App Config to AWS Parameter Store

This is a utility tool using which you can upload your application config to AWS Parameter Store. You need to provide the CSV file of config properties you wish to upload on Parameter Store along with the environment (Development/Production) to correctly select the connection.

Before running the utility, just make sure to update the AWS Access Key and Secret Key in application.properties for your Dev and Prod Environment.

You can use the Swagger UI for the Utility to upload the Config using - http://localhost:8080/config/swagger-ui.html
