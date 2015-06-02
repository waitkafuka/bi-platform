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
package com.baidu.rigel.biplatform.parser.util;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

/**
 *Description:
 * @author david.wang
 *
 */
public class PropertiesUtilTest {
    
    @Test
    public void testToStringWithNull () {
        try {
            PropertiesUtil.toString (null, null);
            Assert.fail ();
        } catch (Exception e) {
            
        }
    }
    
    @Test
    public void testToString () {
        Properties p = System.getProperties ();
        try {
            String rs = PropertiesUtil.toString (p, null);
            Assert.assertNotNull (rs);
            Assert.assertTrue (rs.contains("os.name"));
        } catch (UnsupportedEncodingException e) {
            Assert.fail ();
        }
    }
    
    @Test
    public void testLoadFromFileWithNull () {
        try {
            PropertiesUtil.loadPropertiesFromFile (null);
            Assert.fail ();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testLoadFromFileWithFileEmpty () {
        try {
            PropertiesUtil.loadPropertiesFromFile ("");
            Assert.fail ();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testLoadFromFileWithSpace () {
        try {
            PropertiesUtil.loadPropertiesFromFile (" ");
            Assert.fail ();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testLoadFromFile () {
        try {
            Properties rs = PropertiesUtil.loadPropertiesFromFile ("test.properties");
            Assert.assertNotNull (rs);
            Assert.assertNull (rs.getProperty ("parentheses.pattern"));
            Assert.assertEquals ("b", rs.getProperty ("a"));
        } catch (Exception e) {
            Assert.fail ();
        }
    }
    
    @Test
    public void testLoadFromXmlFile () {
        try {
            PropertiesUtil.loadPropertiesFromFile ("test.xml");
            Assert.fail ();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testLoadFromPathWithNull () {
        try {
            Properties rs = PropertiesUtil.loadPropertiesFromPath (null);
            Assert.assertNotNull (rs.getProperty ("parentheses.pattern"));
        } catch (Exception e) {
            Assert.fail ();
        }
    }
    
    @Test
    public void testLoadFromPathWithEmpty () {
        try {
            Properties rs = PropertiesUtil.loadPropertiesFromPath ("");
            Assert.assertNotNull (rs.getProperty ("parentheses.pattern"));
        } catch (Exception e) {
            Assert.fail ();
        }
    }
    
    @Test
    public void testLoadFromPathWithSpace () {
        try {
            Properties rs = PropertiesUtil.loadPropertiesFromPath (" ");
            Assert.assertNotNull (rs.getProperty ("parentheses.pattern"));
        } catch (Exception e) {
            Assert.fail ();
        }
    }
    
    @Test
    public void testLoadFromPathWithFile () {
        try {
            Properties rs = PropertiesUtil.loadPropertiesFromPath ("test.properties");
            Assert.assertNotNull (rs);
            Assert.assertNull (rs.getProperty ("parentheses.pattern"));
            Assert.assertEquals ("b", rs.getProperty ("a"));
        } catch (Exception e) {
            Assert.fail ();
        }
    }
    
    @Test
    public void testLoadFromPathWithXmlFile () {
        try {
            PropertiesUtil.loadPropertiesFromPath ("test.xml");
            Assert.fail ();
        } catch (Exception e) {
        }
    }
}
