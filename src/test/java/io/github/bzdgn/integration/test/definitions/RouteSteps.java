package io.github.bzdgn.integration.test.definitions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.jayway.jsonpath.JsonPath;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bzdgn.integration.test.client.actuator.ActuatorClient;
import io.github.bzdgn.integration.test.util.ConnectionUtils;
import io.github.bzdgn.integration.test.util.FileUtils;

public class RouteSteps {

    @Value("${integration.test.source-folder}")
    public String testSourceFolder;
    
    @Value("${person-endpoint}")
    public String consumedEndpoint;
    
    private String response;

    @Autowired
    private ActuatorClient actuatorClient;

    @Given("environment for {string} up and running")
    public void environment_for_up_and_running(String routeName) {
        this.actuatorClient.checkRouteIsUp(routeName);
    }

    @When("data received from endpoint")
    public void data_received_from_endpoint() {
    	HttpResponse<String> consumed = ConnectionUtils.getHttpGetResponse(consumedEndpoint);
    	
    	this.response = consumed.body();
    }

    @Given("payload file {string} is set")
    public void payload_file_is_set(String testFileName) throws IOException, URISyntaxException {
		String testPayload = FileUtils.readFileContent("src/test/resources/test-data/" + testFileName);
		String mockRequest = buildRequestObject("/persons", null, "application/json", testPayload).toString();
    	
		HttpResponse<String> mainReqResponse = ConnectionUtils.getHttpPutResponse("http://localhost:1080/mockserver/expectation", mockRequest);
    	
		assertTrue(mainReqResponse.statusCode() == 201);
    }
    
    @Then("consumed endpoint contains the following data")
    public void consumed_endpoint_contains_the_following_data(DataTable dataTable) {
    	System.out.println(this.response);
    	
        for (int i = 1; i < dataTable.height(); i++) {
            List<String> row = dataTable.row(i);
            
            assertEquals(row.get(0), JsonPath.parse(this.response).read("$["+(i-1)+"]['name']"));
            assertEquals(Integer.valueOf(row.get(1)), JsonPath.parse(this.response).read("$["+(i-1)+"]['age']", Integer.class));
        }
    }
    
    private static JsonArray buildRequestObject(String path, String[] parameters, String contentType, String body) {
        var httpRequestBuilder = Json.createObjectBuilder().add("path", path);
        if (parameters != null && parameters.length > 0) {
            var queryStringParametersBuilder = Json.createObjectBuilder();
            for (String parameter : parameters) {
                int splitPos = parameter.indexOf('=');
                queryStringParametersBuilder.add(parameter.substring(0, splitPos), Json.createArrayBuilder().add(parameter.substring(splitPos + 1)));
            }
            httpRequestBuilder.add("queryStringParameters", queryStringParametersBuilder);
        }
        
        return Json.createArrayBuilder()
            .add(Json.createObjectBuilder()
                .add("httpRequest", httpRequestBuilder.build())
                .add("httpResponse", Json.createObjectBuilder()
                    .add("statusCode", 200)
                    .add("headers", Json.createObjectBuilder()
                        .add("Content-Type", Json.createArrayBuilder().add(contentType)))
                    .add("body",body)))
            .build();
    }

}
