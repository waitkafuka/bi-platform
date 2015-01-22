/**
 * 
 */
package com.baidu.com.rigel.demo.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;


/**
 * @author lijin
 *
 */
public class SearchDemo {
	//private String dirName="D:/tesseract_data/indexbase/index_1/85d7fb751cc0a07fdda16a38b3baa68f/data_fc_bd_qs_day_psum/";
	//private String dirName="D:/tesseract_data/indexbase/index_1/78380567fae7428d66af0cfbb4b69ee9/report_matrix_bill/";
	private String dirName="D:/indexbase/index_1/ce7f8a2c05d7d19202903a7d63957995/data_fc_bd_qs_day_psum/";
	private IndexSearcher is; 
	
	public SearchDemo() throws ClassNotFoundException, IOException{
		this.initIndexSearcher();
	}
	
	public void search() {
		try {
			Query query=new TermQuery(new Term("the_date", "20131112"));
			
			//TopDocs result=is.search(new MatchAllDocsQuery(), 1000);
			TopDocs result=is.search(query, 1000);
			System.out.println("RESULT SET SIZE:"+result.totalHits +" Query is "+query);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException  {
		SearchDemo sd=new SearchDemo();
		sd.search();
    }
	
	private void initIndexSearcher()throws IOException, ClassNotFoundException{
        if(is==null){
            MultiReader reader=new MultiReader(this.getSubIndexReader().toArray(new IndexReader[0]));
            ExecutorService pool= Executors.newCachedThreadPool();
            is=new IndexSearcher(reader,pool); 
        }       
    }
	
	private List<IndexReader> getSubIndexReader() throws IOException{
        List<IndexReader> result=new ArrayList<IndexReader>();
        File dir=new File(this.dirName);
        if(dir.isDirectory()){
            File[] indexFiles=dir.listFiles();
            for(File file:indexFiles){
                Directory d1=FSDirectory.open(file);
                RAMDirectory map=new RAMDirectory(d1,IOContext.READ);
                IndexReader reader=DirectoryReader.open(map);
                result.add(reader);
            }
        }
        return result;
    }
	
	

}
