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
package com.baidu.rigel.biplatform.ma.resource.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.Schema;
import com.baidu.rigel.biplatform.ac.query.data.DataModel;
import com.baidu.rigel.biplatform.ac.query.model.PageInfo;
import com.baidu.rigel.biplatform.ac.util.AnswerCoreConstant;
import com.baidu.rigel.biplatform.ma.report.exception.PivotTableParseException;
import com.baidu.rigel.biplatform.ma.report.exception.PlaneTableParseException;
import com.baidu.rigel.biplatform.ma.report.model.ExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.ExtendAreaContext;
import com.baidu.rigel.biplatform.ma.report.model.ExtendAreaType;
import com.baidu.rigel.biplatform.ma.report.model.Item;
import com.baidu.rigel.biplatform.ma.report.model.LogicModel;
import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;
import com.baidu.rigel.biplatform.ma.report.query.QueryAction;
import com.baidu.rigel.biplatform.ma.report.query.ReportRuntimeModel;
import com.baidu.rigel.biplatform.ma.report.query.ResultSet;
import com.baidu.rigel.biplatform.ma.report.query.chart.DIReportChart;
import com.baidu.rigel.biplatform.ma.report.query.pivottable.CellData;
import com.baidu.rigel.biplatform.ma.report.query.pivottable.PivotTable;
import com.baidu.rigel.biplatform.ma.report.query.pivottable.PlaneTable;
import com.baidu.rigel.biplatform.ma.report.query.pivottable.PlaneTableColDefine;
import com.baidu.rigel.biplatform.ma.report.query.pivottable.RowDefine;
import com.baidu.rigel.biplatform.ma.report.query.pivottable.RowHeadField;
import com.baidu.rigel.biplatform.ma.report.service.ChartBuildService;
import com.baidu.rigel.biplatform.ma.report.service.QueryBuildService;
import com.baidu.rigel.biplatform.ma.resource.ResponseResult;

/**
 * 
 * LiteOlapView操作工具测试类
 *
 * @author luowenlei
 * @version 1.0.0.1
 */
@RunWith(PowerMockRunner.class)
public class QueryDataResourceUtilsTest {

    @InjectMocks
    private QueryDataResourceUtils queryDataResourceUtils;

    @Mock
    private QueryBuildService queryBuildService;

    /**
     * chartBuildService
     */
    @Mock
    private ChartBuildService chartBuildService;

    /**
     * 
     */
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testParseQueryResultToResponseResult() {
        PlaneTable planeTable = new PlaneTable();
        planeTable.setColDefines(new ArrayList<PlaneTableColDefine>());
        planeTable.setData(new ArrayList<Map<String, String>>());
        planeTable.setPageInfo(new PageInfo());
        Schema schema = AnswerCoreConstant.GSON.fromJson(
                LiteOlapViewUtilsTest.schemaJson, Schema.class);
        Mockito.when(
                queryBuildService.parseToPlaneTable(Mockito.any(),
                        Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(planeTable);

        ResultSet resultSet = new ResultSet();
        resultSet.setDataModel(new DataModel());

        ReportRuntimeModel reportRuntimeModel = new ReportRuntimeModel("test");
        ReportDesignModel reportDesignModel = new ReportDesignModel();
        reportDesignModel.setSchema(schema);
        reportDesignModel.getSchema().getCubes().get("test");
        reportRuntimeModel.setModel(reportDesignModel);
        ExtendArea targetArea = new ExtendArea();
        targetArea.setCubeId("test");
        targetArea.setReferenceAreaId("test");
        targetArea.setType(ExtendAreaType.PLANE_TABLE);

        QueryAction queryAction = new QueryAction();
        Map<Item, Object> rows = new HashMap<Item, Object>();

        queryAction.setRows(rows);
        LogicModel logicModel = new LogicModel();

        Item item = new Item();
        item.setOlapElementId("test");
        logicModel.addRow(item);
        rows.put(item, "test");

        targetArea.setLogicModel(logicModel);
        Map<String, ExtendArea> extendAreas = new HashMap<String, ExtendArea>();
        extendAreas.put("test", targetArea);
        reportDesignModel.setExtendAreas(extendAreas);

        ResponseResult responseResult = queryDataResourceUtils
                .parseQueryResultToResponseResult(reportRuntimeModel,
                        targetArea, resultSet, new ExtendAreaContext(), queryAction);
        Assert.assertTrue(responseResult.getStatus() == 0);

        targetArea.setType(ExtendAreaType.TABLE);
        PivotTable pivotTable = new PivotTable();
        RowDefine rowDefine = new RowDefine();
        rowDefine.setSelected(true);
        rowDefine.setUniqueName("test");
        ArrayList<RowDefine> arrayList = new ArrayList<RowDefine>();
        arrayList.add(rowDefine);
        pivotTable.setRowDefine(new ArrayList<RowDefine>());
        pivotTable.getRowDefine().add(rowDefine);

        List<List<CellData>> listcell = new ArrayList<List<CellData>>();
        List<CellData> licell = new ArrayList<CellData>();
        CellData cellData = new CellData();
        licell.add(cellData);
        listcell.add(licell);
        pivotTable.setDataSourceColumnBased(listcell);

        List<List<RowHeadField>> list = new ArrayList<List<RowHeadField>>();
        List<RowHeadField> listrf = new ArrayList<RowHeadField>();
        RowHeadField rowHeadField = new RowHeadField();
        rowHeadField.setV("test");
        listrf.add(rowHeadField);
        list.add(listrf);
        pivotTable.setRowHeadFields(list);
        Mockito.when(
                queryBuildService.parseToPivotTable(Mockito.any(),
                        Mockito.any())).thenReturn(pivotTable);
        ResponseResult responseResultLiteOlap = queryDataResourceUtils
                .parseQueryResultToResponseResult(reportRuntimeModel,
                        targetArea, resultSet, new ExtendAreaContext(), queryAction);
        Assert.assertTrue(responseResultLiteOlap.getStatus() == 0);
        List<List<CellData>> dataSourceColumnBased = new ArrayList<List<CellData>>();
        List<CellData> cellDataList = new ArrayList<CellData>();
        dataSourceColumnBased.add(cellDataList);
        pivotTable.setDataSourceColumnBased(dataSourceColumnBased);
        ResponseResult responseResultLiteOlap1 = queryDataResourceUtils
                .parseQueryResultToResponseResult(reportRuntimeModel,
                        targetArea, resultSet, new ExtendAreaContext(), queryAction);
        Assert.assertTrue(responseResultLiteOlap1.getStatus() == 0);

        // 测试 LITEOLAP_TABLE分支 pivotTable.getDataSourceColumnBased().size() == 0
        targetArea.setType(ExtendAreaType.LITEOLAP_TABLE);
        for (int i = 0; i < 5; i++) {
            Item itemTmp = new Item();
            itemTmp.setOlapElementId("test" + i);
            logicModel.addRow(itemTmp);
            rows.put(itemTmp, "test" + i);
        }
        targetArea.setCubeId("79fc851cea4dc7d09492a430fbeb7c39");
        Cube cube = reportRuntimeModel.getModel().getSchema().getCubes()
                .get("79fc851cea4dc7d09492a430fbeb7c39");
        Dimension dim = cube.getDimensions().get(
                "cc72b5b0c600ab9de45461a4c3b18d70");
        cube.getDimensions().put("test", dim);
        // planeTable
        ResponseResult responseResultLiteOlap3 = queryDataResourceUtils
                .parseQueryResultToResponseResult(reportRuntimeModel,
                        targetArea, resultSet, new ExtendAreaContext(), queryAction);
        Assert.assertTrue(responseResultLiteOlap3.getStatus() == 0);

        // 测试 LITEOLAP_TABLE分支 pivotTable.getDataSourceColumnBased().size() == 0
        pivotTable.setDataSourceColumnBased(new ArrayList<List<CellData>>());
        ResponseResult responseResultLiteOlap4 = queryDataResourceUtils
                .parseQueryResultToResponseResult(reportRuntimeModel,
                        targetArea, resultSet, new ExtendAreaContext(), queryAction);
        Assert.assertTrue(responseResultLiteOlap4.getStatus() == 0);

        // 测试 ExtendAreaType.CHART elseif的情况
        targetArea.setType(ExtendAreaType.LITEOLAP_CHART);
        DIReportChart diReportChart = new DIReportChart();
        Mockito.when(
                chartBuildService.parseToChart(Mockito.anyObject(),
                        Mockito.anyObject(), Mockito.anyBoolean())).thenReturn(diReportChart);
        ResponseResult responseResultLiteOlap5 = queryDataResourceUtils
                .parseQueryResultToResponseResult(reportRuntimeModel,
                        targetArea, resultSet, new ExtendAreaContext(), queryAction);
        Assert.assertTrue(responseResultLiteOlap5.getStatus() == 0);

        // 测试 ExtendAreaType.CHART elseif的情况 and action.getRows().size() == 1
        Map<Item, Object> rows1 = new HashMap<Item, Object>();
        Item itemTmp = new Item();
        itemTmp.setOlapElementId("test");
        rows1.put(itemTmp, "test");
        queryAction.setRows(rows1);
        ResponseResult responseResultLiteOlap6 = queryDataResourceUtils
                .parseQueryResultToResponseResult(reportRuntimeModel,
                        targetArea, resultSet, new ExtendAreaContext(), queryAction);
        Assert.assertTrue(responseResultLiteOlap6.getStatus() == 0);

        // 异常情况
        targetArea.setType(ExtendAreaType.TABLE);
        Mockito.when(
                queryBuildService.parseToPivotTable(Mockito.any(),
                        Mockito.any())).thenThrow(new PivotTableParseException(""));
        ResponseResult responseResultLiteOlap2 = queryDataResourceUtils
                .parseQueryResultToResponseResult(reportRuntimeModel,
                        targetArea, resultSet, new ExtendAreaContext(), queryAction);
        Assert.assertEquals(responseResultLiteOlap2.getStatusInfo(),
                "Fail in parsing result to pivotTable. ");
    }

    @Test
    public void testParseQueryResultToResponseResultException() {
        PlaneTable planeTable = new PlaneTable();
        planeTable.setColDefines(new ArrayList<PlaneTableColDefine>());
        planeTable.setData(new ArrayList<Map<String, String>>());
        planeTable.setPageInfo(new PageInfo());
        Schema schema = AnswerCoreConstant.GSON.fromJson(
                LiteOlapViewUtilsTest.schemaJson, Schema.class);
        Mockito.when(
                queryBuildService.parseToPlaneTable(Mockito.any(),
                        Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                                .thenThrow(new PlaneTableParseException(""));

        ResultSet resultSet = new ResultSet();
        resultSet.setDataModel(new DataModel());

        ReportRuntimeModel reportRuntimeModel = new ReportRuntimeModel("test");
        ReportDesignModel reportDesignModel = new ReportDesignModel();
        reportDesignModel.setSchema(schema);
        reportDesignModel.getSchema().getCubes().get("test");
        reportRuntimeModel.setModel(reportDesignModel);
        ExtendArea targetArea = new ExtendArea();
        targetArea.setCubeId("test");
        targetArea.setType(ExtendAreaType.PLANE_TABLE);
        targetArea.setLogicModel(new LogicModel());
        ResponseResult responseResult = queryDataResourceUtils
                .parseQueryResultToResponseResult(reportRuntimeModel,
                        targetArea, resultSet, null, null);
        Assert.assertTrue(responseResult.getStatus() == 1);
    }

    @Test
    public void testGenRootDimCaption() {
        PivotTable pivotTable = new PivotTable();
        RowDefine rowDefine = new RowDefine();
        rowDefine.setSelected(true);
        rowDefine.setUniqueName("test");
        ArrayList<RowDefine> arrayList = new ArrayList<RowDefine>();
        arrayList.add(rowDefine);
        pivotTable.setRowDefine(new ArrayList<RowDefine>());
        pivotTable.getRowDefine().add(rowDefine);

        List<List<CellData>> listcell = new ArrayList<List<CellData>>();
        List<CellData> licell = new ArrayList<CellData>();
        CellData cellData = new CellData();
        licell.add(cellData);
        listcell.add(licell);
        pivotTable.setDataSourceColumnBased(listcell);

        List<List<RowHeadField>> list = new ArrayList<List<RowHeadField>>();
        List<RowHeadField> listrf = new ArrayList<RowHeadField>();
        RowHeadField rowHeadField = new RowHeadField();
        rowHeadField.setV("test");
        listrf.add(rowHeadField);
        list.add(listrf);
        pivotTable.setRowHeadFields(list);
        PlaneTable planeTable = new PlaneTable();
        planeTable.setColDefines(new ArrayList<PlaneTableColDefine>());
        planeTable.setData(new ArrayList<Map<String, String>>());
        planeTable.setPageInfo(new PageInfo());
        Schema schema = AnswerCoreConstant.GSON.fromJson(
                LiteOlapViewUtilsTest.schemaJson, Schema.class);

        ResultSet resultSet = new ResultSet();
        resultSet.setDataModel(new DataModel());

        ReportRuntimeModel reportRuntimeModel = new ReportRuntimeModel("test");
        ReportDesignModel reportDesignModel = new ReportDesignModel();
        reportDesignModel.setSchema(schema);
        reportDesignModel.getSchema().getCubes().get("test");
        reportRuntimeModel.setModel(reportDesignModel);
        ExtendArea targetArea = new ExtendArea();
        targetArea.setCubeId("test");
        targetArea.setReferenceAreaId("test");
        targetArea.setType(ExtendAreaType.PLANE_TABLE);

        QueryAction queryAction = new QueryAction();
        Map<Item, Object> rows = new HashMap<Item, Object>();

        queryAction.setRows(rows);
        LogicModel logicModel = new LogicModel();

        Item item = new Item();
        item.setOlapElementId("test");
        logicModel.addRow(item);
        rows.put(item, "test");
        logicModel.addColumn(item);

        targetArea.setLogicModel(logicModel);
        Map<String, ExtendArea> extendAreas = new HashMap<String, ExtendArea>();
        extendAreas.put("test", targetArea);
        reportDesignModel.setExtendAreas(extendAreas);
        Cube cube = reportRuntimeModel.getModel().getSchema().getCubes()
                .get("79fc851cea4dc7d09492a430fbeb7c39");
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            queryDataResourceUtils.genRootDimCaption(pivotTable, logicModel, params, cube);
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }
}
