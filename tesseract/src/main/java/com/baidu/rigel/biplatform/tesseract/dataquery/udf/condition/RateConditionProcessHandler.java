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

import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryContext;

/**
 * 
 * Description: AbsRateConditionProcessHandler
 * @author david.wang
 *
 */
abstract class RateConditionProcessHandler {
	
	/**
	 * 
	 * @param context 原始查询请求
	 * @return QueryContext 经过处理之后的查询请求
	 */
	public abstract QueryContext processCondition(QueryContext context);
}
