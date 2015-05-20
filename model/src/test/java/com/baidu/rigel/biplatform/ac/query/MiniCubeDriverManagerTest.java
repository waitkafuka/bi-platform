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


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.baidu.rigel.biplatform.ac.exception.MiniCubeQueryException;
import com.baidu.rigel.biplatform.ac.query.MiniCubeConnection.DataSourceType;
import com.baidu.rigel.biplatform.ac.query.data.DataModel;
import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;

/**
 *Description:
 * @author david.wang
 *
 */
public class MiniCubeDriverManagerTest {
    
    @Test
    public void testGetConnection () {
        SqlDataSourceInfo info = Mockito.mock (SqlDataSourceInfo.class);
        Mockito.doReturn (true).when (info).validate ();
        Mockito.doReturn (DataSourceType.SQL).when (info).getDataSourceType ();
        Assert.assertNotNull (MiniCubeDriverManager.getConnection (info));
        
        try {
            MiniCubeDriverManager.getConnection (null);
            Assert.fail ();
        } catch (Exception e) {
            
        }
        
        try {
            SqlDataSourceInfo info1 = Mockito.mock (SqlDataSourceInfo.class);
            Mockito.doReturn (true).when (info1).validate ();
            Mockito.doReturn (DataSourceType.CUSTOM).when (info1).getDataSourceType ();
            MiniCubeDriverManager.getConnection (info1);
            Assert.fail ();
        } catch (Exception e) {
            
        }
        
        MiniCubeDriverManager.registryConn ("HIVE", MiniCubeConnection4Test.class);
        SqlDataSourceInfo info1 = Mockito.mock (SqlDataSourceInfo.class);
        Mockito.doReturn (true).when (info1).validate ();
        Mockito.doReturn (DataSourceType.HIVE).when (info1).getDataSourceType ();
        MiniCubeConnection conn = MiniCubeDriverManager.getConnection (info1);
        Assert.assertNotNull (conn);
        Assert.assertTrue (conn instanceof MiniCubeConnection4Test);
        
    }
    
    public static class MiniCubeConnection4Test implements MiniCubeConnection {
        
        public MiniCubeConnection4Test (DataSourceInfo datasource) {
            
        }
        
        @Override
        public DataModel query(QuestionModel questionModel)
                throws MiniCubeQueryException {
            return null;
        }

        @Override
        public void close() {
            
        }
        
    }
}
