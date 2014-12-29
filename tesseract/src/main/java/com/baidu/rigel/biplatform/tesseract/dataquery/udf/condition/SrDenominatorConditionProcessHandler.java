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
package com.baidu.rigel.biplatform.tesseract.dataquery.udf.condition;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.baidu.rigel.biplatform.ac.minicube.TimeDimension;
import com.baidu.rigel.biplatform.ac.model.TimeType;
import com.baidu.rigel.biplatform.ac.util.TimeRangeDetail;
import com.baidu.rigel.biplatform.ac.util.TimeUtils;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryContext;

/**
 * Description: SrDenominatorConditionProcessHandler
 * 
 * @author david.wang
 *
 */
class SrDenominatorConditionProcessHandler extends RateConditionProcessHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.baidu.rigel.biplatform.tesseract.dataquery.udf.condition.
	 * AbsRateConditionProcessHandler
	 * #processCondition(com.baidu.rigel.biplatform
	 * .tesseract.qsservice.query.vo.QueryContext)
	 */
	@Override
	public QueryContext processCondition(QueryContext context) {
		/*
		 * 同比分母计算条件，修改时间条件为同期时间 如果时间粒度是天，则同期时间为去年的当天 如果时间粒度是周，则同期时间为去年的当周
		 * 如果时间粒度是月，则同期时间为去年的当月 如果时间粒度是季度，则统计周期为去年的当季度 如果时间粒度是年，则同期时间为去年
		 * 需要QueryContext提供：时间维度定义，当前时间维度过滤条件，此处修改时间范围后，重新设置到查询上下文
		 */
		// TODO 确认一下如何获取时间维度并获取当前时间粒度第一天
		TimeDimension dimension = null;
		TimeType timeType = dimension.getDataTimeType();
		Date firstDayOfTimeRange = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(firstDayOfTimeRange);
		// 将时间调整为上年的同天
		cal.add(Calendar.YEAR, -1);
		TimeRangeDetail timeRange = null;
		switch (timeType) {
		case TimeDay:
			timeRange = TimeUtils.getDays(cal.getTime(), 0, 0);
			break;
		case TimeWeekly:
			// 星期需要特殊处理
			try {
				SimpleDateFormat sdf = new SimpleDateFormat();
				sdf.applyPattern(TimeRangeDetail.FORMAT_STRING);
				int weekIndex = TimeUtils.getWeekIndex(sdf.format(firstDayOfTimeRange));
				cal.set(Calendar.MONTH, 0);
				cal.set(Calendar.DAY_OF_YEAR, 1);
				cal.add(Calendar.DAY_OF_YEAR, 7 * weekIndex);
				timeRange = TimeUtils.getWeekDays(cal.getTime());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			break;
		case TimeMonth:
			timeRange = TimeUtils.getMonthDays(cal.getTime());
			break;
		case TimeQuarter:
			timeRange = TimeUtils.getQuarterDays(cal.getTime());
			break;
		case TimeYear:
			timeRange = TimeUtils.getYearDays(cal.getTime());
			break;
		default:
			throw new RuntimeException("未知的时间维度类型：" + timeType.name());
		}
		if (timeRange == null) {
			throw new RuntimeException("未知的时间维度类型：" + timeType.name());
		}
		// TODO 确认当前统计周期的成员数量，从前一统计周期获取相同数量成员
		String[] days = timeRange.getDays();
		// TODO 确认一下如何修改条件并返回context
		return null;
	}

}
