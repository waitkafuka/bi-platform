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
package com.baidu.rigel.biplatform.tesseract.meta.impl;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;

import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.cache.StoreManager;
import com.baidu.rigel.biplatform.tesseract.meta.MetaDataService;

/**
 *Description:
 * @author david.wang
 *
 */
public class MetaDataServiceImplTest {
    
    private MetaDataServiceImpl service = new MetaDataServiceImpl ();
    
    @Test
    public void testGetCube() {
        try {
            service.getCube ("");
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
        
        StoreManager storeManager = Mockito.mock (StoreManager.class);
        service.setStoreManager (storeManager);
        Cache cache = Mockito.mock (Cache.class);
        Mockito.doReturn (cache).when (storeManager).getDataStore (MetaDataService.CUBE_CACHE_NAME);
        Mockito.doReturn (null).when (cache).get ("test");
        try {
            service.getCube ("test");
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
        
        Cube cube = Mockito.mock (Cube.class);
        ValueWrapper wrapper = Mockito.mock (ValueWrapper.class);
        Mockito.doReturn (wrapper).when (cache).get ("test");
        Mockito.doReturn (cube).when (wrapper).get ();
        Assert.assertNotNull (service.getCube ("test"));
    }
    
    @Test
    public void testCacheCube() {
        StoreManager storeManager = Mockito.mock (StoreManager.class);
        service.setStoreManager (storeManager);
        Cache cache = Mockito.mock (Cache.class);
        Mockito.doReturn (cache).when (storeManager).getDataStore (MetaDataService.CUBE_CACHE_NAME);
        try {
            service.cacheCube (null);
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
        Cube cube = Mockito.mock (Cube.class);
        service.cacheCube (cube);
    }
}
