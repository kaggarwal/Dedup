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

import java.util.Vector;

public class Bug {
	private String bugid = "";
	private String title = "";
	private String status = "";
	private String owner = "";
	private String closedOn = "";
	private String type = "";
	private String priority = "";
	private String component = "";
	private int stars;
	private String reportedBy = "";
	private String openedDate = "";
	private String description = "";
	private String author = "";
	private String version = "";
	private int comentCounter = 0;
	private String mergeID = "";
	private String state = "";
	private double priorityNumber = 0;
	private double versionNumber = 0;
	private String product = "";
	
	private int commentAuthorCounter = 0;
	public Vector<Comment> comments;
	
	public Bug() {
		comments = new Vector<Comment>();
	}
	
	@Override
	public String toString() {
		
		String result = "bug [bugid=" + bugid + ", title=" + title + ", status="
				+ status + ", owner =" + owner + ", closedOn=" + closedOn + 
				", type =" + type + ", priority =" + priority +
				", component =" + component + ", stars = " + stars + 
				", reportedBy = " + reportedBy + ", openedDate = " + openedDate + 
				", description = " + description + ", \n";
		
		for(Comment comment:comments){
			result += comment.toString() +"\n";
		}
		
		result += "] \n";
		result += "*********************************** \n";
		
		return result;
	}
	

	public String getBugid() {
		return bugid;
	}

	public void setBugid(String bugid) {
		this.bugid += bugid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title += title;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status += status;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner += owner;
	}

	public String getClosedOn() {
		return closedOn;
	}

	public void setClosedOn(String closedOn) {
		this.closedOn += closedOn;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type += type;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority += priority;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component += component;
	}

	public int getStars() {
		return stars;
	}

	public void setStars(String stars) {
		
		if(stars.equals("")){
			this.stars = 0;
			return;
		}
		if(Character.isDigit(stars.charAt(0))){
			this.stars = Integer.parseInt(stars);
		}
	}

	public String getReportedBy() {
		return reportedBy;
	}

	public void setReportedBy(String reportedBy) {
		this.reportedBy += reportedBy;
	}

	public String getOpenedDate() {
		return openedDate;
	}

	public void setOpenedDate(String openedDate) {
		this.openedDate += openedDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description += description;
	}

	public int getComentCounter() {
		return comentCounter;
	}

	public void setComentCounter(int comentCounter) {
		this.comentCounter = comentCounter;
	}

	public int getCommentAuthorCounter() {
		return commentAuthorCounter;
	}

	public void setCommentAuthorCounter(int commentAuthorCounter) {
		this.commentAuthorCounter = commentAuthorCounter;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getMergeID() {
		return mergeID;
	}

	public void setMergeID(String mergeID) {
		this.mergeID = mergeID;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
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