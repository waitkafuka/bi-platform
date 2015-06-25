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

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ma.PrepareModelObject4Test;
import com.google.common.collect.Maps;

/**
 * PlaneTableUtil测试类
 * @author jiangjiangyichao
 * @version 2015年6月3日
 * @since jdk 1.8 or after
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/test/resources/applicationContext.xml")
public class PlaneTableUtilsTest {
    /**
     * 密钥key
     */
    @Value("${biplatform.ma.ser_key}")
    private String securityKey;
    
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
     * 
     * testHandleTimeCondition
     */
    @Test
    public void testHandleTimeConditionNull() {
        
        /**
         * 测试空cube
         */
        Assert.assertEquals(Maps.newHashMap(), PlaneTableUtils.handelTimeCondition(null, Maps.newHashMap()));
        
        // 空请求参数
        Cube cube = PrepareModelObject4Test.getCube();
        Assert.assertEquals(Maps.newHashMap(), PlaneTableUtils.handelTimeCondition(cube, null));
    }
    
    /**
     * 
     * testHandleTimeCondition
     */
    @Test
    public void testHandleTimeCondition() {       
        // 获取cube
        Cube cube = PrepareModelObject4Test.getCube();
        // 测试天粒度
        String dayId = "3da5f26e1ec5244c5b0cdbf4ced9ac73";       
        Map<String, Object> requestParams = Maps.newHashMap();
        String dayValue = "{'start':'2015-06-03','end':'2015-06-03','granularity':'D'}";        
        requestParams.put("testId", dayValue);        
        Map<String, Object> actualParams = PlaneTableUtils.handelTimeCondition(cube, requestParams);
        Assert.assertNotNull(actualParams);
        Assert.assertTrue(actualParams.containsKey(dayId));      
    }
    
    /**
     * 
     * testHandleTimeWithNull
     */
    @Test
    public void testHandleTimeDimNull() {
        // 维度值为null
        Cube cube = PrepareModelObject4Test.getCube();
        ((MiniCube) cube).setDimensions(null);
        Map<String, Object> params = Maps.newHashMap();
        params.put("test1", "test1");
        params.put("test2", "test2");
        String dayValue = "{'start':'2015-06-03','end':'2015-06-03','granularity':'D'}";
        params.put("time", dayValue);
        Assert.assertEquals(params, PlaneTableUtils.handelTimeCondition(cube, params));
    }
    
    /**
     * cube中没有时间维度
     * testHanleNoTimeDim
     */
    @Test
    public void testHanleNoTimeDim() {
        Cube cube = PrepareModelObject4Test.getCube();
        Map<String, Dimension> dimensions = Maps.newHashMap();
        dimensions.put("dimKey", Mockito.mock(Dimension.class));
        ((MiniCube) cube).setDimensions(dimensions);
        Map<String, Object> params = Maps.newHashMap();
        params.put("test1", "test1");
        params.put("test2", "test2");
        String dayValue = "{'start':'2015-06-03','end':'2015-06-03','granularity':'D'}";
        params.put("time", dayValue);
        Assert.assertEquals(params, PlaneTableUtils.handelTimeCondition(cube, params));
    }
    
    /**
     * 
     * testHandleTimeWrongJson
     */
    @Test
    public void testHandleTimeWrongJson() {
        Cube cube = PrepareModelObject4Test.getCube();
        Map<String, Object> params = Maps.newHashMap();
        params.put("test1", "test1");
        params.put("test2", "test2");
        String dayValue = "{'start':'2015-06-03','end'2015-06-03','granularity':'D'}";
        params.put("time", dayValue);
        try {
            PlaneTableUtils.handelTimeCondition(cube, params);            
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }
    
    /**
     * 参数数值为数组
     * testHandleTimeWithParamsArray
     */
    @Test
    public void testHandleTimeWithParamsArray() {
        Cube cube = PrepareModelObject4Test.getCube();
        Map<String, Object> params = Maps.newHashMap();
        params.put("test1", new String[] {"test1_1", "test1_2"});
        params.put("test2", "test2");
        Assert.assertEquals(params, PlaneTableUtils.handelTimeCondition(cube, params));
    }
    
    /**
     * 
     * testHandleTimeWithDifferentGra
     */
    @Test
    public void testHandleTimeWithDifferentGra() {
        // 获取cube
        Cube cube = PrepareModelObject4Test.getCube();
        // 测试天粒度
        String dayId = "3da5f26e1ec5244c5b0cdbf4ced9ac73";       
        Map<String, Object> requestParams = Maps.newHashMap();
        String dayValue = "{'start':'2015-06-03','end':'2015-06-03','granularity':'D'}";        
        requestParams.put("testId", dayValue);        
        Map<String, Object> actualParams = PlaneTableUtils.handelTimeCondition(cube, requestParams);
        Assert.assertNotNull(actualParams);
        Assert.assertTrue(actualParams.containsKey(dayId));  
             
        // 测试周粒度
        String weekId = "5cdf3831879c5e48ec25b529258b6ad6";
        String weekValue = "{'start':'2015-06-01','end':'2015-06-01','granularity':'W'}";
        requestParams.put("testId", weekValue);
        actualParams = PlaneTableUtils.handelTimeCondition(cube, requestParams);
        Assert.assertNotNull(actualParams);
        Assert.assertTrue(actualParams.containsKey(weekId));
        
        // 测试月粒度
        String monthId = "ab2b863f9673567ea1e634742d910bd0";
        String monthValue = "{'start':'2015-06','end':'2015-06','granularity':'M'}";
        requestParams.put("testId", monthValue);
        actualParams = PlaneTableUtils.handelTimeCondition(cube, requestParams);
        Assert.assertNotNull(actualParams);
        Assert.assertTrue(actualParams.containsKey(monthId));
        
        // 测试季粒度
        String quarterId = "9ac1ea7cc2ede6abe5c72217235f8434";
        String quarterValue = "{'start':'2015-Q2','end':'2015-Q2','granularity':'Q'}";
        requestParams.put("testId", quarterValue);
        actualParams = PlaneTableUtils.handelTimeCondition(cube, requestParams);
        Assert.assertNotNull(actualParams);
        Assert.assertTrue(actualParams.containsKey(quarterId));
        
        // 测试年粒度
        String yearValue = "{'start':'2015','end':'2015','granularity':'Y'}";
        requestParams.put("testId", yearValue);
        actualParams = PlaneTableUtils.handelTimeCondition(cube, requestParams);
        Assert.assertNotNull(actualParams);
        Assert.assertEquals(requestParams, actualParams);
    }
    
    
}
