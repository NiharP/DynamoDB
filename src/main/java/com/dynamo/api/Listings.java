package com.dynamo.api;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.dynamo.connection.Connection;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Iterator;

/**
 * Created by Niharp on 11/3/2017.
 */
@Path("getListings")
public class Listings {
    @Path("/{city}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public String getListings(@PathParam("city") String city) {

        return getListingsByCity(city);
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
