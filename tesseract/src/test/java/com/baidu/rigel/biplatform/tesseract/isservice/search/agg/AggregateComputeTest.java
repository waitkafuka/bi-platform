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
package com.baidu.rigel.biplatform.tesseract.isservice.search.agg;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.baidu.rigel.biplatform.ac.model.Aggregator;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.GroupBy;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryMeasure;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryRequest;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.Select;
import com.baidu.rigel.biplatform.tesseract.resultset.isservice.SearchIndexResultRecord;
import com.google.common.collect.Lists;

/**
 *Description:
 * @author david.wang
 *
 */
public class AggregateComputeTest {
    
    @Test
    public void testDistinct() {
        List<SearchIndexResultRecord> list = Lists.newArrayList ();
        list.add (new SearchIndexResultRecord (1));
        list.add (new SearchIndexResultRecord (1));
        List<SearchIndexResultRecord> rs = AggregateCompute.distinct (list);
        Assert.assertEquals (1, rs.size ());
    }
    
    @Test
    public void testAggregateWithEmpty() {
        List<SearchIndexResultRecord> dataList = new ArrayList<SearchIndexResultRecord>();
        List<QueryMeasure> queryMeasures = new ArrayList<QueryMeasure>(0);
        List<SearchIndexResultRecord> aggregate = AggregateCompute.aggregate (dataList, 0, queryMeasures);
        Assert.assertEquals (0, aggregate.size ());
        dataList.add (new SearchIndexResultRecord (1));
        aggregate = AggregateCompute.aggregate (dataList, 0, queryMeasures);
        Assert.assertEquals (1, aggregate.size ());
    }
    
    @Test
    public void testAggregate() {
        List<QueryMeasure> queryMeasures = new ArrayList<QueryMeasure>(2);
        QueryMeasure sum = new QueryMeasure ("test1");
        sum.setAggregator (Aggregator.SUM);
        queryMeasures.add (sum);
        QueryMeasure count = new QueryMeasure ("test2");
        count.setAggregator (Aggregator.COUNT);
        queryMeasures.add (count);
        QueryMeasure distCount = new QueryMeasure ("test3");
        distCount.setAggregator (Aggregator.DISTINCT_COUNT);
        queryMeasures.add (distCount);
        
        List<SearchIndexResultRecord> records = new ArrayList<SearchIndexResultRecord>(3);
        
        String[] feildArray = new String[]{"dim", "1", "2", "3"};
        SearchIndexResultRecord record1 = new SearchIndexResultRecord (feildArray, "dim");
        records.add (record1);
        
        String[] feildArray2 = new String[]{"dim", "1", "3", "3"};
        SearchIndexResultRecord record2 = new SearchIndexResultRecord (feildArray2, "dim");
        records.add (record2);
        
        String[] feildArray3 = new String[]{"dim", "1", "3", "2"};
        SearchIndexResultRecord record3 = new SearchIndexResultRecord (feildArray3, "dim");
        records.add (record3);
        
        List<SearchIndexResultRecord> rs = AggregateCompute.aggregate (records, 1, queryMeasures);
        Assert.assertNotNull (rs);
        Assert.assertEquals (1, rs.size ());
        Assert.assertNotNull (rs.get (0).getDistinctMeasures ());
        SearchIndexResultRecord calRs = rs.get (0);
        Assert.assertEquals ("3", calRs.getField (1).toString ());
        Assert.assertEquals ("2", calRs.getField (3).toString ());
    }
    
    @Test
    public void testTwoArgsAggregate() {
        List<QueryMeasure> queryMeasures = new ArrayList<QueryMeasure>(2);
        QueryMeasure sum = new QueryMeasure ("test1");
        sum.setAggregator (Aggregator.SUM);
        queryMeasures.add (sum);
        QueryMeasure count = new QueryMeasure ("test2");
        count.setAggregator (Aggregator.COUNT);
        queryMeasures.add (count);
        QueryMeasure distCount = new QueryMeasure ("test3");
        distCount.setAggregator (Aggregator.DISTINCT_COUNT);
        queryMeasures.add (distCount);
        
        List<SearchIndexResultRecord> records = new ArrayList<SearchIndexResultRecord>(3);
        
        String[] feildArray = new String[]{"dim", "1", "2", "3"};
        SearchIndexResultRecord record1 = new SearchIndexResultRecord (feildArray, "dim");
        records.add (record1);
        
        String[] feildArray2 = new String[]{"dim", "1", "3", "3"};
        SearchIndexResultRecord record2 = new SearchIndexResultRecord (feildArray2, "dim");
        records.add (record2);
        
        String[] feildArray3 = new String[]{"dim", "1", "3", "2"};
        SearchIndexResultRecord record3 = new SearchIndexResultRecord (feildArray3, "dim");
        records.add (record3);
        
        QueryRequest queryRequest = new QueryRequest ();
        List<SearchIndexResultRecord> rs = AggregateCompute.aggregate (records, queryRequest);
        Assert.assertEquals (3, rs.size ());
        QueryRequest request = Mockito.mock (QueryRequest.class);
        Select select = Mockito.mock (Select.class);
        GroupBy groupBy = Mockito.mock (GroupBy.class);
        Mockito.doReturn (select).when (request).getSelect ();
        Mockito.doReturn (groupBy).when (request).getGroupBy ();
        List<String> queryProperties = Lists.newArrayList ("dim");
        Mockito.doReturn (queryProperties).when (select).getQueryProperties ();
        Mockito.doReturn (queryMeasures).when (select).getQueryMeasures ();
        rs = AggregateCompute.aggregate (records, request);
    }
}
