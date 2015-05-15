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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.rigel.biplatform.ac.minicube.ExtendMinicubeMeasure;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMeasure;
import com.baidu.rigel.biplatform.ac.model.MeasureType;


/**
 *Description:
 * @author david.wang
 *
 */
public class DerivativeIndUtilsTest {
    
    @Test
    public void testGetOriIndNamesWithNull () {
        try {
            DerivativeIndUtils.getOriIndNames (null);
            Assert.fail ();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testGetOriIndNames () {
        MiniCubeMeasure m = new MiniCubeMeasure ("test");
        List<String> names = DerivativeIndUtils.getOriIndNames (m);
        Assert.assertEquals (0, names.size ());
        ExtendMinicubeMeasure m1 = new ExtendMinicubeMeasure ("test");
        names = DerivativeIndUtils.getOriIndNames (m1);
        Assert.assertEquals (0, names.size ());
        m1.setType (MeasureType.CAL);
        names = DerivativeIndUtils.getOriIndNames (m1);
        Assert.assertEquals (0, names.size ());
        Set<String> tmp = new HashSet<String> ();
        tmp.add ("a");
        m1.setRefIndNames (tmp);
        names = DerivativeIndUtils.getOriIndNames (m1);
        Assert.assertEquals (1, names.size ());
        m1.setType (MeasureType.CALLBACK);
        names = DerivativeIndUtils.getOriIndNames (m1);
        Assert.assertEquals (0, names.size ());
    }
}
