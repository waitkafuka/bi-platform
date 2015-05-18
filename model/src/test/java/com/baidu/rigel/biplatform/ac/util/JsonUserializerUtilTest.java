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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeLevel;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMeasure;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMember;
import com.baidu.rigel.biplatform.ac.minicube.StandardDimension;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.query.data.DataModel;
import com.baidu.rigel.biplatform.ac.query.data.HeadField;
import com.baidu.rigel.biplatform.ac.query.data.vo.MetaJsonDataInfo;
import com.baidu.rigel.biplatform.ac.query.data.vo.MetaJsonDataInfo.MetaType;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 *Description:
 * @author david.wang
 *
 */
public class JsonUserializerUtilTest {
    
    @Test
    public void testParseMember2MetaJson () {
        try {
            JsonUnSeriallizableUtils.parseMember2MetaJson (null);
            Assert.fail ();
        } catch (Exception e) {
            
        }
        MiniCubeMember m = new MiniCubeMember ("test");
        MiniCube cube = new MiniCube ();
        StandardDimension dim = new StandardDimension ("test");
        MiniCubeLevel l = new MiniCubeLevel ("test");
        dim.getLevels ().put (l.getName (), l);
        Map<String, Dimension> dims = new HashMap<> ();
        dims.put (dim.getName (), dim);
        cube.setDimensions (dims);
        MiniCubeMeasure measure = new MiniCubeMeasure ("test");
        cube.getMeasures ().put (measure.getName (), measure);
        m.setLevel (l);
        JsonUnSeriallizableUtils.fillCubeInfo (cube);
        m.setCaption ("test");
        m.setName ("test");
        Set<String> queryDatas = Sets.newHashSet ();
        queryDatas.add ("test");
        m.setQueryNodes (queryDatas);
        MetaJsonDataInfo info = JsonUnSeriallizableUtils.parseMember2MetaJson (m);
        
        MiniCubeMember c = new MiniCubeMember ("test");
        c.setLevel (l);
        c.setCaption ("test");
        c.setName ("test");
        c.setQueryNodes (queryDatas);
        List<MiniCubeMember> children = new ArrayList<> ();
        children.add (c);
        m.setChildren (children);
        info = JsonUnSeriallizableUtils.parseMember2MetaJson (m);
        Assert.assertNotNull (info);
        Assert.assertEquals ("test", info.getMemberName ());
    }
    
    @Test
    public void testFillCubeInfo () {
        try {
            JsonUnSeriallizableUtils.fillCubeInfo (null);
            Assert.fail ();
        } catch (Exception e) {
        }
        
        MiniCube cube = new MiniCube ();
        StandardDimension dim = new StandardDimension ("test");
        MiniCubeLevel l = new MiniCubeLevel ("test");
        dim.getLevels ().put (l.getName (), l);
        Map<String, Dimension> dims = Maps.newHashMap ();
        dims.put (dim.getName (), dim);
        cube.setDimensions (dims);
        MiniCubeMeasure m = new MiniCubeMeasure ("test");
        cube.getMeasures ().put (m.getName (), m);
        JsonUnSeriallizableUtils.fillCubeInfo (cube);
        Assert.assertNotNull (l.getDimension ());
        Assert.assertNotNull (m.getCube ());
    }
    
    @Test
    public void testDataModelFromJson () {
        try {
            JsonUnSeriallizableUtils.dataModelFromJson (null);
            Assert.fail ();
        } catch (Exception e) {
            
        }
        DataModel model = genDataModel () ;
        String json = AnswerCoreConstant.GSON.toJson (model);
        DataModel rs = JsonUnSeriallizableUtils.dataModelFromJson (json);
        Assert.assertNotNull (rs);
    }
    
    private DataModel genDataModel () {
        DataModel dataModel = new DataModel();
        List<HeadField> columnHeader = new ArrayList<HeadField>();
        // int c
        String[] trades = { "房屋出租", "房地产其他", "皮革", "服装", "鞋帽", "纺织辅料", "服装鞋帽其他", "工艺品", "礼品", "饰品" };
        
        String[] cols = { "消费", "点击", "访问人数", "人均消费", "平均价格", "总量", "分量", "单位产出", "月均订单数", "收入" };
        
        for (int i = 0; i < 10; ++i) {
            HeadField header = new HeadField(null);
            header.setCaption(cols[i]);
            header.setValue("[Measures].[column_" + i + "]");
            columnHeader.add(header);
        }
        dataModel.setColumnHeadFields(columnHeader);
        List<HeadField> rowHeader = new ArrayList<HeadField>();
        for (int i = 0; i < 10; ++i) {
            HeadField header = new HeadField(null);
            header.setCaption(trades[i]);
            header.setValue("[Dmension].[row_" + i + "]");
            rowHeader.add(header);
        }
        dataModel.setRowHeadFields(rowHeader);
        List<List<BigDecimal>> datas = new ArrayList<List<BigDecimal>>();
        
        for (int i = 0; i < 10; ++i) {
            List<BigDecimal> cellDatas = new ArrayList<BigDecimal>();
            for (int j = 0; j < 10; ++j) {
                Random random = new Random();
                cellDatas.add(BigDecimal.valueOf(random.nextInt(10000)));
            }
            datas.add(cellDatas);
        }
        dataModel.setColumnBaseData(datas);
        return dataModel;
    }
    
    @Test
    public void testParseMetaJson2Member () {
        try {
            JsonUnSeriallizableUtils.parseMetaJson2Member (null, null);
            Assert.fail ();
        } catch (Exception e) {
            
        }
        
        MiniCube cube = new MiniCube ();
        StandardDimension dim = new StandardDimension ("test");
        MiniCubeLevel l = new MiniCubeLevel ("test");
        dim.getLevels ().put (l.getName (), l);
        Map<String, Dimension> dims = Maps.newHashMap ();
        dims.put (dim.getName (), dim);
        cube.setDimensions (dims);
        MiniCubeMeasure m = new MiniCubeMeasure ("test");
        cube.getMeasures ().put (m.getName (), m);
        
        try {
            JsonUnSeriallizableUtils.parseMetaJson2Member (cube, null);
            Assert.fail ();
        } catch (Exception e) {
            
        }
        
        MetaJsonDataInfo metaInfo = new MetaJsonDataInfo (MetaType.Dimension);
        try {
            JsonUnSeriallizableUtils.parseMetaJson2Member (cube, metaInfo);
            Assert.fail ();
        } catch (Exception e) {
            
        }
        metaInfo = new MetaJsonDataInfo (MetaType.Member);
        metaInfo.setLevelName ("test");
        metaInfo.setDimensionName ("test");
        metaInfo.setMemberName ("test");
        metaInfo.setMemberName ("test");
        metaInfo.setMemberUniqueName ("[test].[test]");
        MiniCubeMember rs = JsonUnSeriallizableUtils.parseMetaJson2Member (cube, metaInfo);
        Assert.assertNotNull (rs);
        MetaJsonDataInfo child = new MetaJsonDataInfo (MetaType.Member);
        metaInfo.getChildren ().add (child);
        child.setLevelName ("test");
        child.setDimensionName ("test");
        child.setMemberName ("test");
        child.setMemberName ("test");
        child.setMemberUniqueName ("[test].[test]");
        rs = JsonUnSeriallizableUtils.parseMetaJson2Member (cube, metaInfo);
        Assert.assertNotNull (rs);
        Assert.assertEquals (rs.getChildren ().size (), 1);
    }
}
