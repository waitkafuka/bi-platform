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

import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMember;
import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.Level;
import com.baidu.rigel.biplatform.ac.model.LevelType;
import com.google.common.collect.Maps;

/**
 * 
 *Description:
 * @author david.wang
 *
 */
public class TimeDimensionMemberServiceTest {
    
    private TimeDimensionMemberServiceImpl service = new TimeDimensionMemberServiceImpl ();
    
    @Test
    public void testGetMemberFromLevelByNames() {
        Assert.assertNotNull (service.getMemberFromLevelByNames (null, null, null, null, null));
    }
    
    @Test
    public void testGetMemberFromLevelByNameWithNameNull() {
        try {
            service.getMemberFromLevelByName (null, null, null, null, null, null);
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
    }
    
    @Test
    public void testGetMemberFromLevelByNameWithName() {
        try {
            MiniCubeMember m = service.getMemberFromLevelByName (null, null, null, "test", null, null);
            Assert.assertNotNull (m);
            Assert.assertEquals ("test", m.getName ());
        } catch (Exception e) {
            Assert.fail ();
        }
    }
    
    @Test
    public void testGetMemberFromLevelByName() throws Exception {
        Cube cube = Mockito.mock (Cube.class);
        Level level = Mockito.mock (Level.class);
        Dimension dim = Mockito.mock (Dimension.class);
        Mockito.doReturn (dim).when (level).getDimension ();
        Mockito.doReturn ("test").when (dim).getId ();
        MiniCubeMember m = service.getMemberFromLevelByName (null, cube, level, "All_test", null, Maps.newHashMap ());
        Assert.assertNotNull (m);
        Assert.assertNotNull(m.getQueryNodes ());
    }
    
    @Test
    public void testGetMemberFromLevelByName1() throws Exception {
        Cube cube = Mockito.mock (Cube.class);
        Level level = Mockito.mock (Level.class);
        Dimension dim = Mockito.mock (Dimension.class);
        Mockito.doReturn (dim).when (level).getDimension ();
        Mockito.doReturn ("test").when (dim).getId ();
        HashMap<String, String> params = Maps.newHashMap ();
        params.put ("test", "test");
        MiniCubeMember m = service.getMemberFromLevelByName (null, cube, level, "All_test", null, params);
        Assert.assertNotNull (m);
        Assert.assertNotNull(m.getQueryNodes ());
    }
    
    @Test
    public void testGetMemberFromLevelByName2() throws Exception {
        Cube cube = Mockito.mock (Cube.class);
        Level level = Mockito.mock (Level.class);
        Dimension dim = Mockito.mock (Dimension.class);
        Mockito.doReturn (dim).when (level).getDimension ();
        Mockito.doReturn ("test").when (dim).getId ();
        HashMap<String, String> params = Maps.newHashMap ();
        params.put ("test", "[test].[20110101]");
        MiniCubeMember m = service.getMemberFromLevelByName (null, cube, level, "All_test", null, params);
        Assert.assertNotNull (m);
        Assert.assertNotNull(m.getQueryNodes ());
        Assert.assertEquals (1, m.getQueryNodes ().size ());
    }
    
    @Test
    public void testGetMembersWithNullParent() throws Exception {
        Level level = Mockito.mock (Level.class);
        Mockito.doReturn (LevelType.PARENT_CHILD).when (level).getType ();
        try {
            service.getMembers (null, level, null, null, null);
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
        
        Mockito.doReturn (LevelType.TIME_DAYS).when (level).getType ();
        List<MiniCubeMember> rs = service.getMembers (null, level, null, null, null);
        Assert.assertEquals (rs.size (), 1);
        
        Mockito.doReturn (LevelType.TIME_WEEKS).when (level).getType ();
        rs = service.getMembers (null, level, null, null, null);
        Assert.assertEquals (rs.size (), 1);
        
        Mockito.doReturn (LevelType.TIME_MONTHS).when (level).getType ();
        rs = service.getMembers (null, level, null, null, null);
        Assert.assertEquals (rs.size (), 12);
        
        Mockito.doReturn (LevelType.TIME_QUARTERS).when (level).getType ();
        rs = service.getMembers (null, level, null, null, null);
        Assert.assertEquals (rs.size (), 4);
        
        Mockito.doReturn (LevelType.TIME_YEARS).when (level).getType ();
        rs = service.getMembers (null, level, null, null, null);
        Assert.assertEquals (rs.size (), 1);
    }
    
    @Test
    public void testGetMembersWithParent() throws Exception {
        Level level = Mockito.mock (Level.class);
        Mockito.doReturn (LevelType.PARENT_CHILD).when (level).getType ();
        MiniCubeMember parent = Mockito.mock (MiniCubeMember.class);
        Level parentLevel = Mockito.mock (Level.class);
        Mockito.doReturn (parentLevel).when (parent).getLevel ();
        Mockito.doReturn (LevelType.PARENT_CHILD).when (parentLevel).getType ();
        
        try {
            service.getMembers (null, level, null, parent, null);
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
        
        testGetMembersWithYearParent (level, parent, parentLevel);
        
        testGetMembersWithQueaterParent (level, parent, parentLevel);
    }

    private void testGetMembersWithQueaterParent(Level level, MiniCubeMember parent, Level parentLevel) {
        Mockito.doReturn (LevelType.TIME_QUARTERS).when (parentLevel).getType ();
        Mockito.doReturn ("2011").when (parent).getCaption ();
        Mockito.doReturn (true).when (parent).isAll ();
        List<MiniCubeMember> rs = null;
        try {
            rs = service.getMembers (null, level, null, parent, null);
            Assert.assertEquals (1, rs.size ());
        } catch (Exception e) {
            Assert.fail ();
        }
        Mockito.doReturn (false).when (parent).isAll ();
        Mockito.doReturn ("20110101").when (parent).getName ();
        
        try {
            Mockito.doReturn (LevelType.TIME_MONTHS).when (level).getType ();
            rs = service.getMembers (null, level, null, parent, null);
            Assert.assertEquals (3, rs.size ());
        } catch (Exception e) {
            Assert.fail ();
        }
        
        try {
            Mockito.doReturn (LevelType.TIME_DAYS).when (level).getType ();
            rs = service.getMembers (null, level, null, parent, null);
            Assert.assertEquals (90, rs.size ());
        } catch (Exception e) {
            Assert.fail ();
        }
        Mockito.doReturn (parentLevel).when (parent).getLevel ();
        Mockito.doReturn ("2011").when (parent).getCaption ();
        Dimension dim = Mockito.mock (Dimension.class);
        Mockito.doReturn (dim).when (level).getDimension ();
        Mockito.doReturn ("20110101").when (dim).getName ();
        try {
            Mockito.doReturn (LevelType.TIME_YEARS).when (level).getType ();
            rs = service.getMembers (null, level, null, parent, null);
            Assert.assertEquals (14, rs.size ());
        } catch (Exception e) {
            Assert.fail ();
        }
        
        try {
            Mockito.doReturn (LevelType.TIME_QUARTERS).when (level).getType ();
            rs = service.getMembers (null, level, null, parent, null);
            Assert.assertEquals (14, rs.size ());
        } catch (Exception e) {
            Assert.fail ();
        }
        
        try {
            Mockito.doReturn (LevelType.TIME_WEEKS).when (level).getType ();
            rs = service.getMembers (null, level, null, parent, null);
            Assert.assertEquals (14, rs.size ());
        } catch (Exception e) {
            Assert.fail ();
        }
        
        try {
            Mockito.doReturn (LevelType.CALL_BACK).when (level).getType ();
            service.getMembers (null, level, null, parent, null);
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
    }

    private void testGetMembersWithYearParent(Level level, MiniCubeMember parent, Level parentLevel) {
        Mockito.doReturn (LevelType.TIME_YEARS).when (parentLevel).getType ();
        Mockito.doReturn ("2011").when (parent).getCaption ();
        try {
            Mockito.doReturn (LevelType.TIME_YEARS).when (level).getType ();
            service.getMembers (null, level, null, parent, null);
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
        try {
            Mockito.doReturn (LevelType.TIME_WEEKS).when (level).getType ();
            service.getMembers (null, level, null, parent, null);
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
        List<MiniCubeMember> rs = null;
        try {
            Mockito.doReturn (LevelType.TIME_QUARTERS).when (level).getType ();
            rs = service.getMembers (null, level, null, parent, null);
            Assert.assertEquals (4, rs.size ());
        } catch (Exception e) {
            Assert.fail ();
        }
        
        try {
            Mockito.doReturn (LevelType.TIME_MONTHS).when (level).getType ();
            rs = service.getMembers (null, level, null, parent, null);
            Assert.assertEquals (12, rs.size ());
        } catch (Exception e) {
            Assert.fail ();
        }
        
        try {
            Mockito.doReturn (LevelType.TIME_DAYS).when (level).getType ();
            rs = service.getMembers (null, level, null, parent, null);
            Assert.assertEquals (365, rs.size ());
        } catch (Exception e) {
            Assert.fail ();
        }
    }
}
