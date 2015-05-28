package com.baidu.rigel.biplatform.ma.resource.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.rigel.biplatform.ac.model.TimeType;
import com.baidu.rigel.biplatform.ac.query.model.MeasureCondition.SQLCondition;
import com.baidu.rigel.biplatform.ac.util.TimeRangeDetail;
import com.baidu.rigel.biplatform.ac.util.TimeUtils;
import com.baidu.rigel.biplatform.ma.comm.util.ParamValidateUtils;
import com.google.common.collect.Lists;

/**
 * 平面表工具类
 * @author yichao.jiang
 *
 */
public class PlaneTableUtils {
    
    /**
     * 日志记录对象
     */
    private static final Logger LOG = LoggerFactory.getLogger(PlaneTableUtils.class);
	/**
	 * 校验设置的平面表条件值是否合理
	 * @param sqlStr
	 * @param value
	 * @return
	 */
	public static boolean checkSQLCondition(String sqlStr, String value) {
		if (!ParamValidateUtils.check("sqlStr", sqlStr)) {
			return false;
		}
		if (!ParamValidateUtils.check("value", value)) {
			return false;
		}
		
		String [] tmpValue = value.split(",");
		List<String> conditionValues = Lists.newArrayList();
		CollectionUtils.addAll(conditionValues, tmpValue);
		SQLCondition[] sqlConditions = SQLCondition.values();
		for (SQLCondition sqlCondition : sqlConditions) {
		    if (sqlCondition.getValue().equals(sqlStr)){
		        sqlCondition.setConditionValues(conditionValues);
		        String expression = sqlCondition.parseToExpression();
		        return ParamValidateUtils.check("expression", expression);		        
		    }
		}
		return false;
	}
	
	/**
	 * 平面表中对时间条件的特殊处理
	 * @param timeJson
	 * @return 处理后的，起始日期和截止日期之间的所有天
	 */
	public static String handleTimeCondition(String timeJson) {
	    // 对时间JSON串进行处理
	    return getTimeCondition(timeJson);
	}
	
	/**
	 * 对时间JSON串进行处理，获取起始日期和截止日期之间的所有天
	 * @param timeValue
	 * @return 起始日期和截止日期之间的所有天
	 */
    private static String getTimeCondition(String timeValue) {
        String start;
        String end;
        String result = null;
        try {
            JSONObject json = new JSONObject(String.valueOf(timeValue));
            start = json.getString("start").replace("-", "");
            end = json.getString("end").replace("-", "");
            String granularity = json.getString("granularity");
            // 保证开始时间小于结束时间
            if (start.compareTo(end) > 0) {
                String tmp = start;
                start = end;
                end = tmp;
            }
            Map<String, String> time = null;
            switch (granularity) {
                // 年
                case "Y":
                    time = TimeUtils.getTimeCondition(start, end, TimeType.TimeYear);
                    break;
                // 季度
                case "Q":
                    time = TimeUtils.getTimeCondition(start, end, TimeType.TimeQuarter);
                    break;
                // 月份
                case "M":
                    time = TimeUtils.getTimeCondition(start, end, TimeType.TimeMonth);
                    break;
                // 星期
                case "W":
                    time = TimeUtils.getTimeCondition(start, end, TimeType.TimeWeekly);
                    break;
                // 天
                case "D":
                    time = TimeUtils.getTimeCondition(start, end, TimeType.TimeDay);
                    break;
                default:
                    break;
            }
            start = time.get("start");
            end = time.get("end");
            LOG.info("the planeTable time condition :start time is [" + start + "], end time is [" + end + "], and "
                    + "the granularity is " + granularity);
            // 获取具体的日期天
            result = getDetailTimeCondition(start, end);
        } catch (Exception e) {
            LOG.debug("the input time format is wrong" + timeValue, e);
        }
        return result;
    }
    
    /**
     * 获取起始日期和截止日期之间的所有天，之间用逗号分隔
     * @param startTime 起始日期
     * @param endTime 截止日期
     * @return 
     */
    private static String getDetailTimeCondition(String startTime, String endTime) {
        TimeRangeDetail range = new TimeRangeDetail(startTime , endTime);
        StringBuilder message = new StringBuilder();
        String[] days = range.getDays();
        for (int i = 0; i < days.length-1 ; i++) {
            message.append(days[i] + ",");
        }
        message.append(days[days.length-1] );
        return message.toString();
    }
    
	/**
	 * 平面表中的层级条件的特殊处理
	 * @param layerJson
	 */
	public static String handleLayerCondition(String layerJson) {
	    return layerJson;
	}
}
