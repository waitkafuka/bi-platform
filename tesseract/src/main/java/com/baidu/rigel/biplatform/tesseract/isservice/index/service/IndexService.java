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
package com.baidu.rigel.biplatform.tesseract.isservice.index.service;

import java.util.List;

import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.tesseract.exception.DataSourceException;
import com.baidu.rigel.biplatform.tesseract.isservice.exception.IndexAndSearchException;
import com.baidu.rigel.biplatform.tesseract.isservice.exception.IndexMetaIsNullException;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexAction;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexMeta;
import com.baidu.rigel.biplatform.tesseract.netty.exception.HandlerRegistException;

/**
 * 
 * IndexService
 * 
 * @author lijin
 *
 */
public interface IndexService {
    
    /**
     * 
     * doIndex
     * 
     * @param idxMeta
     *            索引元数据
     * @param idxAction
     *            索引动作
     * @return boolean
     * @throws IndexMetaIsNullException
     *             可能抛出的异常
     * @throws IllegalArgumentException
     *             可能抛出的异常
     * @throws DataSourceException
     *             可能抛出的异常
     * @throws InstantiationException
     *             可能抛出的异常
     * @throws IllegalAccessException
     *             可能抛出的异常
     * @throws HandlerRegistException
     *             可能抛出的异常
     * @throws InterruptedException
     *             可能抛出的异常
     */
    boolean doIndex(IndexMeta idxMeta, IndexAction idxAction) throws IndexMetaIsNullException,
        IllegalArgumentException, DataSourceException, InstantiationException,
        IllegalAccessException, HandlerRegistException, InterruptedException, IndexAndSearchException;
    
    /**
     * 
     * initMiniCubeIndex 初始化新Cube索引
     * 
     * @param cubeList
     *            待初始化的cube列表
     * @param dataSourceInfo
     *            数据源信息
     * @param indexAsap
     *            是否立即索引
     * @param limited
     *            是否是sample模式索引
     * @return boolean true表示成功；
     */
    boolean initMiniCubeIndex(List<Cube> cubeList, DataSourceInfo dataSourceInfo,
        boolean indexAsap, boolean limited) throws IndexAndSearchException;
    
    /**
     * 
     * updateIndexByDataSourceKey
     * 
     * @param dataSourceKey
     *            dataSourceKey
     * @throws IndexAndSearchException
     *             可能抛出的异常
     */
    void updateIndexByDataSourceKey(String dataSourceKey) throws IndexAndSearchException;
    
}
