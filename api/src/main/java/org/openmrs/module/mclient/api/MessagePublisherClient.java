package org.openmrs.module.mclient.api;

import org.openmrs.event.EventListener;
import org.openmrs.module.mclient.api.impl.MessagePublisherClientImpl;
import org.openmrs.notification.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessagePublisherClient implements EventListener {

    @Autowired
    MessagePublisherClientImpl messagePublisherClientImpl;

    @Override
    public void onMessage(Message message){
        messagePublisherClientImpl.publish(message.getContent());
    }
}
