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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.baidu.rigel.biplatform.ac.minicube.CallbackMeasure;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMeasure;
import com.baidu.rigel.biplatform.ac.model.MeasureType;
import com.baidu.rigel.biplatform.ac.model.callback.CallbackMeasureVaue;
import com.baidu.rigel.biplatform.ac.model.callback.CallbackResponse;
import com.baidu.rigel.biplatform.ac.model.callback.CallbackValue;
import com.baidu.rigel.biplatform.ac.model.callback.ResponseStatus;
import com.baidu.rigel.biplatform.ac.query.model.ConfigQuestionModel;
import com.baidu.rigel.biplatform.ac.util.HttpRequest;
import com.baidu.rigel.biplatform.tesseract.dataquery.udf.condition.QueryContextAdapter;
import com.baidu.rigel.biplatform.tesseract.isservice.search.service.impl.CallbackSearchServiceImpl.CallbackExecutor;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.Expression;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.From;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.GroupBy;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryContext;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryObject;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryRequest;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.Select;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.Where;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 *Description:
 * @author david.wang
 *
 */
@RunWith(PowerMockRunner.class)
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
    @PrepareForTest({HttpRequest.class })
    public void testQuery() throws Exception {
        QueryContextAdapter context = Mockito.mock (QueryContextAdapter.class);
        ConfigQuestionModel questionModel = Mockito.mock (ConfigQuestionModel.class);
        Mockito.doReturn (questionModel).when (context).getQuestionModel ();
        Map<String, String> params = Maps.newHashMap ();
        params.put ("b", "c");
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
        
        List<MiniCubeMeasure> measures = Lists.newArrayList ();
        CallbackMeasure m = new CallbackMeasure ("test");
        Map<String, String> p = Maps.newHashMap ();
        p.put ("a", "a");
        p.put ("b", "b");
        m.setCallbackUrl ("test");
        m.setType (MeasureType.CALLBACK);
        m.setCallbackParams (p);
        measures.add (m);
        
        Mockito.doReturn (measures).when (context).getQueryMeasures ();
        Mockito.doReturn (Sets.newHashSet ("p")).when (groupBy).getGroups ();
        Where where = Mockito.mock (Where.class);
        Mockito.doReturn (where).when (query).getWhere ();
        List<Expression> expressions = Lists.newArrayList ();
        QueryObject queryObject = new QueryObject (null, null);
        queryObject.setLeafValues (Sets.newHashSet ("tmp"));
        HashSet<QueryObject> newHashSet = Sets.newHashSet (queryObject);
        Expression tmp = new Expression ("tmp", newHashSet);
        expressions.add (tmp);
        try {
            Mockito.doReturn (expressions).when(where).getAndList();
            service.query (context, query);
        } catch (Exception e) {
            Assert.assertNotNull (e);
        }
        Expression e = new Expression ("f", Sets.newHashSet (new QueryObject ("f", Sets.newHashSet ("f"))));
        expressions.add (e);
        Expression e1 = new Expression ("p", Sets.newHashSet (new QueryObject ("p", Sets.newHashSet ("p"))));
        expressions.add (e1);
        Mockito.doReturn (expressions).when(where).getAndList();
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor ();
        taskExecutor.initialize ();
        From from = Mockito.mock (From.class);
        Mockito.doReturn (from).when (query).getFrom ();
        Mockito.doReturn ("test_fact").when (from).getFrom ();
        service.setTaskExecutor (taskExecutor);
        service.query (context, query);
        
        String url = "test";
        Map<String, String> requestParams = Maps.newHashMap ();
        requestParams.put ("filter", "{\"tmp\":[\"tmp\"],\"f\":[\"f\"]}");
        requestParams.put ("a", "a");
        requestParams.put ("b", "c");
        requestParams.put ("measureNames", "test");
        requestParams.put ("groupBy", "{\"p\":[\"p\",\"SUMMARY\"]}");
        requestParams.put ("timeOut", "30000");
        String response = "{"
                + "status:2000,"
                + "data:["
                + "{p:100, SUMMARY:100}"
                + "]"
                + "}";
        PowerMockito.mockStatic (HttpRequest.class);
        PowerMockito.when(HttpRequest.sendPost1( url, requestParams)).thenReturn (response);
        Assert.assertNotNull (service.query (context, query));
        Map<CallbackExecutor, CallbackResponse> tmpRs = Maps.newHashMap ();
        LinkedHashMap<String, List<String>> groupParams = Maps.newLinkedHashMap ();
        groupParams.put ("p", Lists.newArrayList ("p", "SUMMARY"));
        LinkedHashMap<String, List<String>> filterParams = Maps.newLinkedHashMap ();
        filterParams.put ("tmp", Lists.newArrayList ("tmp"));
        filterParams.put ("f", Lists.newArrayList ("f"));
        Map.Entry<String, List<MiniCubeMeasure>> entry = new Map.Entry<String, List<MiniCubeMeasure>>() {

            @Override
            public String getKey() {
                return "p";
            }

            @Override
            public List<MiniCubeMeasure> getValue() {
                List<MiniCubeMeasure> rs = Lists.newArrayList ();
                CallbackMeasure m = new CallbackMeasure ("m");
                rs.add (m);
                return rs;
            }

            @Override
            public List<MiniCubeMeasure> setValue(List<MiniCubeMeasure> value) {
                return null;
            }
        };
        CallbackExecutor key = service.new CallbackExecutor (entry, groupParams, filterParams);
        key.setCallbackMeasuers ("test, p");
        CallbackResponse value = new CallbackResponse ();
        value.setStatus (ResponseStatus.SUCCESS);
        List<CallbackValue> data = Lists.newArrayList ();
        CallbackMeasureVaue d1 = new CallbackMeasureVaue ();
        d1.put ("test", Lists.newArrayList ("100", "100"));
        data.add (d1);
        value.setData (data);
        value.setVersion ("1.0.0");
        
        tmpRs.put (key, value);
        service.packageResultRecords (query, null, tmpRs);
    }
}
