/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baidu.rigel.biplatform.cache.config;

import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.codec.SerializationCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.redis.RedisProperties.Sentinel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.baidu.rigel.biplatform.cache.StoreManager;
import com.baidu.rigel.biplatform.cache.redis.config.RedisPoolProperties;
import com.baidu.rigel.biplatform.cache.redis.listener.RedisQueueListener;
import com.baidu.rigel.biplatform.cache.redis.listener.RedisTopicListener;
import com.baidu.rigel.biplatform.cache.store.service.HazelcastNoticePort;
import com.baidu.rigel.biplatform.cache.store.service.HazelcastQueueItemListener;
import com.baidu.rigel.biplatform.cache.store.service.LocalEventListenerThread;
import com.baidu.rigel.biplatform.cache.store.service.impl.HazelcastStoreManager;
import com.baidu.rigel.biplatform.cache.store.service.impl.RedisStoreManagerImpl;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring Data's Redis support.
 * 
 * @author Dave Syer
 * @author Andy Wilkinson
 * @author Christian Dupuis
 */
@Configuration
@ConditionalOnClass({ Redisson.class})
@EnableConfigurationProperties
public class BiplatformRedisConfiguration {


    @Bean
    @ConditionalOnMissingBean
    RedisPoolProperties redisPoolProperties() {
        return new RedisPoolProperties();
    }

    /**
     * Base class for Redis configurations.
     */
    protected static abstract class AbstractRedisConfiguration {

        @Autowired
        protected RedisPoolProperties properties;

        protected final Config getConfig() {
            Config config = new Config();
            config.setCodec(new SerializationCodec());
            if(properties.getSentinel() != null) {
                return createSentinelServerConfig(config, properties.getSentinel());
            } else {
                config.useSingleServer()
                .setAddress(properties.getHost())
                .setConnectionPoolSize(properties.getPoolConfig().getMaxActive())
                .setDatabase(properties.getDatabase())
                .setPassword(properties.getPassword())
                .setTimeout(properties.getPoolConfig().getTimeout());
                return config;
            }
        }
        
        
        private Config createSentinelServerConfig(Config config, Sentinel sentinel) {
            config.useSentinelConnection()
            .setMasterName(sentinel.getMaster())
            .addSentinelAddress(StringUtils.commaDelimitedListToStringArray(sentinel.getNodes()))
            .setDatabase(properties.getDatabase())
            .setPassword(properties.getPassword())
            .setMasterConnectionPoolSize(properties.getPoolConfig().getMaxActive())
            .setTimeout(properties.getPoolConfig().getTimeout());
            return config;
        }


    }

    /**
     * Redis pooled connection configuration.
     */
    @Configuration
    protected static class RedisPooledConnectionConfiguration extends
            AbstractRedisConfiguration {

        
        @Bean
        @ConditionalOnProperty(prefix = "config.redis", name = "active", havingValue = "true")
        public Redisson redisson(){
            Redisson redisson = Redisson.create(getConfig());
            redisson.getTopic(StoreManager.TOPICS).addListener(new RedisTopicListener());
            return redisson;
        }

        
        @Bean(name="redisStoreManager")
        @ConditionalOnBean(Redisson.class)
        public StoreManager redisStoreManager() {
            return new RedisStoreManagerImpl();
        }
        
        @Bean
        @ConditionalOnBean(Redisson.class)
        public RedisQueueListener redisQueueListener() {
            return new RedisQueueListener();
        }
        
        @Bean(name="hazelcastStoreManager")
        @ConditionalOnMissingBean(name = "redisStoreManager")
        public StoreManager hazelcastStoreManager() {
            return new HazelcastStoreManager();
        }
        
        @Bean
        @ConditionalOnBean(name="hazelcastStoreManager")
        public HazelcastNoticePort hazelcastNoticePort() {
            return new HazelcastNoticePort();
        }
        
        @Bean
        @ConditionalOnBean(name="hazelcastStoreManager")
        public HazelcastQueueItemListener hazelcastQueueItemListener() {
            return new HazelcastQueueItemListener();
        }
        
        @Bean
        @ConditionalOnBean(name="hazelcastStoreManager")
        public LocalEventListenerThread localEventListenerThread() {
            return new LocalEventListenerThread();
        }

//        private JedisConnectionFactory createJedisConnectionFactory() {
//            JedisConnectionFactory factory = null;
//            if (this.properties.getPoolConfig() != null) {
//                factory = new JedisConnectionFactory(getSentinelConfig(), jedisPoolConfig());
//            }
//            factory = new JedisConnectionFactory(getSentinelConfig());
//            factory.setUsePool(this.properties.isUsePool());
//            factory.setPassword(this.properties.getPassword());
//            return factory;
//        }

//        private JedisPoolConfig jedisPoolConfig() {
//            JedisPoolConfig config = new JedisPoolConfig();
//            RedisPoolProperties.Pool props = this.properties.getPoolConfig();
//            config.setMaxTotal(props.getMaxActive());
//            config.setMaxIdle(props.getMaxIdle());
//            config.setMinIdle(props.getMinIdle());
//            config.setMaxWaitMillis(props.getMaxWait());
//            config.setTestOnBorrow(props.isTestOnBorrow());
//            config.setTestOnCreate(props.isTestOnCreate());
//            config.setTestOnReturn(props.isTestOnReturn());
//            config.setTestWhileIdle(props.isTestWhileIdle());
//            config.setTimeBetweenEvictionRunsMillis(props.getTimeBetweenEvictionRunsMillis());
//            config.setMinEvictableIdleTimeMillis(props.getMinEvictableIdleTimeMillis());
//            config.setNumTestsPerEvictionRun(props.getNumTestsPerEvictionRun());
//            config.setSoftMinEvictableIdleTimeMillis(props.getSoftMinEvictableIdleTimeMillis());
//            config.setLifo(props.isLifo());
//            return config;
//        }

    }



}
