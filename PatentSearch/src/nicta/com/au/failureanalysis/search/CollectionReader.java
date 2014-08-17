package nicta.com.au.failureanalysis.search;

import java.io.File;
import java.io.IOException;

import nicta.com.au.patent.document.PatentDocument;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

/**
 * @author mona
 * 
 */
public class CollectionReader {

	private final IndexReader ir;
//	private final int topK;

	public CollectionReader(String indexDir/*, String similarity, int topK*/)
			throws IOException {
		ir = DirectoryReader.open(FSDirectory.open(new File(indexDir)));
		// ir = IndexReader.open(dir);
//		this.topK = topK;
	}

	/**
	 * @param field
	 * @param term
	 * @throws IOException
	 */
	public void termFreqInDocs(String field, String term) throws IOException {

		DocsEnum de = MultiFields.getTermDocsEnum(ir, MultiFields.getLiveDocs(ir), field, new BytesRef(term));

		int num = 0;
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
		ir.close();

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
//			num++;
			
			if(ir.document(de.docID()).get(PatentDocument.FileName).contains(filename)){
				// .substring(3).equals(filename)
				termfreq = de.freq();
				return termfreq;
				
			}
			// if(de.freq() >= 4){
			/*System.out.println("("
					+ num
					+ ") "
					+ ir.document(de.docID()).get(PatentDocument.FileName)
							.substring(3) + "  " + de.freq() + "  "
					+ de.docID());*/
			// }

		}
		}
//		ir.close();
		return 0;
		

	}


	public static void main(String[] args) {
//		String indexDir = args[0];
		String indexDir =  "data/INDEX/indexWithoutSW-Vec-CLEF-IP2010";

		try {
			String term = /*"methyl" */"resin"/*"excel"*/ /*"mixtur"*/ /*"mona"*//*"adhesiveport"*/;
			String field = /* PatentDocument.Classification */PatentDocument.Description;
			String filename = "EP-0415270";

			CollectionReader reader = new CollectionReader(indexDir/*, "bm25ro",
					10*/);			
			
			int a = reader.getTFreq(field, term, filename);
			System.out.println(a);

			
			//reader.termFreqInDocs(field, term);
			
			

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
