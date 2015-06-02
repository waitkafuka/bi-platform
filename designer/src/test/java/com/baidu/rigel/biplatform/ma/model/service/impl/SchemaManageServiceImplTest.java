/*
 * Copyright 2000-2011 baidu.com All right reserved. 
 */
package com.baidu.rigel.biplatform.ma.model.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.rigel.biplatform.ac.minicube.ExtendMinicubeMeasure;
import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeDimension;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMeasure;
import com.baidu.rigel.biplatform.ac.minicube.StandardDimension;
import com.baidu.rigel.biplatform.ac.model.Aggregator;
import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.DimensionType;
import com.baidu.rigel.biplatform.ac.model.Measure;
import com.baidu.rigel.biplatform.ac.model.MeasureType;
import com.baidu.rigel.biplatform.ac.model.Schema;
import com.baidu.rigel.biplatform.ma.model.builder.Director;
import com.baidu.rigel.biplatform.ma.model.builder.impl.DirectorImpl;
import com.baidu.rigel.biplatform.ma.model.meta.ColumnMetaDefine;
import com.baidu.rigel.biplatform.ma.model.meta.DimTableMetaDefine;
import com.baidu.rigel.biplatform.ma.model.meta.FactTableMetaDefine;
import com.baidu.rigel.biplatform.ma.model.meta.ReferenceDefine;
import com.baidu.rigel.biplatform.ma.model.meta.StandardDimTableMetaDefine;
import com.baidu.rigel.biplatform.ma.model.meta.StarModel;
import com.baidu.rigel.biplatform.ma.model.service.SchemaManageService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * 测试SchemaManagerServiceImpl
 * @author yichao.jiang 2015年6月1日 下午8:41:43
 */
public class SchemaManageServiceImplTest {

    /**
     * 日志对象
     */
    private static final Logger LOG = LoggerFactory.getLogger(SchemaManageServiceImplTest.class);
    
    /**
     * 
     */
    Director director = new DirectorImpl();
    
    /**
     * 
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
    private Dimension dimension;
    
    /**
     * 指标
     */
    private Measure measure;
    
     
    /**
     * 
     */
    @Test
    public void testModifyDimension() {
        
        /**
         * 初始化模型
         */
        this.initAllElement();
        // 验证空
        Assert.assertNull(schemaManageServiceImpl.modifyDimension(null, null, null));
        
        // 使用不存在的cube的id
        Assert.assertNull(schemaManageServiceImpl.modifyDimension(schema, "id", dimension));
        // 使用Mock的维度
        MiniCubeDimension mockDim = PowerMockito.mock(MiniCubeDimension.class);
        Assert.assertNull(schemaManageServiceImpl.modifyDimension(schema, miniCube.getId(), mockDim));
        
        
        String caption = dimension.getCaption(); 
        String id = dimension.getId();
        
        MiniCubeDimension miniCubeDim = (MiniCubeDimension) dimension;
        miniCubeDim.setId("id");
        // 维度id不存在
        Assert.assertNull(schemaManageServiceImpl.modifyDimension(schema, miniCube.getId(), miniCubeDim));
        
        miniCubeDim.setCaption(caption);
        miniCubeDim.setId(id);
        // 正确修改维度
        Assert.assertEquals(schema, schemaManageServiceImpl.modifyDimension(schema, miniCube.getId(), miniCubeDim));
    }
    
    /**
     * 测试修改指标
     */
    @Test
    public void testModifyMeasure() {
        // 初始化模型
        this.initAllElement();
        // 测试空对象
        Assert.assertNull(schemaManageServiceImpl.modifyMeasure(null, null, null));
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
     * 初始化所有对象
     */
    private void initAllElement() {
        // 获取Schema对象
        schema = this.genSchema();
        Map<String, ? extends Cube> cubes = schema.getCubes();
        // 假定的schema中仅有一个cube, cube中仅有一个维度，一个指标
        Assert.assertEquals(1, cubes.size());

        /**
         * 获取Schema中唯一的cube
         */
        miniCube = cubes.values().toArray(new MiniCube[0])[0];
        Assert.assertNotNull(miniCube);
        dimension = miniCube.getDimensions().values().toArray(new Dimension[0])[0];
        Assert.assertNotNull(dimension);
        measure = miniCube.getMeasures().values().toArray(new Measure[0])[0];
        Assert.assertNotNull(measure);
    }
    /**
     * 产生schema对象
     * 
     * @return
     */
    private Schema genSchema() {
        StarModel starModel = genStarModel();

        StarModel[] starModels = new StarModel[] { starModel };
        Schema schema = director.getSchema(starModels);
        return schema;
    }

    /**
     * 构建星型模型
     * 
     * @return
     */
    private StarModel genStarModel() {
        StarModel starModel = new StarModel();
        starModel.setDsId("test");
        // 建立事实表
        FactTableMetaDefine factTable = new FactTableMetaDefine();
        factTable.setName("fact");
        // 建立事实表的第一列
        ColumnMetaDefine abc = new ColumnMetaDefine();
        abc.setName("fact_abc");
        factTable.addColumn(abc);
        // 建立事实表的第二列
        ColumnMetaDefine def = new ColumnMetaDefine();
        def.setName("fact_def");
        factTable.addColumn(def);
        
        // 设置starModel的事实表
        starModel.setFactTable(factTable);

        // 建立时间维度表
        List<DimTableMetaDefine> dimTables = this.buildBaseDimTables();
        // 设置维度表
        starModel.setDimTables(dimTables);
        
        // 设置星型模型的维度表
        starModel.setDimTables(dimTables);
        return starModel;
    }

//    /**
//     * 构建回调维度
//     * 
//     * @return
//     */
//    private List<DimTableMetaDefine> buildCallbackDimTable() {
//        List<DimTableMetaDefine> callbackDimTable = Lists.newArrayList();
//        // 建立callback回调维度
//        CallbackDimTableMetaDefine callBackDim = new CallbackDimTableMetaDefine();
//        callBackDim.setName("call_back");
//        callBackDim.setUrl("http://host:port/callbackService");
//        ColumnMetaDefine callBackCol = new ColumnMetaDefine();
//        callBackCol.setName("fact_callback");
//        callBackCol.setCaption("fact_callback");
//        callBackDim.addColumn(callBackCol);
//        // 构建回调维度对应的引用列
//        ReferenceDefine callbackRef = new ReferenceDefine();
//        callbackRef.setMajorColumn("fact_callback");
//        callbackRef.setMajorTable("fact");
//        callbackRef.setSalveColumn("gen_call_back_id");
//        callBackDim.setReference(callbackRef);
//        callbackDimTable.add(callBackDim);
//        return callbackDimTable;
//    }
//
//    /**
//     * 构建时间维度表
//     * 
//     * @return
//     */
//    private List<DimTableMetaDefine> buildDimTablesWithTimeDim() {
//        List<DimTableMetaDefine> dimTables = Lists.newArrayList();
//        // 时间维度表
//        TimeDimTableMetaDefine timeDimTable = new TimeDimTableMetaDefine(
//                TimeDimType.STANDARD_TIME);
//        ColumnMetaDefine timeId = new ColumnMetaDefine();
//        timeId.setName(TimeType.TimeMonth.toString());
//        timeId.setCaption(TimeType.TimeMonth.toString());
//        timeDimTable.addColumn(timeId);
//        timeDimTable.setName("time");
//        // 构建时间维度对应的事实表引用列
//        ReferenceDefine timeReference = new ReferenceDefine();
//        timeReference.setMajorColumn("fact_time_id");
//        timeReference.setMajorTable("fact");
//        timeReference.setSalveColumn(TimeType.TimeMonth.toString());
//        timeDimTable.setReference(timeReference);
//        dimTables.add(timeDimTable);
//        return dimTables;
//    }

    /**
     * 构建基本维度表
     * 
     * @return
     */
    private List<DimTableMetaDefine> buildBaseDimTables() {
        List<DimTableMetaDefine> dimTables = Lists.newArrayList();
        DimTableMetaDefine dimTable = new StandardDimTableMetaDefine();
        dimTable.setName("dim_table");
        ColumnMetaDefine dimColumn = new ColumnMetaDefine();
        dimColumn.setName("dim_abc");
        dimColumn.setCaption("dim_abc");
        dimTable.addColumn(dimColumn);
        ColumnMetaDefine anotherDimCol = new ColumnMetaDefine();
        anotherDimCol.setName("dim_def");
        anotherDimCol.setCaption("dim_def");
        dimTable.addColumn(anotherDimCol);
        // 建立引用关系
        ReferenceDefine reference = new ReferenceDefine();
        reference.setMajorColumn("fact_abc");
        reference.setMajorTable("fact");
        reference.setSalveColumn("dim_abc");
        dimTable.setReference(reference);
        dimTables.add(dimTable);
        return dimTables;
    }

//    /**
//     * 构建用户自定义维度表
//     * 
//     * @return
//     */
//    private List<DimTableMetaDefine> buildUserDefineDimTableMetaDefine() {
//        List<DimTableMetaDefine> dimTable = Lists.newArrayList();
//        UserDefineDimTableMetaDefine ud = new UserDefineDimTableMetaDefine();
//        ColumnMetaDefine dimColumn = new ColumnMetaDefine();
//        dimColumn.setName("dim_user");
//        dimColumn.setCaption("dim_user");
//        ud.setSourceType(DimSourceType.SQL);
//        ud.addColumn(dimColumn);
//        ReferenceDefine reference = new ReferenceDefine();
//        reference.setMajorColumn("fact_user");
//        reference.setMajorTable("fact");
//        reference.setSalveColumn("dim_user");
//        ud.setReference(reference);
//        ud.setParams(Maps.newHashMap());
//        dimTable.add(ud);
//        return dimTable;
//    }


    /**
     * 建立新的Cube
     */
    private void buildNewCube(Map<String, ? extends Cube> cubes) {
        for(Cube cube :cubes.values()) {
            MiniCube miniCube = (MiniCube) cube;
            Map<String, Measure> measures= miniCube.getMeasures();
            for (Measure measure : measures.values()) {
                
                MiniCubeMeasure miniMeasure = (MiniCubeMeasure) measure;
                miniMeasure.setAggregator(Aggregator.COUNT);
            }
            
            // 增加扩展指标
            ExtendMinicubeMeasure extendMeasure = new ExtendMinicubeMeasure("CAL");
            extendMeasure.setAggregator(Aggregator.COUNT);
            extendMeasure.setId("id");
            extendMeasure.setCaption("CAL");
            extendMeasure.setType(MeasureType.CAL);
            extendMeasure.setVisible(true);
            Set<String> refIndName = Sets.newHashSet();
            refIndName.add("fact_def");
            extendMeasure.setRefIndNames(refIndName);
            measures.put("id", extendMeasure);
            miniCube.setMeasures(measures);
            
            // 修改维度为维度组
            Map<String, Dimension> dimensions = miniCube.getDimensions();
            for (Dimension dimension : dimensions.values()) {
                switch (dimension.getType()) {
                case STANDARD_DIMENSION:
                    StandardDimension standardDimension = (StandardDimension) dimension;
                    standardDimension.setType(DimensionType.GROUP_DIMENSION);
                    break;
                case TIME_DIMENSION:
                case CALLBACK:
                case GROUP_DIMENSION:
                    break;
                default:
                    break;
                }
            }
            miniCube.setDimensions(dimensions);
        }
    }
}
