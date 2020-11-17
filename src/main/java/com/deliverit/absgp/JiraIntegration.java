package com.deliverit.absgp;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import sun.misc.BASE64Encoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JiraIntegration {

    public static List<JiraData> consume(String username, String password, String initialDate, String finalDate) {
        Client client = Client.create();

        String authString = username + ":" + password;
        String authStringEnc = new BASE64Encoder().encode(authString.getBytes());
        WebResource webResource = client
                .resource("http://tools.dootax.com.br:8080/jira/rest/tempo-timesheets/4/worklogs/search");

        JsonObject json = new JsonObject();
        json.addProperty("from", initialDate);
        json.addProperty("to", finalDate);

        ClientResponse response = webResource.type("application/json").header("authorization", "Basic " + authStringEnc).post(ClientResponse.class, json.toString());

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String output = response.getEntity(String.class);

        JsonArray jiraData = new Gson().fromJson(output, JsonArray.class);

        List<JiraData> cards = StreamSupport.stream(jiraData.spliterator(), false).map(jsonElement -> {
            JsonObject object = jsonElement.getAsJsonObject();
            return new JiraData(object.get("timeSpentSeconds").getAsInt(), object.get("issue").getAsJsonObject().get("key").getAsString(), LocalDateTime.parse(object.get("started").getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.000")));
        }).collect(Collectors.toList());

        return cards;
    }


}
