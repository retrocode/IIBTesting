package com.retrocode.iib.testing

import org.apache.camel.CamelContext
import org.apache.camel.EndpointInject
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.junit.After
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = EmptyRouteCamelConfiguration.class)
class BaseSpecification extends Specification  {
    @Autowired
    CamelContext camelContext

    protected static final NO_SOURCE_CHANGE_REQUIRED = "NO_SOURCE_CHANGE_REQUIRED"

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @EndpointInject(uri = "mock:backout")
    protected MockEndpoint backoutEndpoint;

    //private static Set<String> fromEndpoints;
    private static Map<String,String> fromEndpoints;
    ProducerTemplate producer

    @Before
    public void setup() {
        producer = camelContext.createProducerTemplate()
    }

    @After
    public void teardown(){
        producer = null
    }


}
