
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

public class Candidate implements Comparable<Candidate>{
	
	int id1;
	int id2;
	double value;
	String label;
	int index;
	
	public Candidate(int input_id1, int input_id2, double input_value, String input_label){
		
		id1 = input_id1;
		id2 = input_id2;
		value = input_value;
		label = input_label;
		//index = input_index;
		
	}
	

	@Override
	public int compareTo(Candidate o) {
		// TODO Auto-generated method stub
		if(value < o.value)
			return 1;
		
		if(o.value < value)
			return -1;
		
		return 0;
	}

}
