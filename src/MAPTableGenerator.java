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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;

import au.com.bytecode.opencsv.CSVReader;


public class MAPTableGenerator extends TableGenerator{
	
	public MAPTableGenerator(boolean doStem, Map<String,Double> freeVars, Corpus inputCorpus) {
		super(doStem,freeVars,inputCorpus);
		// TODO Auto-generated constructor stub
	}

	public static void joinTables(String dup_ids_file, Vector<String> csvFiles, String output_file){

		HashSet<Integer> dup_ids = new HashSet<Integer>();

		try{
			FileInputStream fstream = new FileInputStream(dup_ids_file);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null)   {
				dup_ids.add(Integer.parseInt(strLine));
			}
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

		Vector<CSVReader> csvReaders = new Vector<CSVReader>();

		try {

			FileWriter fstream = new FileWriter(output_file);
			BufferedWriter out = new BufferedWriter(fstream);

			//csv file containing data
			for(String csvFile:csvFiles){
				CSVReader reader = new CSVReader(new FileReader(csvFile));
				csvReaders.add(reader);
			}

			Vector<String[]> nextLines = new Vector<String[]>();
			String[] temp;
			String id1 = "";
			String id2 = "";
			String Class = "";
			StringBuffer line = new StringBuffer();
		
			while ((temp = csvReaders.get(0).readNext()) != null) {

				nextLines = new Vector<String[]>();
				nextLines.add(temp);
				id1 = temp[0];
				id2 = temp[1];
				Class = temp[3];
				line = new StringBuffer();
				line.append(id1+","+id2+","+Class);

				for(int i=1;i< csvReaders.size();i++)
					nextLines.add(csvReaders.get(i).readNext());

				//if(!dup_ids.contains(Integer.parseInt(id1)) && !dup_ids.contains(Integer.parseInt(id2)))
				//	continue;

				for(int i=0;i<nextLines.size();i++){


					if(!nextLines.get(i)[0].equals(id1) || !nextLines.get(i)[1].equals(id2)){
						throw new Exception();
					}
					line.append(","+nextLines.get(i)[2]);
				}

				line.append('\n');

				out.append(line);
			}
			out.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}


	public void createREPTable(String outputFileName,String dup_ids_filename, String sync_context_filename){

		Map<Integer,Double[]> contextMap = new HashMap<Integer, Double[]>();

		HashSet<Integer> dup_ids = new HashSet<Integer>();

		try{
			FileInputStream fstream = new FileInputStream(dup_ids_filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null)   {
				dup_ids.add(Integer.parseInt(strLine));
			}
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

		try {
			//csv file containing data
			String strFile = sync_context_filename;
			CSVReader reader = new CSVReader(new FileReader(strFile));
			String [] nextLine;
			reader.readNext();
			Double[] contextValues = {};
			while ((nextLine = reader.readNext()) != null) {

				contextValues = new Double[nextLine.length-1];

				for(int i=0;i<(nextLine.length-1);i++)
					contextValues[i] = Double.parseDouble(nextLine[i+1]);

				contextMap.put(Integer.parseInt(nextLine[0]), contextValues);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println("error");
		}

		Integer[] ids = contextMap.keySet().toArray(new Integer[0]);

		System.out.println("kango: "+corpus.getDocuments().size());
		try{
			// Create file 
			FileWriter fstream = new FileWriter(outputFileName);
			BufferedWriter out = new BufferedWriter(fstream);
			StringBuffer line = new StringBuffer("");
			double rep_value = 0.0;
			String separator = ",";
			Document doc1;
			Document doc2;
			int id1;
			int id2;

			for(int i=0;i<ids.length;i++){
				id1 = ids[i];
				doc1 = corpus.idDocMap.get(id1);
				for(int j=i+1; j<ids.length; j++){
					id2 = ids[j];
					if(!dup_ids.contains(id1) && !dup_ids.contains(id2))
						continue;
					doc2 = corpus.idDocMap.get(id2);
					rep_value = rep.getREP(doc1, doc2, freeVariables);
					line = new StringBuffer("");

					line.append(doc1.getBugID()+separator);
					line.append(doc2.getBugID()+separator);

					line.append(rep_value+separator);

					line.append((isDuplicate(doc1, doc2)?"dup":"non")+"\n");
					out.append(line);
				}
			}
			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	private void createPairContextDistanceTable(String fileName,String dup_ids_filename){

		Map<Integer,Double[]> contextMap = new HashMap<Integer, Double[]>();

		try {
			//csv file containing data
			String strFile = fileName;
			CSVReader reader = new CSVReader(new FileReader(strFile));
			String [] nextLine;
			reader.readNext();
			while ((nextLine = reader.readNext()) != null) {

				Double[] contextValues = new Double[nextLine.length-1];

				for(int i=0;i<(nextLine.length-1);i++)
					contextValues[i] = Double.parseDouble(nextLine[i+1]);

				contextMap.put(Integer.parseInt(nextLine[0]), contextValues);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println("error");
		}

		HashSet<Integer> dup_ids = new HashSet<Integer>();

		try{
			FileInputStream fstream = new FileInputStream(dup_ids_filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null)   {
				dup_ids.add(Integer.parseInt(strLine));
			}
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

		Integer[] ids = contextMap.keySet().toArray(new Integer[0]);

		try{
			// Create file 
			FileWriter fstream = new FileWriter("cosine_distance_"+fileName);
			BufferedWriter out = new BufferedWriter(fstream);
			Double[] vec1 = {};
			Double[] vec2 = {};
			double difference = 0;
			double rounded = 0;
			double sum = 0;

			for(int i=0;i<ids.length;i++){
				int id1 = ids[i];
				for(int j=i+1; j<ids.length; j++){

					sum = 0;
					int id2 = ids[j];
					if(id1 == id2)
						continue;
					if(!dup_ids.contains(id1) && !dup_ids.contains(id2))
						continue;
					double cosine = cosineSimilarity(contextMap.get(id1), contextMap.get(id2));
					vec1 = contextMap.get(id1);
					vec2 = contextMap.get(id2);
					StringBuffer line = new StringBuffer();
					line.append(id1+",");
					line.append(id2+",");

					/*for(int index=0;index<vec1.length;index++){

						if(vec1[index] == 0 && vec2[index] == 0)
							continue;
						difference = Math.abs(vec1[index]-vec2[index]);
						
						sum += 1/(1+difference);
					}*/
					
					//rounded = (double)(Math.round(sum * 10000)) / 10000;
					rounded = (double)(Math.round(cosine * 10000)) / 10000;
					line.append(rounded+",");
					line.append((isDuplicate(corpus.idDocMap.get(id1), corpus.idDocMap.get(id2))?"dup":"non"));
					out.append(line+"\n");
				}
			}

			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		
		HashMap<String,Double> freeVariables = new HashMap<String, Double>(){{
			put("w1",1.163);
			put("w2",0.013);
			put("w3",2.285);
			put("w4",0.032);
			put("w5",0.772);
			put("w6",0.381);
			put("w7",2.427);
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

		Vector<String> cosine_files = new Vector<String>();
		cosine_files.add("android_rep.csv");
		cosine_files.add("cosine_distance_android_architecture_context.csv");
		cosine_files.add("cosine_distance_android_nfr_context.csv");
		cosine_files.add("cosine_distance_android_junk_context.csv");
		//cosine_files.add("cosine_distance_android_lda_context.csv");
		cosine_files.add("cosine_distance_android_labeled_context.csv");
		

		/*Preprocessing prep = new Preprocessing();
		AndroidXmlParser AXP = new AndroidXmlParser();
		Corpus corpus = new Corpus(prep.process(AXP.getBugs(), false));*/
		
		//MAPTableGenerator maptg = new MAPTableGenerator(false, freeVariables, corpus);
		//maptg.createREPTable("android_rep.csv", "android_dup_ids.csv", "android_architecture_context.csv");
		//maptg.createPairContextDistanceTable("android_architecture_context.csv","android_dup_ids.csv");
		//maptg.createPairContextDistanceTable("android_nfr_context.csv","android_dup_ids.csv");
		//maptg.createPairContextDistanceTable("android_junk_context.csv","android_dup_ids.csv");
		//maptg.createPairContextDistanceTable("android_lda_context.csv","android_dup_ids.csv");
		//maptg.createPairContextDistanceTable("android_labeled_context.csv","android_dup_ids.csv");
		MAPTableGenerator.joinTables("android_dup_ids.csv", cosine_files, "android_cosines.csv");
	}
	
	
}
