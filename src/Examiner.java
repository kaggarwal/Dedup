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
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import au.com.bytecode.opencsv.CSVReader;


public class Examiner {


	public Vector<Integer> extracting_false_negatives(String input_file){

		Vector<Integer> instance_indexes = new Vector<Integer>();

		int temp1 = 0;
		int temp2 = 0;

		try{
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream(input_file);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				if(strLine.contains("2:dup      1:non")){

					temp1 = strLine.indexOf("(");
					temp2 = strLine.indexOf(")");
					instance_indexes.add(Integer.parseInt(strLine.substring(temp1+1, temp2)));

				}
			}
			//Close the input stream
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		System.out.println(instance_indexes.size());

		return instance_indexes;
	}

	public Vector<Integer> extract_duplicates(Vector<Integer> lines, String file_name){


		Vector<Integer> duplicate_ids = new Vector<Integer>();
		Collections.sort(lines);

		try {
			//csv file containing data
			String strFile = file_name;
			CSVReader reader = new CSVReader(new FileReader(strFile));
			String [] nextLine;
			int line_counter=0;

			for(int i=0;i<lines.size();i++){

				while ((nextLine = reader.readNext()) != null) {

					line_counter++;
					if(line_counter==lines.get(i)+1){

						duplicate_ids.add(Integer.parseInt(nextLine[0]));
						break;
					}
				}
			}
		}
		catch (Exception e) {
			System.out.println("kangoro");
			// TODO: handle exception
		}

		return duplicate_ids;
	}

	public void extract_bucket_sizes(Vector<Integer> duplicate_ids){

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

		Preprocessing prep = new Preprocessing();
		AndroidXmlParser AXP = new AndroidXmlParser();
		Corpus corpus = new Corpus(prep.process(AXP.getBugs(), false));

		Vector<Integer> dups = new Vector<Integer>();

		Buckets buckets = new Buckets(corpus.getDocuments());

		System.out.println("Start");

		for(int current_dup:duplicate_ids){

			for(Integer key:buckets.getBuckets().keySet()){

				
				dups = buckets.getBuckets().get(key);

				if(dups.size()==0)
					continue;

				if(dups.contains(current_dup))
					System.out.println(dups.size());
				
				
				if(key == current_dup)
					System.out.println(dups.size());

			}
		}

	}


	public static void main(String[] args) {


		Examiner examiner = new Examiner();
		examiner.extract_bucket_sizes(examiner.extract_duplicates(
				examiner.extracting_false_negatives("openoffice_context_only_result_buffer"), "shuffed_openoffice.csv"));

	}

}
