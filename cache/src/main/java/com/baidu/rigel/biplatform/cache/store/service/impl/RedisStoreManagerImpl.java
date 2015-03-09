
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
package com.baidu.rigel.biplatform.cache.store.service.impl;

import java.util.EventObject;
import java.util.concurrent.locks.Lock;

import org.redisson.Redisson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;

import com.baidu.rigel.biplatform.cache.RedissonCache;
import com.baidu.rigel.biplatform.cache.StoreManager;
import com.baidu.rigel.biplatform.cache.redis.config.RedisPoolProperties;
import com.baidu.rigel.biplatform.cache.util.MacAddressUtil;

/** 
 *  
 * @author xiaoming.chen
 * @version  2015年2月9日 
 * @since jdk 1.8 or after
 */
public class RedisStoreManagerImpl implements StoreManager, InitializingBean {
    
    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    private static final String REDIS_LOCK = "RedisLock";
    
    
    @Autowired(required = false)
    private Redisson redisson;
    
    @Autowired(required=false)
    private RedisPoolProperties redisProperties;
    
    private String topicKey = TOPICS;
    
    private String queueKey = EVENT_QUEUE;
    
    private String lockKey = REDIS_LOCK;
    
    

    /*
     * (non-Javadoc) 
     * @see com.baidu.rigel.biplatform.cache.StoreManager#getDataStore(java.lang.String) 
     */
    @Override
    public Cache getDataStore(String name) {
        
        return new RedissonCache(redisson.getMap(name), name);

    }

    /*
     * (non-Javadoc) 
     * @see com.baidu.rigel.biplatform.cache.StoreManager#putEvent(java.util.EventObject) 
     */
    @Override
    public void putEvent(EventObject event) throws Exception {
        redisson.getQueue(queueKey).add(event);
    }

    /*
     * (non-Javadoc) 
     * @see com.baidu.rigel.biplatform.cache.StoreManager#getNextEvent() 
     */
    @Override
    public EventObject getNextEvent() throws Exception {
        Object obj = redisson.getQueue(queueKey).poll();
        return obj == null ? null : (EventObject)obj;
    }

    /*
     * (non-Javadoc) 
     * @see com.baidu.rigel.biplatform.cache.StoreManager#postEvent(java.util.EventObject) 
     */
    @Override
    public void postEvent(EventObject event) throws Exception {
        redisson.getTopic(topicKey).publish(event);
    }

    /*
     * (non-Javadoc) 
     * @see com.baidu.rigel.biplatform.cache.StoreManager#getClusterLock() 
     */
    @Override
    public Lock getClusterLock() {
        return redisson.getLock(lockKey);

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (redisProperties.isDev()) {
            String currentMac = MacAddressUtil.getMacAddress(null);
            log.info("this instance is run with dev mode,current mac :{}", currentMac);
            topicKey = currentMac + "_" + topicKey;
            queueKey = currentMac + "_" + queueKey;
            lockKey = currentMac + "_" + lockKey;
        }
    }

}

