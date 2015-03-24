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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.query.data.DataModel;
import com.baidu.rigel.biplatform.ac.query.data.HeadField;
import com.baidu.rigel.biplatform.ma.report.model.FormatModel;
import com.baidu.rigel.biplatform.ma.report.query.pivottable.ColDefine;
import com.baidu.rigel.biplatform.ma.report.query.pivottable.PivotTable;
import com.google.common.collect.Lists;

/**
 * 
 * DataModel操作工具测试类
 *
 * @author david.wang
 * @version 1.0.0.1
 */
public class DataModelUtilsTest {
    
    /**
     * 
     */
    @Test
    public void testTransDataModel2PivotTableWithNullModel() {
        try {
//            DataModelUtils.transDataModel2PivotTable(null, true, 100, true);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertNotNull(e);
        }
    }
    /**
     * 
     */
    @Test
    public void testTransDataModel2PivotTableWithHideWhiteRow() throws Exception {
        DataModel dataModel = Mockito.mock(DataModel.class);
        Cube cube = Mockito.mock (Cube.class);
        long begin = System.currentTimeMillis();
        DataModelUtils.transDataModel2PivotTable(cube, dataModel, true, 100, true);
        long end = System.currentTimeMillis() - begin;
        begin = System.currentTimeMillis();
        DataModelUtils.transDataModel2PivotTable(cube, dataModel, true, 100, false);
        long endAnohter = System.currentTimeMillis() - begin;
        Assert.assertTrue(end > endAnohter);
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testTransDataModel2PivotTableWithNullRowHeadFields() {
        try {
            DataModel dataModel = Mockito.mock(DataModel.class);
            dataModel.setRowHeadFields(null);
            Cube cube = Mockito.mock (Cube.class);
            PivotTable table = DataModelUtils.transDataModel2PivotTable(cube, dataModel, true, 100, false);
            Assert.assertTrue(table.getRowHeadFields().size() == 0);
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testTransDataModel2PivotTableWithEmptyRowHeadFields() {
        try {
            DataModel dataModel = Mockito.mock(DataModel.class);
            dataModel.setRowHeadFields(Lists.newArrayList());
            Cube cube = Mockito.mock (Cube.class);
            PivotTable table = DataModelUtils.transDataModel2PivotTable(cube, dataModel, true, 100, false);
            Assert.assertTrue(table.getRowHeadFields().size() == 0);
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testTransDataModel2PivotTableWithNullColHeadFields() {
        try {
            DataModel dataModel = Mockito.mock(DataModel.class);
            dataModel.setColumnHeadFields(null);
            Cube cube = Mockito.mock (Cube.class);
            PivotTable table = DataModelUtils.transDataModel2PivotTable(cube, dataModel, true, 100, false);
            Assert.assertTrue(table.getColFields().size() == 0);
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testTransDataModel2PivotTableWithEmptyColHeadFields() {
        try {
            DataModel dataModel = Mockito.mock(DataModel.class);
            dataModel.setColumnHeadFields(Lists.newArrayList());
            Cube cube = Mockito.mock (Cube.class);
            PivotTable table = DataModelUtils.transDataModel2PivotTable(cube, dataModel, true, 100, false);
            Assert.assertTrue(table.getColFields().size() == 0);
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }
    
    /**
     * 
     */
    @Test
    public void testTransDataModel2PivotTable() throws Exception {
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
        Cube cube = Mockito.mock (Cube.class);
        PivotTable pivotTable = DataModelUtils.transDataModel2PivotTable(cube, dataModel, true, 100, false);
        Assert.assertNotNull(pivotTable);
        Assert.assertEquals(1, pivotTable.getColFields().size());
        Assert.assertEquals(10, pivotTable.getRowHeadFields().size());
    }
    
    /**
     * 
     */
    @Test
    public void testDecorateTableWithNullTable () {
        FormatModel formatModel = new FormatModel ();
        Map<String, String> dataFormat = formatModel.getDataFormat ();
        dataFormat.put ("a", "1");
        try {
            DataModelUtils.decorateTable (formatModel, null);
            Assert.fail ();
        } catch (Exception e) {
            
        }
    }
    
    /**
     * 
     */
    @Test
    public void testDecorateTableWithNullData () {
        FormatModel formatModel = new FormatModel ();
        PivotTable table = Mockito.mock (PivotTable.class);
        Mockito.doReturn (null).when(table).getColDefine ();
        formatModel.getDataFormat ().put ("a", "1");
        try {
            DataModelUtils.decorateTable (formatModel, table);
            Assert.fail ();
        } catch (Exception e) {
        }
    }
    
    /**
     * 
     */
    @Test
    public void testDecorateTableWithEmptyFormate () {
        FormatModel formatModel = new FormatModel ();
        PivotTable table = Mockito.mock (PivotTable.class);
        Mockito.doReturn (Lists.newArrayList ()).when(table).getColDefine ();
        try {
            DataModelUtils.decorateTable (formatModel, table);
        } catch (Exception e) {
            Assert.fail ();
        }
    }
    
    /**
     * 
     */
    @Test
    public void testDecorateTable () {
        FormatModel formatModel = new FormatModel ();
        PivotTable table = new PivotTable();
        List<ColDefine> newArrayList = Lists.newLinkedList ();
        ColDefine colDefine = new ColDefine ();
        colDefine.setCaption ("test");
        colDefine.setUniqueName ("[Measures].[a]");
        newArrayList.add (colDefine);
        table.setColDefine (newArrayList);
        formatModel.getDataFormat ().put ("a", "abc");
        try {
            DataModelUtils.decorateTable (formatModel, table);
            Assert.assertEquals ("abc", table.getColDefine ().get (0).getFormat ());
        } catch (Exception e) {
            Assert.fail ();
        }
    }
    
    /**
     * 
     */
    @Test
    public void testDecorateTableWithToolTips () {
        FormatModel formatModel = new FormatModel ();
        PivotTable table = new PivotTable();
        List<ColDefine> newArrayList = Lists.newLinkedList ();
        ColDefine colDefine = new ColDefine ();
        colDefine.setCaption ("test");
        colDefine.setUniqueName ("[Measures].[a]");
        newArrayList.add (colDefine);
        table.setColDefine (newArrayList);
        formatModel.getToolTips ().put ("a", "abc");
        try {
            DataModelUtils.decorateTable (formatModel, table);
            Assert.assertEquals ("abc", table.getColDefine ().get (0).getToolTip ());
            Assert.assertNull (table.getColDefine ().get (0).getFormat ());
        } catch (Exception e) {
            Assert.fail ();
        }
    }
    
    @Test
    public void testDecorateTableWithEmptyToolTips () {
        FormatModel formatModel = new FormatModel ();
        PivotTable table = new PivotTable();
        List<ColDefine> newArrayList = Lists.newLinkedList ();
        ColDefine colDefine = new ColDefine ();
        colDefine.setCaption ("test");
        colDefine.setUniqueName ("[Measures].[a]");
        newArrayList.add (colDefine);
        table.setColDefine (newArrayList);
        try {
            DataModelUtils.decorateTable (formatModel, table);
            Assert.assertEquals ("a", table.getColDefine ().get (0).getToolTip ());
            Assert.assertNull (table.getColDefine ().get (0).getFormat ());
        } catch (Exception e) {
            Assert.fail ();
        }
    }
    
    @Test
    public void testDecorateTableWithDefaultFormat () {
        FormatModel formatModel = new FormatModel ();
        formatModel.getDataFormat ().put ("defaultFormat", "abc");
        PivotTable table = new PivotTable();
        List<ColDefine> newArrayList = Lists.newLinkedList ();
        ColDefine colDefine = new ColDefine ();
        colDefine.setCaption ("test");
        colDefine.setUniqueName ("[Measures].[a]");
        newArrayList.add (colDefine);
        table.setColDefine (newArrayList);
        try {
            DataModelUtils.decorateTable (formatModel, table);
            Assert.assertEquals ("a", table.getColDefine ().get (0).getToolTip ());
            Assert.assertEquals ("abc", table.getColDefine ().get (0).getFormat ());
        } catch (Exception e) {
            Assert.fail ();
        }
    }
    
    @Test
    public void testMergedataModelWithRowNum () {
        DataModel dataModel = Mockito.mock (DataModel.class);
        DataModel otherDataModel = Mockito.mock (DataModel.class);
        
        try {
            DataModelUtils.merageDataModel (dataModel, otherDataModel, -1);
            Assert.fail ();
        } catch (Exception e) {
        }
        
        try {
            DataModelUtils.merageDataModel (dataModel, otherDataModel, Integer.MAX_VALUE);
            Assert.fail ();
        } catch (Exception e) {
        }
        try {
            DataModelUtils.merageDataModel (dataModel, otherDataModel, dataModel.getRowHeadFields ().size ());
            Assert.fail ();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testMerageDataModelWithNullModel () {
        DataModel dataModel = Mockito.mock (DataModel.class);
        DataModel otherDataModel = Mockito.mock (DataModel.class);
        try {
            DataModelUtils.merageDataModel (null, otherDataModel, 0);
            Assert.fail ();
        } catch (Exception e) {
        }
        try {
            DataModelUtils.merageDataModel (dataModel, null, 0);
            Assert.fail ();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testMerageDataModelWithEmptyChildren () {
        DataModel dataModel = Mockito.mock (DataModel.class);
        DataModel otherDataModel = Mockito.mock (DataModel.class);
        dataModel.setRowHeadFields (null);
        try {
            DataModelUtils.merageDataModel (dataModel, otherDataModel, 0);
            Assert.fail ();
        } catch (Exception e) {
        }
        dataModel = Mockito.mock (DataModel.class);
        otherDataModel.setRowHeadFields (null);
        try {
            DataModelUtils.merageDataModel (dataModel, otherDataModel, 0);
            Assert.fail ();
        } catch (Exception e) {
        }
    }
}
