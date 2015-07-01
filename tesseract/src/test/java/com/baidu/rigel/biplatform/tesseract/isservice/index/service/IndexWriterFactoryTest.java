package com.baidu.rigel.biplatform.tesseract.isservice.index.service;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexWriterFactoryTest {
    private static final Logger logger = LoggerFactory.getLogger(IndexWriterFactoryTest.class);
    
    @Before
    public void initMocks() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testGetIndexWriter() throws IOException {
        String filePath = "./testcase/";
        IndexWriter idxWriter1 = IndexWriterFactory.getIndexWriter(filePath);
        Assert.assertNotNull(idxWriter1);
        logger.info("Now get an existed indexWriter");
        IndexWriter idxWriter2 = IndexWriterFactory.getIndexWriter(filePath);
        Assert.assertEquals(idxWriter1, idxWriter2);
        
    }
    
    @Test
    public void testDestoryWriters() throws IOException {
        String filePath = "./testcase/";
        // filePath没有对应的indexWriter
        IndexWriterFactory.destoryWriters(filePath);
        
        // filePath已有对应的indexWriter
        IndexWriter idxWriter1 = IndexWriterFactory.getIndexWriter(filePath);
        IndexWriterFactory.destoryWriters(filePath);
        
        idxWriter1 = IndexWriterFactory.getIndexWriter(filePath);
        Document doc = new Document();
        idxWriter1.addDocument(doc);
        
        IndexWriterFactory.destoryWriters(filePath);
        
    }
}
