package com.dynamo.connection;



import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author : Group 5
 */


public class CreateTable {
    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration("dynamodb.us-west-2.amazonaws.com", "us-west-2"))
            .withCredentials(new EnvironmentVariableCredentialsProvider())
            .build();

    static DynamoDB dynamoDB = new DynamoDB(client);


    static SimpleDateFormat dateFormatter = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    static String airbnbListings = "ListingsNEW";

    static String csvPath = "E:/1RIT/FIS/airbnbproject/nyc/dataset/listings.csv";
    

    public static void main(String[] args) throws Exception {

        try {

            //deleteTable(airbnbListings);
            

            // Parameter1: table name // Parameter2: reads per second //
            // Parameter3: writes per second // Parameter4/5: hash key and type
            // Parameter6/7: range key and type (if applicable)

            createTable(airbnbListings, 10L, 5L, "id", "N");
            System.out.println("created");
            readFile(airbnbListings);


            //loadAirbnbListings(airbnbListings);


        } catch (Exception e) {
            System.err.println("Program failed:");
            System.err.println(e.getMessage());
        }
        System.out.println("Success.");

    }


    private static void deleteTable(String tableName) {
        Table table = dynamoDB.getTable(tableName);
        try {
            System.out.println("Issuing DeleteTable request for " + tableName);
            table.delete();
            System.out.println("Waiting for " + tableName
                    + " to be deleted...this may take a while...");
            table.waitForDelete();

        } catch (Exception e) {
            System.err.println("DeleteTable request failed for " + tableName);
            System.err.println(e.getMessage());
        }
    }

    private static void createTable(
            String tableName, long readCapacityUnits, long writeCapacityUnits,
            String hashKeyName, String hashKeyType) {

        createTable(tableName, readCapacityUnits, writeCapacityUnits,
                hashKeyName, hashKeyType, null, null);
    }

    private static void createTable(
            String tableName, long readCapacityUnits, long writeCapacityUnits,
            String hashKeyName, String hashKeyType,
            String rangeKeyName, String rangeKeyType) {

        try {

            ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
            keySchema.add(new KeySchemaElement()
                    .withAttributeName(hashKeyName)
                    .withKeyType(KeyType.HASH));

            ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
            attributeDefinitions.add(new AttributeDefinition()
                    .withAttributeName(hashKeyName)
                    .withAttributeType(hashKeyType));

            if (rangeKeyName != null) {
                keySchema.add(new KeySchemaElement()
                        .withAttributeName(rangeKeyName)
                        .withKeyType(KeyType.RANGE));
                attributeDefinitions.add(new AttributeDefinition()
                        .withAttributeName(rangeKeyName)
                        .withAttributeType(rangeKeyType));
            }

            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(keySchema)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(readCapacityUnits)
                            .withWriteCapacityUnits(writeCapacityUnits));



        } catch (Exception e) {
            System.err.println("CreateTable request failed for " + tableName);
            System.err.println(e.getMessage());
        }
    }


    private static void readFile(String tableName){

        try {
            Table table = dynamoDB.getTable(tableName);
            BufferedReader br = new BufferedReader(new FileReader(csvPath));

            String[] attributes = br.readLine().split(",");
            String row = "";

            String id = attributes[0];
            String host_id = attributes[19];
            String neighbourhood = attributes[38];
            String street = attributes[37];
            String city = attributes[41];
            String zipcode = attributes[43];
            String property_type = attributes[51];
            String price = attributes[60];
            int count = 0;
            System.out.println(property_type);

            while((row = br.readLine()) != null){
                if(count <5) {
                    String[] col = row.split(",");

                    System.out.println(id+" "+col[19]);


                    Item item = new Item()
                            .withPrimaryKey(id, col[0])
                            .withString(host_id, col[19])
                            .withString(neighbourhood, col[38])
                            .withString(city, col[41])
                            .withString(zipcode, col[43]);
                    table.putItem(item);
                    count++;
                }
                else {
                    break;
                }

            }

        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

//    private static void loadAirbnbListings(String tableName) {
//
//        Table table = dynamoDB.getTable(tableName);
//
//        try {
//
//            System.out.println("Adding data to " + tableName);
//
//
//
//        } catch (Exception e) {
//            System.err.println("Failed to create item in " + tableName);
//            System.err.println(e.getMessage());
//        }
//
//    }


}

