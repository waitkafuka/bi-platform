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
/**
 * 
 */
package com.baidu.rigel.biplatform.tesseract.isservice.event;

import org.springframework.context.ApplicationEvent;

/**
 * UpdateIndexByDatasourceEvent
 * 
 * @author lijin
 *
 */
public class UpdateIndexByDatasourceEvent extends ApplicationEvent {
    
    /**
     * serialVersionUID long
     */
    private static final long serialVersionUID = 5000031730541321727L;
    /**
     * 要刷新的数据源Key信息
     */
    private String dataSourceKey;
    
    /** 
     * dataSetNames 更新的数据集名称
     */
    private String[] dataSetNames;
    
    /**
     * Constructor by
     * 
     * @param source
     */
    public UpdateIndexByDatasourceEvent(Object source) {
        super(source);
        if (source != null) {
            dataSourceKey = (String) source;
        }
    }
    
    /** 
     * 构造函数
     */
    public UpdateIndexByDatasourceEvent(String dataSourceKey, String[] dataSetNames) {
        this(dataSourceKey);
        this.dataSetNames = dataSetNames;
    }
    
    /**
     * getter method for property dataSourceKey
     * 
     * @return the dataSourceKey
     */
    public String getDataSourceKey() {
        return dataSourceKey;
    }
    
    /**
     * setter method for property dataSourceKey
     * 
     * @param dataSourceKey
     *            the dataSourceKey to set
     */
    public void setDataSourceKey(String dataSourceKey) {
        this.dataSourceKey = dataSourceKey;
    }
    
    /** 
     * getDataSetNames
     * @return
     */
    public String[] getDataSetNames() {
    
        return dataSetNames;
    }

    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UpdateIndexByDatasourceEvent [dataSourceKey=" + dataSourceKey + "]";
    }
    
}
