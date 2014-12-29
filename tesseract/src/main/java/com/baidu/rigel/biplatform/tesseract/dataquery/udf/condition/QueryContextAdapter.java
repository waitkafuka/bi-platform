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
package com.baidu.rigel.biplatform.tesseract.dataquery.udf.condition;

import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.QueryContextBuilder;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryContext;

/**
 * Description: QueryContextAdapter
 * @author david.wang
 *
 */
public class QueryContextAdapter extends QueryContext {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7631126790794933111L;
	
	/**
	 * queryContext
	 */
	private final QueryContext queryContext;
	
	/**
	 * questionModel
	 */
	private final QuestionModel questionModel;
	
	/**
	 * cube
	 */
	private final Cube cube;
	
	/**
	 * dataSoruceInfo
	 */
	private final DataSourceInfo dataSoruceInfo;
	
	private final QueryContextBuilder builder;

	/**
	 * 构造函数
	 * @param queryContext
	 * @param questionModel
	 * @param cube
	 * @param dataSoruceInfo
	 */
	public QueryContextAdapter(QueryContext queryContext,
			QuestionModel questionModel, Cube cube,
			DataSourceInfo dataSoruceInfo, QueryContextBuilder builder) {
		super();
		this.queryContext = queryContext;
		this.questionModel = questionModel;
		this.cube = cube;
		this.dataSoruceInfo = dataSoruceInfo;
		this.builder = builder;
	}

	/**
	 * @return the queryContext
	 */
	public QueryContext getQueryContext() {
		return queryContext;
	}

	/**
	 * @return the questionModel
	 */
	public QuestionModel getQuestionModel() {
		return questionModel;
	}

	/**
	 * @return the cube
	 */
	public Cube getCube() {
		return cube;
	}

	/**
	 * @return the dataSoruceInfo
	 */
	public DataSourceInfo getDataSoruceInfo() {
		return dataSoruceInfo;
	}

	/**
	 * @return the builder
	 */
	public QueryContextBuilder getBuilder() {
		return builder;
	}
	
}
