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
package com.baidu.rigel.biplatform.ma.rt.request.build;

import java.util.Map;

import com.baidu.rigel.biplatform.ma.rt.Context;
import com.baidu.rigel.biplatform.ma.rt.query.model.QueryRequest;
import com.baidu.rigel.biplatform.ma.rt.query.model.QueryStrategy;

/**
 * 查询请求构建工具类：负责将用户的请求封装成查询请求（QueryRequest）
 * @author wangyuxue
 * @since 1.1.0
 * 
 */
public final class QueryRequestBuilder {
	
	/**
	 * 查询请求
	 */
	private QueryRequestBuilder() {
	}
	
	/**
	 * 查询请求构建对象
	 * @param context
	 * @param params
	 * @return QueryRequest
	 */
	public static QueryRequest buildQueryRequest(Context context, QueryStrategy queryStrategy, 
				Map<String, Object> params) {
		return null;
	}
}
