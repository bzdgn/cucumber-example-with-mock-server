package io.github.bzdgn.integration.test.definitions.configuration;

import org.springframework.test.context.ContextConfiguration;

import io.cucumber.spring.CucumberContextConfiguration;
import io.github.bzdgn.integration.test.client.actuator.ActuatorClient;

/*
 * This is where SpringContext is configured.
 *
 * Add all autowired candidates to the SpringBootTest classes parameter list
 * so that the autowired candidate implementations can be instantiated.
 *
 * BeanConfiguration class is where Spring singleton beans are configured.
 *
 */
@CucumberContextConfiguration
@ContextConfiguration(classes = { BeanConfiguration.class, ActuatorClient.class })
public class SpringContextConfiguration {

}
