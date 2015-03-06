/**
 * Copyright (c) 2014 Baidu, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package com.baidu.rigel.biplatform.tesseract.store.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;

import com.baidu.rigel.biplatform.tesseract.isservice.event.IndexMetaWriteImageEvent;
import com.baidu.rigel.biplatform.tesseract.isservice.event.IndexUpdateEvent;
import com.baidu.rigel.biplatform.tesseract.util.isservice.LogInfoConstants;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * HazelcastNoticePort
 * 
 * @author lijin
 *
 */
@Service
public class HazelcastNoticePort implements ApplicationContextAware, MessageListener<Object> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastNoticePort.class);
    
    private ApplicationContext context;
    
    @Override
    public void onMessage(Message<Object> message) {
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_BEGIN, "onMessage",
            message));
        if (context == null || message == null) {
            LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_EXCEPTION,
                "onMessage", message));
            return;
        }
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_GET_MULTIBROAD_EVENT_SUCC,
            "onMessage", message.getMessageObject()));
        ApplicationEvent event = null;
        
//        if (message.getMessageObject() instanceof IndexUpdateEvent) {
//            event = (IndexUpdateEvent) message.getMessageObject();
//        }else if(message.getMessageObject() instanceof IndexMetaWriteImageEvent){
//        	event = (IndexMetaWriteImageEvent) message.getMessageObject();
//        }
        event=(ApplicationEvent)message.getMessageObject();
        context.publishEvent(event);
        
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_PUBLISH_LOCALEVENT_SUCC,
            "onMessage", event));
        LOGGER
            .info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_END, "onMessage", message));
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.context.ApplicationContextAware#setApplicationContext
     * (org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        
    }
    
}
