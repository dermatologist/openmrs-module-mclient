package org.openmrs.module.mclient.api.db;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.api.FeedConfigurationService;
import org.openmrs.module.atomfeed.api.db.EventManager;
import org.openmrs.module.atomfeed.api.exceptions.AtomfeedException;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.atomfeed.api.writers.FeedWriter;
import org.springframework.beans.factory.annotation.Autowired;


public class MessageEventManager extends EventManager {

    @Autowired
    FeedConfigurationService feedConfigurationService;

    private FeedWriter getFeedWriter(FeedConfiguration feedConfiguration) {
        String feedWriterBeanId;
        if (StringUtils.isBlank(feedConfiguration.getFeedWriter())) {
            feedWriterBeanId = "mclient.KafkaProducerServiceImpl";
        } else {
            feedWriterBeanId = feedConfiguration.getFeedWriter();
        }

        try {
            return Context.getRegisteredComponent(feedWriterBeanId, FeedWriter.class);
        } catch (Exception e) {
            throw new AtomfeedException("`" + feedWriterBeanId + "` bean for FeedWriter has not been found", e);
        }
    }
}
