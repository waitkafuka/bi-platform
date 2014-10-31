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
package com.baidu.rigel.biplatform.tesseract.isservice.search.collector;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.util.BytesRef;

import com.baidu.rigel.biplatform.tesseract.resultset.isservice.Meta;
import com.baidu.rigel.biplatform.tesseract.resultset.isservice.ResultRecord;

/**
 * Collector，用于在检索时按doc收集数据
 * 
 * @author lijin
 *
 */
public class TesseractResultRecordCollector extends Collector {
    
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
     * meta
     */
    private Meta meta;
    /**
     * currBinaryDocValuesMap
     */
    private Map<String, BinaryDocValues> currBinaryDocValuesMap;
    /**
     * currDoubleValuesMap
     */
    private Map<String, FieldCache.Doubles> currDoubleValuesMap;
    /**
     * result
     */
    private List<ResultRecord> result;
    
    /**
     * 
     * Constructor by 
     * @param dimFields dimFields
     * @param measureFields measureFields
     */
    public TesseractResultRecordCollector(String[] dimFields, String[] measureFields) {
        this.dimFields = dimFields;
        this.measureFields = measureFields;
        this.result = new ArrayList<ResultRecord>();
        
        this.currBinaryDocValuesMap = new HashMap<String, BinaryDocValues>();
        this.currDoubleValuesMap = new HashMap<String, FieldCache.Doubles>();
        List<String> fieldNameList = new ArrayList<String>();
        fieldNameList.addAll(Arrays.asList(measureFields));
        fieldNameList.addAll(Arrays.asList(dimFields));
        this.meta = new Meta(fieldNameList.toArray(new String[0]));
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.lucene.search.Collector#acceptsDocsOutOfOrder()
     */
    @Override
    public boolean acceptsDocsOutOfOrder() {
        // TODO Auto-generated method stub
        return false;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.lucene.search.Collector#collect(int)
     */
    @Override
    public void collect(int doc) throws IOException {
        List<Serializable> fieldValueList = new ArrayList<Serializable>();
        // List<String> fieldNameList=new ArrayList<String>();
        
        for (String measure : this.measureFields) {
            FieldCache.Doubles fieldValues = currDoubleValuesMap.get(measure);
            fieldValueList.add(fieldValues.get(doc));
        }
        
        for (String dim : dimFields) {
            BinaryDocValues fieldValues = currBinaryDocValuesMap.get(dim);
            BytesRef byteRef = fieldValues.get(doc);
            fieldValueList.add(byteRef.utf8ToString());
        }
        
        ResultRecord record = new ResultRecord(fieldValueList.toArray(new Serializable[0]),
                this.meta);
        this.result.add(record);
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.lucene.search.Collector#setNextReader(org.apache.lucene.index
     * .AtomicReaderContext)
     */
    @Override
    public void setNextReader(AtomicReaderContext context) throws IOException {
        this.reader = context.reader();
        for (String measure : measureFields) {
            currDoubleValuesMap.put(measure,
                    FieldCache.DEFAULT.getDoubles(this.reader, measure, false));
        }
        for (String dim : dimFields) {
            currBinaryDocValuesMap.put(dim, FieldCache.DEFAULT.getTerms(this.reader, dim, false));
        }
        
    }
    
    @Override
    public void setScorer(Scorer arg0) throws IOException {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * getter method for property result
     * 
     * @return the result
     */
    public List<ResultRecord> getResult() {
        return result;
    }
    
}
