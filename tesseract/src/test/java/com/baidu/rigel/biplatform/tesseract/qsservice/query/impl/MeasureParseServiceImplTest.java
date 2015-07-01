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
package com.baidu.rigel.biplatform.tesseract.qsservice.query.impl;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMeasure;
import com.baidu.rigel.biplatform.ac.model.Aggregator;
import com.baidu.rigel.biplatform.ac.model.MeasureType;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.MeasureParseResult;

/**
 *Description:
 * @author david.wang
 *
 */
public class MeasureParseServiceImplTest {
    
    @Test
    public void testParseMeasure() {
        MeasureParseServiceImpl service = new MeasureParseServiceImpl();
        MiniCube cube  = new MiniCube ();
        try {
            service.parseMeasure (cube, "test");
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
        MiniCubeMeasure m = new MiniCubeMeasure ("test");
        m.setType (MeasureType.CAL);
        cube.getMeasures ().put ("test", m);
        try {
            service.parseMeasure (cube, "test");
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
        MiniCubeMeasure m1 = new MiniCubeMeasure ("test");
        m1.setType (MeasureType.COMMON);
        m1.setAggregator (Aggregator.DISTINCT_COUNT);
        cube.getMeasures ().put ("test", m1);
        try {
            service.parseMeasure (cube, "test");
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
        MiniCubeMeasure m2 = new MiniCubeMeasure ("test");
        m2.setType (MeasureType.COMMON);
        m2.setAggregator (Aggregator.COUNT);
        cube.getMeasures ().put ("test", m2);
        try {
            MeasureParseResult rs = service.parseMeasure (cube, "test");
            Assert.assertNotNull(rs);
        } catch (Exception e) {
            Assert.fail ();
        }
    }
}
