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

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.text.html.HTMLDocument.Iterator;


public class Buckets {

	private Map<Integer,Vector<Integer>> buckets = new HashMap<Integer, Vector<Integer>>();
	public Map<Integer,Document> idDocMap = new HashMap<Integer,Document>();

	public Buckets(Vector<Document> corpus){
		
		Document master;

		createIdDocMap(corpus);
		
		for(Document doc: corpus){
			if(!doc.getStatus().toLowerCase().equals("duplicate"))
				buckets.put(doc.getBugID(), new Vector<Integer>());	
		}
		
		for(Document doc: corpus){
			if(doc.getStatus().toLowerCase().equals("duplicate")){
				
				if(buckets.containsKey(doc.getBugID()))
					continue;
				
				master = idDocMap.get(doc.getMergeID());
				if(master.getMergeID()==doc.getBugID())
				buckets.put(master.getBugID(), new Vector<Integer>());
				
				buckets.get(doc.getMasterID()).add(doc.getBugID());
			}
		}
	}

	public void insert(Document doc){
		
		if(doc.getStatus().toLowerCase().equals("duplicate")){
			buckets.get(doc.getMasterID()).add(doc.getBugID());
		}
		
		else{
			buckets.put(doc.getBugID(), new Vector<Integer>());	
		}
	}
	
	private void createIdDocMap(Vector<Document> corpus){
		for(Document doc: corpus){
			idDocMap.put(doc.getBugID(), doc);
		}
	}
	
	public Map<Integer, Vector<Integer>> getBuckets() {
		
		return buckets;
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
		
		Preprocessing prep = new Preprocessing();
		AndroidXmlParser AXP = new AndroidXmlParser();
		Corpus corpus = new Corpus(prep.process(AXP.getBugs(), false));
		
		Vector<Integer> dups = new Vector<Integer>();
		
		Buckets buckets = new Buckets(corpus.getDocuments());
		
		System.out.println("Start");
		
		for(Integer key:buckets.getBuckets().keySet()){
			
			dups = buckets.getBuckets().get(key);
			
			if(dups.size()==0)
				continue;
			
			System.out.println("***************************************s");
			System.out.println("Master: "+ key);
			for(Integer dup:dups)
				System.out.println("Duplicate: "+dup);
			
		}
		
		/*System.out.println("number of buckets: "+buckets.getBuckets().size());
		
		for(Integer key:buckets.getBuckets().keySet()){
			
			dups = buckets.getBuckets().get(key);
			
			if(dups.size()==0)
				continue;
			
			System.out.println(dups.size());
			
		}*/
		
	}

}
