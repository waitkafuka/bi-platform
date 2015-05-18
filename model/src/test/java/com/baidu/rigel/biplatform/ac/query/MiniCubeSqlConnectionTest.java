
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

package com.baidu.rigel.biplatform.ac.query;

import java.io.IOException;
import java.util.HashMap;



import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.baidu.rigel.biplatform.ac.query.data.DataModel;
import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;
import com.baidu.rigel.biplatform.ac.util.ConfigInfoUtils;
import com.baidu.rigel.biplatform.ac.util.HttpRequest;
import com.baidu.rigel.biplatform.ac.util.JsonUnSeriallizableUtils;

/**
 * 
 * @author xiaoming.chen
 * @version 2014年11月27日
 * @since jdk 1.8 or after
 */
@RunWith(PowerMockRunner.class)
public class MiniCubeSqlConnectionTest {

    /**
     * Test method for
     * {@link com.baidu.rigel.biplatform.ac.query.MiniCubeSqlConnection#query(com.baidu.rigel.biplatform.ac.query.model.QuestionModel)}
     * .
     * 
     * @throws IOException
     */
    @Test
    @PrepareForTest({HttpRequest.class, ConfigInfoUtils.class, JsonUnSeriallizableUtils.class})
    public void testQuery() throws Exception {
        PowerMockito.mockStatic (HttpRequest.class);
        PowerMockito.doReturn ("{status : 0, data : test}")
            .when (HttpRequest.class, "sendPost", "/test/query", new HashMap<String, String>());
        PowerMockito.mockStatic (ConfigInfoUtils.class);
        PowerMockito.doReturn ("/test").when (ConfigInfoUtils.class, "getServerAddress");
        PowerMockito.mockStatic (JsonUnSeriallizableUtils.class);
        PowerMockito.doReturn (new DataModel()).when (JsonUnSeriallizableUtils.class, "dataModelFromJson", "test");
        SqlDataSourceInfo info = new SqlDataSourceInfo("test");
        MiniCubeSqlConnection conn = new MiniCubeSqlConnection (info);
        QuestionModel questionModel = PowerMockito.mock (QuestionModel.class);
        DataModel rs = conn.query (questionModel);
        Assert.assertNotNull (rs);
    }

}
