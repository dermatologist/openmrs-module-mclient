package org.openmrs.module.mclient.api.impl;

import org.ict4h.atomfeed.server.service.Event;
import org.openmrs.module.atomfeed.api.writers.impl.DefaultFeedWriter;
import org.openmrs.module.mclient.MessagePublisherClient;
import org.springframework.beans.factory.annotation.Autowired;

public class KafkaProducerServiceImpl extends DefaultFeedWriter {

    @Autowired
    MessagePublisherClient messagePublisherClient;

    @Override
    public void saveEvent(Event event){
        String message = event.getContents();
        messagePublisherClient.publish(message);
    }
}
