package com.baidu.rigel.biplatform.ma.report.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.Schema;
import com.baidu.rigel.biplatform.ma.report.model.ExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.ExtendAreaType;
import com.baidu.rigel.biplatform.ma.report.model.Item;
import com.baidu.rigel.biplatform.ma.report.model.LinkInfo;
import com.baidu.rigel.biplatform.ma.report.model.LinkParams;
import com.baidu.rigel.biplatform.ma.report.model.LogicModel;
import com.baidu.rigel.biplatform.ma.report.model.PlaneTableCondition;
import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;
import com.baidu.rigel.biplatform.ma.report.query.QueryContext;
import com.baidu.rigel.biplatform.ma.report.service.OlapLinkService;
import com.baidu.rigel.biplatform.ma.report.service.ReportDesignModelService;

/**
 * OlapLinkServiceImpl单测
 * 
 * @author majun04
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/test/resources/applicationContext.xml")
public class OlapLinkServiceImplTest {

    @Resource
    @InjectMocks
    private OlapLinkService olapLinkService;
    @Mock
    private ReportDesignModelService reportDesignModelService;
    @Mock
    private ReportDesignModel mockReportDesignModel;
    @Mock
    private ExtendArea mockExtendArea;
    @Mock
    private Item mockItem;
    @Mock
    private MiniCube mockCube;
    @Mock
    private Dimension mockDim;
    @Mock
    private LogicModel logicModel;
    @Mock
    private Schema mockSchema;
    @Mock
    private Map<String, ? extends Cube> mockCubes;
    @Mock
    private Map<String, Dimension> mockDimensions;
    @Mock
    private QueryContext queryContext;

    private static final String UNIQUE_NAME =
            "{[dim_trade_t_trade_name_l1].[食品餐饮]}.{[dim_trade_t_trade_name_l2].[All_dim_trade_t_trade_name_l2s]}";

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetDesignModelListContainsPlaneTable() {
        ReportDesignModel[] mockArrays = new ReportDesignModel[2];
        mockArrays[0] = mockReportDesignModel;
        mockArrays[1] = mockReportDesignModel;
        ExtendArea[] mockExtendAreaArrays = new ExtendArea[1];
        mockExtendAreaArrays[0] = mockExtendArea;
        Mockito.when(reportDesignModelService.queryAllModels(true)).thenReturn(mockArrays);
        Mockito.when(mockReportDesignModel.getExtendAreaList()).thenReturn(mockExtendAreaArrays);
        Mockito.when(mockExtendArea.getType()).thenReturn(ExtendAreaType.PLANE_TABLE);

        List<ReportDesignModel> ls = olapLinkService.getDesignModelListContainsPlaneTable();
        Assert.assertEquals(ls.size(), 2);
    }

    @Test
    public void testGetPlaneTableConditionList() {
        PlaneTableCondition condition = new PlaneTableCondition();
        condition.setElementId("testElementId");
        condition.setName("dim_pos");
        Map<String, PlaneTableCondition> conditionMap = new HashMap<String, PlaneTableCondition>();
        conditionMap.put("testKey", condition);
        Mockito.when(mockReportDesignModel.getPlaneTableConditions()).thenReturn(conditionMap);
        List<String> planeTableParamNameList = olapLinkService.getPlaneTableConditionList(mockReportDesignModel);
        Assert.assertEquals(planeTableParamNameList.get(0), "dim_pos");
    }

    @Test
    public void testGetOlapDims() {
        ExtendArea[] extendAreaArray = new ExtendArea[1];
        extendAreaArray[0] = mockExtendArea;
        Map<String, Item> allItems = new HashMap<String, Item>();
        allItems.put("testOlapElementId", mockItem);
        Mockito.when(mockExtendArea.getType()).thenReturn(ExtendAreaType.SELECT);
        Mockito.when(mockExtendArea.listAllItems()).thenReturn(allItems);
        Mockito.when(mockReportDesignModel.getExtendAreaList()).thenReturn(extendAreaArray);

        Mockito.doReturn(mockSchema).when(mockReportDesignModel).getSchema();
        Mockito.doReturn(mockCubes).when(mockSchema).getCubes();
        Mockito.doReturn(mockCube).when(mockCubes).get(Mockito.anyString());
        // Mockito.doReturn(mockCube).when(mockReportDesignModel).getSchema().getCubes().get(Mockito.anyString());
        Mockito.doReturn(mockDimensions).when(mockCube).getDimensions();
        Mockito.doReturn(mockDim).when(mockDimensions).get(Mockito.anyString());
        // Mockito.when(mockCube.getDimensions().get(Mockito.anyString())).thenReturn(mockDim);
        Mockito.when(mockExtendArea.getLogicModel()).thenReturn(logicModel);

        Item[] items = new Item[1];
        items[0] = mockItem;
        Mockito.when(logicModel.getRows()).thenReturn(items);
        Mockito.when(logicModel.getSlices()).thenReturn(items);
        List<Dimension> dimList = olapLinkService.getOlapDims(mockReportDesignModel, mockExtendArea);
        Assert.assertEquals(dimList.size(), 1);
    }

    @Test
    public void testBuildConditionMapFromRequestParams() {
        ExtendArea[] extendAreaArray = new ExtendArea[1];
        extendAreaArray[0] = mockExtendArea;
        Item[] items = new Item[1];
        items[0] = mockItem;
        Mockito.when(mockExtendArea.getType()).thenReturn(ExtendAreaType.SELECT);
        Mockito.when(mockReportDesignModel.getExtendAreaList()).thenReturn(extendAreaArray);
        Mockito.doReturn(mockSchema).when(mockReportDesignModel).getSchema();
        Mockito.when(logicModel.getRows()).thenReturn(items);
        Mockito.when(mockItem.getOlapElementId()).thenReturn("testOlapElementId");
        Mockito.when(queryContext.get("testOlapElementId")).thenReturn("testCondDimValue");
        Mockito.doReturn(mockSchema).when(mockReportDesignModel).getSchema();
        Mockito.doReturn(mockCubes).when(mockSchema).getCubes();
        Mockito.doReturn(mockCube).when(mockCubes).get(Mockito.anyString());
        Mockito.doReturn(mockDimensions).when(mockCube).getDimensions();
        Mockito.doReturn(mockDim).when(mockDimensions).get(Mockito.anyString());
        Mockito.when(mockExtendArea.getLogicModel()).thenReturn(logicModel);
        Map<String, Map<String, String>> conditionMap =
                olapLinkService.buildConditionMapFromRequestParams(UNIQUE_NAME, mockReportDesignModel, queryContext);
        Assert.assertEquals(conditionMap.get("dim_trade_t_trade_name_l1").get("uniqueName"),
                "[dim_trade_t_trade_name_l1].[食品餐饮]");
    }

    @Test
    public void testBuildLinkBridgeParams() {
        ExtendArea[] extendAreaArray = new ExtendArea[1];
        extendAreaArray[0] = mockExtendArea;
        Item[] items = new Item[1];
        items[0] = mockItem;
        Mockito.when(mockExtendArea.getType()).thenReturn(ExtendAreaType.SELECT);
        Mockito.when(mockReportDesignModel.getExtendAreaList()).thenReturn(extendAreaArray);
        Mockito.doReturn(mockSchema).when(mockReportDesignModel).getSchema();
        Mockito.when(logicModel.getRows()).thenReturn(items);
        Mockito.when(mockItem.getOlapElementId()).thenReturn("testOlapElementId");
        Mockito.when(queryContext.get("testOlapElementId")).thenReturn("testCondDimValue");
        Mockito.doReturn(mockSchema).when(mockReportDesignModel).getSchema();
        Mockito.doReturn(mockCubes).when(mockSchema).getCubes();
        Mockito.doReturn(mockCube).when(mockCubes).get(Mockito.anyString());
        Mockito.doReturn(mockDimensions).when(mockCube).getDimensions();
        Mockito.doReturn(mockDim).when(mockDimensions).get(Mockito.anyString());
        Mockito.when(mockExtendArea.getLogicModel()).thenReturn(logicModel);
        Mockito.when(logicModel.getSlices()).thenReturn(items);
        LinkInfo linkInfo = new LinkInfo();
        Map<String, String> paramMapping = new HashMap<String, String>();
        paramMapping.put("trade_name_l1", "dim_trade_t_trade_name_l1");
        paramMapping.put("trade_name_l2", "dim_trade_t_trade_name_l2");
        linkInfo.setParamMapping(paramMapping);
        Map<String, Map<String, String>> conditionMap =
                olapLinkService.buildConditionMapFromRequestParams(UNIQUE_NAME, mockReportDesignModel, queryContext);
        Map<String, LinkParams> linkBridgeParams = olapLinkService.buildLinkBridgeParams(linkInfo, conditionMap);
        Assert.assertEquals(linkBridgeParams.get("trade_name_l1").getOriginalDimValue(), "食品餐饮");
    }
}
