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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class AndroidXmlParser {

	static final String COMMENT = "comment";
	static final String WHEN = "when";
	static final String WHAT = "what";
	static final String AUTHOR = "author";
	static final String NAME = "name";
	static final String BUGID = "issues:id";
	static final String LABEL = "issues:label";
	static final String STARS = "issues:stars";
	static final String STATE = "issues:state";
	static final String STATUS = "issues:status";
	static final String OPENEDDATE = "published";
	static final String UPDATEDON = "updated";
	static final String CLOSEDON = "issues:closedDate";
	static final String TITLE = "title";
	static final String DESCRIPTION = "content";
	static final String OWNER = "issues:owner";
	static final String USERNAME = "issues:username";
	static final String MERGEDINTO = "issues:mergedInto";
	private Map<String,Double> priorityMap = new HashMap<String,Double>(){{
		put("Small",1.0);
		put("Medium",2.0);
		put("High",3.0);
		put("Critical",4.0);
		put("Blocker",5.0);
	}};
	
	public double getPriorityNumber(String priority){

		if(priority.equals(""))
			return 0;

		return priorityMap.get(priority);
	}
	
	public double getVersionNumber(String version){

		if(version.equals(""))
			return 0;

		if(version.startsWith("r"))
			return Double.parseDouble(version.substring(1));

		if(version.toLowerCase().equals("opensource"))
			return 0.5;

		return Double.parseDouble(version);
	}

	private List<Bug> readRepo(String repositoryFile) {
		List<Bug> bugs = new ArrayList<Bug>();
		try {
			// First create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// Setup a new eventReader
			InputStream in = new FileInputStream(repositoryFile);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			// Read the XML document\
			Bug bug = null;
			int counter = 0;

			boolean authorFlag = false;
			boolean ownerFlag = false;
			boolean mergeFlag = false;


			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();


				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					// If we have a item element we create a new item
					if (startElement.getName().getLocalPart().equals(TITLE)) {
						bug = new Bug();
						// We read the attributes from this tag and add the date
						
						while(!event.isEndElement()){
							event = eventReader.nextEvent();
							bug.setTitle(event.isEndElement()?"":event.asCharacters().getData());
						}
						
						authorFlag = false;
						mergeFlag = false;
						ownerFlag = false;
						counter++;
						continue;

					}
					
					if (event.isStartElement()){
						if ((event.asStartElement().getName().getPrefix()+":"+event.asStartElement().getName().getLocalPart())
								.equals(AUTHOR)) {

							authorFlag = true;
							continue;
						}
					}
					

					if ((event.asStartElement().getName().getPrefix()+":"+event.asStartElement().getName().getLocalPart())
							.equals(MERGEDINTO)) {
						// We read the attributes from this tag and add the date
						// attribute to our object
						mergeFlag = true;
						continue;
					}

					if (event.isStartElement()) {
						if ((event.asStartElement().getName().getPrefix()+":"+event.asStartElement().getName().getLocalPart())
								.equals(BUGID)) {
							event = eventReader.nextEvent();
							String id = event.isEndElement()?"":event.asCharacters().getData();
							if(mergeFlag){
								bug.setMergeID(id);
								mergeFlag = false;
							}
							else
								bug.setBugid(id);

							continue;
						}
					}

					if (event.isStartElement()) {
						if ((event.asStartElement().getName().getPrefix()+":"+event.asStartElement().getName().getLocalPart())
								.equals(USERNAME)) {
							while(!event.isEndElement()){
								event = eventReader.nextEvent();
								if(ownerFlag){
									bug.setOwner(event.isEndElement()?"":event.asCharacters().getData());
									ownerFlag = false;
								}
							}
							continue;
						}
					}

					if (event.isStartElement()) {
						if (event.asStartElement().getName().getLocalPart()
								.equals(NAME)) {
							while(!event.isEndElement()){
								event = eventReader.nextEvent();
								if(authorFlag){
									bug.setAuthor(event.isEndElement()?"":event.asCharacters().getData());
									authorFlag = false;
								}
							}
							continue;
						}
					}

					if (event.isStartElement()) {
						if (event.asStartElement().getName().getLocalPart()
								.equals(TITLE)) {
							while(!event.isEndElement()){
								event = eventReader.nextEvent();
								bug.setTitle(event.isEndElement()?"":event.asCharacters().getData());
							}
							continue;
						}
					}
					if (event.isStartElement()){
						if ((event.asStartElement().getName().getPrefix()+":"+event.asStartElement().getName().getLocalPart())
								.equals(STATUS)) {
							while(!event.isEndElement()){
								event = eventReader.nextEvent();
								bug.setStatus(event.isEndElement()?"":event.asCharacters().getData());
							}
							continue;
						}
					}

					if (event.isStartElement()){
						if ((event.asStartElement().getName().getPrefix()+":"+event.asStartElement().getName().getLocalPart())
								.equals(STATE)) {
							while(!event.isEndElement()){
								event = eventReader.nextEvent();
								bug.setState(event.isEndElement()?"":event.asCharacters().getData());
							}
							continue;
						}
					}

					if (event.isStartElement()){
						if ((event.asStartElement().getName().getPrefix()+":"+event.asStartElement().getName().getLocalPart())
								.equals(OWNER)) {

							ownerFlag = true;
							continue;
						}
					}
					if (event.isStartElement()){
						if ((event.asStartElement().getName().getPrefix()+":"+event.asStartElement().getName().getLocalPart())
								.equals(CLOSEDON)) {
							while(!event.isEndElement()){
								event = eventReader.nextEvent();
								bug.setClosedOn(event.isEndElement()?"":event.asCharacters().getData());
							}
							continue;
						}
					}
					if (event.isStartElement()){
						if ((event.asStartElement().getName().getPrefix()+":"+event.asStartElement().getName().getLocalPart())
								.equals(LABEL)) {
							String label = "";
							while(!event.isEndElement()){
								event = eventReader.nextEvent();
								label += event.isEndElement()?"":event.asCharacters().getData();
							}
							if(label.startsWith("Type-"))
								bug.setType(label.split("-")[1]);

							if(label.startsWith("Priority-")){
								bug.setPriority(label.split("-")[1]);
								bug.setPriorityNumber(getPriorityNumber(bug.getPriority()));
							}

							if(label.startsWith("ReportedBy-"))
								bug.setReportedBy(label.split("-")[1]);

							if(label.startsWith("Component-"))
								bug.setComponent(label.split("-")[1]);

							if(label.startsWith("Version-")){
								bug.setVersion(label.split("-")[1]);
								bug.setVersionNumber(getVersionNumber(bug.getVersion()));
							}


							continue;
						}
					}
					if (event.isStartElement()){
						if ((event.asStartElement().getName().getPrefix()+":"+event.asStartElement().getName().getLocalPart())
								.equals(STARS)) {
							while(!event.isEndElement()){
								event = eventReader.nextEvent();
								bug.setStars(event.isEndElement()?"":event.asCharacters().getData());
							}
							continue;
						}
					}
					if (event.isStartElement()){
						if (event.asStartElement().getName().getLocalPart()
								.equals(OPENEDDATE)) {
							while(!event.isEndElement()){
								event = eventReader.nextEvent();
								bug.setOpenedDate(event.isEndElement()?"":event.asCharacters().getData());
							}
							continue;
						}
					}

					if (event.isStartElement()){
						if (event.asStartElement().getName().getLocalPart()
								.equals(DESCRIPTION)) {
							while(!event.isEndElement()){
								event = eventReader.nextEvent();
								bug.setDescription(event.isEndElement()?"":event.asCharacters().getData());
							}
							bugs.add(bug);
							continue;
						}
					}

				}
				// If we reach the end of an item element we add it to the list
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}

		return bugs;
	}


	public Vector<Document> getBugs(){

		Vector<Document> results = new Vector<Document>();
		List<Bug> repo = readRepo("android_issues.xml");


		for (Bug item : repo) {
			/*if((item.getTitle().contains("message")&&item.getStatus().equals("Duplicate"))||
					item.getDescription().contains("message")&&item.getStatus().equals("Duplicate"))
				System.out.println("bug :"+item.getBugid() +" master: "+item.getMergeID());*/

			//System.out.println(getVersionNumber(item.getVersion()));
			if(!item.getBugid().equals("")){
				results.add(new Document(item.getDescription(), item.getTitle(),
						Integer.parseInt(item.getBugid()),item.getStatus(),item.getStars(),item.getComponent(),
						item.getType(), item.getPriority(), getPriorityNumber(item.getPriority()),getVersionNumber(item.getVersion()),
						item.getVersion(), item.getMergeID().equals("")?0:Integer.parseInt(item.getMergeID()),
						item.getOpenedDate(),item.getClosedOn(), item.getProduct(),"yyyy-MM-dd'T'HH:mm:ss'.000Z'", item.comments));
			}

			//Close the output stream
		}

		return results;
	}

	Vector<Document> getUnitTest(Vector<Integer> ids){

		Vector<Document> results = new Vector<Document>();
		List<Bug> repo = readRepo("android_issues.xml");

		for (Bug item : repo) {

			if(item.getBugid()!="" && ids.contains(Integer.parseInt(item.getBugid()))){
				results.add(new Document(item.getDescription(), item.getTitle(), item.getBugid()==""?0:
					Integer.parseInt(item.getBugid()),item.getStatus(),item.getStars(),item.getComponent(),
					item.getType(), item.getPriority(), item.getPriorityNumber(), item.getVersionNumber(),
					item.getVersion(), item.getMergeID()==""?0:Integer.parseInt(item.getMergeID()),
					item.getOpenedDate(),item.getClosedOn(), item.getProduct(),"yyyy-MM-dd'T'HH:mm:ss'.000Z'", item.comments));
			}
		}
		return results;
	}

	public Vector<Document> getUnitTest(){

		Vector<Document> results = new Vector<Document>();
		List<Bug> repo = readRepo("android_issues.xml");

		DateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.000Z'");

		Date date = new Date();
		Date earliest = new Date();
		Date latest = new Date();

		for (Bug item : repo) {

			Date openDate;
			try {
				openDate = sdf.parse(item.getOpenedDate());
				String GMTOpenTime = openDate.toGMTString();
				openDate = new Date(GMTOpenTime);

				if(earliest.after(openDate))
					earliest = openDate;
				if(openDate.after(latest))
					latest = openDate;

				if(!item.getClosedOn().equals("")){
					Date closeDate = sdf.parse(item.getClosedOn());
					String GMTCloseTime = closeDate.toGMTString();
					closeDate = new Date(GMTCloseTime);
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(earliest);
		System.out.println(latest);
		return results;
	}

	public void writeToFile(Vector<Document> documents){

		try{
			// Create file 
			FileWriter fstream = new FileWriter("statuses.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			for(Document doc: documents){
				out.append(doc.getStatus()+"\n");
			}
			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public static void main(String[] args) {

		Preprocessing prep = new Preprocessing();
		AndroidXmlParser AXP = new AndroidXmlParser();
		
		Set<String> components = new HashSet<String>();
		Set<String> types = new HashSet<String>();
		Set<String> priorities = new HashSet<String>();
		Set<String> version = new HashSet<String>();
		Set<String> status = new HashSet<String>();

		Vector<Document> bugs = prep.process(AXP.getBugs(),false);
		for(Document doc: bugs){
			
			components.add(doc.getComponent());
			types.add(doc.getType());
			priorities.add(doc.getPriority());
			version.add(doc.getVersion());
			status.add(doc.getStatus());
			
		}
		
		System.out.println("components:");
		for(String element: components)
			System.out.println(element);
		
		System.out.println("*****************");
		System.out.println("types:");
		for(String element: types)
			System.out.println(element);
		
		System.out.println("*****************");
		System.out.println("priorities:");
		for(String element: priorities)
			System.out.println(element);
		
		System.out.println("*****************");
		System.out.println("versions:");
		for(String element: version)
			System.out.println(element);
		
		System.out.println("*****************");
		System.out.println("status:");
		for(String element: status)
			System.out.println(element);
		
		/*System.out.println(bugs.size());
		Collections.sort(bugs);
		Map<Integer,Document> idDocMap = (new Corpus(bugs)).getIdDocMap();
		
		Vector<Document> duplicates = new Vector<Document>();

		for(Document doc: bugs){
			if(doc.getStatus().equals("Duplicate")){
				duplicates.add(doc);				
			}
		}
		
		Random randomGenerator = new Random();
		for(int i=0;i<10;i++){
			
			int randomInt = randomGenerator.nextInt(duplicates.size());
			Document currentDup = duplicates.get(randomInt);
			Document currentMerge = idDocMap.get(currentDup.getMergeID());
			Document currentMaster = idDocMap.get(currentDup.getMasterID());
			
			System.out.println("Bug ID: "+currentDup.getBugID());
			System.out.println("Title: "+currentDup.getTitle());
			System.out.println("Description: "+currentDup.getDescription());
			System.out.println("-------------------------------");
			System.out.println("Merge ID: "+currentDup.getMergeID());
			System.out.println("Merge Title: "+currentMerge.getTitle());
			System.out.println("Merge Description: "+currentMerge.getDescription());
			System.out.println("-------------------------------");
			System.out.println("Master ID: "+currentMaster.getBugID());
			System.out.println("Master Title: "+currentMaster.getTitle());
			System.out.println("Master Description: "+currentMaster.getDescription());
			System.out.println("********************************************************");
		}
		*/
		
		//AXP.writeToFile(bugs);


		/*for(Document doc:bugs){

			if(doc.getStatus().equals("Duplicate"))
				System.out.println(idDocMap.get(doc.getMergeID()).getStatus());
		}*/

	}
}
