package com.dynamo.api;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.dynamo.connection.Connection;
import com.dynamo.model.Listing;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Iterator;

/**
 * Created by Niharp on 11/3/2017.
 */
@Path("/")
public class ListingsService {
    @Path("getListings/{city}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public String getListings(@PathParam("city") String city) {

        return getListingsByCity(city);
    }

    @Path("getListingsDetails/{listingId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public String getListingsDetailsById(@PathParam("listingId") String listingId) {
        return getListingsDetails(Long.parseLong(listingId));
    }

    private String getListingsDetails(Long listingId) {
        return new Connection().getListingDetails(listingId);
    }

    @Path("addListing")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addListing(String listing) {
        Listing listing1 = new Gson().fromJson(listing, Listing.class);
        listing1.setId(System.currentTimeMillis());
        return addListingToDb(listing1);

    }

    private String addListingToDb(Listing listing1) {
        if (!new Connection().addListing(listing1)) {
            return "Failed";
        }
        return "Success";
    }

    public static void main(String[] args) {
//        Listing listing1 = new Listing();
//        listing1.setId(System.currentTimeMillis());
//        listing1.setName("Hello");
//        listing1.setCity("Rochester");
//        listing1.setLocation("RIT");
//        listing1.setDescriptor("Beautiful");
//        listing1.setHouseType("1 BHK Apartment");
//        listing1.setAddress("220 John Street");
//        listing1.setPinCode(123456L);
//        listing1.setPrice(122);
//        new ListingsService().addListingToDb(listing1);
        new ListingsService().getListingsDetailsById("5232121");
    }

    private static String getListingsByCity(String city) {
        String listings = "[";
        Table table = Connection.dynamoDB.getTable(Connection.listings);
        Index index = table.getIndex("CityIndex");

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("City = :v_city")
                .withValueMap(new ValueMap()
                        .withString(":v_city", city));

        ItemCollection<QueryOutcome> items = index.query(spec);
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            String listing = iter.next().toJSONPretty();
            System.out.println(listing);
            listings += listing;
            if (iter.hasNext()) {
                listings += ",";
            }
        }
        listings += "]";
        return listings;
    }
}
