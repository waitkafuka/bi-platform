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

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEvent;

import com.baidu.rigel.biplatform.cache.redis.config.HazelcastProperties;
import com.baidu.rigel.biplatform.cache.redis.config.HazelcastProperties.ManCenter;

/**
 *Description:
 * @author david.wang
 *
 */
public class HazelcastStoreManagerTest {
  
    static HazelcastStoreManager m = getManager ();
    
    @Test
    public void testGetClusterLock () {
        Assert.assertNotNull (m.getClusterLock (""));
        Assert.assertNotNull (m.getClusterLock ());
    }

    @Test
    public void testPutEvent () {
        try {
            m.putEvent (null);
            Assert.fail ();
        } catch (Exception e) {
            
        }
        
        try {
            ApplicationEvent e = Mockito.mock (ApplicationEvent.class);
            m.putEvent (e);
        } catch (Exception e) {
        }
    }
    
    private static HazelcastStoreManager getManager() {
        HazelcastProperties hazelcastProperties = Mockito.mock (HazelcastProperties.class);
        Mockito.doReturn ("test").when (hazelcastProperties).getGroupPassword ();
        Mockito.doReturn ("test").when (hazelcastProperties).getGroupUserName ();
        Mockito.doReturn ("test").when (hazelcastProperties).getInstanceName ();
        ManCenter mainCenter = Mockito.mock (ManCenter.class);
        Mockito.doReturn (mainCenter).when (hazelcastProperties).getMancenter ();
        Mockito.doReturn ("127.0.0.1").when (hazelcastProperties).getMembers ();
        Mockito.doReturn (true).when (mainCenter).isEnable ();
        Mockito.doReturn ("localhost:8090").when (mainCenter).getUrl ();
        HazelcastStoreManager m = new HazelcastStoreManager (hazelcastProperties);
        return m;
    }
    
    @Test
    public void testPostEvent () {
        try {
            m.postEvent (null);
            Assert.fail ();
        } catch (Exception e) {
            
        }
        
        try {
            ApplicationEvent e = Mockito.mock (ApplicationEvent.class);
            m.postEvent (e);
        } catch (Exception e) {
        }
    }
}
