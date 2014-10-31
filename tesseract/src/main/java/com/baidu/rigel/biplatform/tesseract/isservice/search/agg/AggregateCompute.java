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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryMeasure;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryRequest;
import com.baidu.rigel.biplatform.tesseract.resultset.isservice.ResultRecord;
import com.baidu.rigel.biplatform.tesseract.resultset.isservice.StatFieldObject;
import com.baidu.rigel.biplatform.tesseract.resultset.isservice.StatFieldRecord;

/**
 * 
 * 聚集计算
 * 
 * @author lijin
 *
 */
public class AggregateCompute {
    
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
     * 聚集计算
     * 
     * @param dataList
     *            待计算的数据
     * @param query
     *            原始查询请求
     * @return LinkedList<ResultRecord> 计算后的数据
     */
    public static LinkedList<ResultRecord> aggregate(LinkedList<ResultRecord> dataList,
        QueryRequest query) {
        Stream<ResultRecord> stream = dataList.stream();
        LinkedList<ResultRecord> result = new LinkedList<ResultRecord>();
        
        Map<Map<String, String>, StatFieldRecord> group = new HashMap<Map<String, String>, StatFieldRecord>();
        group=stream.collect(Collectors.groupingBy(new Function<ResultRecord, Map<String, String>>() {
            @Override
            public Map<String, String> apply(ResultRecord t) {
                Map<String, String> groupBy = new HashMap<String, String>();
                for (String groupByField : query.getGroupBy().getGroups()) {
                    try {
                        String value = t.getField(groupByField) != null ? t.getField(groupByField)
                            .toString() : null;
                        groupBy.put(groupByField, value);
                    } catch (NoSuchFieldException e) {  
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                return groupBy;
            }
        }, Collectors.reducing(new StatFieldRecord(),
            new Function<ResultRecord, StatFieldRecord>() {
                @Override
                public StatFieldRecord apply(ResultRecord t) {
                    Map<String, StatFieldObject> curr = new HashMap<String, StatFieldObject>();
                    for (QueryMeasure qm : query.getSelect().getQueryMeasures()) {
                        StatFieldObject currMeasure = new StatFieldObject();
                        currMeasure.setStatFieldName(qm.getProperties());
                        currMeasure.setAggType(qm.getAggregator());
                        AggregatorMethod am = AggregatorMethod.getAggregatorMethod(qm
                            .getAggregator());
                        currMeasure.setAggMethod(am);
                        try {
                            currMeasure.setStatFieldString((t.getField(qm.getProperties())).toString());
                            currMeasure.setStatFieldResult((t.getField(qm.getProperties())).toString());
                        } catch (NoSuchFieldException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        curr.put(qm.getProperties(), currMeasure);
                    }
                    
                    StatFieldRecord record = new StatFieldRecord(curr);
                    return record;
                }
            }, (x, y) -> {
                if (x == null) {
                    return y;
                }
                return x.aggregate(y);
            }))); 
        // 取出聚集计算的结果
        for (Map<String, String> groupKey : group.keySet()) {
            StatFieldRecord sr = group.get(groupKey);
            List<Object> field = new ArrayList<Object>();
            List<String> fieldName = new ArrayList<String>();
            
            for (String keyName : sr.getStatFieldMap().keySet()) {
                field.add(sr.getStatFieldMap().get(keyName).getStatFieldResult());
                fieldName.add(sr.getStatFieldMap().get(keyName).getStatFieldName());
            }
            
            for (String keyName : groupKey.keySet()) {
                field.add(groupKey.get(keyName));
                fieldName.add(keyName);
            }
            
            ResultRecord record = new ResultRecord(field.toArray(new Serializable[0]),
                fieldName.toArray(new String[0]));
            result.add(record);
        }
        return result;
        
    }
    
}
