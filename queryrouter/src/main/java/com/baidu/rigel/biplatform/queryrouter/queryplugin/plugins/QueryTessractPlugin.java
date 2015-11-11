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
package com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.rigel.biplatform.ac.exception.MiniCubeQueryException;
import com.baidu.rigel.biplatform.ac.query.MiniCubeConnection.DataSourceType;
import com.baidu.rigel.biplatform.ac.query.data.DataModel;
import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo.DataBase;
import com.baidu.rigel.biplatform.ac.query.model.ConfigQuestionModel;
import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;
import com.baidu.rigel.biplatform.ac.util.AnswerCoreConstant;
import com.baidu.rigel.biplatform.ac.util.ConfigInfoUtils;
import com.baidu.rigel.biplatform.ac.util.HttpRequest;
import com.baidu.rigel.biplatform.ac.util.JsonUnSeriallizableUtils;
import com.baidu.rigel.biplatform.ac.util.ResponseResult;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.QueryPlugin;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.QueryPluginConstants;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.QuestionModelTransformationException;

/**
 * tessract查询插件
 * 
 * @author luowenlei
 *
 */
@Service("queryTesseractPlugin")
@Scope("prototype")
public class QueryTessractPlugin implements QueryPlugin {

    /**
     * the logger object
     */
    private static Logger logger = LoggerFactory
            .getLogger(QueryTessractPlugin.class);
    /**
     * TESSERACT_NAME
     */
    private static String TESSERACT_TYPE = "TESSERACT";
    /**
     * QUESTIONMODEL_PARAM_KEY
     */
    private static String QUESTIONMODEL_PARAM_KEY = "question";

    /**
     * BIPLATFORM_QUERY_ROUTER_SERVER_TARGET_PARAM
     */
    private static String BIPLATFORM_QUERY_ROUTER_SERVER_TARGET_PARAM = "biplatform_queryrouter_target";

    /**
     * BIPLATFORM_PRODUCTLINE_PARAM
     */
    private static String BIPLATFORM_PRODUCTLINE_PARAM = "_rbk";

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.biplatform.querybus.queryplugin.QueryPlugin#query(com
     * .baidu.rigel.biplatform.ac.query.model.QuestionModel)
     */
    @Override
    public DataModel query(QuestionModel questionModel) {
        long current = System.currentTimeMillis();
        Map<String, String> params = new HashMap<String, String>();
        ConfigQuestionModel configQuestionModel = (ConfigQuestionModel) questionModel;
        /**palo数据源不需要使用索引**/
        if (configQuestionModel.getDataSourceInfo() != null
                && configQuestionModel.getDataSourceInfo() instanceof SqlDataSourceInfo) {
            SqlDataSourceInfo sqlDataSourceInfo = (SqlDataSourceInfo) configQuestionModel.getDataSourceInfo();
            if (sqlDataSourceInfo.getDataBase() == DataBase.PALO) {
                configQuestionModel.setUseIndex(false);
            }
        }
        params.put(QUESTIONMODEL_PARAM_KEY,
                AnswerCoreConstant.GSON.toJson(configQuestionModel));
        Map<String, String> headerParams = new HashMap<String, String>();
        headerParams.put(BIPLATFORM_QUERY_ROUTER_SERVER_TARGET_PARAM, TESSERACT_TYPE);
        headerParams.put(BIPLATFORM_PRODUCTLINE_PARAM, configQuestionModel.getDataSourceInfo().getProductLine());
        long curr = System.currentTimeMillis();
        logger.info("begin execute query with tesseract ");
        String tesseractHost = "";
        tesseractHost = ConfigInfoUtils.getServerAddress ();
        String response = HttpRequest.sendPost(tesseractHost + "/query", params, headerParams);
        logger.info("queryId:{} execute query with tesseract cost {} ms",
        		questionModel.getQueryId(), System.currentTimeMillis() - curr);
        ResponseResult responseResult = AnswerCoreConstant.GSON.fromJson(
                response, ResponseResult.class);
        if (StringUtils.isNotBlank(responseResult.getData())) {
            String dataModelJson = responseResult.getData();
            DataModel dataModel = JsonUnSeriallizableUtils
                    .dataModelFromJson(dataModelJson);
            logger.info("queryId:{} execute query questionModel cost {} ms",
            		questionModel.getQueryId(), System.currentTimeMillis() - current);
            dataModel.setOthers(responseResult.getStatusInfo());
            return dataModel;
        }
        throw new MiniCubeQueryException("queryId:" + questionModel.getQueryId() + " query Tesseract occur error,msg:"
                + responseResult.getStatusInfo());

    }
    
    @Override
    public boolean isSuitable(QuestionModel questionModel)
            throws QuestionModelTransformationException {
        if (questionModel instanceof ConfigQuestionModel) {
            ConfigQuestionModel configQuestionModel = (ConfigQuestionModel) questionModel;
            if (configQuestionModel.getDataSourceInfo().getDataSourceType() == DataSourceType.SQL) {
                if (TESSERACT_TYPE.equals(configQuestionModel.getQuerySource())) {
                    return true; 
                }
            }
        }
        return false;
    }

}
