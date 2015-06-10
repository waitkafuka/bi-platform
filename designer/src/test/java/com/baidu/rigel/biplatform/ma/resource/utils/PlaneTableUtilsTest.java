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

package com.baidu.rigel.biplatform.ma.resource.utils;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PlaneTableUtil测试类
 * @author jiangjiangyichao
 * @version 2015年6月3日
 * @since jdk 1.8 or after
 */
public class PlaneTableUtilsTest {

    /**
     * 日志对象
     */
    private static final Logger LOG = LoggerFactory.getLogger(PlaneTableUtilsTest.class);

    /**
     * 测试检查输入SQL条件是否合理
     */
    @Test
    public void testCheckSQLCondition() {
        // SQL条件和参数值均为null
        Assert.assertFalse(PlaneTableUtils.checkSQLCondition(null, null));
        // SQL条件正常，参数值为null
        Assert.assertFalse(PlaneTableUtils.checkSQLCondition("EQ", null));
        // SQL条件正常，无具体参数值
        Assert.assertFalse(PlaneTableUtils.checkSQLCondition("EQ", ","));

        // 等于条件，参数值为一个
        Assert.assertTrue(PlaneTableUtils.checkSQLCondition("EQ", "test"));
        // 等于条件，参数值为两个
        Assert.assertFalse(PlaneTableUtils.checkSQLCondition("EQ", "test1,test2"));

        // 小于条件，参数值为一个
        Assert.assertTrue(PlaneTableUtils.checkSQLCondition("LT", "test"));
        // 小于条件，参数值为两个
        Assert.assertFalse(PlaneTableUtils.checkSQLCondition("LT", "test1,test2"));

        // between and 条件，参数值为一个
        Assert.assertFalse(PlaneTableUtils.checkSQLCondition("BETWEEN_AND", "test"));
        // between and 条件，参数值为两个
        Assert.assertTrue(PlaneTableUtils.checkSQLCondition("BETWEEN_AND", "test1,test2"));

        // in 条件，参数值为一个
        Assert.assertTrue(PlaneTableUtils.checkSQLCondition("IN", "test"));
        // in 条件，参数值为两个
        Assert.assertTrue(PlaneTableUtils.checkSQLCondition("IN", "test1,test2"));

    }

    /**
     * 测试平面表中对时间条件的特殊处理 testHandleTimeContion
     */
    @Test
    public void testHandleTimeContion() {
        // 时间字符串
        String timeJson = null;
        // 时间字符串为null，原样返回
        Assert.assertEquals(timeJson, PlaneTableUtils.handleTimeCondition(timeJson));

        timeJson = "";
        // 时间字符串为空串，原样返回
        Assert.assertEquals(timeJson, PlaneTableUtils.handleTimeCondition(timeJson));

        timeJson = "timeJson";
        // 时间字符串不包含'start','end','granularity'关键字，原样返回
        Assert.assertEquals(timeJson, PlaneTableUtils.handleTimeCondition(timeJson));

        // 日粒度
        timeJson = "{'start':'2015-06-03','end':'2015-06-03','granularity':'D'}";
        String actualDayTime = PlaneTableUtils.handleTimeCondition(timeJson);
        Assert.assertNotNull(actualDayTime);
        LOG.debug("日粒度:" + actualDayTime);

        // 日粒度, 起始时间大于截止时间
        timeJson = "{'start':'2015-06-03','end':'2015-06-01','granularity':'D'}";
        actualDayTime = PlaneTableUtils.handleTimeCondition(timeJson);
        Assert.assertNotNull(actualDayTime);
        LOG.debug("日粒度:" + actualDayTime);

        // 周粒度
        timeJson = "{'start':'2015-06-01','end':'2015-06-01','granularity':'W'}";
        String actualWeekTime = PlaneTableUtils.handleTimeCondition(timeJson);
        Assert.assertNotNull(actualWeekTime);
        LOG.debug("周粒度:" + actualWeekTime);

        // 月粒度
        timeJson = "{'start':'2015-06','end':'2015-06','granularity':'M'}";
        String actualMonthTime = PlaneTableUtils.handleTimeCondition(timeJson);
        Assert.assertNotNull(actualMonthTime);
        LOG.debug("月粒度:" + actualMonthTime);

        // 季粒度
        timeJson = "{'start':'2015-Q2','end':'2015-Q2','granularity':'Q'}";
        String actualQuarterTime = PlaneTableUtils.handleTimeCondition(timeJson);
        Assert.assertNotNull(actualQuarterTime);
        LOG.debug("季粒度:" + actualQuarterTime);

        // 年粒度
        timeJson = "{'start':'2015','end':'2015','granularity':'Y'}";
        String actualYearTime = PlaneTableUtils.handleTimeCondition(timeJson);
        Assert.assertNotNull(actualYearTime);
        LOG.debug("年粒度:" + actualYearTime);
    }

    /**
     * 测试处理层级条件,TODO后续补充，当前还未实现 testHandleLayerConditon
     */
    public void testHandleLayerConditon() {
        String layerJson = "";
        Assert.assertEquals(layerJson, PlaneTableUtils.handleLayerCondition(layerJson));
    }
}
