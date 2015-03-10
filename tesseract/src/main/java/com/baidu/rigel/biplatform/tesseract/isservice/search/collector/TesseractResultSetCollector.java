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
        
        this.resultDocBaseDocIdMap=new HashMap<Integer,List<Integer>> ();
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
	
	public SearchIndexResultSet buildResultSet(Set<String> groupByFields){		
		Meta meta = new Meta((String[]) ArrayUtils.addAll(this.dimFields,
				this.measureFields));
		SearchIndexResultSet result = new SearchIndexResultSet(meta, this.size);
		if (this.size == 0) {
			return result;
		}
		
		Map<String, FieldCache.Doubles> currDoubleValuesMap=new HashMap<String, FieldCache.Doubles>();
		Map<String, BinaryDocValues> currBinaryDocValuesMap=new HashMap<String, BinaryDocValues>();
		
		for(Integer currDocBase:this.resultDocBaseDocIdMap.keySet()){
			currDoubleValuesMap=this.cacheDoubleValuesMap.get(currDocBase);
			currBinaryDocValuesMap=this.cacheBinaryDocValuesMap.get(currDocBase);
			
			for (Integer docId : this.resultDocBaseDocIdMap.get(currDocBase)) {
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

				SearchIndexResultRecord record = new SearchIndexResultRecord(fieldValueArray,groupBy);
				record.setGroupBy(groupBy);
				result.addRecord(record);
			}
			
		}
		
		return result;
	}


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

}
