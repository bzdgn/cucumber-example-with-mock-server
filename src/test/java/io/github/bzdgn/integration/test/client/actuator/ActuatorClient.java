package io.github.bzdgn.integration.test.client.actuator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;

import io.github.bzdgn.integration.test.util.ConnectionUtils;
import io.github.bzdgn.integration.test.util.Retry;

public class ActuatorClient {

    private static final int AFTER_WAIT = 1_000;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${integration.spring-actuator-root-url}/actuator")
    public String actuatorRootUrl;

    @Autowired
    private Gson gson;

    private boolean dcnUp;

    /**
     * @return True if we have seen DCN up and running. False if it was already healthy, so we can assume everything else is
     *         healthy too
     */
    public boolean waitDcnIsHealthy() {
        this.logger.debug("Checking if DCN is healthy on {}", this.actuatorRootUrl);
        if (this.dcnUp) {
            return false;
        } else {
            Retry.waitForSuccess(this.actuatorRootUrl + "/health");
            this.dcnUp = true; // No need to try this again once we've seen DCN is healthy
            Retry.sleep(AFTER_WAIT);
            return true;
        }
    }

    public void checkRouteIsUp(String routeName) {
        this.logger.debug("Checking whether {} route is up on {}", routeName, this.actuatorRootUrl);

        assertNotNull(routeName, "routeName does not exist");

        Integer statusResponse = JsonPath.parse(ConnectionUtils
                .getHttpGetResponse(
                        String.format("%s/jolokia/read/org.apache.camel:context=*,type=routes,name=%%22%s%%22", this.actuatorRootUrl, routeName))
                .body()).read("$['status']");
        assertNotNull(statusResponse);
        assertEquals(200, statusResponse);
    }

    public String triggerRoute(String routeName) {
        this.logger.debug("Triggering route for {} on {}", routeName, this.actuatorRootUrl);
        JolokiaExecuteRequest jolokiaRequest = new JolokiaExecuteRequest("direct:" + routeName, null, null);
        String responseBody = ConnectionUtils.getHttpPostResponse(this.actuatorRootUrl + "/hawtio/jolokia", this.gson.toJson(jolokiaRequest)).body();
        assertNotNull(responseBody);
        Integer statusResponse = JsonPath.parse(responseBody).read("$['status']");
        assertEquals(200, statusResponse);
        
        return responseBody;
    }
}
