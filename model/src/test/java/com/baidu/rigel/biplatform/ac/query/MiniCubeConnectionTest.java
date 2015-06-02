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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo;
import com.baidu.rigel.biplatform.ac.util.ConfigInfoUtils;
import com.baidu.rigel.biplatform.ac.util.HttpRequest;

/**
 *Description:
 * @author david.wang
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpRequest.class, ConfigInfoUtils.class})
public class MiniCubeConnectionTest {
    
    @Test
    public void testPublishCubes () throws Exception {
//        Cube cube = PowerMockito.mock (Cube.class);
//        List<Cube> cubes = new ArrayList<> ();
//        cubes.add (cube);
//        PowerMockito.mockStatic (HttpRequest.class);
//        PowerMockito.doReturn ("{status : 0, data : test}")
//            .when (HttpRequest.class, "sendPost", "/test/publish", new HashMap<String, String>());
//        PowerMockito.mockStatic (ConfigInfoUtils.class);
//        PowerMockito.doReturn ("/test").when (ConfigInfoUtils.class, "getServerAddress");
//        MiniCubeConnection conn = new MiniCubeSqlConnection (new SqlDataSourceInfo("test"));
//        Assert.assertTrue (conn.publishCubes (cubes, new SqlDataSourceInfo("test")));
    }
}
