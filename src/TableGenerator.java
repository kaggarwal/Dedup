/*
 * Code that applies machine learning concepts to improve
 * bug deduplication accuracy in bug repositories.
 * Copyright (C) 2013  Anahita Alipour, Abram Hindle,
 * Tanner Rutgers, Riley Dawson, Finbarr Timbers, Karan Aggarwal
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import au.com.bytecode.opencsv.CSVReader;


public class TableGenerator {

	Map<String,Double> freeVariables = new HashMap<String, Double>();
	REP rep;
	BM25F uni_bmf;
	BM25F bi_bmf;
	Corpus corpus;
	boolean stemming;
	Preprocessing prep;

	public TableGenerator(boolean doStem, Map<String,Double> freeVars, Corpus inputCorpus){

		corpus = inputCorpus;
		stemming = doStem;
		rep = new REP(corpus, stemming);
		uni_bmf = rep.getBmf_unigram();
		bi_bmf = rep.getBmf_bigram();
		freeVariables = freeVars;
		prep = new Preprocessing();
	}

	//public TableGenerator(){}

	/**
	 * 
	 * Calculates the contextual features for all bug reports in the corpus
	 * @param contextFolderName
	 * @param corpus
	 * @return
	 */

	public Map<Integer,Double[]> initBugContextFeatures(String contextFolderName, Corpus corpus){

		Map<Integer,Double[]> result = new HashMap<Integer,Double[]>();
		Vector<Document> dictionaries = getContextDictionaries(contextFolderName);

		int counter = 0;
		for(Document doc: corpus.getDocuments()){

			//System.out.println("init:" +counter++);

			Double[] features = getContextFeatures(doc, dictionaries);
			result.put(doc.getBugID(), features);
		}

		return result;
	} 


	protected boolean isDuplicate(Document doc1, Document doc2){

		boolean result = (doc1.getMasterID() == 0 && doc2.getMasterID() == 0)?
				false:((doc1.getMasterID() == doc2.getMasterID() || doc1.getMasterID() == doc2.getBugID() || doc1.getBugID() == doc2.getMasterID()));

		return result;
	}


	private Double[] getContextFeatures(Document doc,Vector<Document> dictionaries){

		Double[] features = new Double[dictionaries.size()];

		for(int i=0;i<dictionaries.size();i++){
			features[i] = uni_bmf.calculateBM25F(doc, dictionaries.get(i), freeVariables);
		}

		return features;
	}



	private Vector<String> readDictionaryFile(String filePath, boolean stemming){

		Vector<String> result = new Vector<String>();
		try{
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream(filePath);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				// Print the content on the console
				result.add(stemming?Stemmer.stem(strLine):strLine);
			}
			//Close the input stream
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return result;
	}

	/** 

	 This method creates documents from each context word list.
	 And returns them as a Vector of Documents.

	 **/

	private Vector<Document> getContextDictionaries(String contextFolderName){

		Vector<Document> dictionaries = new Vector<Document>();
		final File folder = new File(contextFolderName);

		for (final File fileEntry : folder.listFiles()) {
			System.out.println(fileEntry.getName());
			//System.out.print(fileEntry.getName().split(".")[0]+",");
			if (!fileEntry.isDirectory()) {
				Vector<String> tmp = readDictionaryFile( contextFolderName+"/"+fileEntry.getName(), stemming );
				StringBuffer buff = new StringBuffer("");
				for (String word: tmp) {
					buff.append(word);
					buff.append(" ");
				}
				String out = buff.toString();
				Document d = new Document(out,out);

				updateCorpus(d);

				dictionaries.add( d );
			} 
		}

		dictionaries = prep.stringDocumentProcess(dictionaries, stemming);

		return dictionaries;
	}



	public double cosineSimilarity(Double[] vec1, Double[] vec2){

		double result = 0.0;
		int n = vec1.length;
		for(int i=0;i<n;i++)
			result += vec1[i]*vec2[i];

		result = result/(magnitude(vec1)*magnitude(vec2));
		result = Math.round(result*1000)/1000.0d;

		return result;
	}

	private double magnitude(Double[] vector){

		double magnitude = 0.0;
		for(int i=0;i<vector.length;i++)
			magnitude += (vector[i]*vector[i]);

		magnitude = Math.sqrt(magnitude);
		return magnitude;
	}


	public void updateCorpus(Document newDoc){

		uni_bmf.tfidf.updateDictionary(newDoc);

	}



	public static void main(String[] args) {

		/*String contextFile = "";

		if (args.length == 1){
			contextFile = args[0];

		}
		else{
			System.err.println("One input argument is needed.");
			System.exit(1);
		}*/

		HashMap<String,Double> freeVariables = new HashMap<String, Double>(){{
			put("w1",0.9);
			put("w2",0.2);
			put("w3",2.0);
			put("w4",0.0);
			put("w5",0.7);
			put("w6",0.0);
			put("w7",0.0);
			put("wsumm_uni",3.0);
			put("wdesc_uni",1.0);
			put("bsumm_uni",0.5);
			put("bdesc_uni",1.0);
			put("k1_uni",2.0);
			put("k3_uni",0.0);
			put("wsumm_bi",3.0);
			put("wdesc_bi",1.0);
			put("bsumm_bi",0.5);
			put("bdesc_bi",1.0);
			put("k1_bi",2.0);
			put("k3_bi",0.0);
		}};

		//Vector<String> cosine_files = new Vector<String>();
		//cosine_files.add("cosine_architecture_context.csv");
		//cosine_files.add("cosine_NFR_context.csv");
		//cosine_files.add("cosine_junk_context.csv");
		//cosine_files.add("cosine_lda_context.csv");
		//cosine_files.add("cosine_labeled_context.csv");
		//cosine_files.add("cosine_rep.csv");
		//cosine_files.add("lda_sums.csv");


		Preprocessing prep = new Preprocessing();
		AndroidXmlParser AXP = new AndroidXmlParser();
		Corpus corpus = new Corpus(prep.process(AXP.getBugs(), false));

		/*Scanner scanner = new Scanner(System.in);
		int currentId = -3;
		Document currentDoc = new Document("", "");

		while(currentId != 0){

		  System.out.println("Enter next var: ");
		  currentId = scanner.nextInt();
		  currentDoc = corpus.idDocMap.get(currentId);
		  System.out.println("bug id: "+currentDoc.getBugID());
		  System.out.println("priority: "+currentDoc.getPriority());
		  System.out.println("component: "+currentDoc.getComponent());
		  System.out.println("status: "+currentDoc.getStatus());
		  System.out.println("type: "+currentDoc.getType());
		  System.out.println("version: "+currentDoc.getVersion());
		  System.out.println("title: "+currentDoc.getTitle());
		  System.out.println("description: "+currentDoc.getDescription());

		}*/

		//System.out.println(corpus.idDocMap.get(13321).getStatus());
		//System.out.println(corpus.idDocMap.get(2282).getStatus());
		//System.out.println(corpus.idDocMap.get(14516).getStatus());

		//System.out.println(corpus.getDocuments().size());

		//	TableGenerator TG = new TableGenerator(false, freeVariables, corpus);

		//TG.createBMFandCategoryTable("bmf.csv");
		//TG.joinTables("dup_ids.csv", cosine_files);
		//TG.createREPTable("cosine_rep.csv");
		//TG.createPairContextCosineTable(contextFile);
		//TG.createJoinTable(bmfFile, contextFile, outputFile);
		//TG.euclideanDistTable(context, outputFile);
		//TG.createContextTable("AndroidLabeledTopicsContext", "labeled_context.csv");
		//TG.createJoinTable("2000_bmfs.csv","labeled_context.csv","bmf_labeled.csv");
		//TG.createPairContextDistanceTable("lda_context.csv");
		//TG.createContextTable("Android All Topics Context");
	}

}
