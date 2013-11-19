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
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BM25F {

	Tfidf tfidf;
	private int n;

	public BM25F(Corpus corps, int input_n_gram, boolean stemming){

		n = input_n_gram;
		tfidf = new Tfidf(corps,n);
	}
	
	public void update(Corpus corps){
		
		tfidf.updateCorpus(corps);
	}

	public double TFQ(Map<String,Integer> titleFreqMap, Map<String,Integer> descriptionFreqMap, String word, double wdesc, double wsumm){

		double TFQ1 = wsumm*(titleFreqMap.containsKey(word)?titleFreqMap.get(word):0);
		double TFQ2 = wdesc*(descriptionFreqMap.containsKey(word)?descriptionFreqMap.get(word):0);

		double TFQ = TFQ1+TFQ2;
		/*if(important_words.contains(word))
			TFQ = TFQ*10;*/
		
		return TFQ;
	}

	public int countFrequencies(String str, String word){

		Pattern pattern = Pattern.compile(word);
		Matcher matcher = pattern.matcher(str);
		int counter = 0;
		while (matcher.find())
			counter++;
		return counter;
	}


	public double WQ(Map<String,Integer> titleFreqMap, Map<String,Integer> descriptionFreqMap, String word, double wdesc, double wsumm, double k3){

		double TFQ = TFQ(titleFreqMap,descriptionFreqMap,word,wdesc,wsumm);
		return (double)((k3+1)*TFQ)/(k3+TFQ);
	}

	public double calculateBM25F(Document document, Document query, Map<String,Double> freeVariables){

		double result = 0;
		Set<String> intersection = new HashSet<String>();
		Set<String> docWords = new HashSet<String>();
		Set<String> qWords = new HashSet<String>();
		docWords.addAll(n==1?document.unigram_descriptionWordFrequencies.keySet():document.bigram_descriptionWordFrequencies.keySet());
		docWords.addAll(n==1?document.unigram_titlenWordFrequencies.keySet():document.bigram_titlenWordFrequencies.keySet());
		qWords.addAll(n==1?query.unigram_descriptionWordFrequencies.keySet():query.bigram_descriptionWordFrequencies.keySet());
		qWords.addAll(n==1?query.unigram_titlenWordFrequencies.keySet():query.bigram_titlenWordFrequencies.keySet());
		
		for(String word: docWords){
			
			if(qWords.contains(word))
				intersection.add(word);
		}
		
		for(String t:intersection){
			double IDF = tfidf.calculateIDF(t);
			double TFD = tfidf.calculateTfD(n==1?document.unigram_titlenWordFrequencies:document.bigram_titlenWordFrequencies,
					n==1?document.unigram_descriptionWordFrequencies:document.bigram_descriptionWordFrequencies,
					document.getTitle().length(), document.getDescription().length(),t,
					freeVariables.get(n==1?"wsumm_uni":"wsumm_bi") , 
					freeVariables.get(n==1?"wdesc_uni":"wdesc_bi") , freeVariables.get(n==1?"bdesc_uni":"bdesc_bi"), 
					freeVariables.get(n==1?"bsumm_uni":"bsumm_bi"));
			double WQ = WQ(n==1?query.unigram_titlenWordFrequencies:query.bigram_titlenWordFrequencies,
					n==1?query.unigram_descriptionWordFrequencies:query.bigram_descriptionWordFrequencies,
					t,freeVariables.get(n==1?"wdesc_uni":"wdesc_bi"),
					freeVariables.get(n==1?"wsumm_uni":"wsumm_bi"),freeVariables.get(n==1?"k3_uni":"k3_bi"));

			result += IDF * (TFD/(freeVariables.get(n==1?"k1_uni":"k1_bi")+TFD)) * WQ;
		}
		return result;
	}

	public static void main(String[] args) {

		//Preprocessing prep = new Preprocessing();
		//AndroidXmlParser read = new AndroidXmlParser();

		//Integer[] arr = {37520,37427,37198,37018,36995,36893,36870,36791,36747,36689,36680,36654,36644,34880,33788,37197,37019,36996,36987,36979};
		//Vector<Integer> unitTestIds = new Vector<Integer>(Arrays.asList(arr));

		//Vector<Document> textFields = read.getBugs();
		//Vector<Document> corpus = new Vector<Document>();

		//Document doc1 = new Document("ana ana from the sina ana", "kan", 1, "new",1);
		//Document doc2 = new Document("sina yasi sina", "k", 2, "new",1);
		//Document doc3 = new Document("a ana from the", "kan", 2, "new",1);

		//corpus.add(doc1);
		//corpus.add(doc2);

		//BM25F bm25f = new BM25F(corpus,1);

		//System.out.println(bm25f.calculateBM25F(doc1, doc3, 0.5));

	}

}
