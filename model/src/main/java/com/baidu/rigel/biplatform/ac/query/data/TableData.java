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
package com.baidu.rigel.biplatform.ac.query.data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 
 *Description: DataModel数据的二维表展现形式
 * @author david.wang
 *
 */
public class TableData implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = -917029423791246077L;

    private List<Column> columuns;
    
    private List<Map<Column, List<String>>> colBaseDatas;

    /**
     * @return the columuns
     */
    public List<Column> getColumuns() {
        return columuns;
    }

    /**
     * @param columuns the columuns to set
     */
    public void setColumuns(List<Column> columuns) {
        this.columuns = columuns;
    }

    /**
     * @return the colBaseDatas
     */
    public List<Map<Column, List<String>>> getColBaseDatas() {
        return colBaseDatas;
    }

    /**
     * @param colBaseDatas the colBaseDatas to set
     */
    public void setColBaseDatas(List<Map<Column, List<String>>> colBaseDatas) {
        this.colBaseDatas = colBaseDatas;
    }
    
    /**
     * 
     *Description: 数据表列元数据信息描述
     * @author david.wang
     *
     */
    public static class Column implements Serializable {
        
        /**
         * 
         */
        private static final long serialVersionUID = 3151301875582323397L;

        /**
         * 
         */
        public final String name;
        
        /**
         * 
         */
        public final String caption;
        
        /**
         * 
         */
        public final String tableName;
        
        /**
         * 保留字段
         */
        private String dbName;
        
        public Column(String name, String caption, String tableName) {
            super ();
            this.name = name;
            this.caption = caption;
            this.tableName = tableName;
        }

        /**
         * @return the dbName
         */
        public String getDbName() {
            return dbName;
        }

        /**
         * @param dbName the dbName to set
         */
        public void setDbName(String dbName) {
            this.dbName = dbName;
        }
        
        
    }
}
