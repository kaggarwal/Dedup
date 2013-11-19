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


public class Corpus{

	private Vector<Document> documents = new Vector<Document>();
	
	public Map<Integer,Document> idDocMap = new HashMap<Integer,Document>();
	private double avgTitleLength;
	private double avgDescriptionLength;

	public Corpus(Vector<Document> inputCorpus){
		documents = inputCorpus;
		createIdDocMap(documents);
		updateAvgDescriptionLength(0);
		updateAvgTitleLength(0);
	}
	
	public void insertDocument(Document newDoc){
		
		documents.add(newDoc);
		idDocMap.put(newDoc.getBugID(), newDoc);
		updateAvgDescriptionLength(documents.size()-1);
		updateAvgTitleLength(documents.size()-1);
	}

	private void createIdDocMap(Vector<Document> corpus){
		for(Document doc: corpus){
			idDocMap.put(doc.getBugID(), doc);
		}
	}
	
	public Map<Integer, Document> getIdDocMap() {
		return idDocMap;
	}
	
	public double getAvgTitleLength(){

		return this.avgTitleLength;

	}

	public double getAvgDescriptionLength(){

		return this.avgDescriptionLength;

	}
	
	public void updateAvgTitleLength(int previousSize) {
		
		double sumLength = getAvgTitleLength()*previousSize;
		for(int i=previousSize;i<documents.size();i++){
			sumLength += documents.get(i).getTitle().length();
		}

		this.avgTitleLength = ((double)(sumLength)/documents.size());
	}

	public void updateAvgDescriptionLength(int previousSize) {
		
		double sumLength = getAvgDescriptionLength()*previousSize;
		for(int i=previousSize;i<documents.size();i++){
			sumLength += documents.get(i).getDescription().length();
		}

		this.avgDescriptionLength = ((double)(sumLength)/documents.size());
	}

	public Vector<Document> getDocuments() {
		return documents;
	}
	
	public void setDocuments(Vector<Document> documents) {
		this.documents = documents;
	}

	public static void main(String[] args) {
		
	}
}
