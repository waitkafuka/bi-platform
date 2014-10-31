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
package com.baidu.rigel.biplatform.ma.rt.query.request.trans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.baidu.rigel.biplatform.ac.minicube.TimeDimension;
import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.OlapElement;
import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.ac.util.TimeRangeDetail;
import com.baidu.rigel.biplatform.ma.model.service.PositionType;
import com.baidu.rigel.biplatform.ma.report.model.ExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.Item;
import com.baidu.rigel.biplatform.ma.report.model.LogicModel;
import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;
import com.baidu.rigel.biplatform.ma.report.utils.ItemUtils;
import com.baidu.rigel.biplatform.ma.rt.query.request.asst.DataSourceServiceHelper;
import com.baidu.rigel.biplatform.ma.rt.query.request.asst.ReportDesignModelServiceHelper;
import com.baidu.rigel.biplatform.ma.rt.query.request.asst.SchemaManageServiceHelper;
import com.baidu.rigel.biplatform.ma.rt.query.service.OrderType;
import com.baidu.rigel.biplatform.ma.rt.query.service.QueryAction;
import com.baidu.rigel.biplatform.ma.rt.query.service.QueryRequest;
import com.baidu.rigel.biplatform.ma.rt.query.service.QueryStrategy;
import com.baidu.rigel.biplatform.ma.rt.utils.OlapElementQueryUtils;
import com.google.common.collect.Maps;

/**
 *
 * @author wangyuxue
 * @version 1.0.0.1
 */
class TableQueryTransHandler extends QueryRequestTransHandler {
    
    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(TableQueryTransHandler.class);
    
    /**
     * 
     * QueryRequestBaseTransHandler
     */
    protected TableQueryTransHandler() {
    }
    
    /* (non-Javadoc)
     * @see com.baidu.rigel.biplatform.ma.runtime.QueryRequestTransHandler#transRequest(com.baidu.rigel.biplatform.ma.runtime.QueryRequest)
     */
    @Override
    QueryAction transRequest(QueryRequest request) {
        
        QueryAction action = new QueryAction();
        
        // 数据源
        DataSourceInfo dataSource = DataSourceServiceHelper.getInstance().getDsInfoByReportId(request.getReportId());
        action.setDataSource(dataSource);
        
        // 立方体
        Cube cube  = SchemaManageServiceHelper.getInstance().getCube(request.getReportId(), request.getAreaId());
        action.setCube(cube);
        
        // 纵轴
        LinkedHashMap<Item, Object> columns = genColumns(request);
        action.setColumns(columns);
        
        // 横轴
//        LinkedHashMap<Item, Object> rows = genRows(request);;
//        action.setRows(rows);
        
        // 过滤轴
        LinkedHashMap<Item, Object> slices = genSlice(request);
        action.setSlices(slices);
        
        // TODO 排序轴 排序支持
        LinkedHashMap<Item, OrderType> orders = Maps.newLinkedHashMap();
        action.setOrders(orders);
        
        return action;
    }
    
    /**
     * 依据查询请求生成查询轴的定义
     * @param request 查询请求
     * @return LinkedHashMap<Item, Object>
     */
    private LinkedHashMap<Item, Object> genColumns(QueryRequest request) {
        /**
         * item必须保证顺序
         */
        LinkedHashMap<Item, Object> itemValues = Maps.newLinkedHashMap();
        ReportDesignModel model = ReportDesignModelServiceHelper.getInstance()
                .getReportDesignModel(request.getReportId());
        ExtendArea area = model.getExtendById(request.getAreaId());
        Item[] items = area.getLogicModel().getColumns();
        Map<String, String[]> values = request.getConditions();
        for (Item item : items) {
            String elementId = item.getOlapElementId();
            if (StringUtils.isEmpty(elementId)) {
                continue;
            }
            Object value =  values.containsKey(elementId) ? values.get(elementId) : item
                    .getParams().get(elementId);
            OlapElement element = ItemUtils.getOlapElementByItem(item, model.getSchema(), area.getCubeId());
            if (value != null && element instanceof TimeDimension) {
                String start;
                String end;
                try {
                    JSONObject json = new JSONObject(String.valueOf(value));
                    /**
                     * TODO 考虑月/周/年等
                     */
                    start = json.getString("start").replace("-", "");
                    end = json.getString("end").replace("-", "");
                } catch (JSONException e) {
                    logger.warn(
                            "Time Condition not Correct. Maybe from row."
                            + " Try to use it as UniqueName. Time: " + value, e);
                    String formatString = "yyyyMMdd";
                    if (value instanceof String[]) {
                        String[] dates = (String[]) value;
                        /**
                         * TODO 如果有多选时间，把第一个时间和最后一个时间作为start和end，不支持间断时间
                         * 以后要考虑重构，支持间断时间
                         */
                        start = parseToDate(dates[0]);
                        end = parseToDate(dates[dates.length - 1]);
                    } else {
                        start = parseToDate(String.valueOf(value));
                        end = parseToDate(String.valueOf(value));
                    }
                    
                }
                TimeRangeDetail range = new TimeRangeDetail(start, end);
                if (start.equals(end)) {
                    /**
                     * 如果是时间区域，并且时间参数中起始和结束相同，把时间扩展为start过去一个月以来的数据
                     */
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                    Date startDate = new Date();
                    try {
                        startDate = df.parse(start);
                    } catch (ParseException e) {
                        logger.error("Date Format Error. Use current date instead. ", e);
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startDate);
                    calendar.add(Calendar.MONTH, -1);
                    range = new TimeRangeDetail(df.format(calendar.getTime()), end);
                }
                String[] days = new String[range.getDays().length];
                for (int i = 0; i < days.length; i++) {
                    days[i] = "[" + element.getName() + "].[" + range.getDays()[i] + "]";
                }
                value = days;

            }
            itemValues.put(item, value);
        }
        return itemValues;
    }

    /**
     * 依据查询请求生成查询列的定义
     * @param request 查询请求
     * @return LinkedHashMap<Item, Object> 
     */
    private LinkedHashMap<Item, Object> genSlice(QueryRequest request) {
        LinkedHashMap<Item, Object> slices = Maps.newLinkedHashMap();
        Map<String, String[]> conditions = request.getConditions();
        String reportId = request.getReportId();
        String areaId = request.getAreaId();
        LogicModel logicModel = SchemaManageServiceHelper.getInstance().queryLogicModel(reportId, areaId);
        Cube defineCube = SchemaManageServiceHelper.getInstance().getCubeDefine(reportId, areaId);
        conditions.keySet().parallelStream().forEach(key -> {
            OlapElement element = OlapElementQueryUtils.queryElementById(defineCube, key);
            if (element != null && !logicModel.containsOlapElement(element.getId())) {
                Item item = new Item();
                item.setAreaId(areaId);
                item.setId(element.getId());
                item.setOlapElementId(element.getId());
                item.setPositionType(PositionType.S);
                slices.put(item, conditions.get(key));
            }
        });
        return slices;
    }

    /* (non-Javadoc)
     * @see com.baidu.rigel.biplatform.ma.runtime.QueryRequestTransHandler#isSupportedQueryStrategy(com.baidu.rigel.biplatform.ma.runtime.QueryStrategy)
     */
    @Override
    boolean isSupportedQueryStrategy(QueryStrategy queryStrategy) {
        return QueryStrategy.TABLE_QUERY == queryStrategy;
    }
    
    /**
     * parseToDate
     * @param uniqueName
     * @return
     */
    private String parseToDate(String uniqueName) {
        String[] valueParts = StringUtils.split(uniqueName, "].[");
        /**
         * TODO 假定时间的uniqueName是[***].[yyyyMMdd]这种，以后要考虑兼容性
         */
        if (valueParts.length > 1) {
            String part = valueParts[valueParts.length - 1];
            return part.replace("]", "");
        }
        return uniqueName;
    }
    
}
