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
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.xml.soap.Text;

public class Preprocessing {


	public String removeJunkChars(String input){

		String result = "";
		result = input.replaceAll("Q'E", "'");
		result = result.replaceAll("Q\"E", "\"");
		result = result.replaceAll("Q>E", ">");
		result = result.replaceAll("Q<E", "<");

		return result;
	}

	public Vector<Document> removeAllJunkChars(Vector<Document> input){

		Vector<Document> result = new Vector<Document>();
		int counter = 0;
		for(Document tf: input){
			
			
			String newDesc = removeJunkChars(tf.getDescription());
			String newTitle = removeJunkChars(tf.getTitle());
			tf.setDescription(newDesc);
			tf.setTitle(newTitle);
			result.add(tf);

		}

		return result;
	}

	public Vector<Document> removeJunkDocuments(Vector<Document> inputDocuments){

		Map<Integer,Document> idDocMap = new Corpus(inputDocuments).getIdDocMap();
		Vector<Document> result = new Vector<Document>();
		Vector<Integer> junks = new Vector<Integer>();

		for(Document doc: inputDocuments){

			if(doc.getStatus().toLowerCase().equals("duplicate") && !idDocMap.containsKey(doc.getMergeID()))
				junks.add(doc.getBugID());
		}
		int counter = 0;
		for(Document doc:inputDocuments){
			

			if(junks.contains(doc.getBugID()))
				continue;

			if(doc.getBugID() == 0)
				continue;

			if(doc.getStatus().toLowerCase().equals("duplicate")){

				if(doc.getMergeID() == 0)
					continue;

				Document master = idDocMap.get(doc.getMergeID());

				while(master.getStatus().toLowerCase().equals("duplicate") && master.getMergeID() != 0 && idDocMap.containsKey(master.getMergeID())){
					
					
					master = idDocMap.get(master.getMergeID());
					
					if(master.getMergeID() == doc.getBugID())
						break;
					
				}

				if(master.getStatus().toLowerCase().equals("duplicate") && (master.getMergeID() != doc.getBugID()))
					continue;

				//else{ 
				//	if(doc.compareTo(master) < 1)
				//		continue;

				//	else
						doc.setMasterID(master.getBugID());
			//	}
			}
			result.add(doc);
		}
		
		
		return result;
	}

	public String StopWordsRemoval(String input){

		Vector<String> stopWords = new Vector<String>();
		StringBuffer result = new StringBuffer("");
		StringBuffer output = new StringBuffer("");

		try{
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream("stop words.txt");
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				// Print the content on the console
				stopWords.add(strLine);
			}
			//Close the input stream
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

		Pattern p = Pattern.compile(" ");

		String[] inputString = p.split(input);
		Vector<String> outputString = new Vector<String>();

		for(String word: inputString){
			if(!stopWords.contains(word.toLowerCase()))
				outputString.add(word);
		}

		for(String s:outputString)
			output.append(" "+s);
		
		p = Pattern.compile("\\W");
		//p = Pattern.compile("[^a-zA-Z0-9\\-\\._\\/]");
		//p = Pattern.compile("[\\n\\[\\]\\,\\{\\}\\\"]");
		
		inputString = p.split(output);

		for(String word: inputString){
			if(!stopWords.contains(word.toLowerCase()))
				result.append(" "+word);
		}

		return result.toString();
	}

	public Vector<Document> allStopWordsRemoval(Vector<Document> inputTextFields){

		Vector<Document> results = new Vector<Document>();
		int counter = 0;
		for(Document tf:inputTextFields){

			String newDescription = StopWordsRemoval(tf.getDescription());
			String newTitle = StopWordsRemoval(tf.getTitle());
			tf.setDescription(newDescription);
			tf.setTitle(newTitle);
			results.add(tf);
		}
		return results;
	}

	public Vector<Document> stemmingAll(Vector<Document> inputTextFields){

		Vector<Document> results = new Vector<Document>();

		for(Document tf:inputTextFields){

			Pattern p = Pattern.compile("[^A-Za-z]");
			String[] splittedDesc = p.split(tf.getDescription());
			String newDescription = "";
			for(String descWord: splittedDesc)
				newDescription += " "+Stemmer.stem(descWord);

			String[] splittedTitle = p.split(tf.getTitle());
			String newTitle = "";
			for(String titleWord: splittedTitle)
				newTitle += " "+Stemmer.stem(titleWord);

			tf.setDescription(newDescription);
			tf.setTitle(newTitle);

			results.add(tf);
		}

		return results;
	}

	public Vector<Document> process(Vector<Document> input, boolean stemming){

		Vector<Document> result = new Vector<Document>();
		input = removeJunkDocuments(input);
		input = removeAllJunkChars(input);
		input = allStopWordsRemoval(input);
		if(stemming){
			input = stemmingAll(input);
			input = allStopWordsRemoval(input);
		}

		Pattern p = Pattern.compile("(\\s)+");
		int counter = 0;
		for(Document tf: input){

			
			StringBuffer newDesc = new StringBuffer("");
			StringBuffer newTitle = new StringBuffer("");
			String[] description = p.split(tf.getDescription());
			String[] title = p.split(tf.getTitle());

			for(String s: description)
				newDesc.append(s+" ");

			for(String s: title)
				newTitle.append(s+" ");

			tf.setTitle(newTitle.toString().toLowerCase());
			tf.setDescription(newDesc.toString().toLowerCase());

			result.add(tf);

		}
		return result;
	}

	public Vector<Document> stringDocumentProcess(Vector<Document> input, boolean stemming){

		Vector<Document> result = new Vector<Document>();
		input = removeAllJunkChars(input);
		input = allStopWordsRemoval(input);
		if(stemming){
			input = stemmingAll(input);
			input = allStopWordsRemoval(input);
		}

		Pattern p = Pattern.compile("(\\s)+");
		for(Document tf: input){

			String newDesc = "";
			String newTitle = "";
			String[] description = p.split(tf.getDescription());
			String[] title = p.split(tf.getTitle());

			for(String s: description)
				newDesc += s+" ";

			for(String s: title)
				newTitle += s+" ";

			tf.setTitle(newTitle.toLowerCase());
			tf.setDescription(newDesc.toLowerCase());

			result.add(tf);

		}
		return result;
	}

    /**
     * Returns a sublist of the input containing ALL "duplicate"
     * Documents from the input and the needed quantity of "non-duplictes"
     * to make the sublist contain dupRatio "duplicates"
     * Documents
     **/
    public Vector<Document> getDistributedSubset(Vector<Document> input, double dupRatio) {

        Vector<Document> nonDups = new Vector<Document>();
        Vector<Document> result = new Vector<Document>();

        for (Document doc : input) {
            (doc.getStatus().equalsIgnoreCase("duplicate") ? result : nonDups).add(doc);
        }

        int remaining = (int)Math.round(((1-dupRatio)/dupRatio)*result.size());
        Random rand = new Random();

        for (int i = 0; i < remaining; i++) {
            result.add(nonDups.get(rand.nextInt(nonDups.size())));
        }

        return result;
    }


	public static void main(String[] args) {

		Preprocessing prep = new Preprocessing();
		String result = prep.StopWordsRemoval("Mail Reader Message Refers:"+'\n'+
"http://www.openoffice.org/servlets/ReadMsg?list=dev&amp;msgNo=21247"+'\n'+
"User Forum Message Refers:"+'\n'+
"http://user.services.openoffice.org/en/forum/viewtopic.php?f=5&amp;t=1289"+'\n'+
""+'\n'+
"In your dialogue you mentions that Excel has a bug in its implementation of"+'\n'+
"30U/360, and states that Calc MUST be compatible with Excel.  Yes Excel has a"+'\n'+
"bug, and NO CALC is NOT compatible with Excel.  I have attached a spreadsheet"+'\n'+
"* which compares Excel and Calc against 3OU/360 against a reasonable test cover"+'\n'+
"showing how Excel deviates from 3OU/360 and Calc from Excel"+'\n'+
"* Provides Basic functions which implements"+'\n'+
"  * The 3OU/360 Algo"+'\n'+
"  * The Excel Algo showing the bug and why it gets the wrong answer"+'\n'+
"  * The Calc Algo showing its bug and why it disagrees with Excel."+'\n'+
""+'\n'+
"The code in sc/source/core/tool/interpr2.cxx would be trivial to make compatible"+'\n'+
"with Excel, so it seems a shame for this incompatibility to remain.  Do you want"+'\n'+
"me to propose a patch?");
		System.out.println(result);
		/*Preprocessing prep = new Preprocessing();
		AndroidXmlParser read = new AndroidXmlParser();

		Integer[] arr = {37520,37427,37198,37018,36995,36893,36870,36791,36747,36689,36680,36654,36644,34880,33788,37197,37019,36996,36987,36979};
		Vector<Integer> unitTestIds = new Vector<Integer>(Arrays.asList(arr));
		Vector<Document> textFields = read.getUnitTest(unitTestIds);

		Vector<Document> corpus = prep.process(textFields);
		Vector<Document> preprocessedText = prep.process(textFields);*/

	}

}
