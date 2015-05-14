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
package com.baidu.rigel.biplatform.ac.util;


import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 *Description:
 * @author david.wang
 *
 */
public class PlaceHolderUtilsTest {
    
    @Test
    public void testGetKeyFromPlaceHolderWithNull () {
        try {
            PlaceHolderUtils.getKeyFromPlaceHolder (null);
            Assert.fail ();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testGetKeyFromPlactHolder () {
        String rs = PlaceHolderUtils.getKeyFromPlaceHolder ("abc");
        Assert.assertEquals ("abc", rs);
        rs = PlaceHolderUtils.getKeyFromPlaceHolder ("${abc}");
        Assert.assertEquals ("abc", rs);
    }
    
    @Test
    public void testGetPlaceHolderKeys () {
        Set<String> rs = PlaceHolderUtils.getPlaceHolderKeys ("abc + ${def}");
        Assert.assertEquals (1, rs.size ());
        
        rs = PlaceHolderUtils.getPlaceHolderKeys ("${abc} + ${def}");
        Assert.assertEquals (2, rs.size ());
        
        try {
            PlaceHolderUtils.getPlaceHolderKeys (null);
            Assert.fail ();
        } catch (Exception e) {
            
        }
    }
    
    @Test
    public void testGetPlaceHolders () {
        try {
            PlaceHolderUtils.getPlaceHolders (null);
            Assert.fail ();
        } catch (Exception e) {
        }
        
        List<String> rs = PlaceHolderUtils.getPlaceHolders ("abc + ${def}");
        Assert.assertEquals (1, rs.size ());
        
        rs = PlaceHolderUtils.getPlaceHolders ("abc + def");
        Assert.assertEquals (0, rs.size ());
    }
    
    @Test
    public void testReplacePlaceHolderWithValue () {
        try {
            PlaceHolderUtils.replacePlaceHolderWithValue (null, null, null);
            Assert.fail ();
        } catch (Exception e) {
        }
        try {
            PlaceHolderUtils.replacePlaceHolderWithValue ("test", null, null);
            Assert.fail ();
        } catch (Exception e) {
        }
        String rs = PlaceHolderUtils.replacePlaceHolderWithValue ("${test}", "test", "abc");
        Assert.assertEquals (rs, "abc");
    }
}
