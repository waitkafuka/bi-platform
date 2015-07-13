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
package com.baidu.rigel.biplatform.tesseract.meta;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.minicube.StandardDimension;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.google.common.collect.Maps;

/**
 * 
 *Description:
 * @author david.wang
 *
 */
public class MetaDataServiceTest {
    
    @Test
    public void testCheckDataSourceInfo() {
        try {
            MetaDataService.checkDataSourceInfo (null);
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
        DataSourceInfo ds = Mockito.mock (DataSourceInfo.class);
        Mockito.doReturn (false).when (ds).validate ();
        try {
            MetaDataService.checkDataSourceInfo (null);
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
    }
   
    @Test
    public void testCheckCube() {
        try {
            MetaDataService.checkCube (null);
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
        MiniCube cube = Mockito.mock (MiniCube.class);
        try {
            MetaDataService.checkCube (cube);
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
        Map<String, Dimension> dims = Maps.newHashMap ();
        dims.put ("t", new StandardDimension ());
        Mockito.doReturn (dims).when (cube).getDimensions ();
        try {
            MetaDataService.checkCube (cube);
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
    }
}
