

/**
 * Copyright (c) 2015 Baidu, Inc. All Rights Reserved.
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

package com.baidu.rigel.biplatform.ma;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * 准备测试使用的内存数据库
 * @author yichao.jiang 
 * @version  2015年6月20日 
 * @since jdk 1.8 or after
 */
public class PrepareMemDb4Test {
    /**
     * 日志对象
     */
    public static final Logger LOG = LoggerFactory.getLogger(PrepareMemDb4Test.class);
    
    /**
     * 测试数据库
     * @throws Exception
     */
    public static void createMemDb() throws Exception {  
        // 启动服务
        Server server = Server.createTcpServer ("-tcpAllowOthers");
        server.start ();
        // 数据库连接基本信息，并构建内存数据库H2
        String username = "sa";
        String password = "sa";
        String dbInstance = "sample";
        String url = "jdbc:h2:tcp://localhost/~/" + dbInstance ;
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection(url, username, password); 
            if (conn != null) {
                LOG.info("get H2 datasource by username:" + username);                
            } 
            // 创建地域维度表，id、省份、城市
            String sql = "DROP TABLE IF EXISTS dim_district;"
                    + "CREATE TABLE dim_district ("
                    + " id INT(11) NOT NULL , "
                    + " province VARCHAR(45) DEFAULT NULL , "
                    + " city VARCHAR(45)  DEFAULT NULL,"
                    + " PRIMARY KEY (id))"
                    + " ENGINE=INNODB DEFAULT CHARSET=utf8 ";
            PreparedStatement pt = conn.prepareStatement(sql);
            pt.execute();
            
            // 创建事实表
            sql = "DROP TABLE IF EXISTS fact_detail;"
                    + "CREATE TABLE fact_detail("
                    + " id INT(11) NOT NULL ,"
                    + " district_id int(11) NOT NULL,"
                    + " the_date varchar(45) NOT NULL,"
                    + " cash int(11) DEFAULT 0,"
                    + " click int(11) DEFAULT 0,"
                    + " PRIMARY KEY (id)) "
                    + "ENGINE=INNODB DEFAULT CHARSET=utf8 ";
            pt = conn.prepareStatement(sql);
            pt.execute();
            
            // 向维度表中插入数据
            sql = "insert into DIM_DISTRICT(id,province,city ) values(1, '河北','石家庄');"
                    + "insert into DIM_DISTRICT(id,province,city ) values(2, '河南','郑州');";
            pt = conn.prepareStatement(sql);
            pt.execute();
            
            sql = "insert into FACT_DETAIL (id,district_id,the_date,cash,click) values(4,2,'20150620',200,200);"
                    + " insert into FACT_DETAIL (id,district_id,the_date,cash,click) values(3,2,'20150619',100,100);"
                    + " insert into FACT_DETAIL (id,district_id,the_date,cash,click) values(2,1,'20150620',200,200);"
                    + " insert into FACT_DETAIL (id,district_id,the_date,cash,click) values(1,1,'20150619',100,100);";
            pt = conn.prepareStatement(sql);
            pt.execute();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }   
    }
}

