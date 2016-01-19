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
package com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.mysql;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baidu.rigel.biplatform.ac.query.data.DataModel;
import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo.DataBase;
import com.baidu.rigel.biplatform.ac.query.model.ConfigQuestionModel;
import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;
import com.baidu.rigel.biplatform.queryrouter.calculate.operator.utils.OperatorUtils;
import com.baidu.rigel.biplatform.queryrouter.handle.model.QueryHandler;
import com.baidu.rigel.biplatform.queryrouter.query.service.impl.QueryServiceImpl;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.QueryPlugin;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.convert.DataModelConvertService;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.convert.PlaneTableUtils;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.convert.SqlColumnUtils;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.jdbc.service.impl.JdbcCountNumServiceImpl;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.meta.TableExistCheckService;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.sql.SqlExpression;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.sql.model.PlaneTableQuestionModel;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.sql.model.QuestionModelTransformationException;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.sql.model.SqlColumn;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.utils.QueryHandlerBuilder;

/**
 * mysql查询的插件
 * 
 * @author luowenlei
 *
 */
@Service("mySqlPlaneTablePlugin")
@Scope("prototype")
public class MySqlPlaneTablePlugin implements QueryPlugin {
    
    /**
     * logger
     */
    private static Logger logger = LoggerFactory.getLogger(MySqlPlaneTablePlugin.class);
    
    /**
     * dataModelConvertService
     */
    @Resource(name = "dataModelConvertService")
    private DataModelConvertService dataModelConvertService;
    
    /**
     * jdbcCountNumServiceImpl
     */
    @Resource(name = "jdbcCountNumServiceImpl")
    private JdbcCountNumServiceImpl jdbcCountNumService;
    
    /**
     * TableExistCheck
     */
    @Resource(name = "jdbcTableExistCheckServiceImpl")
    private TableExistCheckService tableExistCheckService;
    
    
    @Resource(name = "queryService")
    private QueryServiceImpl queryService;
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.biplatform.querybus.queryplugin.QueryPlugin#query(com
     * .baidu.rigel.biplatform.ac.query.model.QuestionModel)
     */
    @Override
    public DataModel query(QuestionModel questionModel) throws QuestionModelTransformationException {
        PlaneTableQuestionModel planeTableQuestionModel = PlaneTableUtils
                .convertConfigQuestionModel2PtQuestionModel((ConfigQuestionModel) questionModel);
        QueryHandler queryHandler = QueryHandlerBuilder.buildQueryHandler(questionModel);
        List<SqlColumn> needColumns = queryHandler.getSqlExpression().getNeedColums();
        SqlExpression sqlExpression = queryHandler.getSqlExpression();
        
        // 2.检验cube.getSource中的事实表是否在数据库中存在，并过滤不存在的数据表
        sqlExpression.setHasAlias(true);
        String tableNames = tableExistCheckService.getExistTableList(
                sqlExpression.getTableName(), queryHandler.getJdbcHandler());
        
        if (StringUtils.isEmpty(tableNames) || CollectionUtils.isEmpty(needColumns)) {
            // 如果获取的cube的数据为空
            logger.info("queryId:{} QuerySqlPlugin find no tables in Db.",
                    questionModel.getQueryId());
            return dataModelConvertService.getEmptyDataModel(needColumns);
        }
        // 3.生成sql
        queryHandler.getSqlExpression().setTableName(tableNames);
        queryHandler.getSqlExpression().generateSql(questionModel);
        
        // 4.execute sql
        String sql = queryHandler.getSqlExpression().getSqlQuery().toSql();
        List<Object> values = queryHandler.getSqlExpression().getSqlQuery().getWhere()
                .getValues();
        List<Map<String, Object>> rowBasedList = queryHandler.getJdbcHandler().queryForList(sql, values);
        // 5.convert data to datamodel
        DataModel dataModel = dataModelConvertService.convert(needColumns, rowBasedList);
        
        // 6.生成pageSize
        if (planeTableQuestionModel.isGenerateTotalSize()) {
            dataModel.setRecordSize(jdbcCountNumService.getTotalRecordSize(
                    planeTableQuestionModel, queryHandler));
        }
        return dataModel;
    }
    
    @Override
    public boolean isSuitable(QuestionModel questionModel)
            throws QuestionModelTransformationException {
        if (questionModel instanceof ConfigQuestionModel) {
            ConfigQuestionModel configQuestionModel = (ConfigQuestionModel) questionModel;
            PlaneTableQuestionModel planeTableQuestionModel = PlaneTableUtils
                    .convertConfigQuestionModel2PtQuestionModel(configQuestionModel);
            SqlDataSourceInfo sqlDataSourceInfo = ((SqlDataSourceInfo) configQuestionModel
                    .getDataSourceInfo());
            SqlExpression sqlExpression = QueryHandlerBuilder.buildSqlExpressionWithCube(
                    configQuestionModel.getCube(), sqlDataSourceInfo,
                    configQuestionModel.getQueryConditions(), configQuestionModel.getSortRecord(), null);
            List<SqlColumn> needColumns =
                    SqlColumnUtils.getNeedColumns(sqlExpression.getQueryMeta(),
                            planeTableQuestionModel.getSelection());
            if (!OperatorUtils.isAggQuery(needColumns)
                    && (sqlDataSourceInfo.getDataBase() == DataBase.PALO
                    || sqlDataSourceInfo.getDataBase() == DataBase.MYSQL)) {
                logger.info("queryId:{} " + this.getClass().getSimpleName()
                        + " find this questionModel isSuitable!",
                        questionModel.getQueryId());
                return true;
            }
        }
        return false;
    }
}
