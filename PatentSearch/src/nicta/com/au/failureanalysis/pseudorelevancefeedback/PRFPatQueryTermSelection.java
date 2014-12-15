package nicta.com.au.failureanalysis.pseudorelevancefeedback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import nicta.com.au.failureanalysis.GeneralExecuteTopic.GeneralParseQuery;
import nicta.com.au.failureanalysis.query.QueryGneration;
import nicta.com.au.failureanalysis.search.CollectionReader;
import nicta.com.au.main.Functions;
import nicta.com.au.patent.document.PatentDocument;
import nicta.com.au.patent.pac.evaluation.TopicsInMemory;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;

public class PRFPatQueryTermSelection {
	static String indexDir = /*"data/INDEX/indexWithoutSW-Vec-CLEF-IP2010"*/"data/DocPLusQueryINDEX"/*"data/QINDEX"*/;

	public String GeneratePRFtPatentQuery(String queryid, String qUcid, float tau) throws IOException, ParseException{
		CollectionReader reader = new CollectionReader(indexDir); 
//		PositiveTermsOverlap olap = new PositiveTermsOverlap();
		PRFTermsScores prf = new PRFTermsScores();
		

		/*--------------------------------- Query Words -------------------------------*/
		//			HashMap<String, Integer> query_termsfreqspair = reader.gettermfreqpair(qUcid, PatentDocument.Description);
		HashMap<String, Integer> query_terms = reader.gettermfreqpairAllsecs(qUcid); /*reader.getDocTerms(, PatentDocument.Description);*/
		int querysize = query_terms.size();
		//			System.out.println(query_termsfreqspair.size() +"\t"+ query_termsfreqspair);
//					System.out.println(queryid + " (" + querysize + ") " + query_terms);
		/*-----------------------------------------------------------------------------*/			

//		TreeMap<String, Float> RFtermsscores = olap.getTermsScoresPair(queryid);
		Map<String, Float> PRFtermsscores = prf.getTermsScoresPairPRF(queryid);
		
		int size = 0;
		String optimal_query = "";
		for(Entry<String, Float> tspair : PRFtermsscores.entrySet()){
			String term = tspair.getKey();
			Float score = tspair.getValue();

			/*------------------- Select query terms from useful RF terms ----------------------*/
			
			if(score > tau){
				if(query_terms.keySet().contains(term)){
					if (!Functions.isNumeric(term) && !Functions.isSpecialCahr(term)) {
						size++;
						optimal_query += term + "^" + query_terms.get(term) + " ";
//						optimal_query += term + "^" + 1 + " ";
					}	
//											System.out.println(term);
				}					
			}			
		}	
		/*System.out.println();
		System.out.println(queryid + " (" + size + ") " + optimal_query);
		System.out.println();*/
		return optimal_query;
	}

	public static void main(String[] args) throws IOException, ParseException {
		String path = "data/CLEF-IP-2010/PAC_test/topics/";

		int tau = Integer.parseInt(args[0]);
		PRFPatQueryTermSelection PRFts = new PRFPatQueryTermSelection();
//		CreateOptimalPatentQuery optpatentq = new CreateOptimalPatentQuery();
		GeneralParseQuery pq = new GeneralParseQuery();
	
		TopicsInMemory topics = new TopicsInMemory("data/CLEF-IP-2010/PAC_test/topics/PAC_topics-filtered.xml");  /*PAC_topics-omit-PAC-1094.xml");*/
		for(Map.Entry<String, PatentDocument> topic : topics.getTopics().entrySet()){
			String qUcid = topic.getValue().getUcid();
			String queryid = topic.getKey();
			String queryName = topic.getKey() + "_" + topic.getValue().getUcid();
			String queryfile = topic.getKey() + "_" + topic.getValue().getUcid() + ".xml";
			
			QueryGneration g = new QueryGneration(path + queryfile, 0, 0, 1, 0, 0, 0, true, true);
			String ipcfilter = g.getIpc();
			
			String patentquery = PRFts.GeneratePRFtPatentQuery(queryid, qUcid, tau);  /*optpatentq.GenerateOptPatentQuery(queryid, qUcid, tau)*/;
//			System.out.println(queryid + " (" + patentquery + ") ");
			Query q = pq.parse(patentquery, ipcfilter);
			System.out.println(queryid + " (" + q + ") ");
		}
	}

}
