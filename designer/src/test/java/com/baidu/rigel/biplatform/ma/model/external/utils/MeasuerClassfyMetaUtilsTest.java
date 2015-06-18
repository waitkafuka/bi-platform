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
package com.baidu.rigel.biplatform.ma.model.external.utils;


import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMeasure;
import com.baidu.rigel.biplatform.ma.model.external.vo.MeasureClassfyObject;
import com.baidu.rigel.biplatform.ma.report.model.Item;
import com.baidu.rigel.biplatform.ma.report.model.LogicModel;
import com.google.common.collect.Lists;

/**
 *Description:
 * @author david.wang
 *
 */
public class MeasuerClassfyMetaUtilsTest {
    
    @Test
    public void testGetLeafMeasuerMeta() {
        try {
            MeasureClassfyMetaUtils.getLeafMeasureMeta (null);
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
        
        MeasureClassfyObject obj = new MeasureClassfyObject ();
        List<MeasureClassfyObject> rs = MeasureClassfyMetaUtils.getLeafMeasureMeta (obj);
        Assert.assertEquals (1, rs.size ());
        obj.getChildren ().add (new MeasureClassfyObject ());
        rs = MeasureClassfyMetaUtils.getLeafMeasureMeta (obj);
        Assert.assertEquals (1, rs.size ());
    }
    
    @Test
    public void testchangeIndMetaSelectStatus() {
        List<MeasureClassfyObject> tmp = preparedMetaList ();
        LogicModel model = preparedLogicModel ();
        MiniCube cube = preparedCube ();
        List<MeasureClassfyObject> rs = MeasureClassfyMetaUtils.changeIndMetaSelectStatus ("ind", model, cube, tmp);
        Assert.assertEquals (3, rs.size ());
        
        rs.forEach (meta -> {
            Assert.assertTrue (meta.isSelected ());
        });
    }

    private MiniCube preparedCube() {
        MiniCube cube = new MiniCube ();
        MiniCubeMeasure m1 = new MiniCubeMeasure ("test");
        cube.setSource ("ind");
        cube.getMeasures ().put ("1", m1);
        MiniCubeMeasure m2 = new MiniCubeMeasure ("test1");
        cube.getMeasures ().put ("2", m2);
        return cube;
    }

    private LogicModel preparedLogicModel() {
        LogicModel model = new LogicModel ();
        Item item = new Item();
        item.setOlapElementId ("1");
        model.addColumn (item);
        Item item1 = new Item();
        item1.setOlapElementId ("2");
        model.addColumn (item1);
        return model;
    }

    private List<MeasureClassfyObject> preparedMetaList() {
        List<MeasureClassfyObject> tmp = Lists.newArrayList ();
        MeasureClassfyObject obj = new MeasureClassfyObject ();
        obj.setName ("test");
        tmp.add (obj);
        MeasureClassfyObject obj1 = new MeasureClassfyObject ();
        obj1.setName ("ind.test1");
        tmp.add (obj1);
        MeasureClassfyObject obj2 = new MeasureClassfyObject ();
        obj2.setName ("ind.test2");
        obj2.setSelected (true);
        tmp.add (obj2);
        return tmp;
    }
}
