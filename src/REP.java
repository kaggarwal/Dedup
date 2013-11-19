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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class REP {
	
	private BM25F bmf_unigram;
	private BM25F bmf_bigram;
	Corpus corp;

	public REP(Corpus corps, boolean stemming){
		
		corp = corps;
		bmf_unigram = new BM25F(corp, 1, stemming);
		bmf_bigram = new BM25F(corp, 2, stemming);
	}
	
	public BM25F getBmf_bigram() {
		return bmf_bigram;
	}
	
	public BM25F getBmf_unigram() {
		return bmf_unigram;
	}
	
	public void update(Corpus corps){
		
		bmf_unigram.update(corps);
		bmf_bigram.update(corps);
		corp = corps;
	}
	
	public double[] getFeatures(Document doc, Document query, Map<String, Double> freeVariables){
		
		//System.out.print(doc.getBugID()+"	");
		//System.out.print(query.getBugID()+"		");
		//System.out.print(doc.getVersionNumber()+"	");
		//System.out.print(query.getVersionNumber()+"		");
		
		
		double[] features = new double[7];
		features[0] = bmf_unigram.calculateBM25F(doc, query, freeVariables);
		features[1] = bmf_bigram.calculateBM25F(doc, query, freeVariables);
		features[2] = zero_one_function(doc.getProduct(), query.getProduct());
		features[3] = zero_one_function(doc.getComponent(), query.getComponent());
		features[4] = zero_one_function(doc.getType(), query.getType());
		features[5] = reciprocal_function(doc.getPriorityNumber(), query.getPriorityNumber());
		features[6] = reciprocal_function(doc.getVersionNumber(), query.getVersionNumber());
		
		//System.out.println(features[6]);
		
		return features;
	}
	
	public double getREP(Document doc, Document query, Map<String , Double> freeVariables){
		
		double[] features = getFeatures(doc, query, freeVariables);
		double result = 0;
		
		for(int i=0;i<features.length;i++){
			result += freeVariables.get("w"+(i+1))*features[i];
		}
		return (double)((int)(result*1000))/1000;
	}
	
	private int zero_one_function(Object o1, Object o2){
		
		if(o1.toString().equals("") && o2.toString().equals(""))
			return 0;
		
		if(o1.equals(o2))
			return 1;
		
		return 0;
	}

	private double reciprocal_function(double d1, double d2){
		
		if(d1 == 0  && d2 == 0)
			return 0;
		
		return (double)(1.0/(1+Math.abs(d1-d2)));
	}
	
	public static void main(String[] args) {
		
		/*Preprocessing prep = new Preprocessing();
		AndroidXmlParser AXP = new AndroidXmlParser();

		Vector<Document> corpus = AXP.getBugs();
		Collections.sort(corpus);
		corpus = prep.process(corpus,false);
		Collections.sort(corpus);
		
		Corpus inputCorpus =  new Corpus(corpus);
		
		TrainSet TS = new TrainSet(inputCorpus, 24);
		Tuning tuning = new Tuning(inputCorpus,false,false,"");
		Map<String,Double> freeVariables = tuning.tuningParametersInREP(TS.getTS());
		
		
		Validate validate = new Validate(inputCorpus,freeVariables,false,true,"NFR words.txt",true);
		Map<Document,Vector<SortableDoc>> duplicate_masters_map = validate.traverseTestSet(testSet);

		validate.writeToFile("witht_NFR_words", 0, "map", validate.MAP(duplicate_masters_map), "witht_NFR_words_MAP.txt");
		for(int k=1;k<21;k++){
			validate.writeToFile("with NFR words", k,"recall",validate.recall(duplicate_masters_map, k),
					"with_NFR_words_recall_k"+k+".txt"); 
		}*/
		
	}
	
}
