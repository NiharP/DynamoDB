package com.dynamo.connection;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;

/**
 * Created by Niharp on 11/1/2017.
 */
public class LocalDynamoConnection {
    public static void main(String[] args) throws Exception {
        AmazonDynamoDB dynamoDB = null;
        try {
            dynamoDB = DynamoDBEmbedded.create().amazonDynamoDB();
            listTables(dynamoDB.listTables(), "DynamoDB Embedded");
        } finally {
            if (dynamoDB != null) {
                dynamoDB.shutdown();
            }
        }

        // Create an in-memory and in-process instance of DynamoDB Local that runs over HTTP
        final String[] localArgs = {"-inMemory"};
        DynamoDBProxyServer server = null;
        try {
            server = ServerRunner.createServerFromCommandLineArgs(localArgs);
            server.start();

            dynamoDB = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
                    // we can use any region here
                    new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
                    .build();

            // use the DynamoDB API over HTTP
            listTables(dynamoDB.listTables(), "DynamoDB Local over HTTP");
        } finally {
            // Stop the DynamoDB Local endpoint
            if (server != null) {
                server.stop();
            }
        }
    }

    public static void listTables(ListTablesResult result, String method) {
        System.out.println("found " + Integer.toString(result.getTableNames().size()) + " tables with " + method);
        for (String table : result.getTableNames()) {
            System.out.println(table);
        }
    }
}

