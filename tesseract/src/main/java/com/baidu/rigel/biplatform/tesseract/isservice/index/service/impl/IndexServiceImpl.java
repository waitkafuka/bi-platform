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
/**
 * 
 */
package com.baidu.rigel.biplatform.tesseract.isservice.index.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.tesseract.dataquery.service.DataQueryService;
import com.baidu.rigel.biplatform.tesseract.datasource.DataSourcePoolService;
import com.baidu.rigel.biplatform.tesseract.datasource.impl.SqlDataSourceWrap;
import com.baidu.rigel.biplatform.tesseract.exception.DataSourceException;
import com.baidu.rigel.biplatform.tesseract.isservice.event.IndexUpdateEvent;
import com.baidu.rigel.biplatform.tesseract.isservice.event.IndexUpdateEvent.IndexUpdateInfo;
import com.baidu.rigel.biplatform.tesseract.isservice.exception.IndexAndSearchException;
import com.baidu.rigel.biplatform.tesseract.isservice.exception.IndexAndSearchExceptionType;
import com.baidu.rigel.biplatform.tesseract.isservice.exception.IndexMetaIsNullException;
import com.baidu.rigel.biplatform.tesseract.isservice.index.service.IndexMetaService;
import com.baidu.rigel.biplatform.tesseract.isservice.index.service.IndexService;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexAction;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexMeta;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexShard;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexState;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.SqlQuery;
import com.baidu.rigel.biplatform.tesseract.netty.message.isservice.IndexMessage;
import com.baidu.rigel.biplatform.tesseract.netty.message.isservice.ServerFeedbackMessage;
import com.baidu.rigel.biplatform.tesseract.node.meta.Node;
import com.baidu.rigel.biplatform.tesseract.node.meta.NodeState;
import com.baidu.rigel.biplatform.tesseract.node.service.IndexAndSearchClient;
import com.baidu.rigel.biplatform.tesseract.node.service.IsNodeService;
import com.baidu.rigel.biplatform.tesseract.resultset.TesseractResultSet;
import com.baidu.rigel.biplatform.tesseract.store.service.StoreManager;
import com.baidu.rigel.biplatform.tesseract.util.FileUtils;
import com.baidu.rigel.biplatform.tesseract.util.IndexFileSystemConstants;
import com.baidu.rigel.biplatform.tesseract.util.TesseractConstant;
import com.baidu.rigel.biplatform.tesseract.util.TesseractExceptionUtils;
import com.baidu.rigel.biplatform.tesseract.util.isservice.LogInfoConstants;

/**
 * IndexService 实现类
 * 
 * @author lijin
 *
 */
@Service("indexService")
public class IndexServiceImpl implements IndexService {
    /**
     * LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexServiceImpl.class);
    /**
     * LOGGER
     */
    private static final String RESULT_KEY_DATA = "RESULT_KEY_DATA";
    /**
     * RESULT_KEY_INDEXSHARD
     */
    private static final String RESULT_KEY_INDEXSHARD = "RESULT_KEY_INDEXSHARD";
    
    /**
     * RESULT_KEY_MAXID
     */
    private static final String RESULT_KEY_MAXID = "RESULT_KEY_MAXID";
    
    /**
     * indexMetaService
     */
    @Resource
    private IndexMetaService indexMetaService;
    
    @Resource
    private IsNodeService isNodeService;
    
    @Resource
    private StoreManager storeManager;
    
    @Resource
    private DataSourcePoolService dataSourcePoolService;
    
    /**
     * dataQueryService
     */
    @Resource(name = "sqlDataQueryService")
    private DataQueryService dataQueryService;
    
    /**
     * dataQueryService
     */
    
    private IndexAndSearchClient isClient;
    
    /**
     * Constructor by no param
     */
    public IndexServiceImpl() {
        super();
        this.isClient = IndexAndSearchClient.getNodeClient();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.biplatform.tesseract.isservice.index.service.IndexService
     * #initMiniCubeIndex(java.util.List,
     * com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo, boolean,
     * boolean)
     */
    @Override
    public boolean initMiniCubeIndex(List<Cube> cubeList, DataSourceInfo dataSourceInfo,
        boolean indexAsap, boolean limited) throws IndexAndSearchException {
        /**
         * 当通过MiniCubeConnection.publishCubes(List<String> cubes, DataSourceInfo
         * dataSourceInfo);通知索引服务端建立索引数据
         */
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_BEGIN,
            "initMiniCubeIndex", "[cubeList:" + cubeList + "][dataSourceInfo:" + dataSourceInfo
                + "][indexAsap:" + indexAsap + "][limited:" + limited + "]"));
        
        // step 1 process cubeList and fill indexMeta infomation
        List<IndexMeta> idxMetaList = this.indexMetaService.initMiniCubeIndexMeta(cubeList,
            dataSourceInfo);
        
        if (idxMetaList.size() == 0) {
            LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_PROCESS,
                "initMiniCubeIndex", "[cubeList:" + cubeList + "][dataSourceInfo:" + dataSourceInfo
                    + "][indexAsap:" + indexAsap + "][limited:" + limited + "]",
                "Init MiniCube IndexMeta failed"));
            return false;
        } else {
            LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_PROCESS_NO_PARAM,
                "initMiniCubeIndex", "Success init " + idxMetaList.size() + " MiniCube"));
        }
        
        // step 2 merge indexMeta with exist indexMetas and update indexMeta
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_PROCESS_NO_PARAM,
            "initMiniCubeIndex", "Merging IndexMeta with exist indexMetas"));
        
        LinkedList<IndexMeta> idxMetaListForIndex = new LinkedList<IndexMeta>();
        for (IndexMeta idxMeta : idxMetaList) {
            idxMeta = this.indexMetaService.mergeIndexMeta(idxMeta);
            
            LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_PROCESS_NO_PARAM,
                "initMiniCubeIndex", "Merge indexMeta success. After merge:[" + idxMeta.toString()
                    + "]"));
            
            idxMetaListForIndex.add(idxMeta);
        }
        
        // step 3 if(indexAsap) then call doIndex else return
        
        if (indexAsap) {
            LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_PROCESS_NO_PARAM,
                "initMiniCubeIndex", "index as soon as possible"));
            // if need index as soon as possible
            IndexAction idxAction = IndexAction.INDEX_INIT;
            if (limited) {
                idxAction = IndexAction.INDEX_INIT_LIMITED;
            }
            while (idxMetaListForIndex.size() > 0) {
                
                IndexMeta idxMeta = idxMetaListForIndex.poll();
                if (idxMeta.getIdxState().equals(IndexState.INDEX_AVAILABLE_MERGE)) {
                    idxMeta.setIdxState(IndexState.INDEX_AVAILABLE);
                    this.indexMetaService.saveOrUpdateIndexMeta(idxMeta);
                    continue;
                } else if (idxMeta.getIdxState().equals(IndexState.INDEX_AVAILABLE_NEEDMERGE)) {
                    idxAction = IndexAction.INDEX_MERGE;
                }
 //               boolean idxResult = false;
                
                try {
                    
//                    idxResult = doIndex(idxMeta, idxAction);
                	doIndexByIndexAction(idxMeta, idxAction, null);
                    
                } catch (Exception e) {
                    LOGGER.error(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_EXCEPTION,
                        "initMiniCubeIndex", "[cubeList:" + cubeList + "][dataSourceInfo:"
                            + dataSourceInfo + "][indexAsap:" + indexAsap + "][limited:" + limited
                            + "]"), e);
                    
                    String message = TesseractExceptionUtils.getExceptionMessage(
                        IndexAndSearchException.INDEXEXCEPTION_MESSAGE,
                        IndexAndSearchExceptionType.INDEX_EXCEPTION);
                    throw new IndexAndSearchException(message, e,
                        IndexAndSearchExceptionType.INDEX_EXCEPTION);
                } finally {
                    LOGGER.info(String.format(
                        LogInfoConstants.INFO_PATTERN_FUNCTION_PROCESS_NO_PARAM,
                        "initMiniCubeIndex", "[Index indexmeta : " + idxMeta.toString()));
                }
            }
        }
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_END, "initMiniCubeIndex",
            "[cubeList:" + cubeList + "][dataSourceInfo:" + dataSourceInfo + "][indexAsap:"
                + indexAsap + "][limited:" + limited + "]"));
        return true;
    }
    
    private void publishIndexUpdateEvent(List<IndexMeta> metaList) throws Exception {
        if (metaList != null && metaList.size() > 0) {
            List<String> idxServiceList = new ArrayList<String>();
            List<String> idxNoServiceList = new ArrayList<String>();
            for (IndexMeta meta : metaList) {
                for (IndexShard idxShard : meta.getIdxShardList()) {
                    idxServiceList.add(idxShard.getAbsoluteIdxFilePath(this.isNodeService
                        .getCurrentNode()));
                    idxNoServiceList.add(idxShard.getAbsoluteFilePath(this.isNodeService
                        .getCurrentNode()));
                }
            }
            IndexUpdateInfo udpateInfo = new IndexUpdateInfo(idxServiceList, idxNoServiceList);
            IndexUpdateEvent updateEvent = new IndexUpdateEvent(udpateInfo);
            this.storeManager.postEvent(updateEvent);
        }
    }
    
    @Override
	public void updateIndexByDataSourceKey(String dataSourceKey,String[] factTableNames,
			Map<String, Map<String, BigDecimal>> dataSetMap) throws IndexAndSearchException {
        
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_BEGIN,
            "updateIndexByDataSourceKey", dataSourceKey));
        if (StringUtils.isEmpty(dataSourceKey)) {
            LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_EXCEPTION,
                "updateIndexByDataSourceKey", dataSourceKey));
            throw new IllegalArgumentException();
        }
        
        List<IndexMeta> metaList = new ArrayList<IndexMeta>();
        IndexAction idxAction=IndexAction.INDEX_UPDATE;
        
        if(MapUtils.isEmpty(dataSetMap)){
        	if(!ArrayUtils.isEmpty(factTableNames)){
        		for (String factTableName : factTableNames) {
        			List<IndexMeta> fTableMetaList=this.indexMetaService.getIndexMetasByFactTableName(factTableName,dataSourceKey);
        			if(!CollectionUtils.isEmpty(fTableMetaList)){
        				metaList.addAll(fTableMetaList);
        			}else{
        				LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_PROCESS,
        			            "updateIndexByDataSourceKey", dataSourceKey,"can not find IndexMeta for Facttable:["+factTableNames+"]"));
        			}
    			}
        	}else {
        		metaList = this.indexMetaService
    					.getIndexMetasByDataSourceKey(dataSourceKey);
        	}
        }else {
        	idxAction=IndexAction.INDEX_MOD;
			for (String factTableName : dataSetMap.keySet()) {
				List<IndexMeta> fTableMetaList = this.indexMetaService
						.getIndexMetasByFactTableName(factTableName,
								dataSourceKey);
				if (!CollectionUtils.isEmpty(fTableMetaList)) {
					metaList.addAll(fTableMetaList);
				}
			}
        	
        }     
		
        
        for (IndexMeta meta : metaList) {
        	Map<String,BigDecimal> tableDataSetMap=null;
        	if(!MapUtils.isEmpty(dataSetMap)){
        		tableDataSetMap=dataSetMap.get(meta.getFacttableName());
        	}
            try {
                doIndexByIndexAction(meta, idxAction,tableDataSetMap );
            } catch (Exception e) {
				LOGGER.info(String.format(
						LogInfoConstants.INFO_PATTERN_FUNCTION_EXCEPTION,
						"updateIndexByDataSourceKey",
						"DataSourceKey:[" + dataSourceKey + "] FactTable:["
								+ meta.getFacttableName() + "] IndexMetaId:["
								+ meta.getIndexMetaId() + "]"));
				String message = TesseractExceptionUtils.getExceptionMessage(
						IndexAndSearchException.INDEXEXCEPTION_MESSAGE,
						IndexAndSearchExceptionType.INDEX_EXCEPTION);
				throw new IndexAndSearchException(message, e.getCause(),
						IndexAndSearchExceptionType.INDEX_EXCEPTION);
            }
        }
        try {
            publishIndexUpdateEvent(metaList);
        } catch (Exception e) {
            LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_EXCEPTION,
                "updateIndexByDataSourceKey", dataSourceKey));
            String message = TesseractExceptionUtils.getExceptionMessage(
                IndexAndSearchException.INDEXEXCEPTION_MESSAGE,
                IndexAndSearchExceptionType.INDEX_UPDATE_EXCEPTION);
            throw new IndexAndSearchException(message, e.getCause(),
                IndexAndSearchExceptionType.INDEX_EXCEPTION);
        }
        
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_END,
            "updateIndexByDataSourceKey", dataSourceKey));
    }
    
    public void doIndexByIndexAction(IndexMeta indexMeta,IndexAction idxAction,Map<String, BigDecimal> dataMap) throws Exception{
    	LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_BEGIN, "doIndexByIndexAction",
                "[indexMeta:" + indexMeta + "][idxAction:" + idxAction + "]"));
    	IndexMeta idxMeta = indexMeta;
    	if (idxMeta == null || idxAction == null) {
            LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_EXCEPTION, "doIndexByIndexAction",
                "[indexMeta:" + indexMeta + "][idxAction:" + idxAction + "]"));
            throw new IllegalArgumentException();
        }
    	//1. IndexMeta-->SQLQuery
    	Map<String, SqlQuery> sqlQueryMap = transIndexMeta2SQLQuery(idxMeta, idxAction, dataMap);
    	//2. get a connection
    	SqlDataSourceWrap dataSourceWrape = (SqlDataSourceWrap) this.dataSourcePoolService.getDataSourceByKey(idxMeta.getDataSourceInfo());
    	if (dataSourceWrape == null) {
            LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_PROCESS, "doIndexByIndexAction",
                "[indexMeta:" + indexMeta + "][idxAction:" + idxAction + "]"),
                "getDataSourceByKey return null");
            throw new DataSourceException();
        }
    	// 3. get data & write index
        Map<String, BigDecimal> maxDataIdMap = new HashMap<String, BigDecimal>();
        
        
        for (String tableName : sqlQueryMap.keySet()) {
            SqlQuery sqlQuery = sqlQueryMap.get(tableName);
            
            if(!StringUtils.isEmpty(sqlQuery.getIdName())){
            	sqlQuery.getOrderBy().add(sqlQuery.getIdName()); 
            }
            
            long total = 0;
			BigDecimal currMaxId = BigDecimal.valueOf(-1);

			if (idxMeta.getDataDescInfo().getMaxDataId(tableName) != null
					&& !idxAction.getFromScratch()) {
				// 数据正常更新，最上次的最大ID
				currMaxId = idxMeta.getDataDescInfo().getMaxDataId(tableName);
			}
			// 其它情况，init、merge情况currMaxId=0，mod情况currMaxId可以为0 
			String currWhereStr=sqlQuery.getIdName() + " > " + currMaxId.longValue();
			sqlQuery.getWhereList().add(currWhereStr);
            if (idxAction.equals(IndexAction.INDEX_INIT_LIMITED)) {
                total = IndexFileSystemConstants.INDEX_DATA_TOTAL_IN_LIMITEDMODEL;
            } else {
                total = this.dataQueryService.queryForLongWithSql(getCountSQLBySQLQuery(sqlQuery),
                    dataSourceWrape);
            }
            
            boolean isLastPiece = false;  
            long pcount = IndexFileSystemConstants.FETCH_SIZE_FROM_DATASOURCE;
            // 目前是跟据数据量进行划分
            if (pcount > total) {
                pcount = total;
            }
            if (!StringUtils.isEmpty(sqlQuery.getIdName())
                    && !CollectionUtils.isEmpty(sqlQuery.getSelectList())) {
                sqlQuery.getSelectList().add(sqlQuery.getIdName());
            }
            
            
            for (int i = 0; i * pcount < total; i++) {
                long limitStart = 0;
                long limitEnd = pcount;
                if ((i + 1) * pcount >= total || idxAction.equals(IndexAction.INDEX_MOD)) {
                    isLastPiece = true;
                }
                
                if(sqlQuery.getWhereList().contains(currWhereStr)){
                	sqlQuery.getWhereList().remove(currWhereStr);
                }
                
                currWhereStr=sqlQuery.getIdName() + " > " + currMaxId.longValue();
				sqlQuery.getWhereList().add(currWhereStr);
				
                
                TesseractResultSet currResult = this.dataQueryService.queryForDocListWithSQLQuery(
                    sqlQuery, dataSourceWrape, limitStart, limitEnd);
                
                IndexShard currIdxShard = null; 
                int currIdxShardIdx=-1;
                
                while(currResult.size()!=0){
                	//当前数据待处理，获取待处理的索引分片
                	if(currIdxShard==null){
                		currIdxShardIdx=this.getIndexShardByIndexAction(idxMeta, idxAction, currIdxShardIdx);
                		currIdxShard=idxMeta.getIdxShardList().get(currIdxShardIdx);                		
                	}
                	
                	//处理
                	Map<String, Object> result = writeIndex(currResult, idxAction, currIdxShard,
                            isLastPiece, sqlQuery.getIdName());
                	
                	currResult = (TesseractResultSet) result.get(RESULT_KEY_DATA);
                    currMaxId = (BigDecimal) result.get(RESULT_KEY_MAXID);                    
                    currIdxShard=(IndexShard)result.get(RESULT_KEY_INDEXSHARD);
                    if (currIdxShard.isFull() || isLastPiece) {
                        //设置当前分片的状态为内容已变更
                    	currIdxShard=null;
                    	
                    	if(idxAction.equals(IndexAction.INDEX_MOD) || idxAction.equals(IndexAction.INDEX_MERGE) || idxAction.equals(IndexAction.INDEX_MERGE_NORMAL)){
                    		currIdxShardIdx++;
                    	}else{
                    		currIdxShardIdx=-1;
                    	}
						
                    }
                    if(idxAction.equals(IndexAction.INDEX_MERGE)){                    	
                    	idxAction=IndexAction.INDEX_MERGE_NORMAL;
                    }else if(!idxAction.equals(IndexAction.INDEX_MOD) && !idxAction.equals(IndexAction.INDEX_MERGE_NORMAL)){
                    	idxAction=IndexAction.INDEX_NORMAL;
                    }
                    
                }
                
            }
            
            maxDataIdMap.put(tableName, currMaxId);
            
        }
        
        if(!idxAction.equals(IndexAction.INDEX_MOD)){
        	//除了修订的情况外，init merge update都需要保存上次索引后的最大id
        	idxMeta.getDataDescInfo().setMaxDataIdMap(maxDataIdMap);
        }
        
        if (idxMeta.getIdxState().equals(IndexState.INDEX_AVAILABLE_NEEDMERGE)) {
            idxMeta.getCubeIdSet().addAll(idxMeta.getCubeIdMergeSet());
            idxMeta.getCubeIdMergeSet().clear();
            
            idxMeta.getDimSet().addAll(idxMeta.getDimInfoMergeSet());
            idxMeta.getMeasureSet().addAll(idxMeta.getMeasureInfoMergeSet());
            idxMeta.getDimInfoMergeSet().clear();
            idxMeta.getMeasureInfoMergeSet().clear();
            
        }
        
        if(idxMeta.getIdxState().equals(IndexState.INDEX_AVAILABLE_NEEDMERGE) || idxMeta.getIdxState().equals(IndexState.INDEX_UNINIT)){
        	idxMeta.setIdxState(IndexState.INDEX_AVAILABLE);
        }
        
        for(IndexShard idxShard:idxMeta.getIdxShardList()){
        	if(idxShard.isUpdate()){
        		String servicePath=idxShard.getFilePath();
        		String bakFilePath=idxShard.getIdxFilePath();
        		idxShard.setIdxFilePath(servicePath);
        		idxShard.setFilePath(bakFilePath);
        	}else if(idxAction.equals(IndexAction.INDEX_MERGE)){
        		idxMeta.getIdxShardList().remove(idxShard);
        	}
        }
        this.indexMetaService.saveOrUpdateIndexMeta(idxMeta);
        
        
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_END, "doIndex",
            "[indexMeta:" + indexMeta + "][idxAction:" + idxAction + "]"));
        return;
        
    }
    
    
    
    
    /**
     * getIndexShardByIndexAction 获取当前待处理的分片的数组下标
     * @param idxMeta 索引元数据
     * @param idxAction 动作
     * @param idxShardIdx 要获取的数组下标
     * @return
     */
	private int getIndexShardByIndexAction(IndexMeta idxMeta,
			IndexAction idxAction, int idxShardIdx) {
		if (idxAction.getFromScratch() && idxShardIdx >= 0
				&& idxShardIdx < idxMeta.getIdxShardList().size()) {
			//idxShardIdx正常，且idxAction为从0开始的，则直接反回
			return idxShardIdx;
		} else if (idxAction.getFromScratch() && idxShardIdx == -1 && !CollectionUtils.isEmpty(idxMeta.getIdxShardList())) {
			for(int i=0; i<idxMeta.getIdxShardList().size();i++){
				if(!idxMeta.getIdxShardList().get(i).isUpdate()){
					idxShardIdx = i;
					break;
				}
			}
			
			return idxShardIdx;
		} else {
			return getFreeIndexShardIndexForIndex(idxMeta);
		}
		

	}
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.biplatform.tesseract.isservice.index.service.IndexService
     * #doIndex(com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexMeta)
     */
    @Override
    public boolean doIndex(IndexMeta indexMeta, IndexAction idxAction)
        throws IndexMetaIsNullException, DataSourceException, IndexAndSearchException {
        // 在进行索引更新时，需要提供每个表的更新规则
        
        // 检查idxMeta是否需要初始化处理
        // if 需要初始化：
        // 1、IndexMeta-->SQLQuery
        // 2、初始化索引分片
        // 2、索引文件路径准备（如果是初始化，则直接创建路径，否则cp出一个新的路径）
        // 3、获取数据连接
        // 4、查询数据
        // 5、写索引（涉及将通过socket发送数据写数据的过程进行封装），一个分片一个分片的写，也就是说当前分片写满了，除非是修改这个分片中的数据，否则后续更新时也不会向这个分片写文件
        // 6、一块写满了，换另一块
        // 7、写数据结束
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_BEGIN, "doIndex",
            "[indexMeta:" + indexMeta + "][idxAction:" + idxAction + "]"));
        
        IndexMeta idxMeta = indexMeta;
        if (idxMeta == null || idxAction == null) {
            LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_EXCEPTION, "doIndex",
                "[indexMeta:" + indexMeta + "][idxAction:" + idxAction + "]"));
            throw new IllegalArgumentException();
        }
        // s1. transfer indexMeta to SQLQuery
        Map<String, SqlQuery> sqlQueryMap = transIndexMeta2SQLQuery(idxMeta,false);
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_PROCESS_NO_PARAM,
            "doIndex", "transIndexMeta2SQLQuery success"));
        
        // s2. prepare index depends on idxState and indexAction
        //boolean isUpdate = false;
        
        // init params depend on action and idxState
        if (idxMeta.getIdxState().equals(IndexState.INDEX_UNINIT)
            && (idxAction.equals(IndexAction.INDEX_INIT) || idxAction
                .equals(IndexAction.INDEX_INIT_LIMITED))) {
            LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_PROCESS_NO_PARAM,
                "doIndex", "process init before index"));
            // 索引分片申请
            idxMeta = this.indexMetaService.assignIndexShard(idxMeta, this.isNodeService
                .getCurrentNode().getClusterName());
            
        } else if ((idxMeta.getIdxState().equals(IndexState.INDEX_AVAILABLE_NEEDMERGE) || !idxMeta
            .getIdxState().equals(IndexState.INDEX_UNAVAILABLE)) && idxAction.equals(IndexAction.INDEX_MERGE)
            || idxAction.equals(IndexAction.INDEX_UPDATE) ) {
            
            LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_PROCESS_NO_PARAM,
                "doIndex", "index merge and update before index"));
            // 索引更新
            if (idxAction.equals(IndexAction.INDEX_MERGE)) {
                LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_PROCESS_NO_PARAM,
                    "doIndex", "process merge"));
                boolean needMerge=true;
                sqlQueryMap = transIndexMeta2SQLQuery(idxMeta,needMerge);
                
            } else if (!idxMeta.getIdxState().equals(IndexState.INDEX_UNAVAILABLE)) {
                LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_PROCESS_NO_PARAM,
                    "doIndex", "process update before index"));
                // 正常更新               
//                for (String tableName : idxMeta.getDataDescInfo().getTableNameList()) {
//                    List<String> whereList = sqlQueryMap.get(tableName).getWhereList();
//                    if (whereList == null) {
//                        whereList = new ArrayList<String>();
//                    }
//                    BigDecimal maxId = idxMeta.getDataDescInfo().getMaxDataId(tableName);
//                    if (maxId != null && !maxId.equals(BigDecimal.ZERO)) {
//                        String where = idxMeta.getDataDescInfo().getIdStr() + " > "
//                            + maxId.longValue();
//                        whereList.add(where);
//                    }
//                    sqlQueryMap.get(tableName).setWhereList(whereList);
//                }
				
                
                
            }
            
        } else {
            throw new IllegalArgumentException();
        }
        
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_PROCESS_NO_PARAM,
            "doIndex", "init before index done"));
        // s3. get connection
        
        SqlDataSourceWrap dataSourceWrape = (SqlDataSourceWrap) this.dataSourcePoolService
            .getDataSourceByKey(idxMeta.getDataSourceInfo());
        if (dataSourceWrape == null) {
            LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_PROCESS, "doIndex",
                "[indexMeta:" + indexMeta + "][idxAction:" + idxAction + "]"),
                "getDataSourceByKey return null");
            throw new DataSourceException();
        }
        
        // s4. get data & write index
        Map<String, BigDecimal> maxDataIdMap = new HashMap<String, BigDecimal>();
        
        //8888888888888888888888888888888888888888888888
        long startIndex=System.nanoTime();
        
        long totalTime=0;
        long totalForTime=0;
        long totalOtherTime=0;
        long totalSqlTime=0;
        IndexShard currIdxShard = getFreeIndexShardForIndex(idxMeta);
        
        boolean finishIndex=false;
        for (String tableName : sqlQueryMap.keySet()) {
            SqlQuery sqlQuery = sqlQueryMap.get(tableName);
            long total = 0;
            if (idxAction.equals(IndexAction.INDEX_INIT_LIMITED)) {
                total = IndexFileSystemConstants.INDEX_DATA_TOTAL_IN_LIMITEDMODEL;
            } else {
                total = this.dataQueryService.queryForLongWithSql(getCountSQLBySQLQuery(sqlQuery),
                    dataSourceWrape);
            }
            
            boolean isLastPiece = false;
            
            
            //初始化finishIndex
            if(!isLastPiece){
            	finishIndex=false;
            }
            
            BigDecimal currMaxId = BigDecimal.ZERO;            
            //初始化maxId
            
            if(idxMeta.getDataDescInfo().getMaxDataId(tableName)!=null && idxAction.equals(IndexAction.INDEX_UPDATE)){
            	//数据正常更新，最上次的最大ID
            	currMaxId=idxMeta.getDataDescInfo().getMaxDataId(tableName);
            }
            //否则，init和merge情况currMaxId=0   
            
            long pcount = IndexFileSystemConstants.FETCH_SIZE_FROM_DATASOURCE;
            // 目前是跟据数据量进行划分
            if (pcount > total) {
                pcount = total;
            }
            if (!StringUtils.isEmpty(sqlQuery.getIdName())
                    && !CollectionUtils.isEmpty(sqlQuery.getSelectList())) {
                sqlQuery.getSelectList().add(sqlQuery.getIdName());
            }
            
            String currWhereStr="";
            for (int i = 0; i * pcount < total; i++) {
            	long startForTime=System.nanoTime();
            	
                long limitStart = 0;
                long limitEnd = pcount;
                if ((i + 1) * pcount >= total) {
                    isLastPiece = true;
                }
                
                if(sqlQuery.getWhereList().contains(currWhereStr)){
                	sqlQuery.getWhereList().remove(currWhereStr);
                }
                
                currWhereStr=sqlQuery.getIdName() + " > " + currMaxId.longValue();
				sqlQuery.getWhereList().add(currWhereStr);
                long startSqlTime=System.nanoTime();
                TesseractResultSet currResult = this.dataQueryService.queryForDocListWithSQLQuery(
                    sqlQuery, dataSourceWrape, limitStart, limitEnd);
                long endTime=System.nanoTime();
                totalOtherTime+=(endTime-startForTime);
                totalSqlTime+=(endTime-startSqlTime);
                //取出数据待处理
                while (currResult.size() != 0) {
                    // 向索引分片中写入数据
					if (currIdxShard == null) {
						currIdxShard = getFreeIndexShardForIndex(idxMeta);
					}
                    long startWriteIndex=System.nanoTime();
                    Map<String, Object> result = writeIndex(currResult, idxAction, currIdxShard,
                        isLastPiece, sqlQuery.getIdName());
                    long endWriteIndex=System.nanoTime();
                    
                    totalTime=totalTime+(endWriteIndex-startWriteIndex);
                    System.out.println("******************************************write index cost : "+(endWriteIndex-startWriteIndex)+" ns ******************************************");
                    currResult = (TesseractResultSet) result.get(RESULT_KEY_DATA);
                    currMaxId = (BigDecimal) result.get(RESULT_KEY_MAXID);
                    // 更新数据
                    currIdxShard = (IndexShard) result.get(RESULT_KEY_INDEXSHARD);
                    
                    if (currIdxShard.isFull() || isLastPiece) {
                        // 当一个分片更新后，整个cube数据存在不一致的情况，这个状态会持续到所有分片都更新完成后
                        // 这时，需要设置cube数据不可用
                        idxMeta.setIdxState(IndexState.INDEX_UNAVAILABLE);
                        idxMeta = saveIndexShardIntoIndexMeta(currIdxShard, idxMeta);
						currIdxShard = null;
						if(isLastPiece){
							finishIndex=true;
						}
                    }
                    
                    idxAction=IndexAction.INDEX_NORMAL;
                    
                }
                
                long endForTime=System.nanoTime();
                totalForTime=totalForTime+(endForTime-startForTime);
                
                
            }
           
            maxDataIdMap.put(tableName, currMaxId);
            
        }
        //8888888888888888888888888888888888888888888888
        long endIndex=System.nanoTime();
        System.out.println("******************************************index cost : "+(endIndex-startIndex)+" ns ******************************************");
        System.out.println("******************************************write index cost : "+totalTime +" ns ***************************************");
        System.out.println("******************************************total for time cost : " +totalForTime+" ns ***************************************");
        System.out.println("******************************************total other time cost : " +totalOtherTime+" ns ***************************************");
        System.out.println("******************************************total sql time cost : " +totalSqlTime+" ns ***************************************");
        if(!idxAction.equals(IndexAction.INDEX_MOD)){
        	//除了修订的情况外，init merge update都需要保存上次索引后的最大id
        	idxMeta.getDataDescInfo().setMaxDataIdMap(maxDataIdMap);
        }
        
        if (idxMeta.getIdxState().equals(IndexState.INDEX_AVAILABLE_NEEDMERGE)) {
            idxMeta.getCubeIdSet().addAll(idxMeta.getCubeIdMergeSet());
            idxMeta.getCubeIdMergeSet().clear();
            
            idxMeta.getDimSet().addAll(idxMeta.getDimInfoMergeSet());
            idxMeta.getMeasureSet().addAll(idxMeta.getMeasureInfoMergeSet());
            idxMeta.getDimInfoMergeSet().clear();
            idxMeta.getMeasureInfoMergeSet().clear();
        }
        if(finishIndex){
        	idxMeta.setIdxState(IndexState.INDEX_AVAILABLE);
            this.indexMetaService.saveOrUpdateIndexMeta(idxMeta);
        }
        
        
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_END, "doIndex",
            "[indexMeta:" + indexMeta + "][idxAction:" + idxAction + "]"));
        return true;
    }
    
    
    /**
     * 
     * getFreeIndexShardForIndex 从idxMeta中获取空闲的索引分片
     * 
     * @param indexMeta
     *            索引元数据
     * @return IndexShard 如果有未满的分片，则返回对应分片;否则申请新的分片;
     */
    private IndexShard getFreeIndexShardForIndex(IndexMeta indexMeta) {
        IndexMeta idxMeta = indexMeta;
        IndexShard result = null;
        if (idxMeta == null || idxMeta.getIdxShardList() == null) {
            throw new IllegalArgumentException();
        }
        
        if (this.indexMetaService.isIndexShardFull(idxMeta)) {
            idxMeta = this.indexMetaService.assignIndexShard(idxMeta, this.isNodeService
                    .getCurrentNode().getClusterName());
        }
        
        for (IndexShard idxShard : idxMeta.getIdxShardList()) {
            if (!idxShard.isFull()) {
                result = idxShard;
                break;
            }
        }
        
        return result;
    }
    
    /**
     * 从索引元数据中获取空闲索引分片的数组下标
     * @param indexMeta
     * @return int 若找到，返回>-1的值，否则为-1
     */
    private int getFreeIndexShardIndexForIndex(IndexMeta indexMeta){
    	int result=-1;
    	IndexMeta idxMeta = indexMeta;
    	if (idxMeta == null || idxMeta.getIdxShardList() == null) {
            throw new IllegalArgumentException();
        }
    	if (this.indexMetaService.isIndexShardFull(idxMeta)) {
            idxMeta = this.indexMetaService.assignIndexShard(idxMeta, this.isNodeService
                    .getCurrentNode().getClusterName());
        }
    	for(int i=0;i<idxMeta.getIdxShardList().size();i++){
    		if(idxMeta.getIdxShardList().get(i)!=null && !idxMeta.getIdxShardList().get(i).isFull()){
    			result=i;
    			break;
    		}
    	}
    	return result;
    }
    
    /**
     * 
     * saveIndexShardIntoIndexMeta 保存索引分片到元数据中
     * 
     * @param idxShard
     *            索引分片
     * @param idxMeta
     *            索引元数据
     * @return IndexMeta 保存后的索引元数据
     */
    private IndexMeta saveIndexShardIntoIndexMeta(IndexShard idxShard, IndexMeta idxMeta) {
        if (idxMeta == null || idxShard == null || idxMeta.getIdxShardList() == null
                || !idxMeta.getIdxShardList().contains(idxShard)) {
            throw new IllegalArgumentException();
        }
        idxMeta.getIdxShardList().remove(idxShard);
        idxShard.setIdxMeta(idxMeta);
        idxMeta.getIdxShardList().add(idxShard);
        this.indexMetaService.saveOrUpdateIndexMeta(idxMeta);
        return idxMeta;
    }
    
    /**
     * 
     * 跟据sqlQuery得到select count语句
     * 
     * @param sqlQuery
     *            sqlQuery
     * @return String select count语句
     */
    private String getCountSQLBySQLQuery(SqlQuery sqlQuery) {
        StringBuffer sb = new StringBuffer();
        sb.append("select count(*) from (");
        sb.append(sqlQuery.toSql());
        sb.append(") as t");
        return sb.toString();
    }
    
    /**
     * 
     * writeIndex 向指定的分片中写数据，如果该分片写满或者写入的数据是最后一片，则启动数据拷贝线程
     * 
     * @param data
     *            要写的数据
     * @param isUpdate
     *            是否是更新
     * @param idxShard
     *            索引分片
     * @param lastPiece
     *            是否是最后一片数据
     * @return Map<String,Object>
     * @throws IndexAndSearchException
     *             可能抛出的异常
     * 
     */
    public Map<String, Object> writeIndex(TesseractResultSet data, IndexAction idxAction, IndexShard idxShard, boolean lastPiece, String idName)
            throws IndexAndSearchException {
        
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_BEGIN, "writeIndex",
            "[data:" + data + "][idxAction:" + idxAction + "][idxShard:" + idxShard + "][lastPiece:"
                + lastPiece + "][idName:" + idName + "]"));
        // 数据分片规则
        // 调用
        
        IndexMessage message = null;
        message = isClient.index(data, idxAction, idxShard, idName, lastPiece);
        
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_PROCESS_NO_PARAM, 
                "writeIndex", "index success"));
        
        
        
        idxShard.setDiskSize(message.getDiskSize());
        
        if (idxShard.isFull() || lastPiece) {
            // 设置提供服务的目录：
            String absoluteIdxFilePath = message.getIdxServicePath();
//            
//            idxShard.setFilePathWithAbsoluteFilePath(absoluteFilePath, idxShard.getNode());
//            idxShard.setIdxFilePathWithAbsoluteIdxFilePath(absoluteIdxFilePath, idxShard.getNode());
            idxShard.setUpdate(Boolean.TRUE);
            // 启动数据copy线程拷贝数据到备分节点上
            if (CollectionUtils.isEmpty(idxShard.getReplicaNodeList())) {
                List<Node> assignedNodeList = this.isNodeService.assignFreeNodeForReplica(
                        IndexShard.getDefaultShardReplicaNum() - 1, idxShard.getNode());
                
                if (assignedNodeList != null && assignedNodeList.size() > 0) {
                    if (assignedNodeList.contains(idxShard.getNode())) {
                        assignedNodeList.remove(idxShard.getNode());
                    }
                    idxShard.setReplicaNodeList(assignedNodeList);
                }
                
            }
            
            for (Node node : idxShard.getReplicaNodeList()) {
                node.setNodeState(NodeState.NODE_UNAVAILABLE);
                this.isNodeService.saveOrUpdateNodeInfo(node);
                
            }
            
            for (Node node : idxShard.getReplicaNodeList()) {
                int retryTimes = 0;
                //拷贝到指定的目录
                String targetFilePath = idxShard.getAbsoluteFilePath(node);
                ServerFeedbackMessage backMessage = null;
                while (retryTimes < TesseractConstant.RETRY_TIMES) {
                    backMessage = isClient.copyIndexDataToRemoteNode(absoluteIdxFilePath,
                        targetFilePath, true, node);
                    if (backMessage.getResult().equals(FileUtils.SUCC)) {
                        node.setNodeState(NodeState.NODE_AVAILABLE);
                        this.isNodeService.saveOrUpdateNodeInfo(node);
                        LOGGER.info(String.format(
                            LogInfoConstants.INFO_PATTERN_FUNCTION_PROCESS_NO_PARAM, "writeIndex",
                            "copy index success to " + node));
                        break;
                    } else {
                        LOGGER.info(String.format(
                            LogInfoConstants.INFO_PATTERN_FUNCTION_PROCESS_NO_PARAM, "writeIndex",
                            "retry copy index to " + node));
                        retryTimes++;
                    }
                    
                }
                
            }
        }
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT_KEY_INDEXSHARD, idxShard);
        result.put(RESULT_KEY_DATA, message.getDataBody());
        result.put(RESULT_KEY_MAXID, message.getMaxId());
        
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_END, "writeIndex",
            "[data:" + data + "][idxAction:" + idxAction + "][idxShard:" + idxShard + "][lastPiece:"
                + lastPiece + "][idName:" + idName + "]"));
        return result;
        
    }
    
    /**
     * 
     * 根据IndexMeta获取从事实表中取数据的SQLQuery对像
     * 
     * @param idxMeta
     *            当前的idxMeta
     * @param needMerge
     *            idxMeta是否需要合并
     * @return List<SQLQuery> 返回sqlquery对像
     * @throws IndexMetaIsNullException
     *             当idxMeta为空时会抛出异常
     */
    private Map<String, SqlQuery> transIndexMeta2SQLQuery(IndexMeta idxMeta,boolean needMerge)
            throws IndexMetaIsNullException {
        Map<String, SqlQuery> result = new HashMap<String, SqlQuery>();
        if (idxMeta == null || idxMeta.getDataDescInfo() == null) {
            throw generateIndexMetaIsNullException(idxMeta);
        }
        
        Set<String> selectList = idxMeta.getSelectList(needMerge);
        if (selectList == null) {
            selectList = new HashSet<String>();
        }
        String idName=idxMeta.getDataDescInfo().getIdStr();
        for (String tableName : idxMeta.getDataDescInfo().getTableNameList()) {
            SqlQuery sqlQuery = new SqlQuery();
            LinkedList<String> fromList = new LinkedList<String>();
            fromList.add(tableName);
            sqlQuery.setFromList(fromList);
            sqlQuery.getSelectList().addAll(selectList);
            result.put(tableName, sqlQuery);
            if(!StringUtils.isEmpty(idName)){
                sqlQuery.setIdName(idName);
            }
        }
        
        return result;
    }
    
    /**
     * 根据IndexMeta获取从事实表中取数据的SQLQuery对像
     * @param idxMeta 当前的idxMeta
     * @param idxAction 索引动作
     * @return Map<String, SqlQuery> 返回sqlquery对像
     * @throws IndexMetaIsNullException 当idxMeta为空时会抛出异常
     */
    private Map<String, SqlQuery> transIndexMeta2SQLQuery(IndexMeta idxMeta,IndexAction idxAction,Map<String, BigDecimal> dataMap)
            throws IndexMetaIsNullException {
        Map<String, SqlQuery> result = new HashMap<String, SqlQuery>();
        if (idxMeta == null || idxMeta.getDataDescInfo() == null) {
            throw generateIndexMetaIsNullException(idxMeta);
        }
        
        if(idxAction.equals(IndexAction.INDEX_MOD) && MapUtils.isEmpty(dataMap) ){
        	throw new IllegalArgumentException();
        }
        
        boolean needMerge=Boolean.FALSE;
        if(idxAction.equals(IndexAction.INDEX_MERGE)){
        	needMerge=Boolean.TRUE;
        }
        Set<String> selectList = idxMeta.getSelectList(needMerge);
        if (selectList == null) {
            selectList = new HashSet<String>();
        }
        String idName=idxMeta.getDataDescInfo().getIdStr();
        BigDecimal start=null;
        BigDecimal end=null;
        if(idxAction.equals(IndexAction.INDEX_MOD) && !MapUtils.isEmpty(dataMap) ){
        	start=dataMap.get(IndexFileSystemConstants.MOD_KEY_START);
        	end=dataMap.get(IndexFileSystemConstants.MOD_KEY_END);
        } 
        for (String tableName : idxMeta.getDataDescInfo().getTableNameList()) {
            SqlQuery sqlQuery = new SqlQuery();
            LinkedList<String> fromList = new LinkedList<String>();
            fromList.add(tableName);
            sqlQuery.setFromList(fromList);
            sqlQuery.getSelectList().addAll(selectList);
            result.put(tableName, sqlQuery);
            if(!StringUtils.isEmpty(idName)){
                sqlQuery.setIdName(idName);
            }
            if(start!=null){
            	sqlQuery.getWhereList().add(idName+" >= "+start);
            	 if(end!=null){
                 	sqlQuery.getWhereList().add(idName+" <= "+end);
                 }
            }
           
        }
        
        return result;
    }
    
    
    /**
     * 
     * 统一的生成IndexMetaIsNullException
     * 
     * @param idxMeta
     *            索引元数据
     * @return IndexMetaIsNullException
     */
    private IndexMetaIsNullException generateIndexMetaIsNullException(IndexMeta idxMeta) {
        StringBuffer sb = new StringBuffer();
        if (idxMeta == null) {
            sb.append("IndexMeta [");
            sb.append(idxMeta);
            sb.append("]");
        } else {
            sb.append(idxMeta.toString());
        }
        LOGGER.info("IndexMetaIsNullException ocurred:" + sb.toString());
        return new IndexMetaIsNullException(sb.toString());
    }
    
}
