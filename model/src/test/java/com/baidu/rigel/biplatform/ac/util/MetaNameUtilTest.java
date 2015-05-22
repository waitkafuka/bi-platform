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

import com.baidu.rigel.biplatform.ac.minicube.MiniCubeLevel;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMember;
import com.baidu.rigel.biplatform.ac.minicube.StandardDimension;
import com.baidu.rigel.biplatform.ac.model.Dimension;

/**
 *Description:
 * @author david.wang
 *
 */
public class MetaNameUtilTest {
    
    @Test
    public void testIsAllMemberName () {
        Assert.assertFalse (MetaNameUtil.isAllMemberName (""));
        Assert.assertFalse (MetaNameUtil.isAllMemberName ("test"));
        Assert.assertTrue (MetaNameUtil.isAllMemberName ("All_test"));
        Assert.assertFalse (MetaNameUtil.isAllMemberName ("test_All_test"));
    }
    
    @Test
    public void testIsAllMemberUniqueName () {
        Assert.assertFalse (MetaNameUtil.isAllMemberUniqueName (""));
        Assert.assertFalse (MetaNameUtil.isAllMemberUniqueName ("test"));
        Assert.assertFalse (MetaNameUtil.isAllMemberUniqueName ("[Measure].[test]"));
        Assert.assertFalse (MetaNameUtil.isAllMemberUniqueName ("[Dim].[test].[test]"));
        Assert.assertTrue (MetaNameUtil.isAllMemberUniqueName ("[Dim].[All_tests]"));
    }
    
    @Test
    public void testGetNameFromMetaName () {
        String rs = MetaNameUtil.getNameFromMetaName ("test");
        Assert.assertEquals ("test", rs);
        rs = MetaNameUtil.getNameFromMetaName ("[Dim].[test]");
        Assert.assertEquals ("test", rs);
    }
    
    @Test
    public void testParseUnique2NameArray () {
        try {
            MetaNameUtil.parseUnique2NameArray ("test");
            Assert.fail ();
        } catch (Exception e) {
        }
        String[] rs = MetaNameUtil.parseUnique2NameArray ("[Dim].[test]");
        Assert.assertEquals (2, rs.length);
        Assert.assertEquals ("Dim", rs[0]);
    }
    
    @Test
    public void testMakeUniqueName () {
        try {
            MetaNameUtil.makeUniqueName (null);
            Assert.fail ();
        } catch (Exception e) {
            
        }
        
        try {
            MetaNameUtil.makeUniqueName ("");
            Assert.fail ();
        } catch (Exception e) {
            
        }
        
        String rs = MetaNameUtil.makeUniqueName ("test");
        Assert.assertEquals ("[test]", rs);
    }
    
    @Test
    public void testIsUniqueName () {
        Assert.assertFalse (MetaNameUtil.isUniqueName (""));
        Assert.assertFalse (MetaNameUtil.isUniqueName ("test"));
        Assert.assertTrue (MetaNameUtil.isUniqueName ("[dim].[test]"));
    }
    
    @Test
    public void testGenMeasureUniqueName () {
        String rs = MetaNameUtil.generateMeasureUniqueName ("test");
        Assert.assertEquals ("[Measure].[test]", rs);
    }
    
    @Test
    public void testMakeUniqueNameWithElement () {
        String rs = MetaNameUtil.makeUniqueName (null, "test");
        Assert.assertEquals ("[test]", rs);
        
        MiniCubeMember m = new MiniCubeMember ("All_tests");
        MiniCubeLevel level = new MiniCubeLevel ("dim");
        Dimension dim = new StandardDimension ("dim");
        level.setDimension (dim);
        m.setLevel (level);
        rs = MetaNameUtil.makeUniqueName (m, "test");
        Assert.assertEquals ("[dim].[test]", rs);
        
        m = new MiniCubeMember ("test");
        m.setLevel (level);
        Assert.assertEquals ("[dim].[test]", rs);
    }
    
    @Test
    public void testGetDimNameFromUniqName () {
        String rs = MetaNameUtil.getDimNameFromUniqueName ("[Dim].[Test]");
        Assert.assertEquals ("Dim", rs);
    }
    
    @Test
    public void testGetParentUniqName () {
        try {
            MetaNameUtil.getParentUniqueName ("");
            Assert.fail ();
        } catch (Exception e) {
            
        }
        String rs = MetaNameUtil.getParentUniqueName ("[Dim]");
        Assert.assertNull (rs);
        
        rs = MetaNameUtil.getParentUniqueName ("[Dim].[test]");
        Assert.assertEquals ("[Dim].[All_Dims]", rs);
        
        
        rs = MetaNameUtil.getParentUniqueName ("[Dim].[test].[1]");
        Assert.assertEquals ("[Dim].[test]", rs);
    }
}
