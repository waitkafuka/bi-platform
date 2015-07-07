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
package com.baidu.rigel.biplatform.tesseract.isservice.search.service.impl;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.baidu.rigel.biplatform.ac.query.model.ConfigQuestionModel;
import com.baidu.rigel.biplatform.tesseract.dataquery.udf.condition.QueryContextAdapter;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.GroupBy;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryContext;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryRequest;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.Select;

/**
 * 
 *Description:
 * @author david.wang
 *
 */
public class CallbackSearchServcieImplTest {

    CallbackSearchServiceImpl service = new CallbackSearchServiceImpl ();
    
    @Test
    public void testInvalidateQuery() throws Exception {
        try {
            service.query (null, null);
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
        QueryContext context = Mockito.mock (QueryContext.class);
        try {
            service.query (context, null);
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
        
        QueryRequest query = Mockito.mock (QueryRequest.class);
        try {
            service.query (context, query);
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
        Mockito.doReturn ("test").when (query).getCubeId ();
        Assert.assertNull (service.query (context, query));
        GroupBy groupBy = Mockito.mock (GroupBy.class);
        Mockito.doReturn (groupBy).when (query).getGroupBy ();
        Assert.assertNull (service.query (context, query));
    }
    
    @Test
    public void testQuery() throws Exception {
        QueryContextAdapter context = Mockito.mock (QueryContextAdapter.class);
        ConfigQuestionModel questionModel = Mockito.mock (ConfigQuestionModel.class);
        Mockito.doReturn (questionModel).when (context).getQuestionModel ();
        Map<String, String> params = Mockito.mock (Map.class);
        Mockito.doReturn (params).when (questionModel).getRequestParams ();
        QueryRequest query = Mockito.mock (QueryRequest.class);
        Mockito.doReturn ("test").when (query).getCubeId ();
        GroupBy groupBy = Mockito.mock (GroupBy.class);
        Mockito.doReturn (groupBy).when (query).getGroupBy ();
        Select select = Mockito.mock (Select.class);
        Mockito.doReturn (select).when (query).getSelect ();
        try {
            service.query (context, query);
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
        
    }
}
