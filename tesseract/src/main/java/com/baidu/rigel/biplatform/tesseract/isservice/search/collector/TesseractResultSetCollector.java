/**
 * 
 */
package com.baidu.rigel.biplatform.tesseract.isservice.search.collector;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.util.BytesRef;

import com.baidu.rigel.biplatform.tesseract.resultset.isservice.Meta;
import com.baidu.rigel.biplatform.tesseract.resultset.isservice.SearchIndexResultRecord;
import com.baidu.rigel.biplatform.tesseract.resultset.isservice.SearchIndexResultSet;

/**
 * @author lijin
 *
 */
public class TesseractResultSetCollector extends Collector {
    
    /**
     * 后续考虑优化线程池配置管理
     */
    private static final ExecutorService service = Executors.newFixedThreadPool (1);
    
    /**
     * dimFields
     */
    private String[] dimFields;
    /**
     * measureFields
     */
    private String[] measureFields;    
    
    /**
     * reader
     */
    private AtomicReader reader;
    
    /**
     * cacheBinaryDocValuesMap
     */
    private Map<Integer,Map<String, BinaryDocValues>> cacheBinaryDocValuesMap;
    /**
     * cacheDoubleValuesMap
     */
    private Map<Integer,Map<String, FieldCache.Doubles>> cacheDoubleValuesMap;
    
    
    
    /**
     * resultDocBaseDocIdMap
     */
    private Map<Integer, List<Integer>> resultDocBaseDocIdMap;
    
    private int size;
    
    /**
     * docBase
     */
    private int docBase;
    
    
    /** 
     * 获取 size 
     * @return the size 
     */
    public int getSize() {
    
        return size;
    }


    /** 
     * 设置 size 
     * @param size the size to set 
     */
    public void setSize(int size) {
    
        this.size = size;
    }
    
    /**
     * 
     * Constructor by 
     * @param dimFields dimFields
     * @param measureFields measureFields
     */
    public TesseractResultSetCollector(String[] dimFields, String[] measureFields) {
        this.dimFields = dimFields;
        this.measureFields = measureFields;
        
        this.cacheBinaryDocValuesMap = new HashMap<Integer,Map<String, BinaryDocValues>>();
        this.cacheDoubleValuesMap = new HashMap<Integer,Map<String, FieldCache.Doubles>>();
        
        this.resultDocBaseDocIdMap = new HashMap<Integer,List<Integer>> ();
        this.size=0;
        
    }
    

    /* (non-Javadoc)
     * @see org.apache.lucene.search.Collector#setScorer(org.apache.lucene.search.Scorer)
     */
    @Override
    public void setScorer(Scorer scorer) throws IOException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.lucene.search.Collector#collect(int)
     */
    @Override
    public void collect(int doc) throws IOException {
        this.size++;
        List<Integer> idList = this.resultDocBaseDocIdMap.get(this.docBase);
        if (idList == null) {
            idList = new ArrayList<Integer>();
        }
        idList.add(doc);
        this.resultDocBaseDocIdMap.put(this.docBase, idList);

    }

    /* (non-Javadoc)
     * @see org.apache.lucene.search.Collector#setNextReader(org.apache.lucene.index.AtomicReaderContext)
     */
    @Override
    public void setNextReader(AtomicReaderContext context) throws IOException {
        this.docBase = context.docBase;
        this.reader = context.reader();
        Map<String, FieldCache.Doubles> currDoubleValuesMap=new HashMap<String, FieldCache.Doubles>();        
        for (String measure : measureFields) {
            currDoubleValuesMap.put(measure,
                    FieldCache.DEFAULT.getDoubles(this.reader, measure, false));
        }
        this.cacheDoubleValuesMap.put(this.docBase, currDoubleValuesMap);
        
        Map<String, BinaryDocValues> currBinaryDocValuesMap=new HashMap<String, BinaryDocValues>();
        for (String dim : dimFields) {
            currBinaryDocValuesMap.put(dim, FieldCache.DEFAULT.getTerms(this.reader, dim, false));
        }
        
        this.cacheBinaryDocValuesMap.put(this.docBase, currBinaryDocValuesMap);

    }

    /* (non-Javadoc)
     * @see org.apache.lucene.search.Collector#acceptsDocsOutOfOrder()
     */
    @Override
    public boolean acceptsDocsOutOfOrder() {
        // TODO Auto-generated method stub
        return false;
    }
    
    public SearchIndexResultSet buildResultSet (Set<String> groupByFields) {
//        Meta meta = new Meta(mergeDimAndMeasure());
        Meta meta = new Meta((String[]) ArrayUtils.addAll(this.dimFields, this.measureFields));
        SearchIndexResultSet result = new SearchIndexResultSet(meta, this.size);
        if (this.size == 0) {
            return result;
        }
        
//        Map<String, FieldCache.Doubles> currDoubleValuesMap = null; //new HashMap<String, FieldCache.Doubles>();
//        Map<String, BinaryDocValues> currBinaryDocValuesMap = null; //new HashMap<String, BinaryDocValues>();
        long begin = System.currentTimeMillis ();
        this.resultDocBaseDocIdMap.keySet().parallelStream ().forEach (docBase -> {
           Map<String, FieldCache.Doubles> currDoubleValuesMap = this.cacheDoubleValuesMap.get(docBase);
           Map<String, BinaryDocValues> currBinaryDocValuesMap = this.cacheBinaryDocValuesMap.get(docBase);
          for (Integer docId : this.resultDocBaseDocIdMap.get(docBase)) {
              Serializable[] fieldValueArray = new Serializable[this.dimFields.length
                      + this.measureFields.length];
              String groupBy = "";
              int i = 0;
              for (String dim : dimFields) {
                  BinaryDocValues fieldValues = currBinaryDocValuesMap
                          .get(dim);
                  BytesRef byteRef = fieldValues.get(docId);
                  String dimVal = byteRef.utf8ToString();
                  fieldValueArray[i++] = dimVal;

                  if (groupByFields.contains(dim)) {
                      groupBy += dimVal + ",";
                  }
              }

              for (String measure : this.measureFields) {
                  FieldCache.Doubles fieldValues = currDoubleValuesMap
                          .get(measure);
                  fieldValueArray[i++] = fieldValues.get(docId);
              }

              SearchIndexResultRecord record = new SearchIndexResultRecord(fieldValueArray, groupBy);
              record.setGroupBy(groupBy);
              result.addRecord(record);
          }
//            final Integer[] docIds = this.resultDocBaseDocIdMap.get(docBase).toArray (new Integer[0]);
//            int gropByNumSize = docIds.length + 1;
//            @SuppressWarnings("unchecked")
//            Future<SearchIndexResultRecord[]>[] resultRecordList = new Future[docIds.length / gropByNumSize + 1];
//            for (int i = 0, j = 0; i < docIds.length; ) {
//                int endIndex = i + gropByNumSize;
//                if (endIndex > docIds.length) {
//                    endIndex = docIds.length;
//                }
//                resultRecordList[j++] = 
//                        service.submit (new ResultRecordBuildTask (i, endIndex, docIds, docBase, groupByFields));
//                i += gropByNumSize;
//            }
//            
//            for (Future<SearchIndexResultRecord[]> f : resultRecordList) {
//                try {
//                    result.addAll (f.get ());
//                } catch (Exception e) {
//                     throw new RuntimeException ("查询请求超时");
//                }
//            }
        });
//        for(Integer currDocBase : this.resultDocBaseDocIdMap.keySet()){
//            currDoubleValuesMap = this.cacheDoubleValuesMap.get(currDocBase);
//            currBinaryDocValuesMap = this.cacheBinaryDocValuesMap.get(currDocBase);
//            for (Integer docId : this.resultDocBaseDocIdMap.get(currDocBase)) {
//                Serializable[] fieldValueArray = new Serializable[this.dimFields.length
//                        + this.measureFields.length];
//                String groupBy = "";
//                int i = 0;
//                for (String dim : dimFields) {
//                    BinaryDocValues fieldValues = currBinaryDocValuesMap
//                            .get(dim);
//                    BytesRef byteRef = fieldValues.get(docId);
//                    String dimVal = byteRef.utf8ToString();
//                    fieldValueArray[i++] = dimVal;
//
//                    if (groupByFields.contains(dim)) {
//                        groupBy += dimVal + ",";
//                    }
//                }
//
//                for (String measure : this.measureFields) {
//                    FieldCache.Doubles fieldValues = currDoubleValuesMap
//                            .get(measure);
//                    fieldValueArray[i++] = fieldValues.get(docId);
//                }
//
//                SearchIndexResultRecord record = new SearchIndexResultRecord(fieldValueArray, groupBy);
//                record.setGroupBy(groupBy);
//                result.addRecord(record);
//            }
            
//        }
        System.out.println ("===================" + (System.currentTimeMillis () - begin));
        return result;
    }

    private class ResultRecordBuildTask implements Callable<SearchIndexResultRecord[]> {

        /**
         * 
         */
        private final int beginIndex;
        
        /**
         * 
         */
        private final int endIndex;
        
        
        /**
         * 
         */
        private final Integer[] idList;
        
        /**
         * 
         */
        private final Integer docBase;
        
        private final Set<String> groupByFields;
        
        /**
         * 
         * @param beginIndex
         * @param endIndex
         * @param idList
         */
        public ResultRecordBuildTask ( final int beginIndex, final int endIndex,
                final Integer[] idList, final Integer docBase, final Set<String> groupByFields) {
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.idList = idList;
            this.docBase = docBase;
            this.groupByFields = groupByFields;
        }
        
        /**
         * 根据指定id列表、起至编号大小构建查询结果单元
         * 注意：该方法不会检验数组越界、起至索引大小不对等数据安全问题，需要由上游任务调度业务关注
         * @return 返回结果为：[beginIndex, endIndex) 半闭区间个数个结果
         */
        @Override
        public SearchIndexResultRecord[] call() throws Exception {
            Map<String, FieldCache.Doubles> currDoubleValuesMap = cacheDoubleValuesMap.get(this.docBase);
            Map<String, BinaryDocValues> currBinaryDocValuesMap = cacheBinaryDocValuesMap.get(this.docBase);
            SearchIndexResultRecord[] rs = new SearchIndexResultRecord[endIndex - beginIndex];
            for (int index = beginIndex, i = 0; index < endIndex; ++index) {
                Serializable[] fieldValueArray = new Serializable[dimFields.length + measureFields.length];
                  String groupBy = "";
                  int fieldValueArrayIndex = 0;
                  for (String dim : dimFields) {
                      BinaryDocValues fieldValues = currBinaryDocValuesMap.get(dim);
                      BytesRef byteRef = fieldValues.get(this.idList[index]);
                      String dimVal = byteRef.utf8ToString();
                      fieldValueArray[fieldValueArrayIndex++] = dimVal;
                      if (groupByFields.contains(dim)) {
                          groupBy += dimVal + ",";
                      }
                  }

                  for (String measure : measureFields) {
                      FieldCache.Doubles fieldValues = currDoubleValuesMap.get(measure);
                      fieldValueArray[fieldValueArrayIndex++] = fieldValues.get(this.idList[index]);
                  }

                  SearchIndexResultRecord record = new SearchIndexResultRecord(fieldValueArray, groupBy);
                  record.setGroupBy(groupBy);
                  rs [i++] = record;
            }
            return rs;
        }
        
    }

//    /**
//     * 合并维度、指标定义
//     * 注意：此处假设dimFields measureFields不会变化，如变化，此方法会有问题
//     * @return String[] 
//     */
//    private String[] mergeDimAndMeasure() {
//        if (this.dimFields == null) {
//            return this.measureFields;
//        }
//        if (this.measureFields == null) {
//            return this.dimFields;
//        }
//        int dimFieldLength = this.dimFields.length;
//        String[] rs = new String[dimFieldLength + this.measureFields.length];
//        System.arraycopy (this.dimFields, 0, rs, 0, dimFieldLength);
//        System.arraycopy (this.measureFields, 0, rs, dimFieldLength, this.measureFields.length);
//        return rs;
//    }

}
