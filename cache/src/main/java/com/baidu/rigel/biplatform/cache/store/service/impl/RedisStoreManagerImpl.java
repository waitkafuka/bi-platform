
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.baidu.rigel.biplatform.cache.StoreManager;

/** 
 *  
 * @author xiaoming.chen
 * @version  2015年2月9日 
 * @since jdk 1.8 or after
 */
public class RedisStoreManagerImpl implements StoreManager {

    @Autowired(required=false)
    private CacheManager cacheManager;
    /*
     * (non-Javadoc) 
     * @see com.baidu.rigel.biplatform.cache.StoreManager#getDataStore(java.lang.String) 
     */
    @Override
    public Cache getDataStore(String name) {
        // TODO Auto-generated method stub
        return null;

    }

    /*
     * (non-Javadoc) 
     * @see com.baidu.rigel.biplatform.cache.StoreManager#putEvent(java.util.EventObject) 
     */
    @Override
    public void putEvent(EventObject event) throws Exception {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc) 
     * @see com.baidu.rigel.biplatform.cache.StoreManager#getNextEvent() 
     */
    @Override
    public EventObject getNextEvent() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc) 
     * @see com.baidu.rigel.biplatform.cache.StoreManager#postEvent(java.util.EventObject) 
     */
    @Override
    public void postEvent(EventObject event) throws Exception {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc) 
     * @see com.baidu.rigel.biplatform.cache.StoreManager#getClusterLock() 
     */
    @Override
    public Lock getClusterLock() {
        // TODO Auto-generated method stub
        return null;

    }

}

