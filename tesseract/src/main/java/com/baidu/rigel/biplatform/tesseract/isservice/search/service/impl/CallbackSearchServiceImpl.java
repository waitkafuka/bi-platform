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
/**
 * 
 */
package com.baidu.rigel.biplatform.tesseract.isservice.search.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.baidu.rigel.biplatform.ac.minicube.CallbackMeasure;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMeasure;
import com.baidu.rigel.biplatform.ac.model.MeasureType;
import com.baidu.rigel.biplatform.ac.model.callback.CallbackMeasureVaue;
import com.baidu.rigel.biplatform.ac.model.callback.CallbackResponse;
import com.baidu.rigel.biplatform.ac.model.callback.CallbackServiceInvoker;
import com.baidu.rigel.biplatform.ac.model.callback.CallbackType;
import com.baidu.rigel.biplatform.ac.util.AnswerCoreConstant;
import com.baidu.rigel.biplatform.tesseract.isservice.exception.IndexAndSearchException;
import com.baidu.rigel.biplatform.tesseract.isservice.exception.IndexAndSearchExceptionType;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.SqlQuery;
import com.baidu.rigel.biplatform.tesseract.isservice.search.agg.AggregateCompute;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.Expression;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryContext;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryRequest;
import com.baidu.rigel.biplatform.tesseract.resultset.TesseractResultSet;
import com.baidu.rigel.biplatform.tesseract.resultset.isservice.Meta;
import com.baidu.rigel.biplatform.tesseract.resultset.isservice.ResultRecord;
import com.baidu.rigel.biplatform.tesseract.resultset.isservice.SearchResultSet;
import com.baidu.rigel.biplatform.tesseract.util.QueryRequestUtil;
import com.baidu.rigel.biplatform.tesseract.util.TesseractExceptionUtils;
import com.baidu.rigel.biplatform.tesseract.util.isservice.LogInfoConstants;
import com.google.common.collect.Lists;

/**
 * SearchService 实现类，用来连接外部的查询API。
 * 
 * @author mengran
 *
 */
@Service("callbackSearchService")
public class CallbackSearchServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackSearchServiceImpl.class);
    
    private static final String MEASURE_NAMES = "measureNames";
    private static final String GROUP_BY = "groupBy";
    private static final String FILTER = "filter";
    private static final String RESPONSE_VALUE_SPLIT = "$";

    @Autowired
    private AsyncListenableTaskExecutor taskExecutor;
    
    @Value(value="${callback.measure.timeout}")
    private long callbackTimeout;
    
    /**
     * Constructor by no param
     */
    public CallbackSearchServiceImpl() {
        super();
    }
    
    /**
     * 目前这个实现是简单策略：超时时间取最大设置，参数带入取并集
     * @author mengran
     *
     */
    private class CallbackExecutor implements Callable<CallbackResponse> {

    	private Entry<String, List<MiniCubeMeasure>> group;
    	private String groupbyParams;
    	private String whereParams;
    	// For external usage only
    	private String callbackMeasures;
    	
		/**
		 * @param group
		 */
		public CallbackExecutor(Entry<String, List<MiniCubeMeasure>> group, 
		        LinkedHashMap<String, List<String>> groupbyParams, LinkedHashMap<String, List<String>> whereParams) {
			super();
			this.group = group;
			this.groupbyParams = AnswerCoreConstant.GSON.toJson(groupbyParams);
			this.whereParams = AnswerCoreConstant.GSON.toJson(whereParams);
		}
		
		public List<String> getCallbackMeasures() {
		    
		    return Arrays.asList(StringUtils.delimitedListToStringArray(callbackMeasures, ","));
		}

		@Override
		public CallbackResponse call() throws Exception {
			
			MiniCubeMeasure maxTimeout = group.getValue().stream().max(new Comparator<MiniCubeMeasure>() {

				@Override
				public int compare(MiniCubeMeasure o1, MiniCubeMeasure o2) {
					return new Long(((CallbackMeasure) o1).getSocketTimeOut() - ((CallbackMeasure) o2).getSocketTimeOut()).intValue();
				}
				
			}).get();
			
			// Build call-back parameter
			Map<String, String> merged = new HashMap<String, String>();
			StringBuilder sb = new StringBuilder();
			for (MiniCubeMeasure m : group.getValue()) {
			    sb.append(",").append(m.getName());
			}
			if (sb.length() > 0) {
			    sb.deleteCharAt(0);
			}
			// Prepared required parameters (other parameters depend on invoke methods)
			merged.put(MEASURE_NAMES, callbackMeasures = sb.toString());
			merged.put(GROUP_BY, groupbyParams);
			merged.put(FILTER, whereParams);
			
            group.getValue().stream().forEach(new Consumer<MiniCubeMeasure>() {

                @Override
                public void accept(MiniCubeMeasure m) {
                    if (((CallbackMeasure) m).getCallbackParams() != null 
                        && !((CallbackMeasure) m).getCallbackParams().isEmpty()) {
                        merged.putAll(((CallbackMeasure) m).getCallbackParams());
                    }
                }
            });
			return CallbackServiceInvoker.invokeCallback(group.getKey(), merged, 
			        CallbackType.MEASURE, ((CallbackMeasure) maxTimeout).getSocketTimeOut());
		}
    	
    }

    /**
     * @param context 本次查询的上下文信息对象
     * @param query 轻量级的查询请求
     * @return 查询结果
     * @throws IndexAndSearchException exception occurred when 
     */
    public TesseractResultSet query(QueryContext context, QueryRequest query) throws IndexAndSearchException {
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_BEGIN, "callbackquery", "[callbackquery:" + query + "]"));
        if (query == null || context == null || StringUtils.isEmpty(query.getCubeId())) {
            LOGGER.error(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_EXCEPTION, "callbackquery", "[callbackquery:" + query
                    + "]"));
            throw new IndexAndSearchException(TesseractExceptionUtils.getExceptionMessage(
                    IndexAndSearchException.QUERYEXCEPTION_MESSAGE,
                    IndexAndSearchExceptionType.ILLEGALARGUMENT_EXCEPTION),
                    IndexAndSearchExceptionType.ILLEGALARGUMENT_EXCEPTION);
        }
        
        // Build query target map
        Map<String, List<MiniCubeMeasure>> callbackMeasures = context.getQueryMeasures().stream()
                .filter(m -> m.getType().equals(MeasureType.CALLBACK))
                .collect(Collectors.groupingBy(c -> ((CallbackMeasure) c).getCallbackUrl(), Collectors.toList()));
        if (callbackMeasures == null || callbackMeasures.isEmpty()) {
        	LOGGER.error(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_EXCEPTION, "Empty callback measure", "[callbackquery:" + query
                    + "]"));
            throw new IndexAndSearchException(TesseractExceptionUtils.getExceptionMessage(
                    IndexAndSearchException.QUERYEXCEPTION_MESSAGE,
                    IndexAndSearchExceptionType.ILLEGALARGUMENT_EXCEPTION),
                    IndexAndSearchExceptionType.ILLEGALARGUMENT_EXCEPTION);
        }
        LOGGER.info("Find callback targets " + callbackMeasures);
        
        // Keep group-by sequence.
        List<String> groupby = new ArrayList<String>(query.getGroupBy().getGroups());
        LinkedHashMap<String, List<String>> groupbyParams = new LinkedHashMap<String, List<String>>(groupby.size());
        for (String g : groupby) {
            groupbyParams.put(g, new ArrayList<String>());
        }
        LinkedHashMap<String, List<String>> whereParams = new LinkedHashMap<String, List<String>>();
        for (Expression e : query.getWhere().getAndList()) {
            List<String> l = e.getQueryValues().stream().filter(v -> !StringUtils.isEmpty(v.getValue()))
                .map(v -> v.getValue() ).collect(Collectors.toList());
            if (groupbyParams.containsKey(e.getProperties())) {
                // Put it into group by field
                groupbyParams.get(e.getProperties()).addAll(l);
            } else {
                // Put it into filter field
                // TODO 需要小明确认一下为什么构建filter时value为空
                if (CollectionUtils.isEmpty(l)) {
                    List<Set<String>> tmp = 
                            e.getQueryValues().stream().map(v -> v.getLeafValues()).collect(Collectors.toList());
                    List<String> values = Lists.newArrayList();
                    tmp.forEach(t -> values.addAll(t));
                    whereParams.put(e.getProperties(), values);
                } else {
                    whereParams.put(e.getProperties(), new ArrayList<String>(l));
                }
            }
        }
        
        // Prepare query tools
        Map<CallbackExecutor, CallbackResponse> response = new ConcurrentHashMap<CallbackExecutor, CallbackResponse>(callbackMeasures.size());
        CountDownLatch latch = new CountDownLatch(response.size());
        for (Entry<String, List<MiniCubeMeasure>> e : callbackMeasures.entrySet()) {
            CallbackExecutor ce = new CallbackExecutor(e, groupbyParams, whereParams);
            while (true) {
                try {
                    ListenableFuture<CallbackResponse> f = taskExecutor.submitListenable(ce);
                    f.addCallback(new ListenableFutureCallback<CallbackResponse>() {
                        @Override
                        public void onSuccess(CallbackResponse result) {
                            latch.countDown();
                            response.put(ce, result);
                        }
                        
                        @Override
                        public void onFailure(Throwable t) {
                            latch.countDown();
                            LOGGER.error(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_EXCEPTION, 
                                    "Error when try to callback " + e, "[callbackquery:]"), t);
                        }
                    });
                    break;
                } catch (TaskRejectedException tre) {
                    try {
                        // FIXME: MENGRAN. Need configure it?
                        Thread.sleep(100L);
                    } catch (InterruptedException e1) {
                        // Ignore
                    }
                }
            }
        }

        // Waiting...
        try {
            latch.await(callbackTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e1) {
            // Ignore
            LOGGER.error(e1.getMessage(), e1);
        }
        
        // Package result
        SqlQuery sqlQuery = QueryRequestUtil.transQueryRequest2SqlQuery(query);
        LinkedList<ResultRecord> resultList = Lists.newLinkedList();
        if (!response.isEmpty()) {
            packageResultRecords(query, sqlQuery, response);
        }
        TesseractResultSet result = new SearchResultSet(AggregateCompute.aggregate(resultList, query));

        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_END, "query", "[query:" + query + "]"));
        return result;
    }
    
    private LinkedList<ResultRecord> packageResultRecords(QueryRequest query, SqlQuery sqlQuery, Map<CallbackExecutor, CallbackResponse> response) {
        
        List<String> groupby = new ArrayList<String>(query.getGroupBy().getGroups());
        // Confirm meta sequence
        List<Entry<CallbackExecutor, CallbackResponse>> fieldValuesHolderList = new ArrayList<Entry<CallbackExecutor, CallbackResponse>>(response.size());
        for (Entry<CallbackExecutor, CallbackResponse> e : response.entrySet()) {
            groupby.addAll(e.getKey().getCallbackMeasures());
            fieldValuesHolderList.add(e);
        }
        
        LinkedList<ResultRecord> result = new LinkedList<ResultRecord>();
        // Use first response as base SEQ. Weak implementation. FIXME: WANGYUXUE.
        for (int index = 0; index < fieldValuesHolderList.get(0).getValue().getData().size(); index++) {
            List<String> fieldValues = new ArrayList<String>(groupby.size());
            CallbackMeasureVaue mv = (CallbackMeasureVaue) fieldValuesHolderList.get(0).getValue().getData().get(index);
            String key = mv.keySet().iterator().next();
            fieldValues.addAll(Arrays.asList(StringUtils.delimitedListToStringArray(key, RESPONSE_VALUE_SPLIT)));
            fieldValues.addAll(mv.get(key));
            for (int i = 1; i < fieldValuesHolderList.size(); i++) {
                CallbackMeasureVaue callbackMeasureValue = (CallbackMeasureVaue) fieldValuesHolderList.get(i).getValue().getData().get(index);
                if (key.equals(callbackMeasureValue.keySet().iterator().next())) {
                    fieldValues.addAll(callbackMeasureValue.get(key));
                } else {
                    LOGGER.error("Wrong SEQ of callback response of {} : {}", fieldValuesHolderList.get(i).getKey().group.getKey(), callbackMeasureValue);
                }
            }
            Meta meta = new Meta(groupby.toArray(new String[0]));
            ResultRecord record = new ResultRecord(fieldValues.toArray(new Serializable[0]), meta);
            record.setGroupBy(StringUtils.collectionToCommaDelimitedString(groupby));
            
            // Fill into result
            result.add(record);
        }
        
        return result;
    }

}
