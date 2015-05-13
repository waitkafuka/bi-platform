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


import org.junit.Assert;
import org.junit.Test;

/**
 *Description:
 * @author david.wang
 *
 */
public class ThreadLocalPlaceholderTest {
    
    @Test
    public void testBindProperty () {
        try {
            ThreadLocalPlaceholder.bindProperty (null, null);
        } catch (Exception e) {
        }
        
        try {
            ThreadLocalPlaceholder.bindProperty ("test", "test");
            Assert.assertEquals ("test", ThreadLocalPlaceholder.getProperty ("test"));
            ThreadLocalPlaceholder.bindProperty ("test", "test");
            Assert.fail ();
        } catch (Exception e) {
        } finally {
            ThreadLocalPlaceholder.unbindProperty ("test");
        }
        
    }
    
    
    @Test
    public void testUnbindProperty () {
        try {
            ThreadLocalPlaceholder.unbindProperty (null);
        } catch (Exception e) {
        }
        
        try {
            ThreadLocalPlaceholder.bindProperty ("test", "test");
            Assert.assertEquals ("test", ThreadLocalPlaceholder.getProperty ("test"));
            ThreadLocalPlaceholder.unbindProperty ("test");
            ThreadLocalPlaceholder.bindProperty ("test", "test");
        } catch (Exception e) {
            Assert.fail ();
        } finally {
            ThreadLocalPlaceholder.unbindProperty ("test");
        }
        
    }
    
    @Test
    public void testGetProperty () {
        try {
            ThreadLocalPlaceholder.getProperty (null);
        } catch (Exception e) {
        }
        
        try {
            ThreadLocalPlaceholder.bindProperty ("test", "test");
            Assert.assertEquals ("test", ThreadLocalPlaceholder.getProperty ("test"));
        } catch (Exception e) {
            Assert.fail ();
        } finally {
            ThreadLocalPlaceholder.unbindProperty ("test");
        }
        
    }
}
