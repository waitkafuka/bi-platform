/**
 * 
 */
package com.baidu.rigel.biplatform.tesseract.isservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMeasure;
import com.baidu.rigel.biplatform.ac.minicube.StandardDimension;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.Measure;
import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.DataDescInfo;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexMeta;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexShard;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexState;
import com.baidu.rigel.biplatform.tesseract.netty.message.MessageHeader;
import com.baidu.rigel.biplatform.tesseract.netty.message.NettyAction;
import com.baidu.rigel.biplatform.tesseract.netty.message.isservice.IndexMessage;
import com.baidu.rigel.biplatform.tesseract.node.meta.Node;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.Expression;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.From;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.GroupBy;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.Limit;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.Order;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryMeasure;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryObject;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryRequest;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.Select;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.Where;
import com.baidu.rigel.biplatform.tesseract.resultset.TesseractResultSet;
import com.baidu.rigel.biplatform.tesseract.resultset.isservice.ResultRecord;
import com.baidu.rigel.biplatform.tesseract.resultset.isservice.SearchResultSet;

/**
 * TODO
 * @author lijin
 *
 */
public class IndexAndSearchTestBase {
	
	/**
     * STR_DIM_NAME_1
     */
	protected static final String STR_DIM_NAME_1 = "dim1";
    
    /**
     * STR_DIM_NAME_2
     */
	protected static final String STR_DIM_NAME_2 = "dim2";
    /**
     * STR_DIM_NAME_3
     */
	protected static final String STR_DIM_NAME_3 = "dim3";
    /**
     * STR_MEASURE_NAME_1
     */
	protected static final String STR_MEASURE_NAME_1="measure1";
    /**
     * STR_MEASURE_NAME_2
     */
	protected static final String STR_MEASURE_NAME_2="measure2";
    /**
     * STR_MEASURE_NAME_3
     */
	protected static final String STR_MEASURE_NAME_3="measure3";
    
    /**
     * 字符串常量
     */
	protected static final String STR_PRODUCT_LINE = "test_prod1";
    /**
     * 测试用的集群名
     */
	protected static final String STR_CLUSTER_NAME = "test_cluster1";
    
    /**
     * 测试用的事实表名-1
     */
	protected static final String STR_FACTTABLE_NAME1 = "data_fc_click_pay";
    
    /**
     * 测试用的事实表名-2
     */
	protected static final String STR_FACTTABLE_NAME2 = "data_click_pay";
    
    /**
     * 测试用的事实表名-3
     */
	protected static final String STR_FACTTABLE_NAME3 = "data_fc_click_pay_notsame";
    
    /**
     * STR_ABC
     */
	protected static final String STR_ABC = "abc";
    
    /**
     * 测试用CUBE名-1
     */
	protected static final String STR_CUBE_NAME1 = "testCube1";
    /**
     * 测试用CUBE名-2
     */
	protected static final String STR_CUBE_NAME2 = "testCube_notsame";
    
    /**
     * 测试用storekey-1
     */
	protected static final String STR_STOREKEY_1 = "test_cluster1_test_prod1_xxkey";
    /**
     * DATA_SOURCE_INFO_UUID
     */
	protected static final String DATA_SOURCE_INFO_UUID = "TEST_UUID";
	
	
    
    /**
     * 测试用storekey-2
     */
	protected static final SqlDataSourceInfo DATA_SOURCE_INFO = new SqlDataSourceInfo(DATA_SOURCE_INFO_UUID);
    /**
     * static 初始化块
     */
    static {
        DATA_SOURCE_INFO.setInstanceName("zongheng");
        DATA_SOURCE_INFO.setDBProxy(true);
        DATA_SOURCE_INFO.setUsername("zongheng");
    }
    
    protected DataSourceInfo mockDataSourceInfo(){
        SqlDataSourceInfo dataSourceInfo = new SqlDataSourceInfo(DATA_SOURCE_INFO_UUID);
        
        return dataSourceInfo;
    }
    
    protected QueryRequest mockQueryRequest(boolean useIndex){
        QueryRequest query=new QueryRequest();
        query.setCubeId(STR_CUBE_NAME1);
        query.setCubeName(STR_CUBE_NAME1);
        DataSourceInfo dataSourceInfo=new SqlDataSourceInfo(null);
        
        query.setDataSourceInfo(dataSourceInfo);
        From from=new From(STR_FACTTABLE_NAME1);        
        query.setFrom(from);
        GroupBy groupBy=new GroupBy();
        Set<String> groupList=new HashSet<String>();
        groupList.add(STR_DIM_NAME_1);        
        groupBy.setGroups(groupList);
        query.setGroupBy(groupBy);
        Limit limit=new Limit(4);
        query.setLimit(limit);
        Order order=new Order();
        List<String> orderDatas=new ArrayList<String>();
        orderDatas.add(STR_DIM_NAME_2);
        order.setOrderDatas(orderDatas);
        query.setOrder(order);
        Select select=new Select();
        List<QueryMeasure> queryMeasures=new ArrayList<QueryMeasure>();
        QueryMeasure qm1=new QueryMeasure(STR_MEASURE_NAME_1);
        queryMeasures.add(qm1);        
        select.setQueryMeasures(queryMeasures);
        List<String> queryProperties=new ArrayList<String>();
        queryProperties.add(STR_DIM_NAME_1);
        queryProperties.add(STR_DIM_NAME_2);        
        select.setQueryProperties(queryProperties);
        query.setSelect(select);
        query.setUseIndex(useIndex);
        Where where=new Where();
        List<Expression> andList=new ArrayList<Expression>();
        Set<QueryObject> ql=new HashSet<QueryObject>();
        QueryObject qo=new QueryObject(STR_DIM_NAME_1+"_"+"0");
        ql.add(qo);
        Expression e1=new Expression(STR_DIM_NAME_1,ql);
        andList.add(e1);
        where.setAndList(andList);
        query.setWhere(where);
       
       
        return query;
    }
	
	protected TesseractResultSet mockResultSet(int num){
        LinkedList<ResultRecord> rList=new LinkedList<ResultRecord>();
        for(int i=0;i<num ; i++){
        	Serializable[] field=new Serializable[5];
            field[0]=new String(STR_MEASURE_NAME_1+"_"+i);
            field[1]=new String(STR_DIM_NAME_1+"_"+i);
            field[2]=new String(STR_DIM_NAME_2+"_"+i);
            field[3]=new String(STR_DIM_NAME_3+"_"+i);
            field[4]=Long.toString(i*4);
            String[] fieldName=new String[5];
            fieldName[0]=new String(STR_MEASURE_NAME_1);
            fieldName[1]=new String(STR_DIM_NAME_1);
            fieldName[2]=new String(STR_DIM_NAME_2);
            fieldName[3]=new String(STR_DIM_NAME_3);
            fieldName[4]=new String(STR_MEASURE_NAME_2);
            ResultRecord r=new ResultRecord(field , fieldName);
            rList.add(r);
        }
        
        
        TesseractResultSet result=new SearchResultSet(rList);
        
        return result;   
        
    }
	
	/**
     * 
     * mockStoreKey
     * 
     * @return String
     */
	protected String mockStoreKey() {
        StringBuffer sb = new StringBuffer();
        sb.append(STR_CLUSTER_NAME);
        sb.append("_");
        sb.append(STR_PRODUCT_LINE);
        sb.append("_");
        sb.append(DATA_SOURCE_INFO.getDataSourceKey());
        return sb.toString();
    }
	
	/**
     * mockIndexShard
     * 
     * @param num
     *            num
     * @return List<IndexShard>
     */
	protected List<IndexShard> mockIndexShard(int num) {
        List<IndexShard> result = new ArrayList<IndexShard>();
        for (int i = 0; i < num; i++) {
//            Node node = new Node("10.12.13." + System.currentTimeMillis(), 8080, STR_CLUSTER_NAME);
        	Node node = new Node("127.0.0.1", 9988, STR_CLUSTER_NAME);
//            node.setDiskCapacity(12340000);
//            node.setDiskUsed(12345);
            
            IndexShard idxShard = new IndexShard("shard_" + System.currentTimeMillis(), node);
            
            result.add(idxShard);
        }
        return result;
    }
	
	protected List<Node> mockNodeList(int num){
	    List<Node> result=new ArrayList<Node>();
	    for(int i=0;i<num;i++){
	        Node node = new Node("127.0.0.1", 9901+i, STR_CLUSTER_NAME);
	        result.add(node);
	    }
	    return result;
	}
	
	
	
	/**
     * 
     * Mock cache
     * 
     * @return Cache 返回mock的cache
     */
	protected Cache mockCache() {
        Cache cache = new ConcurrentMapCache(IndexMeta.getDataStoreName());
        List<IndexMeta> idxMetaList = new ArrayList<IndexMeta>();
        IndexMeta idxMeta1 = new IndexMeta();
        idxMeta1.setClusterName(STR_CLUSTER_NAME);
        Set<String> cubeNameSet = new HashSet<String>();
        cubeNameSet.add(STR_CUBE_NAME1);
        idxMeta1.setCubeIdSet(cubeNameSet);
        idxMeta1.setDataSourceInfo(DATA_SOURCE_INFO);
        DataDescInfo dataDescInfo1 = new DataDescInfo();
        dataDescInfo1.setSplitTable(false);
        dataDescInfo1.setTableName(STR_FACTTABLE_NAME1);
        idxMeta1.setDataDescInfo(dataDescInfo1);
        idxMeta1.setProductLine(STR_PRODUCT_LINE);
        idxMeta1.setReplicaNum(2);
        idxMeta1.setIdxShardList(this.mockIndexShard(2));
//        idxMeta1.setDimInfoMap(this.mockDimInfoMap(2));
//        idxMeta1.setMeasureInfoMap(this.mockMeasureMap(2));
        idxMetaList.add(idxMeta1);
        
        IndexMeta idxMeta2 = new IndexMeta();
        idxMeta2.setClusterName(STR_CLUSTER_NAME);
        Set<String> cubeNameSet2 = new HashSet<String>();
        cubeNameSet2.add("testCube2");
        idxMeta2.setCubeIdSet(cubeNameSet2);
        idxMeta2.setDataSourceInfo(DATA_SOURCE_INFO);
        DataDescInfo dataDescInfo2 = new DataDescInfo();
        dataDescInfo2.setSplitTable(false);
        dataDescInfo2.setTableName(STR_FACTTABLE_NAME2);
        idxMeta2.setDataDescInfo(dataDescInfo2);
        idxMeta2.setProductLine(STR_PRODUCT_LINE);
        idxMeta2.setReplicaNum(2);
        idxMeta2.setIdxShardList(this.mockIndexShard(1));
        idxMetaList.add(idxMeta2);
        
        String storeKey = idxMeta1.getStoreKey();
        
        cache.put(storeKey, idxMetaList);
        
        return cache;
        
    }
    
    /**
     * 
     * mockAssignedNode
     * 
     * @return Map<Node, Integer>
     */
	protected Map<Node, Integer> mockAssignedNode() {
        Map<Node, Integer> result = new HashMap<Node, Integer>();
        Node node = new Node("10.12.13.45", 8080, STR_CLUSTER_NAME);
        result.put(node, 2);
        return result;
    }
	
	protected List<Node> mockAssignedNodeForReplica(int num){
	    List<Node> result=new ArrayList<Node>();
	    for(int i=0;i<num;i++){
	        Node node=new Node("10.150.113.24",8001+i,STR_CLUSTER_NAME);
	        result.add(node);
	    }
	    return result;
	}
	

    /**
     * 
     * mockMeasureMap
     * 
     * @param mNum
     *            mNum
     * @return Map<String, Measure>
     */
	protected Map<String, Measure> mockMeasureMap(int mNum) {
        Map<String, Measure> measureMap = new HashMap<String, Measure>();
        for (int i = 0; i < mNum; i++) {
            MiniCubeMeasure measure = new MiniCubeMeasure("measure_" + i);
            measure.setId("measure_" + i + "_Id");
            measureMap.put(measure.getName(), measure);
        }
        
        return measureMap;
        
    }
	
	
    
	protected IndexMeta mockIndexMeta(String cubeId) {
        
        IndexMeta idxMeta2 = new IndexMeta();
        idxMeta2.setClusterName(STR_CLUSTER_NAME);
        Set<String> cubeNameSet2 = new HashSet<String>();
        cubeNameSet2.add(cubeId);
        idxMeta2.setCubeIdSet(cubeNameSet2);
        idxMeta2.setDataSourceInfo(DATA_SOURCE_INFO);
        DataDescInfo dataDescInfo2 = new DataDescInfo();
        dataDescInfo2.setSplitTable(false);
        dataDescInfo2.setTableName(STR_FACTTABLE_NAME2);
        List<String> tableList = new ArrayList<String>();
        dataDescInfo2.setTableNameList(tableList);
        idxMeta2.setDataDescInfo(dataDescInfo2);
        idxMeta2.setProductLine(STR_PRODUCT_LINE);
        idxMeta2.setReplicaNum(2);
        idxMeta2.setIdxShardList(this.mockIndexShard(0));
        idxMeta2.setIdxState(IndexState.INDEX_UNINIT);
        
        return idxMeta2;
    }
    
   
	
	protected IndexMeta mockAssignIdxshard(IndexMeta idxMeta,int num){
        idxMeta.setIdxShardList(this.mockIndexShard(num));
        return idxMeta;
    }
    
    
    
	protected IndexMessage mockIndexMessage(){
        IndexMessage result=new IndexMessage();
        MessageHeader header=new MessageHeader(NettyAction.NETTY_ACTION_INDEX_FEEDBACK);
        result.setMessageHeader(header);
        result.setDiskSize(234);
        result.setBlockSize(1024);
        result.setIdxPath("xx");
        result.setIdxServicePath("yy");
        result.setDataBody(this.mockResultSet(0));
        return result;
        
    }
	
	protected DataDescInfo mockDataDescInfo(boolean isSplit) {
        DataDescInfo dataDescInfo1 = new DataDescInfo();
        dataDescInfo1.setSplitTable(isSplit);
        dataDescInfo1.setTableName(STR_FACTTABLE_NAME1);
        List<String> tableNameList = new ArrayList<String>();
        if (isSplit) {
            tableNameList.add(STR_FACTTABLE_NAME1 + "_1");
            tableNameList.add(STR_FACTTABLE_NAME1 + "_2");
            tableNameList.add(STR_FACTTABLE_NAME1 + "_3");
        } else {
            tableNameList.add(STR_FACTTABLE_NAME1);
        }
        dataDescInfo1.setTableNameList(tableNameList);
        return dataDescInfo1;
    }

}
