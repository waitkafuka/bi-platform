package com.baidu.rigel.biplatform.queryrouter.handle;

import java.util.concurrent.ConcurrentHashMap;

/**
 * QueryRouterContext记录查询的上下文
 * 
 * @author luowenlei
 *
 */
public class QueryRouterContext {

	/**
	 * queryId的context
	 */
	private static ConcurrentHashMap<String, String> QUERY_REQUEST_CONTEXT = new ConcurrentHashMap<String, String>();
	
	/**
	 * map size的最大值
	 */
	private static final int MAX_SIZE = 10000;

	/**
	 * 通过ThreadId，设置查询的queryId
	 * 
	 * @param threadId
	 * @param queryId
	 */
	public static void setQueryInfo(String queryId) {
		String threadId = Long.valueOf(Thread.currentThread().getId()).toString();
		if (QUERY_REQUEST_CONTEXT.size() > MAX_SIZE) {
			QUERY_REQUEST_CONTEXT = new ConcurrentHashMap<String, String>();
		}
		QUERY_REQUEST_CONTEXT.put(threadId, queryId);
	}
	
	/**
	 * 获取查询的queryId
	 * 
	 * @param threadId
	 * @return
	 */
	public static String getQueryId() {
		String threadId = Long.valueOf(Thread.currentThread().getId()).toString();
		return QUERY_REQUEST_CONTEXT.get(threadId);
	}
	
	/**
	 * 获取当前正在查询的线程数
	 * 
	 * @param threadId
	 * @return
	 */
	public static int getQueryCurrentHandleSize() {
		return QUERY_REQUEST_CONTEXT.size();
	}

	/**
	 * 获取查询的queryId
	 * 
	 * @param threadId
	 * @return
	 */
	public static String removeQueryInfo() {
		String threadId = Long.valueOf(Thread.currentThread().getId()).toString();
		return QUERY_REQUEST_CONTEXT.remove(threadId);
	}
}
