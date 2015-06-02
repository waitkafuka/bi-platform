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

import com.baidu.rigel.biplatform.ac.minicube.MiniCubeDimension;
import com.baidu.rigel.biplatform.ac.minicube.TimeDimension;
import com.baidu.rigel.biplatform.ac.model.OlapElement;
import com.baidu.rigel.biplatform.ac.model.Schema;
import com.baidu.rigel.biplatform.ac.util.AnswerCoreConstant;
import com.baidu.rigel.biplatform.ma.report.model.LiteOlapExtendArea;
import com.baidu.rigel.biplatform.ma.resource.view.liteolap.IndCandicateForChart;
import com.baidu.rigel.biplatform.ma.resource.view.liteolap.MetaData;
import com.baidu.rigel.biplatform.ma.resource.view.liteolap.MetaStatusData;

/**
 * 
 * LiteOlapView操作工具测试类
 *
 * @author luowenlei
 * @version 1.0.0.1
 */
public class LiteOlapViewUtilsTest {
    
    /**
     * 测试的LiteOlapExtendJson
     */
    public static String testLiteOlapExtendJson = "{\"selectionAreaId\":\"de7c2119e1bf80b88e01eef97c12bcff\","
            + "\"tableAreaId\":\"3d7fb4d40119c001ff15f5bc4aaed18f\","
            + "\"chartAreaId\":\"9ba105e62fe7a89872237cf4796b43a7\","
            + "\"candDims\":{\"cc72b5b0c600ab9de45461a4c3b18d70\":{\"id\":\"cc72b5b0c600ab9de45461a4c3b18d70\","
            + "\"cubeId\":\"79fc851cea4dc7d09492a430fbeb7c39\",\"schemaId\":\"ebfb78fcaecece539689f5ec353b9765\","
            + "\"reportId\":\"4001b325e0d9a65ccdf2d045c2688a53\","
            + "\"olapElementId\":\"cc72b5b0c600ab9de45461a4c3b18d70\",\"areaId\":\"e50c6910dc1969992d70af87857cc03e\","
            + "\"params\":{},\"positionType\":\"S\"}},"
            + "\"candInds\":{\"0822254b1a448221ad26dcc55b072141\":{\"id\":\"0822254b1a448221ad26dcc55b072141\","
            + "\"cubeId\":\"79fc851cea4dc7d09492a430fbeb7c39\",\"schemaId\":\"ebfb78fcaecece539689f5ec353b9765\","
            + "\"reportId\":\"4001b325e0d9a65ccdf2d045c2688a53\","
            + "\"olapElementId\":\"0822254b1a448221ad26dcc55b072141\",\"areaId\":\"e50c6910dc1969992d70af87857cc03e\","
            + "\"params\":{},\"positionType\":\"X\"},"
            + "\"81687190ae9647ed36b4316b5c2ff2bc\":{\"id\":\"81687190ae9647ed36b4316b5c2ff2bc\","
            + "\"cubeId\":\"79fc851cea4dc7d09492a430fbeb7c39\",\"schemaId\":\"ebfb78fcaecece539689f5ec353b9765\","
            + "\"reportId\":\"4001b325e0d9a65ccdf2d045c2688a53\","
            + "\"olapElementId\":\"81687190ae9647ed36b4316b5c2ff2bc\",\"areaId\":\"e50c6910dc1969992d70af87857cc03e\","
            + "\"params\":{},\"positionType\":\"X\"}},\"id\":\"e50c6910dc1969992d70af87857cc03e\","
            + "\"cubeId\":\"79fc851cea4dc7d09492a430fbeb7c39\",\"logicModel\":{"
            + "\"columns\":{\"0822254b1a448221ad26dcc55b072141\":{\"id\":\"0822254b1a448221ad26dcc55b072141\","
            + "\"cubeId\":\"79fc851cea4dc7d09492a430fbeb7c39\",\"schemaId\":\"ebfb78fcaecece539689f5ec353b9765\","
            + "\"reportId\":\"4001b325e0d9a65ccdf2d045c2688a53\","
            + "\"olapElementId\":\"0822254b1a448221ad26dcc55b072141\","
            + "\"areaId\":\"e50c6910dc1969992d70af87857cc03e\","
            + "\"params\":{},\"positionType\":\"Y\"},\"81687190ae9647ed36b4316b5c2ff2bc\":{"
            + "\"id\":\"81687190ae9647ed36b4316b5c2ff2bc\",\"cubeId\":\"79fc851cea4dc7d09492a430fbeb7c39\","
            + "\"schemaId\":\"ebfb78fcaecece539689f5ec353b9765\",\"reportId\":\"4001b325e0d9a65ccdf2d045c2688a53\","
            + "\"olapElementId\":\"81687190ae9647ed36b4316b5c2ff2bc\",\"areaId\":\"e50c6910dc1969992d70af87857cc03e\","
            + "\"params\":{},\"positionType\":\"Y\"}},\"rows\":{\"cc72b5b0c600ab9de45461a4c3b18d70\":{"
            + "\"id\":\"cc72b5b0c600ab9de45461a4c3b18d70\",\"cubeId\":\"79fc851cea4dc7d09492a430fbeb7c39\","
            + "\"schemaId\":\"ebfb78fcaecece539689f5ec353b9765\",\"reportId\":\"4001b325e0d9a65ccdf2d045c2688a53\","
            + "\"olapElementId\":\"cc72b5b0c600ab9de45461a4c3b18d70\",\"areaId\":\"e50c6910dc1969992d70af87857cc03e\","
            + "\"params\":{},\"positionType\":\"X\"}},\"slices\":{\"cc72b5b0c600ab9de45461a4c3b18d70\":{"
            + "\"id\":\"cc72b5b0c600ab9de45461a4c3b18d70\",\"cubeId\":\"79fc851cea4dc7d09492a430fbeb7c39\","
            + "\"schemaId\":\"ebfb78fcaecece539689f5ec353b9765\",\"reportId\":\"4001b325e0d9a65ccdf2d045c2688a53\","
            + "\"olapElementId\":\"cc72b5b0c600ab9de45461a4c3b18d70\",\"areaId\":\"e50c6910dc1969992d70af87857cc03e\","
            + "\"params\":{},\"positionType\":\"S\"}},\"selectionDims\":{},\"selectionMeasures\":{}},"
            + "\"type\":\"LITEOLAP\",\"formatModel\":{\"dataFormat\":{\"last_month_cash\":\"I.DD%\","
            + "\"this_month_cash\":\"I,III.DD\",\"defaultFormat\":\"I,III\"},\"conditionFormat\":{},"
            + "\"toolTips\":{\"last_month_cash\":\"last_month_cash\","
            + "\"this_month_cash\":\"this_month_cash\"},\"colorFormat\":{},"
            + "\"positions\":{\"last_month_cash\":\"0\",\"this_month_cash\":\"0\"},"
            + "\"textAlignFormat\":{\"last_month_cash\":\"left\",\"this_month_cash\":\"left\"}},\"otherSetting\":{}}";
    
    public static String schemaJson = "{\"cubes\":{\"79fc851cea4dc7d09492a430fbeb7c39\":{\"dimensions\":{\"cc72b5b0c600ab9de45461a4c3b18d70\":{\"tableName\":\"td_area\","
            + "\"type\":\"STANDARD_DIMENSION\","
            + "\"levels\":{\"d7346c3612312ef26ebe6568fac956e5\":{\"source\":\"province_name\","
            + "\"type\":\"REGULAR\","
            + "\"dimTable\":\"td_area\","
            + "\"factTableColumn\":\"area_id\","
            + "\"id\":\"d7346c3612312ef26ebe6568fac956e5\","
            + "\"name\":\"province_name\","
            + "\"caption\":\"省名称\","
            + "\"visible\":true,"
            + "\"primaryKey\":\"area_id\"}},"
            + "\"facttableColumn\":\"area_id\","
            + "\"facttableCaption\":\"所在地区Id\","
            + "\"id\":\"cc72b5b0c600ab9de45461a4c3b18d70\","
            + "\"name\":\"td_area_province_name\","
            + "\"caption\":\"省名称\","
            + "\"visible\":true,"
            + "\"primaryKey\":\"area_id\"},"
            + "\"df7464aca31fd7943b496f01f697e161\":{\"tableName\":\"td_area\","
            + "\"type\":\"STANDARD_DIMENSION\","
            + "\"levels\":{\"ee48883c6de109fbaf725a28d14fcc95\":{\"source\":\"city_name\","
            + "\"type\":\"REGULAR\","
            + "\"dimTable\":\"td_area\","
            + "\"factTableColumn\":\"area_id\","
            + "\"id\":\"ee48883c6de109fbaf725a28d14fcc95\","
            + "\"name\":\"city_name\","
            + "\"caption\":\"城市名称\","
            + "\"visible\":true,"
            + "\"primaryKey\":\"area_id\"}},"
            + "\"facttableColumn\":\"area_id\","
            + "\"facttableCaption\":\"所在地区Id\","
            + "\"id\":\"df7464aca31fd7943b496f01f697e161\","
            + "\"name\":\"td_area_city_name\","
            + "\"caption\":\"城市名称\","
            + "\"visible\":true,"
            + "\"primaryKey\":\"area_id\"}},"
            + "\"measures\":{\"0822254b1a448221ad26dcc55b072141\":{\"aggregator\":\"SUM\","
            + "\"type\":\"COMMON\","
            + "\"define\":\"last_month_cash\","
            + "\"id\":\"0822254b1a448221ad26dcc55b072141\","
            + "\"name\":\"last_month_cash\","
            + "\"caption\":\"上月消费\","
            + "\"visible\":true},"
            + "\"81687190ae9647ed36b4316b5c2ff2bc\":{\"aggregator\":\"SUM\","
            + "\"type\":\"COMMON\","
            + "\"define\":\"this_month_cash\","
            + "\"id\":\"81687190ae9647ed36b4316b5c2ff2bc\","
            + "\"name\":\"this_month_cash\","
            + "\"caption\":\"本月消费\","
            + "\"visible\":true},"
            + "\"c25357c7ec63a47c9d83be3a2cdd5439\":{\"aggregator\":\"SUM\","
            + "\"type\":\"COMMON\","
            + "\"define\":\"customer_id\","
            + "\"id\":\"c25357c7ec63a47c9d83be3a2cdd5439\","
            + "\"name\":\"customer_id\","
            + "\"caption\":\"客户ID\","
            + "\"visible\":true},"
            + "\"aade54a3e4c52a23cddf5e7e94e9b104\":{\"aggregator\":\"SUM\","
            + "\"type\":\"COMMON\","
            + "\"define\":\"pdate\","
            + "\"id\":\"aade54a3e4c52a23cddf5e7e94e9b104\","
            + "\"name\":\"pdate\","
            + "\"caption\":\"消费日期\","
            + "\"visible\":true},"
            + "\"4565a7cfa5848f536b74d2f46cf60751\":{\"aggregator\":\"SUM\","
            + "\"type\":\"COMMON\","
            + "\"define\":\"cash\","
            + "\"id\":\"4565a7cfa5848f536b74d2f46cf60751\","
            + "\"name\":\"cash\","
            + "\"caption\":\"账户余额\","
            + "\"visible\":true}},"
            + "\"enableCache\":true,"
            + "\"source\":\"tb_customer_cash_psum\","
            + "\"mutilple\":false,"
            + "\"id\":\"79fc851cea4dc7d09492a430fbeb7c39\","
            + "\"name\":\"cube_tb_customer_cash_psum\","
            + "\"caption\":\"tb_customer_cash_psum\","
            + "\"visible\":true}},"
            + "\"datasource\":\"70c160f1b99b3965bc897141a037e1e2\","
            + "\"id\":\"ebfb78fcaecece539689f5ec353b9765\","
            + "\"name\":\"schema_ebfb78fcaecece539689f5ec353b9765\"," + "\"visible\":true}";
    
    @Test
    public void testParseMetaData() {
        LiteOlapExtendArea liteOlapExtendArea = AnswerCoreConstant.GSON.fromJson(
                testLiteOlapExtendJson, LiteOlapExtendArea.class);
        Schema schema = AnswerCoreConstant.GSON.fromJson(schemaJson, Schema.class);
        MetaData metaData = LiteOlapViewUtils.parseMetaData(liteOlapExtendArea, schema);
        Assert.assertNotNull(metaData);
        return;
    }
    
    @Test
    public void testParseMetaStatusData() {
        LiteOlapExtendArea liteOlapExtendArea = AnswerCoreConstant.GSON.fromJson(
                testLiteOlapExtendJson, LiteOlapExtendArea.class);
        Schema schema = AnswerCoreConstant.GSON.fromJson(schemaJson, Schema.class);
        MetaStatusData metaStatusData = LiteOlapViewUtils.parseMetaStatusData(liteOlapExtendArea,
                schema);
        Assert.assertNotNull(metaStatusData);
        return;
    }
    
    @Test
    public void testParseSelectedItemMap() {
        LiteOlapExtendArea liteOlapExtendArea = AnswerCoreConstant.GSON.fromJson(
                testLiteOlapExtendJson, LiteOlapExtendArea.class);
        Schema schema = AnswerCoreConstant.GSON.fromJson(schemaJson, Schema.class);
        Map<String, Object> map = LiteOlapViewUtils
                .parseSelectedItemMap(liteOlapExtendArea, schema);
        Assert.assertNotNull(map);
        return;
    }
    
    @Test
    public void testParseIndForChart() {
        MiniCubeDimension element = new TimeDimension();
        element.setCaption("test");
        element.setId("test");
        IndCandicateForChart indCandicateForChart = LiteOlapViewUtils
                .parseIndForChart((OlapElement) element);
        Assert.assertTrue(indCandicateForChart.getCaption().equals("test"));
        Assert.assertTrue(indCandicateForChart.getCustIndName().equals("test"));
    }
}
