package com.baidu.rigel.biplatform.ma.report.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.baidu.rigel.biplatform.ac.minicube.CallbackLevel;
import com.baidu.rigel.biplatform.ac.minicube.ExtendMinicubeMeasure;
import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeLevel;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMeasure;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMember;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeSchema;
import com.baidu.rigel.biplatform.ac.minicube.StandardDimension;
import com.baidu.rigel.biplatform.ac.model.Aggregator;
import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.DimensionType;
import com.baidu.rigel.biplatform.ac.model.Measure;
import com.baidu.rigel.biplatform.ac.model.MeasureType;
import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.data.vo.MetaJsonDataInfo;
import com.baidu.rigel.biplatform.ac.query.data.vo.MetaJsonDataInfo.MetaType;
import com.baidu.rigel.biplatform.ac.query.model.PageInfo;
import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;
import com.baidu.rigel.biplatform.ac.query.model.SortRecord.SortType;
import com.baidu.rigel.biplatform.ac.util.AnswerCoreConstant;
import com.baidu.rigel.biplatform.ac.util.ConfigInfoUtils;
import com.baidu.rigel.biplatform.ac.util.HttpRequest;
import com.baidu.rigel.biplatform.ac.util.JsonUnSeriallizableUtils;
import com.baidu.rigel.biplatform.ac.util.ResponseResult;
import com.baidu.rigel.biplatform.ma.ds.exception.DataSourceConnectionException;
import com.baidu.rigel.biplatform.ma.ds.exception.DataSourceOperationException;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceConnectionService;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceConnectionServiceFactory;
import com.baidu.rigel.biplatform.ma.model.consts.Constants;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceType;
import com.baidu.rigel.biplatform.ma.report.exception.QueryModelBuildException;
import com.baidu.rigel.biplatform.ma.report.model.ExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.ExtendAreaType;
import com.baidu.rigel.biplatform.ma.report.model.Item;
import com.baidu.rigel.biplatform.ma.report.model.LogicModel;
import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;
import com.baidu.rigel.biplatform.ma.report.query.QueryAction;
import com.baidu.rigel.biplatform.ma.report.query.QueryAction.MeasureOrderDesc;
import com.google.common.collect.Maps;

/**
 * QueryUtils单测类
 * 
 * @author majun04
 *
 */
@RunWith(PowerMockRunner.class)
public class QueryUtilsTest {

    private DataSourceDefine dsDefine;
    private ReportDesignModel reportModel;
    private MiniCubeSchema miniCubeSchema;
    private QueryAction queryAction;
    private Map<String, Object> requestParams;
    private PageInfo pageInfo;
    private String securityKey = "securityKey";

    @Before
    public void init() {
        this.bulidBaseHiglLeveObj();

        reportModel.setExtendAreas(buildExtendAreas());
        reportModel.setSchema(miniCubeSchema);
        miniCubeSchema.setCubes(buildCubes());
        queryAction.setExtendAreaId("testExtendAreaId");
    }

    private void bulidBaseHiglLeveObj() {
        dsDefine = new DataSourceDefine();
        dsDefine.setDataSourceType(DataSourceType.MYSQL);
        reportModel = new ReportDesignModel();
        queryAction = new QueryAction();
        requestParams = new HashMap<String, Object>();
        pageInfo = new PageInfo();
        miniCubeSchema = new MiniCubeSchema();
    }

    private Map<String, ExtendArea> buildExtendAreas() {
        Map<String, ExtendArea> extendAreas = new HashMap<String, ExtendArea>();
        ExtendArea tableExtendArea = new ExtendArea();
        tableExtendArea.setType(ExtendAreaType.TABLE);
        tableExtendArea.setCubeId("testCubeId");
        tableExtendArea.setReferenceAreaId("testTableExtendAreaId");
        tableExtendArea.setLogicModel(buildLogicModel("testTableExtendAreaId", false));
        extendAreas.put("testTableExtendAreaId", tableExtendArea);

        ExtendArea selectExtendArea = new ExtendArea();
        selectExtendArea.setType(ExtendAreaType.SELECT);
        selectExtendArea.setCubeId("testCubeId");
        selectExtendArea.setReferenceAreaId("testSelectExtendAreaId");
        selectExtendArea.setLogicModel(buildLogicModel("testSelectExtendAreaId", true));
        extendAreas.put("testSelectExtendAreaId", selectExtendArea);
        return extendAreas;
    }

    private LogicModel buildLogicModel(String areaId, boolean isFilter) {
        LogicModel logicModel = new LogicModel();

        if (!isFilter) {
            Item column = new Item();
            column.setCubeId("testCubeId");
            column.setAreaId(areaId);
            column.setId("testMeasure");
            column.setOlapElementId("testMeasure");
            column.setReportId("testReportId");
            column.setSchemaId("testSchemaId");
            logicModel.addColumn(column);
        }

        Item row = new Item();
        row.setCubeId("testCubeId");
        row.setAreaId(areaId);
        row.setId("testDim");
        row.setOlapElementId("testDim");
        row.setReportId("testReportId");
        row.setSchemaId("testSchemaId");
        logicModel.addRow(row);

        Item callBackRow = new Item();
        callBackRow.setCubeId("testCubeId");
        callBackRow.setAreaId(areaId);
        callBackRow.setId("testCallBackDim");
        callBackRow.setOlapElementId("testCallBackDim");
        callBackRow.setReportId("testReportId");
        callBackRow.setSchemaId("testSchemaId");
        logicModel.addRow(callBackRow);

        return logicModel;
    }

    private Map<String, MiniCube> buildCubes() {
        Map<String, MiniCube> cubes = new HashMap<String, MiniCube>();
        cubes.put("testCubeId", buildMiniCube());
        return cubes;
    }

    private MiniCube buildMiniCube() {
        MiniCube miniCube = new MiniCube();
        Map<String, Dimension> dimensions = new HashMap<String, Dimension>();
        StandardDimension testDim = new StandardDimension();
        testDim.setCaption("testDimCaption");
        testDim.setDescription("testDimDescription");
        testDim.setId("testDim");

        dimensions.put("testDim", testDim);

        StandardDimension testCallBackDim = new StandardDimension();
        testCallBackDim.setType(DimensionType.CALLBACK);
        testCallBackDim.setCaption("testCallBackDimCaption");
        testCallBackDim.setDescription("testCallBackDimDescription");
        testCallBackDim.setId("testCallBackDim");
        testCallBackDim.setName("testCallBackDimName");
        CallbackLevel callbackLevel = new CallbackLevel();
        callbackLevel.setName("testCallBackDimName");
        testCallBackDim.addLevel(callbackLevel);
        dimensions.put("testCallBackDim", testCallBackDim);

        Map<String, Measure> measures = new HashMap<String, Measure>();
        MiniCubeMeasure testMeasure = new MiniCubeMeasure("testMeasure");
        testMeasure.setCaption("testMeasureCaption");
        testMeasure.setDescription("testMeasureDescription");
        testMeasure.setId("testMeasure");
        measures.put("testMeasure", testMeasure);
        miniCube.setDimensions(dimensions);
        miniCube.setMeasures(measures);
        return miniCube;
    }

    @Test
    public void testConvert2QuestionModelWhitNullAction() {
        reportModel = new ReportDesignModel();
        try {
            QueryUtils.convert2QuestionModel(dsDefine, reportModel, null, requestParams, pageInfo, securityKey);
        } catch (QueryModelBuildException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void testConvert2QuestionModelWhitNullAreaId() {
        reportModel = new ReportDesignModel();
        try {
            QueryUtils.convert2QuestionModel(dsDefine, reportModel, queryAction, requestParams, pageInfo, securityKey);
        } catch (QueryModelBuildException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void testConvert2QuestionModelWhitNullArea() {
        reportModel = new ReportDesignModel();
        queryAction.setExtendAreaId("");
        try {
            QueryUtils.convert2QuestionModel(dsDefine, reportModel, queryAction, requestParams, pageInfo, securityKey);
        } catch (QueryModelBuildException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    @PrepareForTest({ HttpRequest.class, ConfigInfoUtils.class, })
    public void testAAA() {
        PowerMockito.mockStatic(HttpRequest.class);
        PowerMockito.mockStatic(ConfigInfoUtils.class);
        PowerMockito.when(HttpRequest.sendPost(Mockito.anyString(), Mockito.anyMap())).thenReturn("");
        PowerMockito.when(ConfigInfoUtils.getServerAddress()).thenReturn("http://127.0.0.1:8080");
        Map<String, String> params = null;
        String aa = HttpRequest.sendPost("a", params);
        String add = ConfigInfoUtils.getServerAddress();
        Assert.assertEquals("", "");
    }

    @Test
    @PrepareForTest({ DataSourceConnectionServiceFactory.class, HttpRequest.class, JsonUnSeriallizableUtils.class })
    public void testConvert2QuestionModel() throws DataSourceConnectionException, DataSourceOperationException {
        PowerMockito.mockStatic(DataSourceConnectionServiceFactory.class);
        PowerMockito.mockStatic(HttpRequest.class);
        PowerMockito.mockStatic(JsonUnSeriallizableUtils.class);

        DataSourceConnectionService dataSourceConnectionService = PowerMockito.mock(DataSourceConnectionService.class);
        // DataSourceInfo dataSourceInfo = PowerMockito.mock(DataSourceInfo.class);
        DataSourceInfo dataSourceInfo = new SqlDataSourceInfo("testDataSourceKey");
        PowerMockito.when(dataSourceConnectionService.parseToDataSourceInfo(dsDefine, securityKey)).thenReturn(
                dataSourceInfo);
        PowerMockito.when(
                DataSourceConnectionServiceFactory.getDataSourceConnectionServiceInstance(DataSourceType.MYSQL.name()))
                .thenReturn(dataSourceConnectionService);
        MiniCubeMember miniCubeMember = PowerMockito.mock(MiniCubeMember.class);
        PowerMockito.when(
                JsonUnSeriallizableUtils.parseMetaJson2Member(Mockito.any(Cube.class),
                        Mockito.any(MetaJsonDataInfo.class))).thenReturn(miniCubeMember);
        ResponseResult responseResult = new ResponseResult();
        List<MetaJsonDataInfo> metaJsonList = new ArrayList<MetaJsonDataInfo>();
        MetaJsonDataInfo metaJsonObj = new MetaJsonDataInfo(MetaType.Member);
        metaJsonList.add(metaJsonObj);
        responseResult.setData(metaJsonList);
        String jsonStr = AnswerCoreConstant.GSON.toJson(responseResult);
        PowerMockito.when(HttpRequest.sendPost(Mockito.anyString(), Mockito.anyMap())).thenReturn(jsonStr);
        QuestionModel questionModel = null;
        queryAction.setExtendAreaId("testTableExtendAreaId");
        MeasureOrderDesc measureOrderDesc = new MeasureOrderDesc("testMeasure", String.valueOf(SortType.ASC), 100);
        queryAction.setMeasureOrderDesc(measureOrderDesc);
        queryAction.setNeedOthers(true);
        Map<Item, Object> drillDimValues = new HashMap<Item, Object>();
        queryAction.setDrillDimValues(drillDimValues);
        Map<Item, Object> columns = new HashMap<Item, Object>();
        Item column = new Item();
        column.setCubeId("testCubeId");
        column.setAreaId("testTableExtendAreaId");
        column.setId("testMeasure");
        column.setOlapElementId("testMeasure");
        column.setReportId("testReportId");
        column.setSchemaId("testSchemaId");

        Item row = new Item();
        row.setCubeId("testCubeId");
        row.setAreaId("testSelectExtendAreaId");
        row.setId("testDim");
        row.setOlapElementId("testDim");
        row.setReportId("testReportId");
        row.setSchemaId("testSchemaId");
        columns.put(column, null);

        queryAction.setColumns(columns);
        Map<Item, Object> rows = new HashMap<Item, Object>();
        rows.put(row, null);
        Item callBackRow = new Item();
        callBackRow.setCubeId("testCubeId");
        callBackRow.setAreaId("testSelectExtendAreaId");
        callBackRow.setId("testCallBackDim");
        callBackRow.setOlapElementId("testCallBackDim");
        callBackRow.setReportId("testReportId");
        callBackRow.setSchemaId("testSchemaId");
        rows.put(callBackRow, null);
        queryAction.setRows(rows);
        Map<Item, Object> slices = new HashMap<Item, Object>();
        slices.put(row, new String[] { "testRowValue" });
        queryAction.setSlices(slices);

        requestParams.put(Constants.IN_EDITOR, true);
        try {
            questionModel =
                    QueryUtils.convert2QuestionModel(dsDefine, reportModel, queryAction, requestParams, pageInfo,
                            securityKey);
        } catch (QueryModelBuildException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(questionModel);
    }

    @Test
    public void testConvertDim2Dim() {
        Dimension mockDim = Mockito.mock(Dimension.class);
        Mockito.when(mockDim.getId()).thenReturn("testId");
        Dimension newDim = QueryUtils.convertDim2Dim(mockDim);
        Assert.assertEquals(newDim.getId(), "testId");
    }

    @Test
    public void testGetCubeWithExtendArea() throws Exception {
        ReportDesignModel model = new ReportDesignModel();
        ExtendArea area = new ExtendArea();
        area.setCubeId("cubeId");
        area.getOtherSetting().put(Constants.CAN_CHANGED_MEASURE, "true");
        MiniCubeSchema schema = new MiniCubeSchema();
        MiniCube cube = new MiniCube();
        cube.setId("cubeId");
        StandardDimension dim = new StandardDimension("test");
        MiniCubeLevel level = new MiniCubeLevel();
        level.setId("lid");
        level.setName("l");
        dim.getLevels().put(level.getId(), level);
        ExtendMinicubeMeasure m = new ExtendMinicubeMeasure("m");
        m.setId("mid");
        m.setAggregator(Aggregator.SUM);
        m.setType(MeasureType.CAL);
        m.setFormula("${a}");
        cube.getMeasures().put(m.getId(), m);
        MiniCubeMeasure a = new MiniCubeMeasure("a");
        a.setId("aId");
        a.setAggregator(Aggregator.SUM);
        a.setType(MeasureType.COMMON);
        cube.getMeasures().put(a.getId(), a);
        Map<String, MiniCube> cubes = Maps.newHashMap();
        cubes.put(cube.getId(), cube);
        schema.setCubes(cubes);
        model.setSchema(schema);
        Cube rs = QueryUtils.getCubeWithExtendArea(model, area);
        Assert.assertNotNull(rs);
    }
}
