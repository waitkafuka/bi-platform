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

import org.junit.Assert;
import org.junit.Test;

/**
 *Description:
 * @author david.wang
 *
 */
public class BigDecimalUtilsTest {
    
    @Test
    public void testAdd () {
        BigDecimal a = BigDecimal.ZERO;
        BigDecimal b = BigDecimal.ONE;
        BigDecimal rs1 = BigDecimalUtils.addBigDecimal (null, b);
        Assert.assertEquals (rs1, b);
        BigDecimal rs2 = BigDecimalUtils.addBigDecimal (a, null);
        Assert.assertEquals (rs2, a);
        BigDecimal rs3 = BigDecimalUtils.addBigDecimal (a, b);
        Assert.assertEquals (rs3, a.add (b));
    }
}
