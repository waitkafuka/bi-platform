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
package com.baidu.rigel.biplatform.tesseract.isservice.index.service.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.Measure;
import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.tesseract.dataquery.service.DataQueryService;
import com.baidu.rigel.biplatform.tesseract.isservice.index.service.IndexStrategyService;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.DataDescInfo;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexMeta;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexShard;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexState;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexStrategy;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.SqlQuery;

/**
 * 
 * TODO IndexStrategyService实现类
 * 
 * @author lijin
 *
 */
// @Service("indexStrategyService")
public class IndexStrategyServiceImpl implements IndexStrategyService {
    /**
     * LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexStrategyServiceImpl.class);

    /**
     * dataQueryService
     */
    @Resource(name = "sqlDataQueryService")
    private DataQueryService dataQueryService;

    @Override
    public List<IndexStrategy> getIndexStrategyByCube(List<Cube> cubeList, DataSourceInfo dataSourceInfo) {

        List<IndexStrategy> result = new ArrayList<IndexStrategy>();
        if (cubeList == null || cubeList.size() == 0 || dataSourceInfo == null) {
            LOGGER.info("cubeList or dataSourceInfo in param list has null: [cubeList]:[" + cubeList
                    + "][dataSourceInfo]:[" + dataSourceInfo + "]");
            return result;
        }

        // 一个Cube对应一个IndexStrategy
        for (Cube cube : cubeList) {
            List<SqlQuery> sqlQueryList = new ArrayList<SqlQuery>();
            MiniCube currCube = (MiniCube) cube;

            List<String> selectMeasureList = new ArrayList<String>();
            Map<String, Measure> measureMaps = currCube.getMeasures();
            for (String measureName : measureMaps.keySet()) {
                Measure measure = measureMaps.get(measureName);
                selectMeasureList.add(measure.getName());
            }
            List<String> factTableList = new ArrayList<String>();
            if (currCube.isMutilple()) {
                // 思路：
                // cube.getName()是个prefix+正则表达式
                // 从库中检索prefix开头的表，进行匹配

                // 实现：
                // 取到前缀
                String tablePrefix = currCube.getSource().split("_")[0];
                StringBuffer sb = new StringBuffer("show tables like " + tablePrefix + "%;");
                // 获取 dataSource
                // FIXME wait the interface to fill that code in
                DriverManagerDataSource dataSource = null;
                // 从库中检索prefix开头的表
                List<Map<String, Object>> tablesList =
                        this.dataQueryService.queryForListWithSql(sb.toString(), dataSource);
                for (Map<String, Object> tableMap : tablesList) {
                    String currTableName = (String) tableMap.values().toArray()[0];
                    // 对表名进行匹配，如果匹配成功则加入到factTableList中
                    String patternStr = currCube.getSource();
                    boolean matchResult = Pattern.matches(patternStr, currTableName);
                    if (matchResult) {
                        factTableList.add(currTableName);
                    }
                }

            } else {
                factTableList.add(currCube.getSource());
            }

            for (String tableName : factTableList) {
                SqlQuery sqlQuery = new SqlQuery();
                sqlQuery.setSelectList(selectMeasureList);
                LinkedList<String> fromList = new LinkedList<String>();
                fromList.add(tableName);
                sqlQuery.setFromList(fromList);
                sqlQueryList.add(sqlQuery);
            }

            DataDescInfo dataDescInfo = new DataDescInfo();
            // FIXME add productline info
            // dataDescInfo.setProductLine();
            dataDescInfo.setSourceName(dataSourceInfo.getDataSourceKey());

            dataDescInfo.setSplitTable(currCube.isMutilple());
            dataDescInfo.setTableName(currCube.getSource());
            dataDescInfo.setTableNameList(factTableList);

            IndexStrategy idxStrategy = new IndexStrategy(sqlQueryList);
            IndexMeta idxMeta = new IndexMeta();
            // idxMeta.setClusterName(clusterName);
            // idxMeta.setProductLine(productLine);
            idxMeta.setDataDescInfo(dataDescInfo);
            idxMeta.setIdxState(IndexState.INDEX_UNINIT);
            // idxMeta.getCubeIdSet().add(currCube.getName());
            idxMeta.getCubeIdSet().add(currCube.getId());
            idxMeta.setDimInfoMap(currCube.getDimensions());
            idxMeta.setMeasureInfoMap(currCube.getMeasures());
            idxMeta.setReplicaNum(IndexShard.getDefaultShardReplicaNum());
            idxMeta.setDataSourceInfo(dataSourceInfo);
            idxMeta.setDataDescInfo(dataDescInfo);

            result.add(idxStrategy);
        }

        return result;
    }

}
