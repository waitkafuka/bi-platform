package com.baidu.rigel.biplatform.tesseract.isservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.util.StringUtils;

import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.DimensionType;
import com.baidu.rigel.biplatform.ac.model.Level;
import com.baidu.rigel.biplatform.ac.model.Measure;
import com.baidu.rigel.biplatform.ac.model.MeasureType;
import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.model.ConfigQuestionModel;
import com.baidu.rigel.biplatform.ac.util.AnswerCoreConstant;
import com.baidu.rigel.biplatform.ac.util.JsonUnSeriallizableUtils;
import com.baidu.rigel.biplatform.tesseract.datasource.impl.SqlDataSourceWrap;
import com.baidu.rigel.biplatform.tesseract.isservice.index.service.IndexServiceTest;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.DataDescInfo;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexMeta;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexShard;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexShardState;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexState;
import com.baidu.rigel.biplatform.tesseract.netty.message.MessageHeader;
import com.baidu.rigel.biplatform.tesseract.netty.message.NettyAction;
import com.baidu.rigel.biplatform.tesseract.netty.message.isservice.IndexMessage;
import com.baidu.rigel.biplatform.tesseract.node.meta.Node;
import com.baidu.rigel.biplatform.tesseract.resultset.TesseractResultSet;
import com.baidu.rigel.biplatform.tesseract.resultset.isservice.IndexDataResultRecord;
import com.baidu.rigel.biplatform.tesseract.resultset.isservice.IndexDataResultSet;
import com.baidu.rigel.biplatform.tesseract.resultset.isservice.Meta;
import com.baidu.rigel.biplatform.tesseract.util.IndexFileSystemConstants;
import com.baidu.rigel.biplatform.tesseract.util.TesseractConstant;
import com.mchange.v2.c3p0.DriverManagerDataSource;

/**
 * IndexTestBase
 * 
 * @author lijin
 *
 */
public class IndexTestBase {
    
    /**
     * DEFAULT_CLUSTER_NAME
     */
    public static final String DEFAULT_CLUSTER_NAME = "default";
    
    /**
     * mockQuestion
     * 
     * @return ConfigQuestionModel
     * @throws IOException
     */
    protected ConfigQuestionModel mockQuestion() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
            IndexServiceTest.class.getResourceAsStream("/conf/qm.txt")));
        StringBuilder sb = new StringBuilder();
        String lineStr = null;
        while ((lineStr = br.readLine()) != null) {
            sb.append(lineStr);
            sb.append(System.getProperty("line.separator"));
        }
        System.out.println(sb.toString());
        ConfigQuestionModel question = AnswerCoreConstant.GSON.fromJson(sb.toString(),
            ConfigQuestionModel.class);
        System.out.println(question);
        
        br.close();
        JsonUnSeriallizableUtils.fillCubeInfo(question.getCube());
        return question;
    }
    
    /**
     * mockIndexMeta
     * 
     * @param question
     * @return List<IndexMeta>
     */
    protected List<IndexMeta> mockIndexMeta(ConfigQuestionModel question) {
        Cube cube = question.getCube();
        DataSourceInfo dataSourceInfo = question.getDataSourceInfo();
        List<IndexMeta> result = new ArrayList<IndexMeta>();
        
        MiniCube currCube = (MiniCube) cube;
        
        // 事实表
        List<String> factTableList = new ArrayList<String>();
        if (currCube.isMutilple()) {
            // currCube如果是分表的，则source是以","分隔的字符串
            for (String factTable : currCube.getSource().split(
                TesseractConstant.MINI_CUBE_MULTI_FACTTABLE_SPLITTER)) {
                factTableList.add(factTable);
            }
            
        } else {
            factTableList.add(currCube.getSource());
        }
        
        // cube涉及到的数据的描述信息
        DataDescInfo dataDescInfo = new DataDescInfo();
        
        dataDescInfo.setProductLine(currCube.getProductLine());
        // 设置事实表所在数据源key
        dataDescInfo.setSourceName(dataSourceInfo.getDataSourceKey());
        dataDescInfo.setSplitTable(currCube.isMutilple());
        dataDescInfo.setTableName(currCube.getSource());
        dataDescInfo.setTableNameList(factTableList);
        // dataDescInfo.setIdStr(idStr);
        if (StringUtils.isEmpty(dataDescInfo.getIdStr())) {
            dataDescInfo.setIdStr(IndexFileSystemConstants.FACTTABLE_KEY);
        }
        IndexMeta idxMeta = new IndexMeta();
        // 设置索引元数据基本信息
        idxMeta.setIndexMetaId(String.valueOf(UUID.randomUUID()));
        idxMeta.setProductLine(currCube.getProductLine());
        idxMeta.setDataDescInfo(dataDescInfo);
        
        idxMeta.getCubeIdSet().add(currCube.getId());
        
        // 处理维度
        Set<String> dimSet = new HashSet<String>();
        if (currCube.getDimensions() != null) {
            for (String dimKey : currCube.getDimensions().keySet()) {
                Dimension dim = currCube.getDimensions().get(dimKey);
                // 处理维度不同层级
                if (dim.getLevels() != null) {
                    for (String levelKey : dim.getLevels().keySet()) {
                        Level dimLevel = dim.getLevels().get(levelKey);
                        dimSet.add(dimLevel.getFactTableColumn());
                        // 默认按照时间维度分片
                        if (dimLevel.getDimension().getType().equals(DimensionType.TIME_DIMENSION)) {
                            idxMeta.setShardDimBase(dimLevel.getFactTableColumn());
                        }
                    }
                }
            }
        }
        idxMeta.setDimSet(dimSet);
        
        // 处理指标
        Set<String> measureSet = new HashSet<String>();
        if (currCube.getMeasures() != null) {
            for (String measureKey : currCube.getMeasures().keySet()) {
                Measure measure = currCube.getMeasures().get(measureKey);
                if (measure.getType().equals(MeasureType.COMMON)) {
                    // 普通指标，直接加入到select表列中
                    measureSet.add(measure.getDefine());
                } 
            }
        }
        idxMeta.setMeasureSet(measureSet);
        
        idxMeta.setReplicaNum(2);
        idxMeta.setDataSourceInfo(dataSourceInfo);
        idxMeta.setDataDescInfo(dataDescInfo);
        
        // 设置状态
        idxMeta.setIdxState(IndexState.INDEX_UNINIT);
        result.add(idxMeta);
        
        return result;
    }
    
    /**
     * mockIndexDataResultSet
     * 
     * @return IndexDataResultSet
     */
    protected IndexDataResultSet mockIndexDataResultSet() {
        String[] fieldNames = { "second_trade_id", "csm", "cash" };
        Meta meta = new Meta(fieldNames);
        IndexDataResultSet result = new IndexDataResultSet(meta, 10);
        String[] fieldArr1 = { "101", "12.2", "23.4" };
        IndexDataResultRecord ir1 = new IndexDataResultRecord(fieldArr1, "second_trade_id");
        result.addRecord(ir1);
        
        String[] fieldArr2 = { "102", "1234", "45" };
        IndexDataResultRecord ir2 = new IndexDataResultRecord(fieldArr2, "second_trade_id");
        result.addRecord(ir2);
        
        String[] fieldArr3 = { "103", "1234", "45" };
        IndexDataResultRecord ir3 = new IndexDataResultRecord(fieldArr3, "second_trade_id");
        result.addRecord(ir3);
        
        String[] fieldArr4 = { "104", "1234", "45" };
        IndexDataResultRecord ir4 = new IndexDataResultRecord(fieldArr4, "second_trade_id");
        result.addRecord(ir4);
        
        String[] fieldArr5 = { "105", "1234", "45" };
        IndexDataResultRecord ir5 = new IndexDataResultRecord(fieldArr5, "second_trade_id");
        result.addRecord(ir5);
        
        String[] fieldArr6 = { "106", "1234", "45" };
        IndexDataResultRecord ir6 = new IndexDataResultRecord(fieldArr6, "second_trade_id");
        result.addRecord(ir6);
        
        String[] fieldArr7 = { "107", "1234", "45" };
        IndexDataResultRecord ir7 = new IndexDataResultRecord(fieldArr7, "second_trade_id");
        result.addRecord(ir7);
        
        String[] fieldArr8 = { "108", "1234", "45" };
        IndexDataResultRecord ir8 = new IndexDataResultRecord(fieldArr8, "second_trade_id");
        result.addRecord(ir8);
        
        String[] fieldArr9 = { "109", "1234", "45" };
        IndexDataResultRecord ir9 = new IndexDataResultRecord(fieldArr9, "second_trade_id");
        result.addRecord(ir9);
        
        String[] fieldArr10 = { "110", "1234", "45" };
        IndexDataResultRecord ir10 = new IndexDataResultRecord(fieldArr10, "second_trade_id");
        result.addRecord(ir10);
        
        return result;
    }
    
    /**
     * mockNode
     * 
     * @return Node
     */
    protected Node mockNode() {
        Node node = new Node("127.0.0.1", 9988, DEFAULT_CLUSTER_NAME);
        return node;
    }
    
    /**
     * mockAssignIndexShard
     * 
     * @param idxMeta
     * @return IndexMeta
     */
    protected IndexMeta mockAssignIndexShard(IndexMeta idxMeta) {
        IndexMeta indexMeta = idxMeta;
        if (indexMeta.getIdxShardList() == null || indexMeta.getIdxShardList().size() == 0) {
            Map<Node, Integer> assignedNodeMap = new HashMap<Node, Integer>();
            assignedNodeMap.put(this.mockNode(), 1);
            
            // 拼indexShard的分片名前缀：indexMetaId+"_shard"+shardId
            StringBuffer sb = new StringBuffer();
            sb.append(indexMeta.getIndexMetaId());
            sb.append("_");
            sb.append("shard");
            sb.append("_");
            
            // 获取 shardId，shardId是以事实表为基础进行分配，有可能同一事实表的多个Indexmeta中的索引分片的ID是连续的
            Long shardId = 0L;
            
            List<IndexShard> assignIndexShardList = new ArrayList<IndexShard>();
            for (Node node : assignedNodeMap.keySet()) {
                int currNodeShardNum = assignedNodeMap.get(node);
                for (int i = 0; i < currNodeShardNum; i++) {
                    IndexShard idxShard = new IndexShard(sb.toString() + shardId.toString(), node);
                    idxShard.setShardId(shardId);
                    idxShard.setDiskSize(0);
                    idxShard.setFull(false);
                    idxShard.setClusterName(DEFAULT_CLUSTER_NAME);
                    // 设置索引文件路径=datasourceinfo.getKey+"/"+facttablename+shardname
                    // 只设置一次
                    String idxFilePathPrefix = indexMeta.getIndexMetaFileDirPath();
                    idxShard.setFilePath(idxFilePathPrefix + idxShard.getShardName()
                        + File.separator + IndexMeta.getIndexFilePathUpdate());
                    idxShard.setIdxFilePath(idxFilePathPrefix + idxShard.getShardName()
                        + File.separator + IndexMeta.getIndexFilePathIndex());
                    
                    // 设置分片维度名称
                    idxShard.setShardDimBase(indexMeta.getShardDimBase());
                    
                    assignIndexShardList.add(idxShard);
                    
                    shardId = shardId + 1;
                }
            }
            
            indexMeta.getIdxShardList().addAll(assignIndexShardList);
            
        }
        // 第一次申请索引分片时获取集群名称
        if (StringUtils.isEmpty(indexMeta.getClusterName())) {
            indexMeta.setClusterName(DEFAULT_CLUSTER_NAME);
        }
        return indexMeta;
    }
    
    /**
     * mockIndexMessage
     * 
     * @param idxShard
     * @param data
     * @param node
     * @return IndexMessage
     */
    @SuppressWarnings("rawtypes")
    protected IndexMessage mockIndexMessage(IndexShard idxShard, TesseractResultSet data, Node node) {
        MessageHeader messageHeader = new MessageHeader(NettyAction.NETTY_ACTION_INDEX,
            data.toString());
        IndexMessage message = new IndexMessage(messageHeader, data);
        message.setIdxPath(idxShard.getAbsoluteFilePath(node.getIndexBaseDir()));
        message.setIdxServicePath(idxShard.getAbsoluteIdxFilePath(node.getIndexBaseDir()));
        message.setBlockSize(20);
        message.setIdName("id");
        message.setLastPiece(true);
        
        message.setIdxShardState(IndexShardState.INDEXSHARD_INDEXED);
        message.setMaxId(new BigDecimal(10));
        
        return message;
    }
    
    /**
     * mockSqlDataSourceWrap
     * 
     * @return SqlDataSourceWrap
     */
    protected SqlDataSourceWrap mockSqlDataSourceWrap() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        SqlDataSourceWrap result = new SqlDataSourceWrap(dataSource);
        return result;
    }
}
