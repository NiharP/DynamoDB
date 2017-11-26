# DynamoDB
DynamoDB basic feature Implementation. 

Steps to install.
1. Install Gradle 4.3 (https://gradle.org/install/)
2. Run gradle clean build from terminal
3. To import all external dependencies in IntelliJ/ Eclipse run gradle cleanIdea idea / gradle cleanEclipse eclipse.
4. To run application : gradle jettyRun (Make sure that no service is using 8080 port)
5. You can see hello world at http://localhost:8080/DynamoProject/rest/hello.
6. Add lombok plugin https://projectlombok.org/ to your IDE.
7. Add AWS credentials as Environment variables. 
    AWS_ACCESS_KEY_ID = A****************Q
    AWS_SECRET_ACCESS_KEY = v*****************************l
    OR
    To run dyamoDB locally.
    Follow the instructions
    http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html
        1. Download the zip. Extract in some folder, lets say 'temp'
        2. Run command from the folder temp/dynamoDb_latest.
        java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb