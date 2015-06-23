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
package com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.common.jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.query.data.DataModel;
import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.data.TableData;
import com.baidu.rigel.biplatform.ac.query.data.TableData.Column;
import com.baidu.rigel.biplatform.ac.query.model.ConfigQuestionModel;
import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;
import com.baidu.rigel.biplatform.ac.util.UnicodeUtils;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.common.QuestionModel4TableDataUtils;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.SqlColumn;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.SqlExpression;


/**
 * 
 * SQLDataQueryService的实现类
 * 
 * @author luowenlei
 *
 */
@Service("jdbcDataModelUtil")
@Scope("prototype")
public class JdbcDataModelUtil {

    /**
     * 参数需要计算page的totalsize
     */
    private static final int PARMA_NEED_CONTAIN_TOTALSIZE = -1;

    /**
     * JdbcConnectionPool
     */
    @Resource(name = "jdbcHandler")
    private JdbcHandler jdbcHandler;

    /**
     * executeSql
     * 
     * @param questionModel
     *            questionModel
     * @param SqlExpression
     *            sqlExpression
     * @return DataModel DataModel
     */
    public DataModel executeSql(QuestionModel questionModel,
            SqlExpression sqlExpression) {
        ConfigQuestionModel configQuestionModel = (ConfigQuestionModel) questionModel;
        DataSourceInfo dataSourceInfo = configQuestionModel.getDataSourceInfo();
        questionModel.setUseIndex(false);

        List<Map<String, Object>> rowBasedList = jdbcHandler.queryForList(
                sqlExpression, dataSourceInfo);
        // getAll columns from Cube
        Map<String, SqlColumn> allColums = QuestionModel4TableDataUtils
                .getAllCubeColumns(questionModel, configQuestionModel.getCube());

        // get need columns from AxisMetas
        List<SqlColumn> needColums = QuestionModel4TableDataUtils
                .getNeedColumns(allColums, configQuestionModel.getAxisMetas(),
                        (MiniCube) configQuestionModel.getCube());

        // init DataModel
        DataModel dataModel = this.initTableDataModel(needColums);
        // 设置DataModel的ColBased Data
        this.fillModelTableData(dataModel, needColums, rowBasedList);

        // 如果为getRecordSize为-1，那么需要搜索dataModel.getRecordSize() from database
        if (questionModel.getPageInfo() != null
                && questionModel.getPageInfo().getTotalRecordCount() == PARMA_NEED_CONTAIN_TOTALSIZE) {
            dataModel.setRecordSize(jdbcHandler.queryForInt(
                    sqlExpression, dataSourceInfo));
        }
        return dataModel;
    }

    /**
     * initTableDataModel,初始化dataModel
     * 
     * @param List<SqlColumn> needColums
     * @return DataModel DataModel
     */
    public DataModel initTableDataModel(List<SqlColumn> needColums) {
        DataModel dataModel = new DataModel();
        dataModel.setTableData(new TableData());
        dataModel.getTableData().setColumns(new ArrayList<Column>());
        dataModel.getTableData().setColBaseDatas(
                new HashMap<String, List<String>>());
        needColums.forEach((colDefine) -> {
            TableData.Column colum = new TableData.Column(colDefine.getColumnKey(),
                    colDefine.getTableFieldName(), colDefine.getCaption(),
                    colDefine.getSourceTableName());
            dataModel.getTableData().getColumns().add(colum);
            String tableDataColumnKey = colDefine.getColumnKey();
            dataModel
                    .getTableData()
                    .getColBaseDatas()
                    .put(tableDataColumnKey,
                            new ArrayList<String>());
        });
        return dataModel;
    }

    /**
     * 将Rowbased数据集转成colbased的数据集
     * 
     * @param dataModel
     *            dataModel
     * @param rowBasedList
     *            rowBasedList
     */
    public void fillModelTableData(DataModel dataModel,
            List<SqlColumn> needColums, List<Map<String, Object>> rowBasedList) {
        if (dataModel == null || dataModel.getTableData() == null
                || dataModel.getTableData().getColBaseDatas().isEmpty()) {
            return ;
        }
        Map<String, List<String>> rowBaseData = dataModel.getTableData()
                .getColBaseDatas();
        rowBasedList.forEach((row) -> {
            needColums.forEach((column) -> {
                String tableDataColumnKey = column.getColumnKey();
                String cell = "";
                if (rowBaseData.get(tableDataColumnKey) == null) {
                    // init TableData Column List
                    rowBaseData
                            .put(tableDataColumnKey, new ArrayList<String>());
                }
                if (column.getSqlUniqueColumn() != null && row.get(column.getSqlUniqueColumn()) != null) {
                    cell = row.get(column.getSqlUniqueColumn()).toString();
                }
                // get Data from
                List<String> oneColData = rowBaseData.get(tableDataColumnKey);
                oneColData.add(UnicodeUtils.string2Unicode(cell));
            });
        });
    }
}
