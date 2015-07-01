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

package com.baidu.rigel.biplatform.ma.report.utils;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeLevel;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMeasure;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeSchema;
import com.baidu.rigel.biplatform.ac.minicube.StandardDimension;
import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.Measure;
import com.baidu.rigel.biplatform.ac.query.model.DimensionCondition;
import com.baidu.rigel.biplatform.ac.query.model.MeasureCondition;
import com.baidu.rigel.biplatform.ac.query.model.MetaCondition;
import com.baidu.rigel.biplatform.ac.query.model.SQLCondition.SQLConditionType;
import com.baidu.rigel.biplatform.ma.report.model.ExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.Item;
import com.baidu.rigel.biplatform.ma.report.model.LogicModel;
import com.baidu.rigel.biplatform.ma.report.model.PlaneTableCondition;
import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;
import com.baidu.rigel.biplatform.ma.report.query.QueryAction;
import com.google.common.collect.Maps;

/**
 * QueryCondition构建测试类
 * 
 * @author yichao.jiang
 * @version 2015年6月4日
 * @since jdk 1.8 or after
 */
public class QueryConditionUtilsTest {

    /**
     * 测试构建平面表条件，一个维度条件，一个指标条件 testBuildQueryConditionForPlaneTable
     */
    @Test
    public void testBuildQueryConditionForPlaneTable() throws Exception {

        // 设计模型
        ReportDesignModel reportModel = this.buildReportDesignModel();
        // 获取扩展区
        ExtendArea area = reportModel.getExtendById("areaId");

        Item[] items = area.getLogicModel().getColumns();
        Item item1 = items[0];
        Item item2 = items[1];
        // 构建QueryAction，主要为列，以及列上对应的数值
        QueryAction queryAction = new QueryAction();
        Map<Item, Object> columns = Maps.newHashMap();
        columns.put(item1, "defaultValue1");
        columns.put(item2, "defaultValue2");
        queryAction.setColumns(columns);

        Map<String, MetaCondition> conditions =
                QueryConditionUtils.buildQueryConditionsForPlaneTable(reportModel, area, queryAction);
        Assert.assertNotNull(conditions);
        // 2个条件，一个维度条件，一个指标条件
        Assert.assertEquals(2, conditions.size());
        // 维度条件
        Assert.assertTrue(conditions.containsKey("test_Dim"));
        Assert.assertTrue(conditions.get("test_Dim") instanceof DimensionCondition);
        // 指标条件
        Assert.assertTrue(conditions.containsKey("Measure"));
        Assert.assertTrue(conditions.get("Measure") instanceof MeasureCondition);
    }

    /**
     * 测试维度条件为数组 testWithDimConditionArrayValues
     */
    @Test
    public void testWithDimConditionArrayValues() throws Exception {
        // 设计模型
        ReportDesignModel reportModel = this.buildReportDesignModel();
        // 获取扩展区
        ExtendArea area = reportModel.getExtendById("areaId");

        Item[] items = area.getLogicModel().getColumns();
        Item item1 = items[0];
        // 构建QueryAction，主要为列，以及列上对应的数值
        QueryAction queryAction = new QueryAction();
        Map<Item, Object> columns = Maps.newHashMap();
        // 仅有维度条件，条件值为数组
        columns.put(item1, new String[] { "defaultValue1", "defaultValue2" });
        queryAction.setColumns(columns);
        Map<String, MetaCondition> conditions =
                QueryConditionUtils.buildQueryConditionsForPlaneTable(reportModel, area, queryAction);
        Assert.assertNotNull(conditions);
        // 仅有一个维度条件
        Assert.assertEquals(1, conditions.size());
    }

    /**
     * 
     * testDimConditionWithNullValue
     */
    @Test
    public void testDimConditionWithNullValue() throws Exception {
        // 设计模型
        ReportDesignModel reportModel = this.buildReportDesignModel();
        // 获取扩展区
        ExtendArea area = reportModel.getExtendById("areaId");

        Item[] items = area.getLogicModel().getColumns();
        Item item1 = items[0];
        // 构建QueryAction，主要为列，以及列上对应的数值
        QueryAction queryAction = new QueryAction();
        Map<Item, Object> columns = Maps.newHashMap();
        // 仅有维度条件，条件值为空
        columns.put(item1, null);
        queryAction.setColumns(columns);
        Map<String, MetaCondition> conditions =
                QueryConditionUtils.buildQueryConditionsForPlaneTable(reportModel, area, queryAction);
        Assert.assertNotNull(conditions);
        // 仅有一个维度条件
        Assert.assertEquals(1, conditions.size());
    }

    /**
     * 测试指标条件，值为数组 testMeasureConditionWithArrayValue
     */
    public void testMeasureConditionWithArrayValue() throws Exception {
        // 设计模型
        ReportDesignModel reportModel = this.buildReportDesignModel();
        // 获取扩展区
        ExtendArea area = reportModel.getExtendById("areaId");

        Item[] items = area.getLogicModel().getColumns();
        Item item2 = items[1];
        // 构建QueryAction，主要为列，以及列上对应的数值
        QueryAction queryAction = new QueryAction();
        Map<Item, Object> columns = Maps.newHashMap();
        // 仅有指标条件，条件值为数组
        columns.put(item2, new String[] { "defaultValue1", "defaultValue2" });
        queryAction.setColumns(columns);
        Map<String, MetaCondition> conditions =
                QueryConditionUtils.buildQueryConditionsForPlaneTable(reportModel, area, queryAction);
        Assert.assertNotNull(conditions);
        // 仅有一个指标条件
        Assert.assertEquals(1, conditions.size());
    }

    /**
     * 
     * testMeasureConditionWithNullValue
     */
    @Test
    public void testMeasureConditionWithNullValue() throws Exception {
        // 设计模型
        ReportDesignModel reportModel = this.buildReportDesignModel();
        // 获取扩展区
        ExtendArea area = reportModel.getExtendById("areaId");

        Item[] items = area.getLogicModel().getColumns();
        Item item2 = items[1];
        // 构建QueryAction，主要为列，以及列上对应的数值
        QueryAction queryAction = new QueryAction();
        Map<Item, Object> columns = Maps.newHashMap();
        // 仅有指标条件，条件值为数组
        columns.put(item2, null);
        queryAction.setColumns(columns);
        Map<String, MetaCondition> conditions =
                QueryConditionUtils.buildQueryConditionsForPlaneTable(reportModel, area, queryAction);
        Assert.assertNotNull(conditions);
        // 仅有一个指标条件
        Assert.assertEquals(1, conditions.size());
    }

    /**
     * 构建报表设计模型 buildReportDesignModel
     * 
     * @return
     */
    private ReportDesignModel buildReportDesignModel() {
        ReportDesignModel reportModel = new ReportDesignModel();
        // 建立平面表查询条件
        reportModel.setPlaneTableConditions(buildPlaneTableConditions());
        // 设置报表模型的schema信息
        reportModel.setSchema(this.buildMiniCubeSchema());
        // 设置扩展区
        reportModel.addExtendArea(this.buildExtendArea());
        return reportModel;
    }

    /**
     * 构建扩展区 buildExtendArea
     * 
     * @return
     */
    private ExtendArea buildExtendArea() {
        // 建立扩展区域，设置扩展区域对应的逻辑模型，areaId、cubeId信息
        ExtendArea area = new ExtendArea();
        area.setId("areaId");
        area.setCubeId("cubeId");
        area.setLogicModel(this.buildLogicModel());
        return area;
    }

    /**
     * 构建logicModel buildLogicModel
     * 
     * @param items
     * @return
     */
    private LogicModel buildLogicModel() {
        LogicModel logicModel = new LogicModel();
        logicModel.addColumns(this.buildItems());
        return logicModel;
    }

    /**
     * 构建列上的Item数组 buildItem
     * 
     * @return
     */
    private Item[] buildItems() {
        Item[] items = new Item[2];
        // 构建维度和指标对应的Item
        Item item1 = new Item();
        item1.setAreaId("areaId");
        item1.setId("id1");
        item1.setOlapElementId("id1");
        item1.setCubeId("cubeId");
        items[0] = item1;
        // 构建维度和指标对应的Item
        Item item2 = new Item();
        item2.setAreaId("areaId");
        item2.setId("id2");
        item2.setOlapElementId("id2");
        item2.setCubeId("cubeId");
        items[1] = item2;
        return items;
    }

    /**
     * 构建MiniCubeSchema对象 buildMiniCubeSchema
     * 
     * @return
     */
    private MiniCubeSchema buildMiniCubeSchema() {
        // 建立schema信息
        MiniCubeSchema schema = new MiniCubeSchema();
        // 建立schema的cube信息
        Cube cube = this.buildCube();
        Map<String, MiniCube> cubes = Maps.newHashMap();
        // 设置schema的cube信息
        cubes.put("cubeId", (MiniCube) cube);
        schema.setCubes(cubes);
        return schema;
    }

    /**
     * 建立平面表查询条件 buildPlaneTableConditions
     * 
     * @return
     */
    private Map<String, PlaneTableCondition> buildPlaneTableConditions() {
        // 建立平面表查询条件--维度
        PlaneTableCondition planeTableConditionDim = new PlaneTableCondition();
        planeTableConditionDim.setElementId("id1");
        planeTableConditionDim.setName("name1");
        planeTableConditionDim.setSQLCondition(SQLConditionType.EQ);
        planeTableConditionDim.setDefaultValue("defaultValue1");
        // 建立平面表查询条件--指标
        PlaneTableCondition planeTableConditionMeasure = new PlaneTableCondition();
        planeTableConditionMeasure.setElementId("id2");
        planeTableConditionMeasure.setName("name1");
        planeTableConditionMeasure.setSQLCondition(SQLConditionType.EQ);
        planeTableConditionMeasure.setDefaultValue("defaultValue2");
        Map<String, PlaneTableCondition> planeTableConditions = Maps.newHashMap();
        planeTableConditions.put("id2", planeTableConditionMeasure);
        planeTableConditions.put("id1", planeTableConditionDim);
        return planeTableConditions;
    }

    /**
     * 构建Cube buildCube
     * 
     * @return
     */
    private Cube buildCube() {
        MiniCube cube = new MiniCube();
        Map<String, Dimension> dimensions = Maps.newHashMap();
        StandardDimension dimension = new StandardDimension("test_Dim");
        MiniCubeLevel l = new MiniCubeLevel();
        l.setDimTable("test");
        l.setName("Dim");
        dimension.addLevel(l);

        dimension.setId("id1");
        dimensions.put("test_Dim", dimension);

        Map<String, Measure> measures = Maps.newHashMap();
        MiniCubeMeasure measure = new MiniCubeMeasure("Measure");
        measure.setId("id2");
        measures.put("Measure", measure);

        cube.setSource("test");
        cube.setId("cubeId");
        cube.setMeasures(measures);
        cube.setDimensions(dimensions);
        return cube;
    }
}
