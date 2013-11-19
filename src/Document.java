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

import java.awt.im.InputContext;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.InputVerifier;
import javax.swing.text.Utilities;


public class Document implements Comparable<Document>{

	private String description;
	private String title;
	private String status;
	private int bugID;
	public double[] weights;
	private String component;
	private String priority;
	private String type;
	private int stars;
	private String version;
	private int mergeID;
	private int masterID;
	private Date openDate;
	private String dateFormat;
	private Date closeDate;
	private double priorityNumber;
	private double versionNumber;
	private String product;
	Map<String,Integer> unigram_descriptionWordFrequencies = new HashMap<String, Integer>();
	Map<String,Integer> unigram_titlenWordFrequencies = new HashMap<String, Integer>();
	Map<String,Integer> bigram_descriptionWordFrequencies = new HashMap<String, Integer>();
	Map<String,Integer> bigram_titlenWordFrequencies = new HashMap<String, Integer>();
	Vector<Comment> comments = new Vector<Comment>();

	public Document(String inputDesc, String inputTitle, int inputId, String inputStatus,
			int input_stars, String input_component, String input_type , String input_priority, 
			double inputPriorityNumber, double inputVersionNumber, String input_version,int merge_id,
			String openDate, String closeDate, String inputProduct, String inputDateFormat, Vector<Comment> input_comments){
		setDateFormat(inputDateFormat);
		setMergeID(merge_id);
		setStars(input_stars);
		setComponent(input_component);
		setType(input_type);
		setPriority(input_priority);
		setVersion(input_version);
		setDescription(inputDesc);
		setTitle(inputTitle);
		setBugID(inputId);
		setStatus(inputStatus);
		setOpenDate(openDate);
		setCloseDate(closeDate);
		setPriorityNumber(inputPriorityNumber);
		setVersionNumber(inputVersionNumber);
		setProduct(inputProduct);
		comments = input_comments;
	}

	public Document(String inputDesc, String inputTitle){

		setDescription(inputTitle);
		setTitle(inputTitle);
		
	}

	private void insertWordInMap(Map<String,Integer> wordFreqMap,String word){

		if(wordFreqMap.containsKey(word))
			wordFreqMap.put(word, wordFreqMap.get(word)+1);

		else
			wordFreqMap.put(word,1);
	}

	public void setOpenDate(String openDate) {

		if(openDate.equals(""))
			this.openDate = new Date();
		else
			this.openDate = getDate(openDate);
	}

	public Date getOpenDate() {
		return openDate;
	}

	public void setCloseDate(String closeDate) {
		if(closeDate.equals(""))
			this.closeDate = new Date();
		else
			this.closeDate = getDate(closeDate);
	}

	public Date getCloseDate() {
		return closeDate;
	}

	private Date getDate(String inputDate){

		DateFormat sdf = new SimpleDateFormat(this.dateFormat);
		Date date = new Date();
		try {
			date = sdf.parse(inputDate);
			String GMTTime = date.toGMTString();
			date = new Date(GMTTime);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		
		this.description = description.toLowerCase();
		unigram_descriptionWordFrequencies = new HashMap<String,Integer>();
		bigram_descriptionWordFrequencies = new HashMap<String,Integer>();
		
		for(String word: Util.get_n_grams(description, 1)){

			insertWordInMap(unigram_descriptionWordFrequencies, word);
		}

		for(String word: Util.get_n_grams(description, 2)){

			insertWordInMap(bigram_descriptionWordFrequencies, word);
		}

	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		
		this.title = title.toLowerCase();
		
		unigram_titlenWordFrequencies = new HashMap<String,Integer>();
		bigram_titlenWordFrequencies = new HashMap<String,Integer>();
		
		for(String word: Util.get_n_grams(title, 1)){

			insertWordInMap(unigram_titlenWordFrequencies, word);
		}

		for(String word: Util.get_n_grams(title, 2)){

			insertWordInMap(bigram_titlenWordFrequencies, word);
		}
	}
	public int getBugID() {
		return bugID;
	}
	public void setBugID(int bugID) {
		this.bugID = bugID;
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getMergeID() {
		return mergeID;
	}

	public void setMergeID(int mergeID) {
		this.mergeID = mergeID;
	}

	public int getMasterID() {
		return masterID;
	}

	public void setMasterID(int masterID) {
		this.masterID = masterID;
	}

	public void setDateFormat(String dateFormat){
		this.dateFormat = dateFormat;
	}
	
	public String getDateFormat(){
		return dateFormat;
	}
	

	@Override
	public int compareTo(Document doc2) {
		// TODO Auto-generated method stub
		if(this.getOpenDate().after(doc2.getOpenDate()))
			return 1;
		else if(this.getOpenDate().before(doc2.getOpenDate()))
			return -1;

		return 0;
	}

	public double getPriorityNumber() {
		return priorityNumber;
	}

	public void setPriorityNumber(double priorityNumber) {
		this.priorityNumber = priorityNumber;
	}

	public double getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(double versionNumber) {
		this.versionNumber = versionNumber;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}
}
