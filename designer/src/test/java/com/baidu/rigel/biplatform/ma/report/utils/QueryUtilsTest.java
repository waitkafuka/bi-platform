package com.baidu.rigel.biplatform.ma.report.utils;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMeasure;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeSchema;
import com.baidu.rigel.biplatform.ac.minicube.StandardDimension;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.Measure;
import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.model.PageInfo;
import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;
import com.baidu.rigel.biplatform.ma.ds.exception.DataSourceConnectionException;
import com.baidu.rigel.biplatform.ma.ds.exception.DataSourceOperationException;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceConnectionService;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceConnectionServiceFactory;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceType;
import com.baidu.rigel.biplatform.ma.report.exception.QueryModelBuildException;
import com.baidu.rigel.biplatform.ma.report.model.ExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.ExtendAreaType;
import com.baidu.rigel.biplatform.ma.report.model.Item;
import com.baidu.rigel.biplatform.ma.report.model.LogicModel;
import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;
import com.baidu.rigel.biplatform.ma.report.query.QueryAction;

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
        ExtendArea extendArea = new ExtendArea();
        extendArea.setType(ExtendAreaType.TABLE);
        extendArea.setCubeId("testCubeId");
        extendArea.setReferenceAreaId("testExtendAreaId");
        extendArea.setLogicModel(buildLogicModel());
        extendAreas.put("testExtendAreaId", extendArea);
        return extendAreas;
    }

    private LogicModel buildLogicModel() {
        LogicModel logicModel = new LogicModel();
        Item column = new Item();
        column.setCubeId("testCubeId");
        column.setAreaId("testExtendAreaId");
        column.setId("testDim");
        Item row = new Item();
        row.setCubeId("testCubeId");
        row.setAreaId("testExtendAreaId");
        row.setId("testMeasure");
        logicModel.addColumn(column);
        logicModel.addRow(row);
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
        dimensions.put("testDim", testDim);
        Map<String, Measure> measures = new HashMap<String, Measure>();
        MiniCubeMeasure testMeasure = new MiniCubeMeasure("testMeasure");
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
        queryAction.setExtendAreaId("testExtendAreaId");
        try {
            QueryUtils.convert2QuestionModel(dsDefine, reportModel, queryAction, requestParams, pageInfo, securityKey);
        } catch (QueryModelBuildException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    @PrepareForTest(DataSourceConnectionServiceFactory.class)
    public void testConvert2QuestionModelWhitNullCube() throws DataSourceConnectionException,
            DataSourceOperationException {
        PowerMockito.mockStatic(DataSourceConnectionServiceFactory.class);
        DataSourceConnectionService dataSourceConnectionService = PowerMockito.mock(DataSourceConnectionService.class);
        DataSourceInfo dataSourceInfo = PowerMockito.mock(DataSourceInfo.class);
        PowerMockito.when(dataSourceConnectionService.parseToDataSourceInfo(dsDefine, securityKey)).thenReturn(
                dataSourceInfo);
        PowerMockito.when(
                DataSourceConnectionServiceFactory.getDataSourceConnectionServiceInstance(DataSourceType.MYSQL.name()))
                .thenReturn(dataSourceConnectionService);
        QuestionModel questionModel = null;

        try {
            questionModel =
                    QueryUtils.convert2QuestionModel(dsDefine, reportModel, queryAction, requestParams, pageInfo,
                            securityKey);
        } catch (QueryModelBuildException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(questionModel);
    }
}
