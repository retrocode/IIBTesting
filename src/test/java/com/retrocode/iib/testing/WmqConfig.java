package com.retrocode.iib.testing;

import javax.jms.JMSException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;



import org.apache.camel.component.jms.JmsComponent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.ibm.mq.jms.*;
import com.ibm.mq.*;
import com.ibm.msg.client.wmq.*;


@Configuration
@PropertySource("classpath:/wmq.properties")
public class WmqConfig {
    @Autowired
    private Environment env;

    @Bean(name = "wmq")
	public JmsComponent wmq() throws JMSException, Exception {
		return JmsComponent.jmsComponent(createWMQ1());
		//return null;
	}


	/**
	 * To override properties, add beanID.property in the properties file
	 *
	 * @return a configured MQConnectionFactory
	 * @throws JMSException
	 */
	@Bean(name = "qmgr")
	public com.ibm.mq.jms.MQConnectionFactory createWMQ1() throws JMSException {

		MQConnectionFactory mqConnectionFactory = new MQConnectionFactory();
		mqConnectionFactory.setHostName(env.getProperty("qmgr.hostName", "localhost"));
		mqConnectionFactory.setPort(Integer.parseInt(env.getProperty("qmgr.port", "1414")));
		mqConnectionFactory.setQueueManager(env.getProperty("qmgr.queueManager", ""));
		mqConnectionFactory.setChannel(env.getProperty("qmgr.channel", ""));
		mqConnectionFactory.setTransportType(Integer.parseInt(env.getProperty("qmgr.transportType", String.valueOf(WMQConstants.WMQ_CM_BINDINGS_THEN_CLIENT))));

		return mqConnectionFactory;
	}

}
