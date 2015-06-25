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

import com.baidu.rigel.biplatform.tesseract.qsservice.query.QueryContextSplitService;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.QueryContextSplitService.QueryContextSplitStrategy;

/**
 *Description:
 * @author david.wang
 *
 */
public class QueryContextSplitServiceTest {
    
    QueryContextSplitServiceImpl service = new QueryContextSplitServiceImpl();
    
    @Test
    public void testGetNextStrategy() {
        QueryContextSplitStrategy strategy = QueryContextSplitStrategy.Column;
        QueryContextSplitStrategy rs = QueryContextSplitService.QueryContextSplitStrategy.getNextStrategy (null);
        Assert.assertEquals (QueryContextSplitStrategy.MeasureType, rs);
        rs = QueryContextSplitService.QueryContextSplitStrategy.getNextStrategy (strategy);
        Assert.assertEquals (QueryContextSplitStrategy.Row, rs);
        strategy = QueryContextSplitStrategy.Row;
        rs = QueryContextSplitService.QueryContextSplitStrategy.getNextStrategy (strategy);
        Assert.assertNull (rs);
    }
    
    @Test
    public void testSplit() {
        Assert.assertNull (service.split (null, null, null, null, QueryContextSplitStrategy.Row));
        Assert.assertNull (service.split (null, null, null, null, QueryContextSplitStrategy.MeasureType));
        
    }
}
