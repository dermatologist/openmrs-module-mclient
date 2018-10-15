package org.openmrs.module.mclient;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.openmrs.module.mclient.MessagingClientConfig.KAFKA_HOST;
import static org.openmrs.module.mclient.MessagingClientConfig.KAFKA_PORT;
import static org.openmrs.module.mclient.MessagingClientConfig.KAFKA_TOPIC;

public class MessagePublisherClient {
    private static final Logger LOG = LoggerFactory.getLogger(MessagePublisherClient.class);

    CamelContext camelContext = new DefaultCamelContext();

    RouteBuilder routeBuilder = new RouteBuilder() {
        @Override
        public void configure() throws Exception {
            PropertiesComponent pc = getContext().getComponent("properties", PropertiesComponent.class);
            pc.setLocation("classpath:application.properties");

            String host_port = "kafka." + KAFKA_HOST + ":kafka." + KAFKA_PORT;
            String topic = "kafka:" + KAFKA_TOPIC;
            // setup kafka component with the brokers
            KafkaComponent kafka = new KafkaComponent();
            kafka.setBrokers(host_port);
            camelContext.addComponent("kafka", kafka);

            from("direct:kafkaStart").routeId("DirectToKafka")
                    .to(topic).log("${headers}");

//            // Topic can be set in header as well.
//
//            from("direct:kafkaStartNoTopic").routeId("kafkaStartNoTopic")
//                    .to("kafka:dummy")
//                    .log("${headers}");
//
//            // Use custom partitioner based on the key.
//
//            from("direct:kafkaStartWithPartitioner").routeId("kafkaStartWithPartitioner")
//                    .to("kafka:{{producer.topic}}?partitioner={{producer.partitioner}}")
//                    .log("${headers}");
//
//
//            // Takes input from the command line.
//
//            from("stream:in").setHeader(KafkaConstants.PARTITION_KEY, simple("0"))
//                    .setHeader(KafkaConstants.KEY, simple("1")).to("direct:kafkaStart");
        }
    };

    // Constructor
    private MessagePublisherClient() throws Exception {
        this.camelContext.addRoutes(this.routeBuilder);
    }

    public void publish(String message) {

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        try {
            camelContext.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> headers = new HashMap<String, Object>();

        headers.put(KafkaConstants.PARTITION_KEY, 0);
        headers.put(KafkaConstants.KEY, "1");
        producerTemplate.sendBodyAndHeaders("direct:kafkaStart", message, headers);
    }
}
