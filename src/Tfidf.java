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
import java.io.FileInputStream;
import java.io.InputStreamReader;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Tfidf {

	Map<String, Integer> dictionary = new HashMap<String, Integer>();
	int numDocs;
	int n; // n-gram
	private Corpus corps;


	public Tfidf(Corpus corp,int input_n){

		n = input_n;
		Vector<Document> documents = corp.getDocuments();
		createDictionary(documents);
		numDocs = documents.size();
		corps = corp;
	}
	
	public void updateCorpus(Corpus corp){
		
		for(Document doc:corp.getDocuments())
			updateDictionary(doc);
		
		numDocs = corp.getDocuments().size();
		corps = corp;
		
	}


	public void setCorps(Corpus corps) {
		this.corps = corps;
	}
	
	public Corpus getCorps() {
		return corps;
	}
	
	public void updateDictionary(Document document){
		
			
			Set<String> temporarySet = new HashSet<String>();

			for(String word:Util.get_n_grams(document.getDescription(),n)){
				if(!word.equals(""))
					temporarySet.add(word);}
			for(String word:Util.get_n_grams(document.getTitle(),n)){
				if(!word.equals(""))
					temporarySet.add(word);
			}

			for(String word:temporarySet){
				if(dictionary.containsKey(word))
					dictionary.put(word, dictionary.get(word)+1);
				else
					dictionary.put(word, 1);
			}
	}
	
	public void createDictionary(Vector<Document> corpus){

		for(Document doc: corpus){

			Set<String> temporarySet = new HashSet<String>();

			for(String word:Util.get_n_grams(doc.getDescription(),n)){
				if(!word.equals(""))
					temporarySet.add(word);}
			for(String word:Util.get_n_grams(doc.getTitle(),n)){
				if(!word.equals(""))
					temporarySet.add(word);
			}

			for(String word:temporarySet){
				if(dictionary.containsKey(word))
					dictionary.put(word, dictionary.get(word)+1);
				else
					dictionary.put(word, 1);
			}
		}
	}

	/*public double[] calculateTfidfWeights(Document doc){

		Set<String> dictionaryWords = dictionary.keySet();
		List<String> dicWords = new ArrayList<String>(dictionaryWords);
		double[] weights = new double[dictionaryWords.size()];

		for(int i=0;i<dicWords.size();i++){
			weights[i] = calculateTfD(doc, dicWords.get(i), 3 , 1 , 0.5, 0.5)*calculateIDF(dicWords.get(i));
			weights[i] = (double)((int)((weights[i])*10000))/10000;
		}
		return weights;
	}*/

	public double calculateTfD(Map<String,Integer> titleWordsFreqMap, Map<String,Integer> descriptionWordsFreqMap,
			int titleLength, int descriptionLength, String t, double wsumm, double wdesc, double bdesc, double bsumm){

		double tf = 0;
		
		/****calculating TF ***/
		double TF1 = (double)(titleWordsFreqMap.containsKey(t)?wsumm*titleWordsFreqMap.get(t):0)/(1-bsumm+(double)(bsumm*titleLength)/corps.getAvgTitleLength());
		double TF2 = (double)(wdesc*(descriptionWordsFreqMap.containsKey(t)?descriptionWordsFreqMap.get(t):0))/(1-bdesc+(double)(bdesc*descriptionLength)/corps.getAvgDescriptionLength());

		tf = TF1+TF2;
		/***calculating IDF ***/
		//double result = (double)((int)((tf*idf)*10000))/10000;
		/*if(important_words.contains(t))
			tf = tf*10;*/
		
		return tf;
	}

	public double calculateIDF(String t){

		return Math.log10((double)numDocs/dictionary.get(t));
	}

	public void writeToFile(Vector<Document> corpus)
	{
		try{
			// Create file 
			FileWriter fstream = new FileWriter("weights.csv");
			BufferedWriter out = new BufferedWriter(fstream);
			out.append("bugid");
			int counter = 0;
			for(String dicWord:dictionary.keySet()){
				out.append(","+dicWord);
				counter++;
				if (counter == 100)
					break;
			}
			out.append("\n");

			for(Document doc:corpus){
				counter = 0;
				out.append(doc.getBugID()+"");
				for(double weight:doc.weights){
					out.append(","+weight);
					counter++;
					if(counter == 100)
						break;
				}
				out.append("\n");
			}

			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public int calculateMaxFreq(String description, String title){

		Map<String,Integer> wordFreqs = new HashMap<String,Integer>();
		for(String s:Util.get_n_grams(description,n)){
			if(wordFreqs.containsKey(s))
				wordFreqs.put(s, wordFreqs.get(s)+1);
			else
				wordFreqs.put(s, 1);
		}

		for(String s:Util.get_n_grams(title,n)){
			if(wordFreqs.containsKey(s))
				wordFreqs.put(s, wordFreqs.get(s)+1);
			else
				wordFreqs.put(s, 1);
		}

		int max = -1;
		for(int freq:wordFreqs.values()){
			if(max<freq)
				max = freq;
		}

		return max;
	}
	

	public static void main(String[] args) {

		/*Preprocessing prep = new Preprocessing();
		AndroidXmlParser read = new AndroidXmlParser();

		Integer[] arr = {37520,37427,37198,37018,36995,36893,36870,36791,36747,36689,36680,36654,36644,34880,33788,37197,37019,36996,36987,36979};
		Vector<Integer> unitTestIds = new Vector<Integer>(Arrays.asList(arr));

		Vector<Document> textFields = read.getUnitTest(unitTestIds);
		Vector<Document> corpus = prep.process(textFields);

		Tfidf tfidf = new Tfidf(corpus,1);
		for(int i=0;i<corpus.size();i++){
			corpus.get(i).weights = tfidf.calculateTfidfWeights(corpus.get(i));
		}*/
	}
}
