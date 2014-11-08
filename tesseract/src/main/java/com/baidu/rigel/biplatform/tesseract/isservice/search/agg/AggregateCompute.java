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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.rigel.biplatform.ac.model.Aggregator;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryMeasure;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryRequest;
import com.baidu.rigel.biplatform.tesseract.resultset.Aggregate;
import com.baidu.rigel.biplatform.tesseract.resultset.isservice.ResultRecord;

/**
 * 
 * 聚集计算
 * 
 * @author lijin
 *
 */
public class AggregateCompute {
    
    private static Logger LOGGER = LoggerFactory.getLogger(AggregateCompute.class);
    
    public static LinkedList<ResultRecord> distinct(LinkedList<ResultRecord> dataList){
        Stream<ResultRecord> stream = dataList.stream();
        LinkedList<ResultRecord> result= new LinkedList<ResultRecord>();
        stream.distinct().forEach(record -> {
            result.add(record);
        });
//        for(Object os:stream.distinct().toArray()){
//            if(os instanceof ResultRecord){
//            }
//        }
        //result.addAll(stream.distinct().collect(Collectors.toList()));
        return result;
    }
    
    /**
     * 统计内部类耗时
     * @author chenxiaoming01
     *
     */
    static class Cost{
        static long reduceCost = 0;
    }
    
    /**
     * 聚集计算
     * 
     * @param dataList
     *            待计算的数据
     * @param query
     *            原始查询请求
     * @return LinkedList<ResultRecord> 计算后的数据
     */
    public static LinkedList<ResultRecord> aggregate(List<ResultRecord> dataList,
        int dimSize, List<QueryMeasure> queryMeasures) {
        Stream<ResultRecord> stream = dataList.parallelStream();
        LinkedList<ResultRecord> result = new LinkedList<ResultRecord>();
        
        
        Cost.reduceCost = 0;
        
        long current = System.currentTimeMillis();
        Map<String, ResultRecord> groupResult = stream.collect(Collectors.groupingBy(ResultRecord::getGroupBy,
                Collectors.reducing(null, (x,y) ->{
                    long reduceCurrent = System.currentTimeMillis();
                    if(x == null){
                        for(int i = 0; i< queryMeasures.size(); i++){
                            QueryMeasure measure = queryMeasures.get(i);
                            //初始化 count的初始值
                            if(measure.getAggregator().equals(Aggregator.COUNT)){
                                ((ResultRecord) y).setField(i+dimSize, 1);
                            }
                        }
                        return y;
                    }else if(y == null){
                        return x;
                    } else {
                        if(CollectionUtils.isNotEmpty(queryMeasures)){
                            for(int i = 0; i< queryMeasures.size(); i++){
                                QueryMeasure measure = queryMeasures.get(i);
                                try {
                                  Object src1 = ((ResultRecord) x).getField(i+dimSize);
                                  Object src2 = ((ResultRecord) y).getField(i+dimSize);
                                  ((ResultRecord) x).setField(i+dimSize, Aggregate.aggregate(src1 == null ? null:src1.toString(),src2 == null ? null:src2.toString(),measure.getAggregator()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    Cost.reduceCost += (System.currentTimeMillis() - reduceCurrent);
                    return x;
                })));
        
        LOGGER.info("group cost:" + (System.currentTimeMillis() - current) + "ms!,reduce cost:" + Cost.reduceCost);
        result.addAll(groupResult.values());
        return result;
    }

    public static Queue<ResultRecord> aggregate(LinkedList<ResultRecord> resultQ, QueryRequest query) {
        int dimSize = query.getSelect().getQueryProperties().size();
        List<QueryMeasure> queryMeasures = query.getSelect().getQueryMeasures();
        return aggregate(resultQ, dimSize, queryMeasures);
    }
    
    
    
}
