package com.dynamo;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.dynamo.connection.Connection;
import com.dynamo.model.Address;
import com.dynamo.model.Listing;
import com.dynamo.model.PropertyInfo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Niharp on 11/26/2017.
 */
public class LoadDataUtil {

    public static final int ADDRESS_LINE1 = 5;
    public static final int CITY = 7;
    public static final int STATE = 8;
    public static final int COUNTRY = 11;
    public static final int ZIP_CODE = 9;
    public static final int PRICE = 14;
    public static final int ROOM_TYPE = 13;
    public static final int PROPERTY_TYPE = 12;
    public static final int ID = 0;
    public static final int NAME = 1;
    public static final int SUMMARY = 2;
    public static final int SPACE = 3;
    public static final int DESCRIPTOR = 4;
    public static final int LOCATION = 6;

    static Connection connection = new Connection();

    public static void loadData() {
        String csvFile = "listings_clean.csv";
        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] attributes = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                Address address = new Address().builder()
                        .line1(attributes[ADDRESS_LINE1])
                        .city(attributes[CITY])
                        .state(attributes[STATE])
                        .country(attributes[COUNTRY])
                        .zipCode(Long.parseLong(attributes[ZIP_CODE]))
                        .build();
                PropertyInfo propertyInfo = new PropertyInfo().builder()
                        .address(address)
                        .price(attributes[PRICE])
                        .type(attributes[PROPERTY_TYPE])
                        .roomType(attributes[ROOM_TYPE])
                        .build();

                Listing listing = new Listing().builder()
                        .id(Long.parseLong(attributes[ID]))
                        .name(attributes[NAME])
                        .summary(attributes[SUMMARY])
                        .space(attributes[SPACE])
                        .descriptor(attributes[DESCRIPTOR])
                        .address(address)
                        .propertyInfo(propertyInfo)
                        .city(attributes[CITY])
                        .zipCode(Long.parseLong(attributes[ZIP_CODE]))
                        .location(attributes[LOCATION])
                        .build();

                connection.addListing(listing);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        loadData();
    }
}
