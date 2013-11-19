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

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import au.com.bytecode.opencsv.CSVReader;


public class MAPcalculator {


	public void calculate(String input_filename, int context_index, String dup_ids_filename, String output_file_name){

		Vector<Integer> duplicates =  new Vector<Integer>(); 
		Vector<Candidate> all = new Vector<Candidate>();
		Vector<Candidate> all_comparisons = new Vector<Candidate>();
		int id1, id2, hit;
		double sum = 0.0;
		double avg = 0.0;
		StringBuffer line = new StringBuffer("");

		try {
			//csv file containing data
			String strFile = dup_ids_filename;
			CSVReader reader = new CSVReader(new FileReader(strFile));
			String [] nextLine;

			while ((nextLine = reader.readNext()) != null) {
				duplicates.add(Integer.parseInt(nextLine[0]));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		try {
			String strFile = input_filename;
			CSVReader reader = new CSVReader(new FileReader(strFile));
			String [] nextLine;

			int counter = 0;
			while ((nextLine = reader.readNext()) != null) {
				
				counter++;
				if(counter%1000000==0)
					System.out.println("shomAreye: "+counter/1000000);
				
				id1 = Integer.parseInt(nextLine[0]);
				id2 = Integer.parseInt(nextLine[1]);
				all.add(new Candidate(id1, id2, Double.parseDouble(nextLine[3]) + Double.parseDouble(nextLine[context_index]),nextLine[2]));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		
		try {


			for(int current_dup:duplicates){
				
				System.out.println("next bug");

				all_comparisons = new Vector<Candidate>();
				sum = 0.0;
				hit = 0;
				int row = 0;

					//csv file containing data

					
					for(Candidate candidate:all){
						
						if(candidate.id1 != current_dup && candidate.id2 != current_dup)
							continue;
							
						all_comparisons.add(candidate);	
					}
						
					
					Collections.sort(all_comparisons);

					/*for(int row = 0; row<all_comparisons.size(); row++){

						if(all_comparisons.get(row).label.equals("dup")){

							hit++;
							
							sum += (double)hit/(double)(row+1);
						}
					}
					avg = (double)(Math.round((sum/(double)hit) * 100000)) / 100000;*/
					
					
					for(row = 0; row<all_comparisons.size(); row++){

						if(all_comparisons.get(row).label.equals("dup")){

							break;
							
						}
					}
					
					avg = (double)(Math.round((1/(double)(row+1)) * 100000)) / 100000;
					
				    FileWriter fw = new FileWriter(output_file_name,true); //the true will append the new data
				    fw.write(avg+"\n");//appends the string to the file
				    fw.close();
					
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		
		MAPcalculator M = new MAPcalculator();
		M.calculate(args[0], Integer.parseInt(args[1]), args[2], args[3]);
		//M.calculate("openoffice_cosines.csv", 3, "openoffice_dup_ids.csv", "rep_map.csv");
		
	}

}
