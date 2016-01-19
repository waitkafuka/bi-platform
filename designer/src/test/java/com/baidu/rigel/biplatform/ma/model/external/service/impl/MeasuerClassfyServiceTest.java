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
package com.baidu.rigel.biplatform.ma.model.external.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

import org.h2.tools.Server;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.baidu.rigel.biplatform.ac.util.AesUtil;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceType;
import com.baidu.rigel.biplatform.ma.model.external.vo.MeasureClassfyObject;
import com.google.common.collect.Lists;

public class MeasuerClassfyServiceTest {
    
    /**
     * create table sql
     */
    private final String tableCreateSql = "create table FACT_TAB_COL_META_CLASS("
            + "FIRST_CLASS_TYPE varchar(20),"
            + "FIRST_CLASS_TYPE_NAME varchar(20),"
            + "FIRST_CLASS_TYPE_DESC varchar(20),"
            + "SECOND_CLASS_TYPE varchar(20),"
            + "SECOND_CLASS_TYPE_NAME varchar(20),"
            + "SECOND_CLASS_TYPE_DESC varchar(20),"
            + "THIRD_CLASS_TYPE varchar(20),"
            + "THIRD_CLASS_TYPE_NAME varchar(20),"
            + "THIRD_CLASS_TYPE_DESC varchar(20),"
            + "SELECTED_OPERATION_TYPE varchar(2)"
            + ")";
    
    /**
     * drop table sql
     */
    private final String dropTableSql = "drop table FACT_TAB_COL_META_CLASS";
    
    /**
     * insert sql
     */
    private final String insertSql = "insert into FACT_TAB_COL_META_CLASS"
            + " values ('1', 'test', 'abc', '11', 'test11', 'def', '111', 'test111', 'hij', '0'),"
            + " ('1', 'test', 'abc', '12', 'test12', 'def', '121', 'test121', 'hij', '0'),"
            + " ('1', 'test', 'abc', '11', 'test11', 'def', '112', 'test112', 'hij', '0'),"
            + " ('1', 'test', 'abc', '12', 'test12', 'def', '122', 'test122', 'hij', '0'),"
            + " ('2', 'test', 'abc', '21', 'test21', 'def', '211', 'test211', 'hij', '1');";
    
    
    @Before
    public void preTest() {
        
    }
    
    @Test
    public void testGetChangableMeasureClassfyMeta() throws Exception {
        Server server = Server.createTcpServer ("-tcpAllowOthers").start ();
        Class.forName ("org.h2.Driver");
        Connection conn = DriverManager.getConnection ("jdbc:h2:tcp://127.0.0.1/mem:sample", "sa", "sa");
        PreparedStatement ps = conn.prepareStatement (tableCreateSql);
        ps.execute ();
        ps = conn.prepareStatement (insertSql);
        ps.execute ();
        DataSourceDefine ds = Mockito.mock (DataSourceDefine.class);
        Mockito.doReturn ("sa").when (ds).getDbUser ();
        Mockito.doReturn (AesUtil.getInstance ().encryptAndUrlEncoding ("sa")).when (ds).getDbPwd ();
        DataSourceType.H2.setPrefix ("jdbc:h2:tcp://");
        Mockito.doReturn (DataSourceType.H2).when (ds).getDataSourceType ();
        Mockito.doReturn ("127.0.0.1").when (ds).getHostAndPort ();
        Mockito.doReturn ("mem:sample").when (ds).getDbInstance ();
        String secKey = "0000000000000000";
        List<MeasureClassfyObject> rs = 
            new MeasureClassfyServiceImpl ().getChangableMeasureClassfyMeta ("test", ds, secKey, "");
        Assert.assertEquals (2, rs.size ());
        Assert.assertEquals (2, rs.get (0).getChildren ().size ());
        Assert.assertEquals(1, rs.get (1).getChildren ().size ());
        ps = conn.prepareStatement (dropTableSql);
        
        ps.execute ();
        server.shutdown ();
    }
    
    @Test
    public void testChangalbeMeasuerMeta() throws Exception {
        MeasureClassfyServiceImpl service = Mockito.mock (MeasureClassfyServiceImpl.class);
        DataSourceDefine ds = Mockito.mock (DataSourceDefine.class);
        try {
            service.getChangalbeMeasuerMeta (null, ds, null, "");
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
        try {
            service.getChangalbeMeasuerMeta ("", ds, null, "");
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
        String factTable = "test";
        List<MeasureClassfyObject> tmp = genTestObjInst ();
        Mockito.doReturn (tmp).when (service).getChangableMeasureClassfyMeta (factTable, ds, null, "");
        Mockito.doCallRealMethod ().when (service).getChangalbeMeasuerMeta (factTable, ds, null, "");
        List<String> rs = service.getChangalbeMeasuerMeta (factTable, ds, null, "");
        Assert.assertNotNull (rs);
        Assert.assertEquals (2, rs.size ());
        Assert.assertTrue (!rs.contains ("col3"));
    }

    private List<MeasureClassfyObject> genTestObjInst() {
        List<MeasureClassfyObject> tmp = Lists.newArrayList ();
        MeasureClassfyObject obj = new MeasureClassfyObject ();
        obj.setCaption ("test");
        obj.setName ("test");;
        obj.setChildren (Lists.newArrayList ());
        MeasureClassfyObject c1 = new MeasureClassfyObject ();
        c1.setCaption ("test_col1");
        c1.setName ("test.col1");
        obj.getChildren ().add (c1);
        
        MeasureClassfyObject c2 = new MeasureClassfyObject ();
        c2.setCaption ("test_col2");
        c2.setName ("col2");
        obj.getChildren ().add (c2);
        
        MeasureClassfyObject c3 = new MeasureClassfyObject ();
        c3.setCaption ("test_col3");
        c3.setName ("test1.col3");
        obj.getChildren ().add (c3);
        tmp.add (obj);
        
        return tmp;
    }
}
