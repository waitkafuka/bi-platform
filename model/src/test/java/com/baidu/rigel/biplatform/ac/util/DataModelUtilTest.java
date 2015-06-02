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
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.rigel.biplatform.ac.query.data.DataModel;
import com.baidu.rigel.biplatform.ac.query.data.DataModel.FillDataType;
import com.baidu.rigel.biplatform.ac.query.data.HeadField;
import com.baidu.rigel.biplatform.ac.query.model.SortRecord;
import com.baidu.rigel.biplatform.ac.query.model.SortRecord.SortType;
import com.google.common.collect.Lists;

/**
 * Description:
 * 
 * @author david.wang
 *
 */
public class DataModelUtilTest {
    
    @Test
    public void testFieldData () {
        try {
            DataModel dataModel = genDataModel ();
            DataModelUtils.fillFieldData (dataModel, FillDataType.COLUMN);
            DataModelUtils.fillFieldData (dataModel, FillDataType.ROW);
        } catch (Exception e) {
            Assert.fail ();
        }
    }
    
    @Test
    public void testFilterBlankRow () {
        DataModel dataModel = genDataModel ();
        DataModelUtils.filterBlankRow (dataModel);
        for (int i = 0; i < dataModel.getColumnBaseData ().size (); ++i) {
            dataModel.getColumnBaseData ().get (i).set (3, null);
        }
        DataModelUtils.filterBlankRow (dataModel);
        Assert.assertEquals (10, dataModel.getColumnBaseData ().size ());
    }
    
    @Test
    public void testPaseNodeUniqueNameToUniqueName () {
        try {
            DataModelUtils.parseNodeUniqueNameToNodeValueArray (null);
            Assert.fail ();
        } catch (Exception e) {
            
        }
        String nodeUniqueName = "{[dim].[test]}.{[dim1].[test]}";
        String[] rs = DataModelUtils.parseNodeUniqueNameToNodeValueArray (nodeUniqueName);
        Assert.assertEquals (2, rs.length);
    }
    @Test
    public void testSortDataModelBySort () {
        DataModel dataModel = genDataModel ();
        DataModelUtils.sortDataModelBySort (dataModel, null);
        SortRecord r = new SortRecord (SortType.NONE, null, 5);
        DataModelUtils.sortDataModelBySort (dataModel, r);
        r = new SortRecord (SortType.ASC, "[Measure].[column_1]", 5);
        DataModelUtils.sortDataModelBySort (dataModel, r);
        r = new SortRecord (SortType.ASC, "[Measures].[column_1]", 5);
        DataModelUtils.sortDataModelBySort (dataModel, r);
        Assert.assertEquals (10, dataModel.getColumnBaseData ().size ());
    }
    
    @Test
    public void testTruncModel() {
        DataModel model = genDataModel ();
        model = DataModelUtils.truncModel (model, 1000);
        Assert.assertNotNull (model);
        Assert.assertEquals (10, model.getColumnBaseData ().get (0).size ());
        
        model = DataModelUtils.truncModel (model, 5);
        Assert.assertNotNull (model);
        Assert.assertEquals (5, model.getColumnBaseData ().get (0).size ());
        DataModel model1 = genDataModel ();
        HeadField field = model1.getRowHeadFields ().get (0);
        List<HeadField> nodeList = Lists.newArrayList ();
        for (int i = 0; i < 2; ++i) {
            HeadField header = new HeadField (null);
            header.setCaption (i + "");
            header.setValue ("[Dmension].[row_" + i + "]");
            nodeList.add (header);
        }
        field.setNodeList (nodeList);
        model1.getRowHeadFields ().add (0, field);
        model = DataModelUtils.truncModel (model1, 5);
        Assert.assertNotNull (model);
        Assert.assertEquals (model.getRowHeadFields ().size (), 5);
        
        DataModel model2 = genDataModel ();
        HeadField field1 = model2.getRowHeadFields ().get (0);
        List<HeadField> children = Lists.newArrayList ();
        for (int i = 0; i < 2; ++i) {
            HeadField header = new HeadField (null);
            header.setCaption (i + "");
            header.setValue ("[Dmension].[row_" + i + "]");
            children.add (header);
        }
        field1.setChildren (children);
        model2.getRowHeadFields ().add (0, field1);
        model = DataModelUtils.truncModel (model2, 8);
        Assert.assertNotNull (model);
        Assert.assertEquals (8, model.getColumnBaseData ().get (0).size ());
    }
    
    private DataModel genDataModel() {
        DataModel dataModel = new DataModel ();
        List<HeadField> columnHeader = new ArrayList<HeadField> ();
        // int c
        String[] trades = { "房屋出租", "房地产其他", "皮革", "服装", "鞋帽", "纺织辅料",
                "服装鞋帽其他", "工艺品", "礼品", "饰品" };
        
        String[] cols = { "消费", "点击", "访问人数", "人均消费", "平均价格", "总量", "分量",
                "单位产出", "月均订单数", "收入" };
        
        for (int i = 0; i < 10; ++i) {
            HeadField header = new HeadField (null);
            header.setCaption (cols[i]);
            header.setValue ("[Measures].[column_" + i + "]");
            columnHeader.add (header);
        }
        dataModel.setColumnHeadFields (columnHeader);
        List<HeadField> rowHeader = new ArrayList<HeadField> ();
        for (int i = 0; i < 10; ++i) {
            HeadField header = new HeadField (null);
            header.setCaption (trades[i]);
            header.setValue ("[Dmension].[row_" + i + "]");
            rowHeader.add (header);
        }
        dataModel.setRowHeadFields (rowHeader);
        List<List<BigDecimal>> datas = new ArrayList<List<BigDecimal>> ();
        
        for (int i = 0; i < 10; ++i) {
            List<BigDecimal> cellDatas = new ArrayList<BigDecimal> ();
            for (int j = 0; j < 10; ++j) {
                Random random = new Random ();
                cellDatas.add (BigDecimal.valueOf (random.nextInt (10000)));
            }
            datas.add (cellDatas);
        }
        dataModel.setColumnBaseData (datas);
        return dataModel;
    }
}
