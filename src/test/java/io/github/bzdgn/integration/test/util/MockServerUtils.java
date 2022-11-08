package io.github.bzdgn.integration.test.util;

import java.io.IOException;
import java.nio.file.Paths;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

public class MockServerUtils {

    public static JsonArray buildRequest(String pathBegins, String filePath) throws IOException {
        String fileName = Paths.get(filePath).getFileName().toString();
        String body = FileUtils.readFileContent(filePath);
        String endpointPath = pathBegins + ".*";
        String contentType = getContentTypeByFileName(fileName);

        JsonArray requests = Json.createArrayBuilder()
                .add(buildRequestObject(endpointPath, contentType, body))
                .build();

        return requests;
    }

    public static JsonArray buildFileRequest(String externalId, String pathBegins, String filePath) throws IOException {
        String fileName = Paths.get(filePath).getFileName().toString();
        String body = FileUtils.readFileContent(filePath);
        String endpointPath = pathBegins + ".*/" + ".*" + externalId + ".*/" + fileName.replace(".", "\\.") + "$";
        String contentType = getContentTypeByFileName(fileName);

        JsonArray requests = Json.createArrayBuilder()
                .add(buildRequestObject(endpointPath, contentType, body))
                .build();

        return requests;
    }

    public static JsonObject buildRequestObject(String path, String contentType, String body) {
        JsonObject httpRequest = Json.createObjectBuilder()
                .add("path", path)
                .build();

        JsonArray contentTypes = Json.createArrayBuilder()
                .add(contentType)
                .build();

        JsonObject headers = Json.createObjectBuilder()
                .add("Content-Type", contentTypes)
                .build();

        JsonObject httpResponse = Json.createObjectBuilder()
                .add("statusCode", 200)
                .add("headers", headers)
                .add("body", body)
                .build();

        JsonObject request = Json.createObjectBuilder()
                .add("httpRequest", httpRequest)
                .add("httpResponse", httpResponse)
                .build();

        return request;
    }

    public static String getContentTypeByFileName(String filename) {
        String extension = FileUtils.getFileExtension(filename);

        return getContentTypeByExtension(extension);
    }

    public static String getContentTypeByExtension(String extension) {
        switch (extension) {
        case "xml": {
            return "application/xml;charset=UTF-8";
        }

        case "html": {
            return "text/html;charset=utf-8";
        }

        case "pdf": {
            return "application/pdf;charset=UTF-8";
        }

        default: {
            return "application/x-unknown-content-type;charset=UTF-8";
        }
        }
    }

}
