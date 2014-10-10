package nicta.com.au.failureanalysis.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import nicta.com.au.main.Functions;
import nicta.com.au.patent.document.PatentDocument;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

/**
 * @author mona
 * 
 */
public class CollectionReader {

	private static IndexReader ir;
	static String indexDir = "data/DocPLusQueryINDEX" /*"data/INDEX/indexWithoutSW-Vec-CLEF-IP2010"*/;
	//	private final int topK;

	public CollectionReader(String indexDir/*, String similarity, int topK*/)
			throws IOException {
		ir = DirectoryReader.open(FSDirectory.open(new File(indexDir)));
	}
	
	 public IndexReader getIndexReader() {
	        return ir;
	    }

	/**
	 * @param field
	 * @param term
	 * @throws IOException
	 */
	public void termFreqInDocs(String field, String term) throws IOException {

		DocsEnum de = MultiFields.getTermDocsEnum(ir, MultiFields.getLiveDocs(ir), field, new BytesRef(term));

		int num = 0;
		System.out.println(de);
		while ((de.nextDoc()) != DocsEnum.NO_MORE_DOCS) {
			num++;
			// if(de.freq() >= 4){
			System.out.println("("
					+ num
					+ ") "
					+ ir.document(de.docID()).get(PatentDocument.FileName)
					.substring(3) + "  " + de.freq() + "  "
					+ de.docID());
			// }

		}
		//		ir.close();

	}

	/**
	 * @param field
	 * @param term
	 * @throws IOException
	 */
	public int getTFreq(String field, String term, String filename) throws IOException {

		int termfreq = 0;

		DocsEnum de = MultiFields.getTermDocsEnum(ir, MultiFields.getLiveDocs(ir), field, new BytesRef(term));


		//		int num = 0;
		if (de != null){
			while ((de.nextDoc()) != DocsEnum.NO_MORE_DOCS) {
				//							num++;

				if(ir.document(de.docID()).get(PatentDocument.FileName).contains(filename)){
					// .substring(3).equals(filename)
					termfreq = de.freq();
					return termfreq;

				}
			}
		}
		//		ir.close();
		return 0;		

	}

	/**
	 * @param docName: Name of patent files starts with UN
	 * @param field:  PatentDocument.FileName
	 * @return docId assigned by Lucene 
	 * @throws IOException
	 */
	public int getDocId(String docName, String field) throws IOException {
		DocsEnum de = MultiFields.getTermDocsEnum(ir, MultiFields.getLiveDocs(ir), field, new BytesRef(docName));

		while ((de.nextDoc()) != DocsEnum.NO_MORE_DOCS) {
			int docid = de.docID();
			return docid;
		}

		//		ir.close();
		return 0;	
	}

	public HashSet<String> getDocTerms(String docName, String field) throws IOException {

		HashSet<String> termsindoc = new HashSet<>();

		String filenamefield =  PatentDocument.FileName;
		CollectionReader reader = new CollectionReader(indexDir);

		int b = reader.getDocId(docName, filenamefield);
//				System.out.println("document Id: " + b);
		Terms terms = ir.getTermVector(b, field)/* getTermVectors(b)*/;
		//		System.out.println(terms);

		if(terms == null){return null;}
		TermsEnum termsEnum = terms.iterator(null);

		BytesRef text;
		while((text = termsEnum.next()) != null) {
			String k = text.utf8ToString();
			if (!Functions.isNumeric(k)) {
				termsindoc.add(k);
				//		      System.out.println(k);
			}
		}		

		return termsindoc;
	}
	

	public HashMap<String, Integer> gettermfreqpair( String docName, String field) throws IOException {
		String filenamefield =  PatentDocument.FileName;
		HashMap<String, Integer> termfreqs = new HashMap<String, Integer>();
		CollectionReader reader = new CollectionReader(indexDir);

		int docid = reader.getDocId(docName, filenamefield);
		Terms terms = ir.getTermVector(docid, field); //get terms vectors for one document and one field
		if (terms != null && terms.size() > 0) {
		    TermsEnum termsEnum = terms.iterator(null); // access the terms for this field
		    BytesRef text = null;
		    while ((text = termsEnum.next()) != null) {// explore the terms for this field
		        
		    	String term = text.utf8ToString();
		        int freq = (int) termsEnum.totalTermFreq();
		        if (!Functions.isNumeric(term)) {
		        termfreqs.put(term, freq);
		        System.out.println(term +"\t"+freq);}
		    }
		}
		return termfreqs;		
	}

	public static void main(String[] args) throws IOException {

		//		String indexDir =  "data/INDEX/indexWithoutSW-Vec-CLEF-IP2010";

		String docName = /*"UN-EP-0415270"*/"UN-EP-0802230" /*"UN-EP-0663270"*/;
		String filenamefield =  PatentDocument.FileName;

		String term = /*"methyl" */"resin"/*"excel"*/ /*"mixtur"*/ /*"mona"*//*"adhesiveport"*/;
		String field = /* PatentDocument.Classification */PatentDocument.Description/*PatentDocument.Title*/;
		String filename = "EP-0663270"/*"EP-0388383"*//*"EP-0415270"*/;

		CollectionReader reader = new CollectionReader(indexDir);			

		/*int a = reader.getTFreq(field, term, filename);
			System.out.println(a);*/

//					reader.termFreqInDocs(field, term);
			//			System.out.println(reader.getDocTerms(docName, field));
		if(reader.getDocTerms("UN-"+filename, field)!=null){
			HashSet<String> terms = reader.getDocTerms("UN-"+filename, field);
			System.out.println(terms);
			boolean bool = terms.contains("mona"/*"work"*/);
			System.out.println("Word exists: " + bool);
			/*for(String t : terms){
				System.out.println(t);
			}*/
		}else{System.out.println("this file does not exist!");}	
		
		reader.gettermfreqpair(docName, field);
		
	}
}