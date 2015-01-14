1) Make sure that Java1.7 and Maven should be installed on machine.
2) Download source code from GitHUB<<https://github.com/adswizz/cybage-persomap>>
3) Modify the environment specific parameters from 'src/main/resource/persomap-config.yml' like 

a) For AWS configuration provide input to following properties
  # AWS access key
  access_key_id : <<AWS_ACCESS_KEY_ID>>

  # AWS Secret access key
  secret_access_key : <<AWS_ACCESS_SECRET_KEY>>

b) For DynamoDB 

 # Dynamo DB Table name  
  dynamodb_table_name : <<DYNAMODB_TABLE_NAME>>

 # AWS Endpoint URL
  aws_endpoint_url : http://<<IpAddress>>:<<8000>>
   e.g- aws_endpoint_url : http://localhost:8000

c) For Elastic Cache 
   aws_cache_url : <<IPAddress>>:<<11211>>

d) Server port configuration
  server:
    applicationConnectors:
      - type: http
        port: 80
    adminConnectors:
      - type: http
        port: 8081

4) Goto Source folder and open command prompt

5) To build a new persomap jar, use the command 'mvn clean install'. New jar will be created under /target folder.

6) If you don't want to run the Test cases then skip the Test cases. use command as 
'mvn clean install -Dmaven.test.skip=true'

7) Goto /target folder and open command prompt and run the application using command as 
 'java -jar <<Generated_JAR_NAME>> server persomap-config.yml'
 e.g -  java -jar persomap.jar server persomap-config.yml
 
8) Check the application health on admin port using url as 'http://localhost:8081'

9) Check the Swagger API on application port using url as http://localhost:80/swagger
