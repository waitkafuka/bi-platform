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
package com.baidu.rigel.biplatform.ac.util;

import java.net.URLDecoder;
import java.net.URLEncoder;

import org.junit.Assert;
import org.junit.Test;

/**
 * Des+base64加密测试
 * 
 * @author luowenlei
 *
 */
public class DesCoderUtilTest {

    /**
     * util des工具类instantce
     */
    private DesCoderUtil util = new DesCoderUtil();

    /**
     * 测试使用密钥的加密
     * 
     * @throws Exception 调用异常
     */
    @Test
    public void testEncryptAndDecrypt() throws Exception {
    	 String data = "{\"cube\":{\"dimensions\":{\"td_area_city_name\":{\"tableName\":\"td_area\",\"type\":\"STANDARD_DIMENSION\",\"levels\":{\"city_name\":{\"source\":\"city_name\",\"type\":\"REGULAR\",\"dimTable\":\"td_area\",\"factTableColumn\":\"area_id\",\"id\":\"bb9d94bc7b747034e893bc62896b63fc\",\"name\":\"city_name\",\"caption\":\"城市名称\",\"visible\":true,\"primaryKey\":\"area_id\"}},\"facttableColumn\":\"area_id\",\"facttableCaption\":\"所在地区Id\",\"id\":\"6d6db1131533c1af6dbbc13a93ef7ee7\",\"name\":\"td_area_city_name\",\"caption\":\"城市名称\",\"visible\":true,\"primaryKey\":\"area_id\"},\"td_area_province_name\":{\"tableName\":\"td_area\",\"type\":\"STANDARD_DIMENSION\",\"levels\":{\"province_name\":{\"source\":\"province_name\",\"type\":\"REGULAR\",\"dimTable\":\"td_area\",\"factTableColumn\":\"area_id\",\"id\":\"eef6fd7010a5994c1e7dab369527a9a4\",\"name\":\"province_name\",\"caption\":\"省名称\",\"visible\":true,\"primaryKey\":\"area_id\"}},\"facttableColumn\":\"area_id\",\"facttableCaption\":\"所在地区Id\",\"id\":\"6492856d700caa8e8af8edfb4c018efd\",\"name\":\"td_area_province_name\",\"caption\":\"省名称\",\"visible\":true,\"primaryKey\":\"area_id\"}},\"measures\":{\"last_month_cash\":{\"aggregator\":\"SUM\",\"type\":\"COMMON\",\"define\":\"last_month_cash\",\"id\":\"56d5fdae6d3f325c6a9cae0cb2ffe255\",\"name\":\"last_month_cash\",\"caption\":\"上月消费\",\"visible\":true},\"this_month_cash\":{\"aggregator\":\"SUM\",\"type\":\"COMMON\",\"define\":\"this_month_cash\",\"id\":\"5b5e995254b21c3b80578e4018084f54\",\"name\":\"this_month_cash\",\"caption\":\"本月消费\",\"visible\":true}},\"enableCache\":true,\"source\":\"tb_customer_cash_psum\",\"mutilple\":false,\"productLine\":\"tieba\",\"id\":\"51d6e433307a51392aba845f2177a21a_c9e8d2c891c754dd774ddbafab74bef2\",\"name\":\"51d6e433307a51392aba845f2177a21a\",\"visible\":true},\"dataSourceInfo\":{\"dataSourceKey\":\"70c160f1b99b3965bc897141a037e1e2\",\"hosts\":[\"10.48.23.45:8306\"],\"jdbcUrls\":[\"jdbc:mysql://10.48.23.45:8306/lltest\"],\"username\":\"mdm\",\"password\":\"mQ/rJr6aMANggsOhtfKceg\u003d\u003d\",\"instanceName\":\"lltest\",\"isDBProxy\":true,\"dataBase\":\"MYSQL\",\"productLine\":\"tieba\",\"dataSourceType\":\"SQL\"},\"axisMetas\":{\"FILTER\":{\"crossjoinDims\":[\"td_area_province_name\"],\"axisType\":\"FILTER\"},\"ROW\":{\"crossjoinDims\":[\"td_area_city_name\"],\"axisType\":\"ROW\"},\"COLUMN\":{\"queryMeasures\":[\"this_month_cash\",\"last_month_cash\"],\"axisType\":\"COLUMN\"}},\"queryConditions\":{\"td_area_city_name\":{\"queryDataNodes\":[],\"metaName\":\"td_area_city_name\",\"metaType\":\"Dimension\",\"memberSortType\":\"ASC\"},\"td_area_province_name\":{\"queryDataNodes\":[{\"uniqueName\":\"[td_area_province_name].[河北省]\",\"show\":true,\"expand\":false}],\"metaName\":\"td_area_province_name\",\"metaType\":\"Dimension\",\"memberSortType\":\"ASC\"}},\"requestParams\":{\"_cltimezone\":\"-480\",\"_cltime\":\"1432020553070\",\"componentId\":\"c9e8d2c891c754dd774ddbafab74bef2\",\"reportId\":\"bcc48170812fb1a8695305fa91d3eaff\",\"_V_SRC\":\"PROD\",\"6492856d700caa8e8af8edfb4c018efd\":\"[td_area_province_name].[河北省]\"},\"sortRecord\":{\"sortType\":\"DESC\",\"sortColumnUniquename\":\"[Measure].[this_month_cash]\",\"recordSize\":500},\"cubeId\":\"51d6e433307a51392aba845f2177a21a\",\"useCache\":true,\"queryConditionLimit\":{\"warnningConditionSize\":50000,\"warningAtOverFlow\":false},\"needSummary\":false,\"isUseIndex\":true,\"filterBlank\":false,\"querySource\":\"TESSERACT\"}";
    	 String dataCopy = new String(data);
    	 String key = "a";
         try {
             data = DesCoderUtil.encrypt(data, key);
             data = URLEncoder.encode(data, "utf-8");
             // 网络传输
             data = URLDecoder.decode(data, "utf-8");
             String res = DesCoderUtil.decrypt(data, key);
             Assert.assertTrue(res.equals(dataCopy));
         } catch (Exception e) {
        	 Assert.fail();
         }
    }
    
    /**
     * 测试使用密钥的加密
     * 
     * @throws Exception 调用异常
     */
    @Test
    public void testEncryptAndDecrypt2() throws Exception {
         String data = "{\"cube\":{\"dimensions\":{\"td_area_city_name\":{\"tableName\":\"td_area\",\"type\":\"STANDARD_DIMENSION\",\"levels\":{\"city_name\":{\"source\":\"city_name\",\"type\":\"REGULAR\",\"dimTable\":\"td_area\",\"factTableColumn\":\"area_id\",\"id\":\"bb9d94bc7b747034e893bc62896b63fc\",\"name\":\"city_name\",\"caption\":\"城市名称\",\"visible\":true,\"primaryKey\":\"area_id\"}},\"facttableColumn\":\"area_id\",\"facttableCaption\":\"所在地区Id\",\"id\":\"6d6db1131533c1af6dbbc13a93ef7ee7\",\"name\":\"td_area_city_name\",\"caption\":\"城市名称\",\"visible\":true,\"primaryKey\":\"area_id\"},\"td_area_province_name\":{\"tableName\":\"td_area\",\"type\":\"STANDARD_DIMENSION\",\"levels\":{\"province_name\":{\"source\":\"province_name\",\"type\":\"REGULAR\",\"dimTable\":\"td_area\",\"factTableColumn\":\"area_id\",\"id\":\"eef6fd7010a5994c1e7dab369527a9a4\",\"name\":\"province_name\",\"caption\":\"省名称\",\"visible\":true,\"primaryKey\":\"area_id\"}},\"facttableColumn\":\"area_id\",\"facttableCaption\":\"所在地区Id\",\"id\":\"6492856d700caa8e8af8edfb4c018efd\",\"name\":\"td_area_province_name\",\"caption\":\"省名称\",\"visible\":true,\"primaryKey\":\"area_id\"}},\"measures\":{\"last_month_cash\":{\"aggregator\":\"SUM\",\"type\":\"COMMON\",\"define\":\"last_month_cash\",\"id\":\"56d5fdae6d3f325c6a9cae0cb2ffe255\",\"name\":\"last_month_cash\",\"caption\":\"上月消费\",\"visible\":true},\"this_month_cash\":{\"aggregator\":\"SUM\",\"type\":\"COMMON\",\"define\":\"this_month_cash\",\"id\":\"5b5e995254b21c3b80578e4018084f54\",\"name\":\"this_month_cash\",\"caption\":\"本月消费\",\"visible\":true}},\"enableCache\":true,\"source\":\"tb_customer_cash_psum\",\"mutilple\":false,\"productLine\":\"tieba\",\"id\":\"51d6e433307a51392aba845f2177a21a_c9e8d2c891c754dd774ddbafab74bef2\",\"name\":\"51d6e433307a51392aba845f2177a21a\",\"visible\":true},\"dataSourceInfo\":{\"dataSourceKey\":\"70c160f1b99b3965bc897141a037e1e2\",\"hosts\":[\"10.48.23.45:8306\"],\"jdbcUrls\":[\"jdbc:mysql://10.48.23.45:8306/lltest\"],\"username\":\"mdm\",\"password\":\"mQ/rJr6aMANggsOhtfKceg\u003d\u003d\",\"instanceName\":\"lltest\",\"isDBProxy\":true,\"dataBase\":\"MYSQL\",\"productLine\":\"tieba\",\"dataSourceType\":\"SQL\"},\"axisMetas\":{\"FILTER\":{\"crossjoinDims\":[\"td_area_province_name\"],\"axisType\":\"FILTER\"},\"ROW\":{\"crossjoinDims\":[\"td_area_city_name\"],\"axisType\":\"ROW\"},\"COLUMN\":{\"queryMeasures\":[\"this_month_cash\",\"last_month_cash\"],\"axisType\":\"COLUMN\"}},\"queryConditions\":{\"td_area_city_name\":{\"queryDataNodes\":[],\"metaName\":\"td_area_city_name\",\"metaType\":\"Dimension\",\"memberSortType\":\"ASC\"},\"td_area_province_name\":{\"queryDataNodes\":[{\"uniqueName\":\"[td_area_province_name].[河北省]\",\"show\":true,\"expand\":false}],\"metaName\":\"td_area_province_name\",\"metaType\":\"Dimension\",\"memberSortType\":\"ASC\"}},\"requestParams\":{\"_cltimezone\":\"-480\",\"_cltime\":\"1432020553070\",\"componentId\":\"c9e8d2c891c754dd774ddbafab74bef2\",\"reportId\":\"bcc48170812fb1a8695305fa91d3eaff\",\"_V_SRC\":\"PROD\",\"6492856d700caa8e8af8edfb4c018efd\":\"[td_area_province_name].[河北省]\"},\"sortRecord\":{\"sortType\":\"DESC\",\"sortColumnUniquename\":\"[Measure].[this_month_cash]\",\"recordSize\":500},\"cubeId\":\"51d6e433307a51392aba845f2177a21a\",\"useCache\":true,\"queryConditionLimit\":{\"warnningConditionSize\":50000,\"warningAtOverFlow\":false},\"needSummary\":false,\"isUseIndex\":true,\"filterBlank\":false,\"querySource\":\"TESSERACT\"}";
         String dataCopy = new String(data);
         try {
             data = DesCoderUtil.encrypt(data);
             data = URLEncoder.encode(data, "utf-8");
             // 网络传输
             data = URLDecoder.decode(data, "utf-8");
             String res = DesCoderUtil.decrypt(data);
             Assert.assertTrue(res.equals(dataCopy));
         } catch (Exception e) {
             Assert.fail();
         }
    }
    
    /**
     * 测试使用密钥的加密
     * 
     * @throws Exception 调用异常
     */
    @Test
    public void testEncryptException() throws Exception {
    	 String data = "我是learn_more，who are you?";
    	 String key = "a";
    	 // test encrypt exception
         try {
             data = DesCoderUtil.encrypt(data, null);
         } catch (Exception e) {
        	 data = DesCoderUtil.encrypt(data, "a");
         }
         
         try {
        	 data = DesCoderUtil.encrypt(null, key);
         } catch (Exception e) {
        	 data = DesCoderUtil.encrypt(key, key);
         }
         
         try {
             data = DesCoderUtil.encrypt(null);
         } catch (Exception e) {
             data = DesCoderUtil.encrypt(data);
         }
         
         // test decrypt exception
         try {
             data = DesCoderUtil.decrypt(data, null);
         } catch (Exception e) {
        	 data = DesCoderUtil.decrypt(data, "a");
         }
         
         try {
        	 data = DesCoderUtil.decrypt(null, key);
         } catch (Exception e) {
        	 data = DesCoderUtil.decrypt(key, key);
         }
         
         try {
             data = DesCoderUtil.decrypt(null);
         } catch (Exception e) {
             data = DesCoderUtil.decrypt(data);
         }
    }

}
