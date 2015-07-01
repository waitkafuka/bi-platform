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
import java.util.Set;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.util.Assert;

import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeLevel;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMeasure;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMember;
import com.baidu.rigel.biplatform.ac.minicube.StandardDimension;
import com.baidu.rigel.biplatform.ac.minicube.TimeDimension;
import com.baidu.rigel.biplatform.ac.model.LevelType;
import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo;
import com.baidu.rigel.biplatform.tesseract.isservice.search.service.SearchService;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.QueryContextBuilder;
import com.baidu.rigel.biplatform.tesseract.resultset.isservice.Meta;
import com.baidu.rigel.biplatform.tesseract.resultset.isservice.SearchIndexResultRecord;
import com.baidu.rigel.biplatform.tesseract.resultset.isservice.SearchIndexResultSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 *Description:
 * @author david.wang
 *
 */
public class SqlDimensionMemberServiceTest {
    
    @InjectMocks
    private SearchService searchService;
    
    @Test
    public void testGetMembersWithNull() {
        SqlDimensionMemberServiceImpl service = new SqlDimensionMemberServiceImpl();
        try {
            service.getMembers (null, null, null, null, null);
        } catch (Exception e) {
            Assert.notNull (e);
        }
        
        try {
            service.getMembers (new MiniCube (), null, null, null, null);
        } catch (Exception e) {
            Assert.notNull (e);
        }
        
        try {
            service.getMembers (new MiniCube (), new MiniCubeLevel (), null, null, null);
        } catch (Exception e) {
            Assert.notNull (e);
        }
        
        try {
            DataSourceInfo info = Mockito.mock (DataSourceInfo.class);
            Mockito.doReturn (false).when (info).validate ();
            service.getMembers (new MiniCube (), new MiniCubeLevel (), info, null, null);
        } catch (Exception e) {
            Assert.notNull (e);
        }
    }
    
    @Test
    public void testGetMembers() throws Exception {
        MiniCube cube = new MiniCube();
        cube.setSource ("fact");
        StandardDimension dim = new StandardDimension ("test");
        dim.setTableName ("dim_");
        dim.setCaption ("test");
        dim.setFacttableColumn ("test");
        dim.setId ("test");
        dim.setPrimaryKey ("id");
        MiniCubeLevel level = new MiniCubeLevel ("abc");
        level.setDimension (dim);
        level.setFactTableColumn ("test");
        level.setSource ("test");
        level.setCaptionColumn ("test_caption");
        level.setSource ("test_source");
        level.setPrimaryKey ("dim_id");
        dim.getLevels ().put (level.getName (), level);
        cube.getDimensions ().put (dim.getName (), dim);
        DataSourceInfo info = Mockito.mock (DataSourceInfo.class);
        Mockito.doReturn (true).when (info).validate ();
        SqlDimensionMemberServiceImpl service = new SqlDimensionMemberServiceImpl();
        SearchService search = Mockito.mock (SearchService.class);
        service.setSearchService (search);
        SearchIndexResultSet resultSet = Mockito.mock (SearchIndexResultSet.class);
        Mockito.doReturn (resultSet).when (search).query (service.buildQueryRequest (cube, level, null, info));
        HashMap<String, String> params = Maps.newHashMap ();
        params.put (QueryContextBuilder.FILTER_DIM_KEY, "a,b,c,");
        params.put ("b", "e,f,g");
        params.put ("c", "k,m");
        Mockito.doReturn (resultSet).when (search).query (Mockito.any ());
        List<MiniCubeMember> rs = service.getMembers (cube, level, info, null, params);
        Assert.notNull (rs);
        
        MiniCubeMember p = new MiniCubeMember ("parent");
        p.setName ("[p].[12345]");
        MiniCubeLevel callbackLevel = new MiniCubeLevel ();
        callbackLevel.setPrimaryKey ("p");
        callbackLevel.setDimTable ("dim_");
        callbackLevel.setType (LevelType.CALL_BACK);
        p.setLevel (callbackLevel);
        Set<String> queryNodes = Sets.newHashSet ();
        queryNodes.add ("tmp");
        p.setQueryNodes (queryNodes);
        level.setDimTable ("dim_");
        rs = service.getMembers (cube, level, info, p, params);
        Assert.notNull (rs);
        level.setDimTable ("fact");
        SearchIndexResultRecord record = Mockito.mock (SearchIndexResultRecord.class);
        Meta meta = Mockito.mock (Meta.class);
        Mockito.doReturn (meta).when (resultSet).getMeta ();
        
        Mockito.doReturn (100).when (meta).getFieldIndex (level.getSource ());
        Mockito.doReturn (Lists.newArrayList (record)).when (resultSet).getDataList ();
        rs = service.getMembers (cube, level, info, p, params);
        Assert.notNull (rs);
        
        StandardDimension dim1 = new StandardDimension ("test");
        dim1.setTableName ("dim_");
        dim1.setCaption ("test");
        dim1.setFacttableColumn ("test");
        dim1.setId ("test1");
        dim1.setPrimaryKey ("id");
        MiniCubeLevel level1 = new MiniCubeLevel ("abc");
        level1.setDimension (dim1);
        level1.setDimTable ("fact");
        level1.setFactTableColumn ("test");
        level1.setSource ("test");
        level1.setCaptionColumn ("test_caption");
        level1.setSource ("test_source");
        level1.setPrimaryKey ("dim_id");
        dim1.getLevels ().put (level1.getName (), level1);
        cube.getDimensions ().put (dim1.getName (), dim1);
        params.put ("test1", "[test].[test1]");
        rs = service.getMembers (cube, level, info, p, params);
        Assert.notNull (rs);
        TimeDimension dim2 = new TimeDimension ("test");
        dim2.setTableName ("dim_");
        dim2.setCaption ("test");
        dim2.setFacttableColumn ("test");
        dim2.setId ("test2");
        dim2.setPrimaryKey ("id");
        MiniCubeLevel level2 = new MiniCubeLevel ("abc");
        level2.setDimension (dim1);
        level2.setDimTable ("fact");
        level2.setFactTableColumn ("test");
        level2.setSource ("test");
        level2.setCaptionColumn ("test_caption");
        level2.setSource ("test_source");
        level2.setPrimaryKey ("dim_id");
        dim2.getLevels ().put (level2.getName (), level2);
        cube.getDimensions ().put (dim2.getName (), dim2);
        params.put ("test2", "{start:20150501, end:20150507}");
        rs = service.getMembers (cube, level, info, p, params);
        Assert.notNull (rs);

        TimeDimension dim3 = new TimeDimension ("test");
        dim3.setTableName ("dim_");
        dim3.setCaption ("test");
        dim3.setFacttableColumn ("test");
        dim3.setId ("test3");
        dim3.setPrimaryKey ("id");
        MiniCubeLevel level3 = new MiniCubeLevel ("abc");
        level3.setDimension (dim1);
        level3.setDimTable ("fact");
        level3.setFactTableColumn ("test");
        level3.setSource ("test");
        level3.setCaptionColumn ("test_caption");
        level3.setSource ("test_source");
        level3.setPrimaryKey ("dim_id");
        dim3.getLevels ().put (level3.getName (), level3);
        cube.getDimensions ().put (dim3.getName (), dim3);
        params.put ("test3", "[test].[20150501]");
        rs = service.getMembers (cube, level, info, p, params);
        Assert.notNull (rs);
    }
    
    @Test
    public void testGetMemberFromLevleByNameWithNull () {
        try {
            SqlDimensionMemberServiceImpl service = new SqlDimensionMemberServiceImpl();
            service.getMemberFromLevelByName (null, null, null, null, null, null);
        } catch (Exception e) {
            Assert.notNull (e);
        }
        
        MiniCubeLevel level = new MiniCubeLevel ("abc");
        try {
            SqlDimensionMemberServiceImpl service = new SqlDimensionMemberServiceImpl();
            service.getMemberFromLevelByName (null, null, level, "test", null, null);
        } catch (Exception e) {
            Assert.notNull (e);
        }
        
        MiniCube cube = new MiniCube ("test");
        StandardDimension dim = new StandardDimension ("dim");
        try {
            SqlDimensionMemberServiceImpl service = new SqlDimensionMemberServiceImpl();
            service.getMemberFromLevelByName (null, cube, level, "test", null, null);
        } catch (Exception e) {
            Assert.notNull (e);
        }
        
        cube.getDimensions ().put (dim.getId (), dim);
        try {
            SqlDimensionMemberServiceImpl service = new SqlDimensionMemberServiceImpl();
            service.getMemberFromLevelByName (null, cube, level, "test", null, null);
        } catch (Exception e) {
            Assert.notNull (e);
        }
        
        MiniCubeMeasure m = new MiniCubeMeasure ("test");
        cube.getMeasures ().put (m.getId (), m);
        try {
            SqlDimensionMemberServiceImpl service = new SqlDimensionMemberServiceImpl();
            service.getMemberFromLevelByName (null, cube, level, "test", null, null);
        } catch (Exception e) {
            Assert.notNull (e);
        }
        
        SqlDataSourceInfo info = new SqlDataSourceInfo("tmp");
        try {
            SqlDimensionMemberServiceImpl service = new SqlDimensionMemberServiceImpl();
            service.getMemberFromLevelByName (info, cube, level, "test", null, null);
        } catch (Exception e) {
            Assert.notNull (e);
        }
    }
    
    @Test
    public void testGetMemberFromLevelByName() throws Exception {
        MiniCube cube = new MiniCube();
        cube.setSource ("fact");
        StandardDimension dim = new StandardDimension ("test");
        dim.setTableName ("dim_");
        dim.setCaption ("test");
        dim.setFacttableColumn ("test");
        dim.setId ("test");
        dim.setPrimaryKey ("id");
        MiniCubeLevel level = new MiniCubeLevel ("abc");
        level.setDimension (dim);
        level.setFactTableColumn ("test");
        level.setSource ("test");
        level.setCaptionColumn ("test_caption");
        level.setSource ("test_source");
        level.setPrimaryKey ("dim_id");
        dim.getLevels ().put (level.getName (), level);
        cube.getDimensions ().put (dim.getName (), dim);
        MiniCubeMeasure m = new MiniCubeMeasure ("test");
        m.setId ("test_m");
        cube.getMeasures ().put (m.getId (), m);
        DataSourceInfo info = Mockito.mock (DataSourceInfo.class);
        Mockito.doReturn (true).when (info).validate ();
        SqlDimensionMemberServiceImpl service = new SqlDimensionMemberServiceImpl();
        SearchService search = Mockito.mock (SearchService.class);
        service.setSearchService (search);
        SearchIndexResultSet resultSet = Mockito.mock (SearchIndexResultSet.class);
        Mockito.doReturn (resultSet).when (search).query (service.buildQueryRequest (cube, level, null, info));
        HashMap<String, String> params = Maps.newHashMap ();
        params.put (QueryContextBuilder.FILTER_DIM_KEY, "a,b,c,");
        params.put ("b", "e,f,g");
        params.put ("c", "k,m");
        Mockito.doReturn (resultSet).when (search).query (Mockito.any ());
        MiniCubeMember rs = service.getMemberFromLevelByName (info, cube, level, "[abc].[test]", null, params);
        Assert.notNull (rs);
        
        MiniCubeMember p = new MiniCubeMember ("parent");
        p.setName ("[p].[12345]");
        MiniCubeLevel callbackLevel = new MiniCubeLevel ();
        callbackLevel.setPrimaryKey ("p");
        callbackLevel.setDimTable ("dim_");
        callbackLevel.setType (LevelType.CALL_BACK);
        p.setLevel (callbackLevel);
        Set<String> queryNodes = Sets.newHashSet ();
        queryNodes.add ("tmp");
        p.setQueryNodes (queryNodes);
        level.setDimTable ("dim_");
        rs = service.getMemberFromLevelByName (info, cube, level, "[abc].[test]", p, params);
        Assert.notNull (rs);
        
        level.setDimTable ("fact");
        rs = service.getMemberFromLevelByName (info, cube, level, "[abc].[test]", p, params);
        Assert.notNull (rs);

    }
    
    @Test
    public void testGetMemberFromLevelByNames() throws Exception {
        MiniCube cube = new MiniCube();
        cube.setSource ("fact");
        StandardDimension dim = new StandardDimension ("test");
        dim.setTableName ("dim_");
        dim.setCaption ("test");
        dim.setFacttableColumn ("test");
        dim.setId ("test");
        dim.setPrimaryKey ("id");
        MiniCubeLevel level = new MiniCubeLevel ("abc");
        level.setDimension (dim);
        level.setFactTableColumn ("test");
        level.setSource ("test");
        level.setCaptionColumn ("test_caption");
        level.setSource ("test_source");
        level.setPrimaryKey ("dim_id");
        dim.getLevels ().put (level.getName (), level);
        cube.getDimensions ().put (dim.getName (), dim);
        MiniCubeMeasure m = new MiniCubeMeasure ("test");
        m.setId ("test_m");
        cube.getMeasures ().put (m.getId (), m);
        DataSourceInfo info = Mockito.mock (DataSourceInfo.class);
        Mockito.doReturn (true).when (info).validate ();
        SqlDimensionMemberServiceImpl service = new SqlDimensionMemberServiceImpl();
        SearchService search = Mockito.mock (SearchService.class);
        service.setSearchService (search);
        SearchIndexResultSet resultSet = Mockito.mock (SearchIndexResultSet.class);
        Mockito.doReturn (resultSet).when (search).query (service.buildQueryRequest (cube, level, null, info));
        HashMap<String, String> params = Maps.newHashMap ();
        params.put (QueryContextBuilder.FILTER_DIM_KEY, "a,b,c,");
        params.put ("b", "e,f,g");
        params.put ("c", "k,m");
        Mockito.doReturn (resultSet).when (search).query (Mockito.any ());
        level.setDimTable ("fact");
        List<MiniCubeMember> rs = 
            service.getMemberFromLevelByNames(info, cube, level, params, Lists.newArrayList ("[abc].[test]"));
        Assert.notNull (rs);
        
    }
    
    @Test
    public void testGetMemberFromLevleByNamesWithNull () {
        try {
            SqlDimensionMemberServiceImpl service = new SqlDimensionMemberServiceImpl();
            service.getMemberFromLevelByNames (null, null, null, null, null);
        } catch (Exception e) {
            Assert.notNull (e);
        }
        
        MiniCubeLevel level = new MiniCubeLevel ("abc");
        try {
            SqlDimensionMemberServiceImpl service = new SqlDimensionMemberServiceImpl();
            service.getMemberFromLevelByNames (null, null, level, null, null);
        } catch (Exception e) {
            Assert.notNull (e);
        }
        
        MiniCube cube = new MiniCube ("test");
        StandardDimension dim = new StandardDimension ("dim");
        try {
            SqlDimensionMemberServiceImpl service = new SqlDimensionMemberServiceImpl();
            service.getMemberFromLevelByNames (null, cube, level, null, Lists.newArrayList ("[abc].[test]"));
        } catch (Exception e) {
            Assert.notNull (e);
        }
        
        cube.getDimensions ().put (dim.getId (), dim);
        try {
            SqlDimensionMemberServiceImpl service = new SqlDimensionMemberServiceImpl();
            service.getMemberFromLevelByNames (null, cube, level, null, Lists.newArrayList ("[abc].[test]"));
        } catch (Exception e) {
            Assert.notNull (e);
        }
        
        MiniCubeMeasure m = new MiniCubeMeasure ("test");
        cube.getMeasures ().put (m.getId (), m);
        try {
            SqlDimensionMemberServiceImpl service = new SqlDimensionMemberServiceImpl();
            service.getMemberFromLevelByNames (null, cube, level, null, Lists.newArrayList ("[abc].[test]"));
        } catch (Exception e) {
            Assert.notNull (e);
        }
        
        SqlDataSourceInfo info = new SqlDataSourceInfo("tmp");
        try {
            SqlDimensionMemberServiceImpl service = new SqlDimensionMemberServiceImpl();
            service.getMemberFromLevelByNames (info, cube, level, null, Lists.newArrayList ("[abc].[test]"));
        } catch (Exception e) {
            Assert.notNull (e);
        }
    }
}
