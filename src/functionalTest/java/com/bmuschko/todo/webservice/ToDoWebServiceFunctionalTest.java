package com.bmuschko.todo.webservice;

import com.bmuschko.todo.webservice.model.ToDoItem;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class ToDoWebServiceFunctionalTest {

    private final static File DISTRIBUTION_DIR = new File(System.getProperty("distribution.dir"));
    private final static String ARCHIVE_NAME = System.getProperty("archive.name");
    private final static String CONTEXT = "/todos";
    private final static MediaType JSON_MEDIA_TYPE = MediaType.get("application/json");
    private final OkHttpClient client = new OkHttpClient();

    @Container
    private GenericContainer appContainer = createContainer();

    private static GenericContainer createContainer() {
        return new GenericContainer(buildImageDockerfile())
                .withExposedPorts(8080)
                .waitingFor(Wait.forHttp("/actuator/health")
                        .forStatusCode(200));
    }

    private static ImageFromDockerfile buildImageDockerfile() {
        return new ImageFromDockerfile()
                .withFileFromFile(ARCHIVE_NAME, new File(DISTRIBUTION_DIR, ARCHIVE_NAME))
                .withDockerfileFromBuilder(builder -> builder
                        .from("openjdk:jre-alpine")
                        .copy(ARCHIVE_NAME, "/app/" + ARCHIVE_NAME)
                        .entryPoint("java", "-jar", "/app/" + ARCHIVE_NAME)
                        .build());
    }

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
        url.append("http://");
        url.append(appContainer.getContainerIpAddress());
        url.append(":");
        url.append(appContainer.getFirstMappedPort());
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
