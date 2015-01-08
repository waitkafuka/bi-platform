package com.baidu.rigel.biplatform.tesseract.isservice.search.collector;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;

public class TesseractSimpleCollector extends Collector {
	
	private BinaryDocValues docValues; 
	private String fieldName;
	
	public TesseractSimpleCollector(String fieldName){
		this.fieldName=fieldName;
	}

	@Override
	public void setScorer(Scorer scorer) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void collect(int doc) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNextReader(AtomicReaderContext context) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean acceptsDocsOutOfOrder() {
		// TODO Auto-generated method stub
		return false;
	}

}
