/*
 * Copyright 2000-2011 baidu.com All right reserved. 
 */
package com.baidu.rigel.biplatform.ma.model.service.impl;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeDimension;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMeasure;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeSchema;
import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.Level;
import com.baidu.rigel.biplatform.ac.model.Measure;
import com.baidu.rigel.biplatform.ac.model.Schema;
import com.baidu.rigel.biplatform.ma.PrepareModelObject4Test;
import com.baidu.rigel.biplatform.ma.auth.bo.CalMeasureViewBo;
import com.baidu.rigel.biplatform.ma.model.builder.Director;
import com.baidu.rigel.biplatform.ma.model.builder.impl.DirectorImpl;
import com.baidu.rigel.biplatform.ma.model.service.SchemaManageService;
import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 测试SchemaManagerServiceImpl
 * 
 * @author yichao.jiang 2015年6月1日 下午8:41:43
 */
public class SchemaManageServiceImplTest {

    /**
     * director
     */
    Director director = new DirectorImpl();

    /**
     * schemaManageServiceImpl
     */
    private SchemaManageService schemaManageServiceImpl = new SchemaManageServiceImpl();

    /**
     * schema
     */
    private Schema schema;

    /**
     * 立方体定义
     */
    private MiniCube miniCube;

    /**
     * 维度
     */
    private Map<String, Dimension> dimensions;

    /**
     * 指标
     */
    private Map<String, Measure> measures;

    /**
     * 
     * before
     */
    @Before
    public void before() {
        this.initAllElement();
    }

    /**
     * 初始化所有对象
     */
    private void initAllElement() {
        // 获取ReportModel对象
        ReportDesignModel model = PrepareModelObject4Test.getReportDesignModel();
        schema = model.getSchema();
        Map<String, ? extends Cube> cubes = schema.getCubes();
        // 只有一个cube
        Assert.assertEquals(1, cubes.size());

        /**
         * 获取Schema中唯一的cube
         */
        miniCube = cubes.values().toArray(new MiniCube[0])[0];
        Assert.assertNotNull(miniCube);
        // 获取维度，在该cube中，有省份、城市、季度、月份、周、日，另还有一个地区维度组
        dimensions = miniCube.getDimensions();
        Assert.assertNotNull(dimensions);
        Assert.assertEquals(7, dimensions.size());
        // 获取指标，在该cube中，有click、cash、id三个指标
        measures = miniCube.getMeasures();
        Assert.assertNotNull(measures);
        Assert.assertEquals(3, measures.size());
    }

    /**
     * 
     */
    @Test
    public void testModDimWithNull() {
        // 验证空
        Assert.assertNull(schemaManageServiceImpl.modifyDimension(null, null, null));
    }

    /**
     * 
     * testModDimCubeIdNotExis
     */
    @Test
    public void testModDimCubeIdNotExis() {
        // 获取其中一个维度
        Dimension dimension = dimensions.values().toArray(new Dimension[0])[0];
        // 使用不存在的cube的id
        Assert.assertNull(schemaManageServiceImpl.modifyDimension(schema, "id", dimension));
    }

    /**
     * 
     * testModDimWithMockDim
     */
    @Test
    public void testModDimWithMockDim() {
        // 使用Mock的维度
        MiniCubeDimension mockDim = PowerMockito.mock(MiniCubeDimension.class);
        Assert.assertNull(schemaManageServiceImpl.modifyDimension(schema, miniCube.getId(), mockDim));
    }

    /**
     * 
     * testModDimIdNotExists
     */
    @Test
    public void testModDimIdNotExists() {
        // 获取其中一个维度
        Dimension dimension = dimensions.values().toArray(new Dimension[0])[0];
        MiniCubeDimension miniCubeDim = (MiniCubeDimension) dimension;
        miniCubeDim.setId("id");
        // 维度id不存在
        Assert.assertNull(schemaManageServiceImpl.modifyDimension(schema, miniCube.getId(), miniCubeDim));
    }

    /**
     * 
     * testModDim
     */
    @Test
    public void testModDim() {
        // 获取其中一个维度
        Dimension dimension = dimensions.values().toArray(new Dimension[0])[0];
        String caption = dimension.getCaption();
        String id = dimension.getId();
        MiniCubeDimension miniCubeDim = (MiniCubeDimension) dimension;
        miniCubeDim.setCaption(caption);
        miniCubeDim.setId(id);
        // 正确修改维度
        Assert.assertEquals(schema, schemaManageServiceImpl.modifyDimension(schema, miniCube.getId(), miniCubeDim));
    }

    /**
     * 
     * testModDimWithNullCap
     */
    @Test
    public void testModDimWithNullCap() {
        // 获取其中一个维度
        Dimension dimension = dimensions.values().toArray(new Dimension[0])[0];
        MiniCubeDimension miniCubeDim = (MiniCubeDimension) dimension;
        // 测试名称为null
        miniCubeDim.setName(null);
        miniCubeDim.setCaption(null);
        Assert.assertNull(schemaManageServiceImpl.modifyDimension(schema, miniCube.getId(), miniCubeDim));

    }

    /**
     * 测试修改指标
     */
    @Test
    public void testModifyMeasure() {
        // 测试空对象
        Assert.assertNull(schemaManageServiceImpl.modifyMeasure(null, null, null));
        // 获取其中一个指标
        Measure measure = measures.values().toArray(new Measure[0])[0];
        // 使用不存在的cube的id
        Assert.assertNull(schemaManageServiceImpl.modifyMeasure(schema, "id", measure));

        // 使用Mock的指标
        Measure mockMeasure = PowerMockito.mock(Measure.class);
        Assert.assertNull(schemaManageServiceImpl.modifyMeasure(schema, miniCube.getId(), mockMeasure));

        String caption = measure.getCaption();
        String id = measure.getId();

        MiniCubeMeasure miniCubeMeasure = (MiniCubeMeasure) measure;
        miniCubeMeasure.setId("id");
        // 维度id不存在
        Assert.assertNull(schemaManageServiceImpl.modifyMeasure(schema, miniCube.getId(), miniCubeMeasure));

        miniCubeMeasure.setCaption(caption);
        miniCubeMeasure.setId(id);
        // 正确修改维度
        Assert.assertEquals(schema, schemaManageServiceImpl.modifyMeasure(schema, miniCube.getId(), miniCubeMeasure));
    }

    /**
     * 
     * test4CheckValidate
     */
    @Test
    public void test4CheckValidate() {
        // 获取其中一个指标
        Measure measure = measures.values().toArray(new Measure[0])[0];
        // schema为空
        Assert.assertNull(schemaManageServiceImpl.modifyMeasure(null, miniCube.getId(), measure));
        // cube的id为空
        Assert.assertNull(schemaManageServiceImpl.modifyMeasure(schema, null, measure));
        // measure
        Assert.assertNull(schemaManageServiceImpl.modifyMeasure(schema, miniCube.getId(), null));

    }

    /**
     * 
     * testCovMea2DimNull
     */
    @Test
    public void testCovMea2DimNull() {
        Assert.assertNull(schemaManageServiceImpl.converMeasure2Dim(null, null, null));
    }

    /**
     * 使用不存在的一些id testCovMea2DimNotExist
     */
    @Test
    public void testCovMea2DimNotExist() {
        // 获取其中一个指标
        Measure measure = measures.values().toArray(new Measure[0])[0];
        // cube的id不存在
        Assert.assertNull(schemaManageServiceImpl.converMeasure2Dim(schema, "cubeId", measure));

        MiniCubeMeasure miniMeasure = new MiniCubeMeasure("testMeasure");
        miniMeasure.setId("testId");
        // cube中不存在该指标
        Assert.assertNull(schemaManageServiceImpl.converMeasure2Dim(schema, miniCube.getId(), miniMeasure));
    }

    /**
     * 
     * testCovMea2Dim
     */
    @Test
    public void testCovMea2Dim() {
        // 指标id
        String measureId = "19383bb63e4c68512fff16430019a805";
        // 该id对应的是指标中的id列
        Measure measure = measures.get(measureId);
        // 将该指标列转为维度
        MiniCubeSchema actualSchema = 
                (MiniCubeSchema) schemaManageServiceImpl.converMeasure2Dim(schema, miniCube.getId(), measure);
        // 获取转换后的cube
        MiniCube actualCube = actualSchema.getCubes().get(miniCube.getId());
        Map<String, Dimension> actualDimensions = actualCube.getDimensions();
        Map<String, Measure> actualMeasures = actualCube.getMeasures();
        Assert.assertNotNull(actualDimensions);
        Assert.assertNotNull(actualMeasures);
        // 转换后的cube的维度中会包含此id对应的元素
        Assert.assertTrue(actualDimensions.containsKey(measureId));
        // 转换后的cube的指标中不包含此id对应的元素
        Assert.assertFalse(actualMeasures.containsKey(measureId));
    }

    /**
     * 
     * testCovDim2MeaNull
     */
    @Test
    public void testDim2MeaNull() {
        Assert.assertNull(schemaManageServiceImpl.convertDim2Measure(null, null, null));
    }

    /**
     * 使用不存在的一些id testCovDim2MeaNotExist
     */
    @Test
    public void testDim2MeaNotExist() {
        // 获取其中一个维度
        Dimension dimension = dimensions.values().toArray(new Dimension[0])[0];
        // cube的id不存在
        Assert.assertNull(schemaManageServiceImpl
                .convertDim2Measure(schema, "cubeId", dimension));

        MiniCubeDimension miniCubeDim = (MiniCubeDimension) dimension;
        miniCubeDim.setId("testId");
        // cube中不存在该指标
        Assert.assertNull(schemaManageServiceImpl.convertDim2Measure(schema, miniCube.getId(), miniCubeDim));
    }

    /**
     * 
     * testCovDim2Mea
     */
    @Test
    public void testCovDim2Mea() {
        // 维度省份对应id
        String dimensionId = "e22f271d6fa5386816e3176923835ba0";
        // 该id对应的是维度中的省份列
        Dimension dimension = dimensions.get(dimensionId);
        // 将维度转为指标
        MiniCubeSchema actualSchema = 
                (MiniCubeSchema) schemaManageServiceImpl.convertDim2Measure(schema, miniCube.getId(), dimension);
        // 获取转换后的cube
        MiniCube actualCube = actualSchema.getCubes().get(miniCube.getId());
        Map<String, Dimension> actualDimensions = actualCube.getDimensions();
        Map<String, Measure> actualMeasures = actualCube.getMeasures();
        Assert.assertNotNull(actualDimensions);
        Assert.assertNotNull(actualMeasures);
        // 转换后的cube的维度中不会包含此id对应的元素
        Assert.assertFalse(actualDimensions.containsKey(dimensionId));
        // 转换后的cube的指标中包含此id对应的元素
        Assert.assertTrue(actualMeasures.containsKey(dimensionId));
    }

    /**
     * 测试删除维度 testRemoveDim
     */
    @Test
    public void testRemoveDimNull() {
        // schema为空
        Assert.assertNull(schemaManageServiceImpl.removeDimention(null, miniCube.getId(), "dimId"));
        // cubeId为空
        Assert.assertNull(schemaManageServiceImpl.removeDimention(schema, null, "dimId"));
        // dimId为空
        Assert.assertNull(schemaManageServiceImpl.removeDimention(schema, miniCube.getId(), null));
        // cubeId不存在
        Assert.assertNull(schemaManageServiceImpl.removeDimention(schema, "cubeId", "dimId"));
        // dimId在cube中不存在
        Assert.assertNull(schemaManageServiceImpl.removeDimention(schema, miniCube.getId(), "dimId"));
    }

    /**
     * 
     * testRemoveDim
     */
    @Test
    public void testRemoveDim() {
        // 维度省份对应列
        String dimensionId = "e22f271d6fa5386816e3176923835ba0";
        // 将维度转为指标
        MiniCubeSchema actualSchema = 
                (MiniCubeSchema) schemaManageServiceImpl.removeDimention(schema, miniCube.getId(), dimensionId);
        // 获取转换后的cube
        MiniCube actualCube = actualSchema.getCubes().get(miniCube.getId());
        Map<String, Dimension> actualDimensions = actualCube.getDimensions();
        Assert.assertNotNull(actualDimensions);
        // 转换后的cube的维度中不会包含此id对应的元素
        Assert.assertFalse(actualDimensions.containsKey(dimensionId));
    }

    /**
     * 
     * testAddDimGroupNull
     */
    @Test
    public void testAddDimGroupNull() {
        // schema为空
        Assert.assertNull(schemaManageServiceImpl.addDimIntoDimGroup(null,
                miniCube.getId(), "groupId", "dimId"));
        // cubeId为空
        Assert.assertNull(schemaManageServiceImpl.addDimIntoDimGroup(schema, null, "dimId"));
        // groupId为空
        Assert.assertNull(schemaManageServiceImpl.addDimIntoDimGroup(schema,
                miniCube.getId(), null, "dimId"));

        // dimId为空
        Assert.assertNull(schemaManageServiceImpl.addDimIntoDimGroup(schema,
                miniCube.getId(), "groupId"));

        // cubeId不存在
        Assert.assertNull(schemaManageServiceImpl.addDimIntoDimGroup(schema,
                "cubeId", "groupId", "dimId"));
        // dimId在cube中不存在
        Assert.assertNull(schemaManageServiceImpl.addDimIntoDimGroup(schema,
                miniCube.getId(), "groupId", "dimId"));
    }

    /**
     * 
     * testAddDimIntoGroup
     */
    @Test
    public void testAddDimIntoGroup() {
        // 地区维度组id
        String districtDimGroupId = "897a34e21ccf71fd2d04db8c108dd035";
        // 天维度id
        String dayDimId = "3da5f26e1ec5244c5b0cdbf4ced9ac73";
        // 将天维度添加到地区维度组
        MiniCubeSchema actualSchema = 
                (MiniCubeSchema) schemaManageServiceImpl
                .addDimIntoDimGroup(schema, miniCube.getId(), districtDimGroupId, dayDimId);
        // 获取转换后的cube
        MiniCube actualCube = actualSchema.getCubes().get(miniCube.getId());
        Map<String, Dimension> actualDimensions = actualCube.getDimensions();
        Assert.assertNotNull(actualDimensions);
        // 获取该维度组
        Dimension actualDim = actualDimensions.get(districtDimGroupId);
        Assert.assertNotNull(actualDim);
        Map<String, Level> levels = actualDim.getLevels();
        // 当前该维度组中有三个level，省份、城市、天
        Assert.assertEquals(3, levels.size());
    }

    /**
     * 
     * testCreateDimGroup
     */
    @Test
    public void testCreateDimGroupNull() {
        // schema为空
        Assert.assertNull(schemaManageServiceImpl.createDimGroup(null,
                miniCube.getId(), "groupId"));
        // cube为空
        Assert.assertNull(schemaManageServiceImpl.createDimGroup(schema,
                "cubeId", "groupId"));
    }

    /**
     * 
     * testCreateDimGroup
     */
    @Test
    public void testCreateDimGroup() {
        // 维度组id
        String groupName = "groupId";
        // 创建新的维度组
        MiniCubeSchema actualSchema = 
                (MiniCubeSchema) schemaManageServiceImpl.createDimGroup(schema, miniCube.getId(), groupName);
        // 获取转换后的cube
        MiniCube actualCube = actualSchema.getCubes().get(miniCube.getId());
        Map<String, Dimension> actualDimensions = actualCube.getDimensions();
        Assert.assertNotNull(actualDimensions);
        // 获取该维度组
        Dimension groupDim = null;
        for (String key : actualDimensions.keySet()) {
            Dimension dim = actualDimensions.get(key);
            if (dim.getName().equals(groupName)) {
                groupDim = dim;
            }
        }
        Assert.assertNotNull(groupDim);
        Map<String, Level> levels = groupDim.getLevels();
        // 当前该维度组中没有level
        Assert.assertEquals(0, levels.size());
    }

    /**
     * 
     * testRemoveDimFromGroup
     */
    @Test
    public void testRemoveDimFromGroupNull() {
        // schema为空
        Assert.assertNull(schemaManageServiceImpl.removeDimFromGroup(null,
                miniCube.getId(), "groupId", "dimId"));
        // cube
        Assert.assertNull(schemaManageServiceImpl.removeDimFromGroup(schema, miniCube.getId(), "groupId", "dimId"));
    }

    /**
     * 
     * testRemoveDimFromGroup
     */
    @Test
    public void testRemoveDimFromGroup() {
        // 地区维度组id
        String districtDimGroupId = "897a34e21ccf71fd2d04db8c108dd035";
        String provinceLevelId = "30acd0919e395c25705c7aecdd1d8c5f";
        // 将天维度添加到地区维度组
        MiniCubeSchema actualSchema = 
                (MiniCubeSchema) schemaManageServiceImpl
                .removeDimFromGroup(schema, miniCube.getId(), districtDimGroupId, provinceLevelId);
        // 获取转换后的cube
        MiniCube actualCube = actualSchema.getCubes().get(miniCube.getId());
        Map<String, Dimension> actualDimensions = actualCube.getDimensions();
        Assert.assertNotNull(actualDimensions);
        // 获取该维度组
        Dimension actualDim = actualDimensions.get(districtDimGroupId);
        Assert.assertNotNull(actualDim);
        Map<String, Level> levels = actualDim.getLevels();
        // 当前该维度组中有1个level，城市
        Assert.assertEquals(1, levels.size());
    }

    /**
     * 
     * testModIndAndImVisibleNull
     */
    @Test
    public void testModIndAndImVisibleNull() {
        // schema为空
        Assert.assertNull(schemaManageServiceImpl.modifyIndAndDimVisibility(null,
                miniCube.getId(), Lists.newArrayList(), Lists.newArrayList()));
        // cube不存在
        Assert.assertNull(schemaManageServiceImpl
                .modifyIndAndDimVisibility(schema, "cubeId", Lists.newArrayList(), Lists.newArrayList()));
        // 未做修改
        Assert.assertEquals(schema, 
                schemaManageServiceImpl.modifyIndAndDimVisibility(schema, 
                        miniCube.getId(), Lists.newArrayList(), Lists.newArrayList()));
    }

    /**
     * 
     * testModIndAndDimVisible
     */
    @Test
    public void testModIndAndDimVisible() {
        // 维度省份对应id
        String dimensionId = "e22f271d6fa5386816e3176923835ba0";
        // 该id对应的是维度中的省份列
        Dimension dimension = dimensions.get(dimensionId);

        // 指标id
        String measureId = "19383bb63e4c68512fff16430019a805";
        // 该id对应的是指标中的id列
        Measure measure = measures.get(measureId);

        List<Dimension> dimensionList = Lists.newArrayList();
        dimensionList.add(dimension);
        List<Measure> measureList = Lists.newArrayList();
        measureList.add(measure);

        MiniCubeSchema actualSchema = 
                (MiniCubeSchema) schemaManageServiceImpl
                .modifyIndAndDimVisibility(schema, miniCube.getId(), dimensionList, measureList);
        // 获取转换后的cube
        MiniCube actualCube = actualSchema.getCubes().get(miniCube.getId());
        // TODO 未做修改？？？？
        // 修改了维度的visible
        Dimension actualDim = actualCube.getDimensions().get(dimensionId);
        Assert.assertEquals(dimension.isVisible(), actualDim.isVisible());

        // 修改了指标的visible
        Measure actualMeasure = actualCube.getMeasures().get(measureId);
        Assert.assertEquals(measure.isVisible(), actualMeasure.isVisible());
    }

    /**
     * 
     * testModDimOrderNull
     */
    @Test
    public void testModDimOrderNull() {
        // 维度省份对应id
        String dimensionId = "e22f271d6fa5386816e3176923835ba0";
        // schema为空
        Assert.assertNull(schemaManageServiceImpl.modifyDimOrder(null,
                miniCube.getId(), dimensionId, "-1", "1"));
        // cube不存在
        Assert.assertNull(schemaManageServiceImpl
                .modifyDimOrder(schema, "cubeId", dimensionId, "-1", "1"));

        // dimensionId不存在
        Assert.assertNull(schemaManageServiceImpl
                .modifyDimOrder(schema, miniCube.getId(), "dimensionId", "-1", "1"));

        // Target level不存在
        Assert.assertNull(schemaManageServiceImpl
                .modifyDimOrder(schema, miniCube.getId(), dimensionId, "-1", "1"));
    }

    /**
     * 
     * testModDimeOrder
     */
    @Test
    public void testModDimeOrder() {
        // 地区维度组id
        String districtDimGroupId = "897a34e21ccf71fd2d04db8c108dd035";
        String provinceLevelId = "30acd0919e395c25705c7aecdd1d8c5f";
        String cityLevelId = "b057e514f91e5cfc420433f6cd076fb4";
        Dimension dimension = miniCube.getDimensions().get(districtDimGroupId);
        Map<String, Level> levels = dimension.getLevels();
        
        MiniCubeSchema actualSchema = 
                (MiniCubeSchema) schemaManageServiceImpl
                .modifyDimOrder(schema, miniCube.getId(), districtDimGroupId, "-1", provinceLevelId);
        MiniCube actualCube = actualSchema.getCubes().get(miniCube.getId());
        Dimension actualDim = actualCube.getDimensions().get(districtDimGroupId);
        Map<String, Level> actualLevels = actualDim.getLevels();
        // 如果为before为-1，则将target level添加为第一个level
        Assert.assertEquals(2, actualLevels.size());
        
        actualSchema =
                (MiniCubeSchema) schemaManageServiceImpl
                .modifyDimOrder(schema, miniCube.getId(), districtDimGroupId, cityLevelId, provinceLevelId);
        actualCube = actualSchema.getCubes().get(miniCube.getId());
        actualDim = actualCube.getDimensions().get(districtDimGroupId);
        actualLevels = actualDim.getLevels();
        
        // 此时相当于将省份和城市的level进行互换
        Assert.assertEquals(2, actualLevels.size());
        // level值保持不变
        Assert.assertEquals(levels.get(provinceLevelId), actualLevels.get(provinceLevelId));
        Assert.assertEquals(levels.get(cityLevelId), actualLevels.get(cityLevelId));        
    }
    
    /**
     * 
     * testDelExtendMeasureNull
     */
    @Test
    public void testDelExtendMeasureNull() {
        // schema为null
        try {
            schemaManageServiceImpl.delExtendMeasure(null, "cubeId", "measureId"); 
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
        // cube不存在
        try {
            schemaManageServiceImpl.delExtendMeasure(schema, "cubeId", "measureId");            
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
        // 设置指标为null
        miniCube.setMeasures(null);
        try {
            schemaManageServiceImpl
                .delExtendMeasure(schema, miniCube.getId(), "measureId");
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
        // 设置指标为empty
        miniCube.setMeasures(Maps.newHashMap());
        try {
            schemaManageServiceImpl
                .delExtendMeasure(schema, miniCube.getId(), "measureId");
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
        this.initAllElement();
    }
    
    /**
     * 
     * testDelExtendMeasure
     */
    @Test
    public void testDelExtendMeasure()  {
        // id对应的指标列
        String measureId = "19383bb63e4c68512fff16430019a805";
        MiniCubeSchema actualSchema = 
                (MiniCubeSchema) schemaManageServiceImpl.delExtendMeasure(schema, miniCube.getId(), measureId);
        MiniCube actualMiniCube = actualSchema.getCubes().get(miniCube.getId());
        Map<String, Measure> actualMeasures = actualMiniCube.getMeasures();
        Assert.assertNotNull(actualMeasures);
        Assert.assertFalse(actualMeasures.containsKey(measureId));
    }
    
    /**
     * 
     * testAddOrModifyExtendMeasureNull
     */
    @Test
    public void testAddOrModifyExtendMeasureNull() {
        Assert.assertNull(schemaManageServiceImpl.addOrModifyExtendMeasure(schema, miniCube.getId(), null));
    }
    
    /**
     * 
     * testAddOrModifyExtendMeasure
     */
    @Test
    public void testAddOrModifyExtendMeasure() {
        CalMeasureViewBo calMeasureViewBo = this.buildCalColumn();
        MiniCubeSchema actualSchema = 
                (MiniCubeSchema) schemaManageServiceImpl.addOrModifyExtendMeasure(schema, 
                        miniCube.getId(), calMeasureViewBo);
        MiniCube actualMiniCube = actualSchema.getCubes().get(miniCube.getId());
        Map<String, Measure> actualMeasures = actualMiniCube.getMeasures();
        // 原有三个指标+4个计算指标
        Assert.assertEquals(7, actualMeasures.size());
    }
    
    /**
     * 构建三个计算列，包括2*CLICK,CLICK_rr(环比指标), CLICK_sr(同比指标),callback指标
     * buildCalColumn
     * @return
     */
    private CalMeasureViewBo buildCalColumn() {
        String json = "{'extendInds':{'rr':[{'id':'',"
                + "'name':'CLICK_rr','caption':'CLICK_rr'}],"
                + "'sr':[{'id':'','name':'CLICK_sr','caption':'CLICK_sr'}]},"
                + "'calDeriveInds':[{'id':'','name':'','caption':'2*CLICK','formula':'2*${CLICK}'}],"
                + "'callback':[{'id':'',"
                + "'name':'callback_measure','caption':'callback_measure',"
                + "'url':'http://test_call_back?','properties':{'timeOut':'1000'}}]}";
        return CalMeasureViewBo.fromJson(json);
    }
}
