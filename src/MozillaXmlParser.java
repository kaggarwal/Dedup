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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.print.attribute.standard.Severity;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class MozillaXmlParser{

	static final String BUG = "bug";
	static final String BUGID = "bug_id";
	static final String OPENEDDATE = "creation_ts";
	static final String TITLE = "short_desc";
	static final String CLOSEDON = "delta_ts";
	static final String CLASSID = "classification_id";
	static final String PRODUCT = "product";
	static final String COMPONENT = "component";
	static final String VERSION = "version";
	static final String PLATFORM = "rep_platform";
	static final String SYS = "op_sys";
	static final String STATUS = "bug_status";
	static final String RESOLUTION = "resolution";
	static final String PRIORITY = "priority";
	static final String SEVERITY = "bug_severity";
	static final String MILESTONE = "target_milestone";
	static final String MERGEID = "dup_id";
	static final String KEYWORDS = "keywords";
	static final String REPORTER = "reporter";
	static final String ASSIGNEE = "assigned_to";
	static final String VOTES = "votes";
	static final String COMMENT = "long_desc";
	static final String THETEXT = "thetext";
	private Map<String,Integer> priorityMap = new HashMap<String,Integer>(){{
		
		put("--",0);
		put("",0);
		put("P1",1);
		put("P2",2);
		put("P3",3);
		put("P4",4);
		put("P5",5);
	}};


	public List<Bug> readConfig(String configFile) {
		List<Bug> bugs = new ArrayList<Bug>();
		Bug bug = new Bug();
		boolean desc = false;
		Comment comment = new Comment();
		try {
			// First create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// Setup a new eventReader
			InputStream in = new FileInputStream(configFile);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			// Read the XML document\
			
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();

				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					// If we have a item element we create a new item
					if (startElement.getName().getLocalPart().equals(BUG)) {
						bug = new Bug();
						desc = false;
						// We read the attributes from this tag and add the date
					}

					if (event.isStartElement()) {
						if (event.asStartElement().getName().getLocalPart()
								.equals(BUGID)) {
							String bugid = "";
							while(!event.isEndElement()){
								event = eventReader.nextEvent();
								bugid += event.isEndElement()?"":event.asCharacters().getData();
							}
							bug.setBugid(bugid);
							continue;
						}
					}
					

					if (event.isStartElement()) {
						if (event.asStartElement().getName().getLocalPart()
								.equals(TITLE)) {
							String title = "";
							while(!event.isEndElement()){
								event = eventReader.nextEvent();
								title += event.isEndElement()?"":event.asCharacters().getData();
							}
							bug.setTitle(title);
							continue;
						}
					}
					
					
					if (event.isStartElement()) {
						if (event.asStartElement().getName().getLocalPart()
								.equals(SEVERITY)) {
							String severity = "";
							while(!event.isEndElement()){
								event = eventReader.nextEvent();
								severity += event.isEndElement()?"":event.asCharacters().getData();
							}
							bug.setType(severity);
							continue;
						}
					}
					
					if (event.isStartElement()) {
						if (event.asStartElement().getName().getLocalPart()
								.equals(MERGEID)) {
							String mergeid = "";
							while(!event.isEndElement()){
								event = eventReader.nextEvent();
								mergeid += event.isEndElement()?"":event.asCharacters().getData();
							}
							bug.setMergeID(mergeid);
							continue;
						}
					}

					if (event.asStartElement().getName().getLocalPart()
							.equals(CLOSEDON)) {
						while(!event.isEndElement()){
							event = eventReader.nextEvent();
							bug.setClosedOn(event.isEndElement()?"":event.asCharacters().getData());
						}
						continue;
					}

					if (event.isStartElement()) {
						if (event.asStartElement().getName().getLocalPart()
								.equals(PRIORITY)) {
							String priority = "";
							while(!event.isEndElement()){
								event = eventReader.nextEvent();
								priority += event.isEndElement()?"":event.asCharacters().getData();
							}
							bug.setPriority(priority);
							continue;
						}
					}

					
					if (event.isStartElement()) {
						if (event.asStartElement().getName().getLocalPart()
								.equals(COMPONENT)) {
							String component = "";
							while(!event.isEndElement()){
								event = eventReader.nextEvent();
								component += event.isEndElement()?"":event.asCharacters().getData();
							}
							bug.setComponent(component);
							continue;
						}
					}
					
					
					if (event.isStartElement()) {
						if (event.asStartElement().getName().getLocalPart()
								.equals(VERSION)) {
							String version = "";
							while(!event.isEndElement()){
								event = eventReader.nextEvent();
								version += event.isEndElement()?"":event.asCharacters().getData();
							}
							bug.setVersion(version);
							continue;
						}
					}

					
					if (event.isStartElement()) {
						if (event.asStartElement().getName().getLocalPart()
								.equals(RESOLUTION)) {
							String status = "";
							while(!event.isEndElement()){
								event = eventReader.nextEvent();
								status += event.isEndElement()?"":event.asCharacters().getData();
							}
							bug.setStatus(status);
							continue;
						}
					}


					if (event.asStartElement().getName().getLocalPart()
							.equals(OPENEDDATE)) {
						while(!event.isEndElement()){
							event = eventReader.nextEvent();
							bug.setOpenedDate(event.isEndElement()?"":event.asCharacters().getData());
						}
						continue;
					}
					

					
					if (event.isStartElement()) {
						if (event.asStartElement().getName().getLocalPart()
								.equals(PRODUCT)) {
							String product = "";
							while(!event.isEndElement()){
								event = eventReader.nextEvent();
								product += event.isEndElement()?"":event.asCharacters().getData();
							}
							bug.setProduct(product);
							continue;
						}
					}

					
					if (event.asStartElement().getName().getLocalPart()
							.equals(THETEXT) && !desc) {
						String text = "";
						while(!event.isEndElement()){
							event = eventReader.nextEvent();
							text += event.isEndElement()?"":event.asCharacters().getData();
						}
						
						bug.setDescription(text);
						desc = true;
						continue;
					}
					
					
					if (event.asStartElement().getName().getLocalPart()
							.equals(THETEXT) && desc) {
						String text = "";
						while(!event.isEndElement()){
							event = eventReader.nextEvent();
							text += event.isEndElement()?"":event.asCharacters().getData();
						}
						
						comment = new Comment();
						comment.setWhat(text);
						bug.comments.add(comment);
						
						continue;
					}
					

				}
				if (event.isEndElement()) {
					EndElement endElement = event.asEndElement();
					if (endElement.getName().getLocalPart().equals(BUG)) {
						bugs.add(bug);
					}
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		return bugs;
	}

	public double getPriorityNumber(String priority){

		if(priority.equals(""))
			return 0;

		return priorityMap.get(priority);
	}
	
	public double getVersionNumber(String version){
		
		return 0;
	}
	
	public Vector<Document> getBugs(){

		Vector<Document> results = new Vector<Document>();
		List<Bug> repo = readConfig("mozilla_issues.xml");


		for (Bug item : repo) {
			/*if((item.getTitle().contains("message")&&item.getStatus().equals("Duplicate"))||
					item.getDescription().contains("message")&&item.getStatus().equals("Duplicate"))
				System.out.println("bug :"+item.getBugid() +" master: "+item.getMergeID());*/

			if(!item.getBugid().equals("")){
				results.add(new Document(item.getDescription(), item.getTitle(),
						Integer.parseInt(item.getBugid()),item.getStatus(),item.getStars(),item.getComponent(),
						item.getType(), item.getPriority(),getPriorityNumber(item.getPriority()),getVersionNumber(item.getVersion()),
						item.getVersion(), item.getMergeID().equals("")?0:Integer.parseInt(item.getMergeID()),item.getOpenedDate(),
						item.getClosedOn(), item.getProduct(),"yyyy-MM-dd HH:mm:ss Z",item.comments));
			}

			//Close the output stream
		}

		return results;
	}

	/*public String getBug(int index){
		
		EclipseXmlParser read = new EclipseXmlParser();
		List<Bug> bugs = new ArrayList<Bug>();
		Bug bug = read.readConfig(");
		
		String result = "Bug ID: "+bug.getBugid()+"\n"+
				"Status: "+bug.getStatus()+"\n" +
				"State: "+bug.getState()+"\n" +
				"Product: "+bug.getProduct()+"\n"+
				"Component: "+bug.getComponent()+"\n"+
				"Version: "+bug.getVersion()+"\n"+
				"Importance: "+bug.getPriority()+"\n"+
				"Report Date: "+bug.getOpenedDate()+"\n"+
				"Depends on:" + bug.getMergeID()+"\n"+
				"Type :" + bug.getType()+"\n"+
				"Short Description: "+bug.getTitle()+"\n"+
				"Long Description: "+bug.getDescription()+"\n";
		
		return result;
	}*/
	
	
	public void printSet(String name ,Set<String> hs){
		
		System.out.println(name+" :\n");
		
		Iterator iterator = hs.iterator();  
        
		while (iterator.hasNext()){  
		        Object val = iterator.next();  
		        System.out.print(val +"     ");  
		}
		System.out.println();
		System.out.println();
	}
	

	public static void main(String[] args) {

		MozillaXmlParser read = new MozillaXmlParser();
		List<Bug> bugs  = read.readConfig("mozilla_issues.xml");
		//List<EclipseBug> bugs = new ArrayList<EclipseBug>();
		
		Set<String> statusSet = new HashSet<String>();
		Set<String> stateSet = new HashSet<String>();
		Set<String> productSet = new HashSet<String>();
		Set<String> componentSet = new HashSet<String>();
		Set<String> versionSet = new HashSet<String>();
		Set<String> prioritySet = new HashSet<String>();
		Set<String> typeSet = new HashSet<String>();
		
		
			
		for(Bug bug:bugs){
			//System.out.println(bug.getOpenedDate());
			//read.readConfig(bug.getClosedOn());
			statusSet.add(bug.getStatus());
			stateSet.add(bug.getState());
			productSet.add(bug.getProduct());
			componentSet.add(bug.getComponent());
			versionSet.add(bug.getVersion());
			prioritySet.add(bug.getPriority());
			typeSet.add(bug.getType());
		}

		read.printSet("status",statusSet);
		read.printSet("state", stateSet);
		read.printSet("product", productSet);
		read.printSet("component", componentSet);
		read.printSet("version", versionSet);
		read.printSet("priority", prioritySet);
		read.printSet("type", typeSet);
		
		
	/*	for(int i=1;i<=1998;i++){
			System.out.println("reading file "+i);
			bugs.addAll(0, read.readConfig("xmlFiles//"+i+".xml")); 
		}*/

		//read.bugWrite(bugs);

	}
}