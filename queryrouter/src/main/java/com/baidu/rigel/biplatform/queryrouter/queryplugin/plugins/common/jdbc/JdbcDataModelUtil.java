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

import com.baidu.rigel.biplatform.ac.query.data.DataModel;
import com.baidu.rigel.biplatform.ac.query.data.TableData;
import com.baidu.rigel.biplatform.ac.query.data.TableData.Column;
import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;
import com.baidu.rigel.biplatform.ac.util.UnicodeUtils;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.common.PlaneTableQuestionModel2SqlColumnUtils;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.common.jdbc.meta.TableMetaService;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.ColumnType;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.PlaneTableQuestionModel;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.SqlColumn;


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
     * tableMetaService
     */
    @Resource(name = "tableMetaService")
    private TableMetaService tableMetaService;

    /**
     * JoinTableDataService
     */
    @Resource(name = "joinTableDataService")
    private JoinTableDataService joinTableDataService;
    
    /**
     * executeSql
     * 
     * @param questionModel
     *            questionModel
     * @param SqlExpression
     *            sqlExpression
     * @return DataModel DataModel
     */
    public DataModel executeSql(QuestionModel questionModel, Map<String, SqlColumn> allColumns,
            SqlExpression sqlExpression) {
        PlaneTableQuestionModel planeQuestionModel = (PlaneTableQuestionModel) questionModel;
        SqlDataSourceInfo dataSourceInfo = (SqlDataSourceInfo)planeQuestionModel.getDataSourceInfo();

        List<Map<String, Object>> rowBasedList = jdbcHandler.queryForList(
                sqlExpression.getSqlQuery().toSql(),
                        sqlExpression.getSqlQuery().getWhere().getValues(), dataSourceInfo);

        // get need columns from AxisMetas
        List<SqlColumn> needColums = PlaneTableQuestionModel2SqlColumnUtils
                .getNeedColumns(allColumns, planeQuestionModel.getSelection());

        // init DataModel
        DataModel dataModel = this.getEmptyDataModel(needColums);
        // 设置DataModel的ColBased Data
        this.fillModelTableData(dataModel, needColums, rowBasedList);

        // 如果为getRecordSize为-1，那么需要搜索dataModel.getRecordSize() from database
        if (questionModel.getPageInfo() != null
                && questionModel.getPageInfo().getTotalRecordCount() == PARMA_NEED_CONTAIN_TOTALSIZE) {
            // 按照tablename, 组织sqlColumnList，以便于按表查询所有的字段信息
            HashMap<String, List<SqlColumn>> tables = new HashMap<String, List<SqlColumn>>();
            for (SqlColumn sqlColumn : allColumns.values()) {
                if (sqlColumn.getTableName().equals(sqlColumn.getSourceTableName())) {
                // 过滤事实表及退化维
                    continue;
                }
                if (sqlColumn.getType() == ColumnType.JOIN) {
                    if (tables.get(sqlColumn.getTableName()) == null) {
                        tables.put(sqlColumn.getTableName(), new ArrayList<SqlColumn>());
                    }
                    tables.get(sqlColumn.getTableName()).add(sqlColumn);
                }
            }
            // 查询jointable中的主表的id信息
            Map<String, List<Object>> values = joinTableDataService
                    .getJoinTableData(planeQuestionModel, allColumns, tables);
            // set total count sql
            sqlExpression.generateCountSql(planeQuestionModel, allColumns, needColums, values);
           
            // set count size
            dataModel.setRecordSize(jdbcHandler.queryForInt(sqlExpression.getCountSqlQuery().toCountSql(),
                    sqlExpression.getCountSqlQuery().getWhere().getValues(), dataSourceInfo));
        }
        return dataModel;
    }
    
    /**
     * getEmptyDataModel,初始化dataModel
     * 
     * @param List<SqlColumn> needColums 需要select的字段
     * @return DataModel DataModel
     */
    public DataModel getEmptyDataModel(List<SqlColumn> needColums) {
        DataModel dataModel = new DataModel();
        dataModel.setTableData(new TableData());
        dataModel.getTableData().setColumns(new ArrayList<Column>());
        dataModel.getTableData().setColBaseDatas(
                new HashMap<String, List<String>>());
        if (needColums == null || needColums.isEmpty()) {
            return dataModel;
        }
        needColums.forEach((colDefine) -> {
            String tableName = "";
            tableName = colDefine.getTableName();
            if (ColumnType.COMMON == colDefine.getType()) {
                tableName = colDefine.getSourceTableName();
            }
            Column colum = new Column(colDefine.getColumnKey(),
                    colDefine.getTableFieldName(), colDefine.getCaption(), tableName);
            dataModel.getTableData().getColumns().add(colum);
            dataModel
                    .getTableData()
                    .getColBaseDatas()
                    .put(colDefine.getColumnKey(),
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
