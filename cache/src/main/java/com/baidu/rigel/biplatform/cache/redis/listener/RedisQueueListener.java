
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
package com.baidu.rigel.biplatform.cache.redis.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.redis.RedisProperties.Sentinel;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import com.baidu.rigel.biplatform.cache.redis.config.RedisPoolProperties;

/** 
 *  
 * @author xiaoming.chen
 * @version  2015年2月27日 
 * @since jdk 1.8 or after
 */
public class RedisQueueListener implements ApplicationListener<ContextRefreshedEvent> {
    
    
    /** 
     * log
     */
    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired(required=false)
    private RedisPoolProperties poolProperties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(poolProperties == null) {
            log.warn("no redis properties bean...can not listen redis queue");
        }
        Jedis jedis = null;
        Sentinel sentinel = poolProperties.getSentinel();
        if(sentinel != null) {
            JedisSentinelPool sentinelPool = new JedisSentinelPool(sentinel.getMaster(), StringUtils.commaDelimitedListToSet(sentinel.getNodes()));
            jedis = sentinelPool.getResource();
        } else {
            jedis = new Jedis(poolProperties.getHost(), poolProperties.getPort());
        }
        jedis.auth(poolProperties.getPassword());
        
        jedis.subscribe(new PrintListener(), "channel");
        
    }

}

