package com.dynamo.connection;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.*;
import com.dynamo.model.Listing;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Connecting to Local DynamoDB
 */
public class Connection {
    private static final String JOHN_PROPERTY = "{\n" +
            "\t\"Address\": \"220 John Street, 2230, Rochester, NY-14623\",\n" +
            "\t\"Price\": 220,\n" +
            "\t\"Type\": \"House\"\n" +
            "}";
    private static final Set<String> REVIEWS = new HashSet<>(Arrays.asList("1", "2"));
    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
            //    .withCredentials(new EnvironmentVariableCredentialsProvider())
            .build();

    public static DynamoDB dynamoDB = new DynamoDB(client);


    static SimpleDateFormat dateFormatter = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static String listings = "Listings";
    public static String reviews = "Reviews";

    public static void main(String[] args) throws Exception {

        try {

            deleteTable(listings);
            deleteTable(reviews);

            // Parameter1: table name // Parameter2: reads per second //
            // Parameter3: writes per second // Parameter4/5: hash key and type
            // Parameter6/7: range key and type (if applicable)

            createTable(reviews, 10L, 5L, "Id", "N");
            createTable(listings, 10L, 5L, "Id", "N", "PinCode", "N");

            loadSampleReviews(reviews);
            loadSampleListings(listings);

            getListingsByCity("New York");


        } catch (Exception e) {
            System.err.println("Program failed:");
            System.err.println(e.getMessage());
        }
        System.out.println("Success.");

    }

    public Boolean addListing(Listing listing) {
        Table table = dynamoDB.getTable(listings);

        Item item = new Item()
                .withPrimaryKey("Id", listing.getId())
                .withString("Name",listing.getName())
                .withString("City", listing.getCity())
                .withString("Location", listing.getLocation())
                .withString("Address", listing.getAddress())
                .withNumber("PinCode", listing.getPinCode())
                .withNumber("Price", listing.getPrice())
                .withString("Descriptor", listing.getDescriptor())
                .withString("HouseType", listing.getHouseType());
        table.putItem(item);
        return true;

    }

    private static void getListingsByCity(String city) {
        Table table = dynamoDB.getTable(listings);
        Index index = table.getIndex("CityIndex");

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("City = :v_city")
                .withValueMap(new ValueMap()
                        .withString(":v_city", city));

        ItemCollection<QueryOutcome> items = index.query(spec);
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next().toJSONPretty());
        }
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

            if (listings.equals(tableName)) {
                attributeDefinitions.add(new AttributeDefinition()
                        .withAttributeName("City")
                        .withAttributeType("S"));
                attributeDefinitions.add(new AttributeDefinition()
                        .withAttributeName("Location")
                        .withAttributeType("S"));
            }
            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(keySchema)
                    .withAttributeDefinitions(attributeDefinitions)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(readCapacityUnits)
                            .withWriteCapacityUnits(writeCapacityUnits));

            // If table if listing then add secondary index.
            if (listings.equals(tableName)) {

                GlobalSecondaryIndex cityIndex = new GlobalSecondaryIndex()
                        .withIndexName("CityIndex")
                        .withProvisionedThroughput(new ProvisionedThroughput()
                                .withReadCapacityUnits(readCapacityUnits)
                                .withWriteCapacityUnits(writeCapacityUnits))
                        .withProjection(new Projection().withProjectionType(ProjectionType.ALL));
                ArrayList<KeySchemaElement> indexKeySchema = new ArrayList<>();


                indexKeySchema.add(new KeySchemaElement()
                        .withAttributeName("City")
                        .withKeyType(KeyType.HASH));  //Partition key
                indexKeySchema.add(new KeySchemaElement()
                        .withAttributeName("Location")
                        .withKeyType(KeyType.RANGE));  //Sort key

                cityIndex.setKeySchema(indexKeySchema);
                request.setGlobalSecondaryIndexes(Collections.singletonList(cityIndex));
            }


            System.out.println("Issuing CreateTable request for " + tableName);
            Table table = dynamoDB.createTable(request);
            System.out.println("Waiting for " + tableName
                    + " to be created...this may take a while...");
            table.waitForActive();
            System.out.println(table.getDescription());

        } catch (Exception e) {
            System.err.println("CreateTable request failed for " + tableName);
            System.err.println(e.getMessage());
        }
    }

    private static void loadSampleListings(String tableName) {

        Table table = dynamoDB.getTable(tableName);

        try {

            System.out.println("Adding data to " + tableName);

            Item item = new Item()
                    .withPrimaryKey("Id", 101L)
                    .withString("Name", "First Property")
                    .withNumber("HostId", 111)
                    .withString("Location", "John Street")
                    .withString("City", "New York")
                    .withJSON("PropertyInfo", JOHN_PROPERTY)
                    .withNumber("PinCode", 14623L)
                    .withStringSet("Reviews", REVIEWS)
                    .withBoolean("IsAvailable", true)
                    .withString("HouseType", "OwnedHouse");
            table.putItem(item);

            item = new Item()
                    .withPrimaryKey("Id", 103L)
                    .withString("Name", "Second Property")
                    .withNumber("HostId", 111)
                    .withString("Location", "John Street")
                    .withString("City", "New York")
                    .withJSON("PropertyInfo", JOHN_PROPERTY)
                    .withNumber("PinCode", 14623L)
                    .withStringSet("Reviews", REVIEWS)
                    .withBoolean("IsAvailable", true)
                    .withString("HouseType", "OwnedHouse");
            table.putItem(item);

            item = new Item()
                    .withPrimaryKey("Id", 105L)
                    .withString("Name", "Third Property")
                    .withNumber("HostId", 111)
                    .withString("Location", "John Street")
                    .withJSON("PropertyInfo", JOHN_PROPERTY)
                    .withString("City", "Rochester")
                    .withNumber("PinCode", 14623L)
                    .withStringSet("Reviews", REVIEWS)
                    .withBoolean("IsAvailable", true)
                    .withString("HouseType", "OwnedHouse");
            table.putItem(item);

        } catch (Exception e) {
            System.err.println("Failed to create item in " + tableName);
            System.err.println(e.getMessage());
        }

    }

    private static void loadSampleReviews(String tableName) {
        try {
            long time1 = (new Date()).getTime() - (7 * 24 * 60 * 60 * 1000); // 7
            // days
            // ago
            long time2 = (new Date()).getTime() - (14 * 24 * 60 * 60 * 1000); // 14
            // days
            // ago
            long time3 = (new Date()).getTime() - (21 * 24 * 60 * 60 * 1000); // 21
            // days
            // ago

            Date date1 = new Date();
            date1.setTime(time1);

            Date date2 = new Date();
            date2.setTime(time2);

            Date date3 = new Date();
            date3.setTime(time3);

            dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

            Table table = dynamoDB.getTable(tableName);

            System.out.println("Adding data to " + tableName);

            Item item = new Item()
                    .withPrimaryKey("Id", 1)
                    .withString("Subject", "Review 1")
                    .withString("Message", "Review message")
                    .withString("LastPostedBy", "User A")
                    .withString("LastPostedDateTime", dateFormatter.format(date2))
                    .withNumber("Views", 0)
                    .withNumber("Replies", 0)
                    .withNumber("Answered", 0);
            table.putItem(item);

            item = new Item()
                    .withPrimaryKey("Id", 2)
                    .withString("Subject", "Review 2")
                    .withString("Message", "Review message 2")
                    .withString("LastPostedBy", "User B")
                    .withString("LastPostedDateTime", dateFormatter.format(date2))
                    .withNumber("Views", 0)
                    .withNumber("Replies", 0)
                    .withNumber("Answered", 0);
            table.putItem(item);

            item = new Item()
                    .withPrimaryKey("Id", 3)
                    .withString("Subject", "Review 3")
                    .withString("Message", "Review message 3")
                    .withString("LastPostedBy", "User C")
                    .withString("LastPostedDateTime", dateFormatter.format(date2))
                    .withNumber("Views", 0)
                    .withNumber("Replies", 0)
                    .withNumber("Answered", 0);
            table.putItem(item);

        } catch (Exception e) {
            System.err.println("Failed to create item in " + tableName);
            System.err.println(e.getMessage());
        }

    }

    private static void loadSampleReplies(String tableName) {
        try {
            // 1 day ago
            long time0 = (new Date()).getTime() - (1 * 24 * 60 * 60 * 1000);
            // 7 days ago
            long time1 = (new Date()).getTime() - (7 * 24 * 60 * 60 * 1000);
            // 14 days ago
            long time2 = (new Date()).getTime() - (14 * 24 * 60 * 60 * 1000);
            // 21 days ago
            long time3 = (new Date()).getTime() - (21 * 24 * 60 * 60 * 1000);

            Date date0 = new Date();
            date0.setTime(time0);

            Date date1 = new Date();
            date1.setTime(time1);

            Date date2 = new Date();
            date2.setTime(time2);

            Date date3 = new Date();
            date3.setTime(time3);

            dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

            Table table = dynamoDB.getTable(tableName);

            System.out.println("Adding data to " + tableName);

            // Add threads.

            Item item = new Item()
                    .withPrimaryKey("Id", "Amazon DynamoDB#DynamoDB Thread 1")
                    .withString("ReplyDateTime", (dateFormatter.format(date3)))
                    .withString("Message", "DynamoDB Thread 1 Reply 1 text")
                    .withString("PostedBy", "User A");
            table.putItem(item);

            item = new Item()
                    .withPrimaryKey("Id", "Amazon DynamoDB#DynamoDB Thread 1")
                    .withString("ReplyDateTime", dateFormatter.format(date2))
                    .withString("Message", "DynamoDB Thread 1 Reply 2 text")
                    .withString("PostedBy", "User B");
            table.putItem(item);

            item = new Item()
                    .withPrimaryKey("Id", "Amazon DynamoDB#DynamoDB Thread 2")
                    .withString("ReplyDateTime", dateFormatter.format(date1))
                    .withString("Message", "DynamoDB Thread 2 Reply 1 text")
                    .withString("PostedBy", "User A");
            table.putItem(item);

            item = new Item()
                    .withPrimaryKey("Id", "Amazon DynamoDB#DynamoDB Thread 2")
                    .withString("ReplyDateTime", dateFormatter.format(date0))
                    .withString("Message", "DynamoDB Thread 2 Reply 2 text")
                    .withString("PostedBy", "User A");
            table.putItem(item);

        } catch (Exception e) {
            System.err.println("Failed to create item in " + tableName);
            System.err.println(e.getMessage());

        }
    }
}
