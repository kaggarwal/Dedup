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

import java.util.Arrays;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Util {

	public static Vector<String> get_n_grams(String text, int n){

		Vector<String> result = new Vector<String>();
		String patt = "[a-zA-Z]+";
		for(int i=1;i<n;i++)
			patt+= "\\s[a-zA-Z]+";

		Pattern pattern = Pattern.compile(patt); // The regex pattern to use

		Matcher matcher = pattern.matcher(text);   
		int spaceIndex = 0;
		while(matcher.find(spaceIndex)){
			spaceIndex = text.indexOf(" ",spaceIndex)+1;
			result.add(matcher.group());
			if(spaceIndex == 0)
				break;
		}

		return result;
	}
}
