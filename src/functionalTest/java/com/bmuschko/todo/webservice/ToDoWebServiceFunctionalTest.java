package com.bmuschko.todo.webservice;

import com.bmuschko.todo.webservice.model.ToDoItem;
import okhttp3.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ToDoWebServiceFunctionalTest {

    private final static String WEB_SERVICE_URL = "http://localhost:8080";
    private final static String CONTEXT = "/todos";
    private final static MediaType JSON_MEDIA_TYPE = MediaType.get("application/json");
    private final OkHttpClient client = new OkHttpClient();

    @Test
    @DisplayName("can retrieve all items before and after inserting new ones")
    void retrieveAllItems() {
        String allItems = getAllItems();
        assertEquals("[]", allItems);

        ToDoItem toDoItem = new ToDoItem();
        toDoItem.setName("Buy milk");
        toDoItem.setCompleted(false);
        insertItem(toDoItem);

        allItems = getAllItems();
        assertEquals("[{\"id\":1,\"name\":\"Buy milk\",\"completed\":false}]", allItems);
    }

    private String getAllItems() {
        Request request = new Request.Builder()
                .url(buildEndpointUrl(CONTEXT))
                .build();
        Response response = callEndpoint(request);
        try {
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException("Unable to retrieve response body", e);
        }
    }

    private void insertItem(ToDoItem toDoItem) {
        RequestBody requestBody = RequestBody.create(JSON_MEDIA_TYPE, buildToDoItemJson(toDoItem));
        Request request = new Request.Builder()
                .url(buildEndpointUrl(CONTEXT))
                .post(requestBody)
                .build();
        callEndpoint(request);
    }

    private URL buildEndpointUrl(String context) {
        StringBuilder url = new StringBuilder();
        url.append(WEB_SERVICE_URL);
        url.append(context);

        try {
            return new URL(url.toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid URL", e);
        }
    }

    private Response callEndpoint(Request request) {
        try {
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to call endpoint '" + request.url().url().toString() + "' [HTTP " + response.code() + "]");
            }

            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildToDoItemJson(ToDoItem toDoItem) {
        return "{ \"name\": \"" + toDoItem.getName() + "\", \"completed\": " + toDoItem.isCompleted() + " }";
    }
}
