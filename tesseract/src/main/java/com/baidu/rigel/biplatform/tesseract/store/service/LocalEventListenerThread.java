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

import java.util.EventObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;

import com.baidu.rigel.biplatform.tesseract.util.isservice.LogInfoConstants;

/**
 * LocalEventListenerThread 本地监听事件线程
 * 
 * @author lijin
 *
 */
@Service("localEventListenerThread")
public class LocalEventListenerThread implements Runnable, ApplicationContextAware {
    /**
     * LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalEventListenerThread.class);
    
    private ApplicationContext context;
    
    /**
     * storeManager
     */
    @Resource
    private StoreManager storeManager;
    
    
    /**
     * Constructor by no param
     */
    public LocalEventListenerThread() {
        super();

    }
    
    public void start(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> run());
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        // TODO Auto-generated method stub
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_BEGIN, "run","no param"));
        while (true) {
            try {
                EventObject item = this.storeManager.getNextEvent();
                context.publishEvent((ApplicationEvent) item);
                LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_PUBLISH_EVENT_SUCC, "run", item));
                
            } catch (Exception e) {
                LOGGER.error(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_EXCEPTION, "run","no param"), e);
                //LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_PUBLISH_EVENT_FAIL, "run"));
                
            }
            
        }
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
