/*
 * Description: this is the defining class for an SCP. this class will be used to make an SCP list in the scpList class and MainGUI class
 * Author: Jordan Fitzenberger
 * Date: 10/21/2021
 */
package scpPackage;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
public class scp {
	//
	private String item;// the numerical name of the SCP instance. example(SCP-087)
	private int rating;//the rating of the SCPs article on the web site. this is determined by user voting.
	private String level;// the assigned danger class renamed level here to avoid programming confusion, options include: Safe, Euclid, Keter, Thaumiel, Neutralized, Apollyon, Archon, Explained, Esoteric, Decommissioned, Anomalous Safe
	private String content;//the parsed content from the original web site article
	private ArrayList <PicObj> picArray;// each SCP instance saves an array of pictures (if any) that are part of their original article online
	
	public static String fs = FileSystems.getDefault().getSeparator();//saves the default file separator for the computer
	public static String curDir = System.getProperty("user.dir");//saves the current directory of the program so it can reference outside files in the "SCPs" directory
	
	//default constructor, initializes all variables the scp possess.
	public scp() {
		item = "";
		rating = 0;
		level = "";
		content = "";
		picArray = new ArrayList<PicObj>();

	}
	
	//alternate constructor, initializes all the variables the scp posses. the item of the scp is initialized with the given string
	public scp(String item) {
		this.item = item;
		rating = 0;
		level = "";
		content = "";
		picArray = new ArrayList<PicObj>();

	}
	
	public ArrayList<PicObj> getPicArray() {//returns ArrayList of object PicObj
		return picArray;
	}
	public void setPicArray(ArrayList<PicObj> picArray) {//sets the ArrayList to the given ArrayList for its SCP instance
		this.picArray = picArray;
	}
		

	//getters and setters
	public String getItem() {//returns the numerical name of the SCP instance
		return item;
	}

	public void setItem(String strLine) {//sets the numerical name of the SCP instance with the given string
		this.item = strLine;
	}
	
	public int getRating() {//returns the saved rating of the SCP instance
		return rating;
	}
	
	public void setRating(int rating) {//sets the rating of the SCP if the original scan of the SCP article content did not find it, with the provided integer
		this.rating = rating;
	}
	
	public String getLevel() {//returns the danger class (level) of the SCP instance
		return level;
	}

	public void setLevel(String level) {//sets the danger class (level) of the SCP instance to the given string. this is in case the original scan of the article was not able to find it
		this.level = level;
	}

	public String getContent() {//returns the article content of the SCP instance.
		return content;
	}

	public void setContent(String content) {//sets the content of the SCP instance.
		content = parseEnd(content);//gets rid of the tail end of the content like annotations to other SCP pages and citations
		this.content = content;
	}

	
public void getInfo() throws IOException {//saves the contents of multiple directories into memory for each SCP instance
	//System.setProperty("file.encoding","UTF-8");
	
	String item = this.item;//saves current SCP item (numerical name ex.SCP-087)
	String half = item.substring(4 );//saves only the numbers of the numerical name for further use
	File newFile = new File(curDir+fs+"SCPs"+fs+"entries"+fs+half+".txt");//gets the SCP instance text file from the entries directory. it contains the scp numerical name, the rating, and its content.
	File PicDir = new File(curDir+fs+"SCPs"+fs+"pictures"+fs+half+fs);//saves the directory for the SCP instances pictures
	File capDir = new File(curDir+fs+"SCPs"+fs+"captions"+fs+half+fs);//saves the directory for the SCP instances captions for each picture
	
	
	if(newFile.length()!=0) {//some scp pages will enter no information in to the text files, this if statement prevents crashing in those cases
	try {
		//System.out.println(curDir+fs+"SCPs"+fs+"entries"+fs+half+".txt");//print to console for troubleshooting
		
		String ratingl1 = Files.readAllLines(Paths.get(curDir+fs+"SCPs"+fs+"entries"+fs+half+".txt"), StandardCharsets.UTF_8).get(0);//saves the rating of the SCP from its text file
		if(newFile.length()!=1) {//only executes if there are more than 1 line in the text files in case of errors in the text files
		String contentl2 = Files.readAllLines(Paths.get(curDir+fs+"SCPs"+fs+"entries"+fs+half+".txt"), StandardCharsets.UTF_8).get(1);//saves the content of the SCP from its text file
		if ( ratingl1.equals("+"))
		  { //gets rid of "+" at the beginning of the rating if its there, if not then "-" will be parsed fine
			ratingl1 = ratingl1.substring(1 );
		  }
		  int ratingParsed = Integer.parseInt(ratingl1);
		  this.setRating(ratingParsed);//sets the rating for the SCP instance in memory
		  if (ratingParsed==-999) {//stops scp-001 from getting its content incorrectly parsed due to not having an easily accessible rating on the web page
			  
			  //here should be the calling of the method that escapes any characters 

			  this.setContent(escapeMetaCharacters(contentl2));//sets the content for the SCP instance in memory
		  }
		  else {  //sets the content for the SCP instance in memory after removing the rating from the beginning of the string
			  this.setContent(escapeMetaCharacters(parseRating(contentl2)));
		  }
		  this.setLevel(parseLevel(contentl2));//sets the level for the SCP instance in memory after removing the rest of the content from the string
		
		}
		//insert data into linked list
		PicObj myPic;//creates temporary PicObj instance for generating the ArrayList of PicObj's
		int captionCount=0;//starts the number of captions at 0, each new caption is represented by an incrementing number, this is so it can easily line up with pictures in the pictures directory
		BufferedReader br;// creates a bufferReader to read each caption text file
    	String caption, captionOut = "";//initialize both strings, the first to save the line being read from the buffer reader, the next to save it to a variable to create a PicObj instance below
    	

    	//System.out.println(PicDir.toString());//print to console for troubleshooting
    	//for each file in the picture directory, the corresponding caption text file (based on where in the directory) in the caption directory is read into memory and joined in a PicObj constructor, which that instance is added into the ArrayList of PicObj's for the appropriate SCp instance
		
    	for (File file : PicDir.listFiles()) {
    		String paddedCaptionCount = SCPUtils.padSCP(captionCount);
			br = new BufferedReader(new FileReader(capDir +fs+ paddedCaptionCount +".txt"));
			try {
		    	while((caption = br.readLine()) != null){
		    		captionOut = caption;
		    	}
		    	}catch (IOException e) {
		    		e.printStackTrace();
		    	}
			myPic = new PicObj(file,captionOut);
			
			try {writePicsDB(half,myPic);} catch (ClassNotFoundException e) {e.printStackTrace();}
			

			captionCount++;
			br.close();
		}//end for loop
	}//end outer try
	catch (IOException e) { e.printStackTrace();}
	}//end if

	}//end getInfo

public static void writePicsDB(String name,PicObj myPicObj) throws ClassNotFoundException, FileNotFoundException {
	 Statement statement = null;
     ResultSet resultSet = null;
     FileInputStream myInputStream = new FileInputStream(new File(myPicObj.getPicture().toString()));
     int nameInt = Integer.parseInt(name);//gets scp item name and parses out the "SCP-" just leaving the integer.

     String inputString = "insert into pictures values ( ?, ?, ?, ?);";
	try {

		Connection connect = SCPUtils.connect();

	    // Do something with the Connection
	    PreparedStatement prepStmt = connect.prepareStatement(inputString);
	    prepStmt.setNull(1, Types.INTEGER);
	    prepStmt.setInt(2, nameInt);
	    prepStmt.setBlob(3, myInputStream);
	    String outCaption = myPicObj.getCaption();
	    prepStmt.setString(4, outCaption);

	    prepStmt.execute();
	    connect.close();
	} catch (SQLException ex) {
	    // handle any errors
		ex.printStackTrace();
	}
}
public static String parseRating(String text) {//finds and deletes the "rating..." text at the beginning of almost every content string of an scp page
    Pattern pattern = Pattern.compile(".((\\+|\\-)(\\d+))");
    Matcher matcher = pattern.matcher(text);
    // Check all occurrences
    boolean artBreak = true;
     while (matcher.find() && artBreak) {// this artificially stops the loop after finding the first instance. this is because we can assume that the text "rating ..." only occurs at the start of the string
        String out= text.substring(matcher.end()+4 );
        artBreak = false;
       return out;
    }
   return "no pattern found.";
}
public static String parseLevel(String text) {// finds the danger class (level) from the given string
	Pattern pattern = Pattern.compile("(Object Class:|CONTAINMENT CLASS:)[ \\t]((Safe)|(Euclid)|(Keter)|(Thaumiel)|(Neutralized)|(Apollyon)|(Archon)|(Explained)|(Esoteric)|(Decommissioned)|(Anomalous Safe))");
	Matcher matcher = pattern.matcher(text);
	boolean artBreak = true;
	 while(matcher.find()&& artBreak) {// this artificially stops the loop after finding the first instance. this is because we can assume that the text "Object Class:" or "CONTAINMENT CLASS:" only occurs at the start of the string
		 String out = matcher.group(2);//saves the danger class (level), not the first part of the match which is "Object Class:" or "CONTAINMENT CLASS:"
		 artBreak = false;
	     return out;
	 }
	 return "SCP class not found";
}
public static String parseEnd(String text) {//finds and remove the end of the article, so while the program is being used to browse articles, there isn't anything that isn't part of the main article
	Pattern pattern = Pattern.compile("(«)|(‡)");
	Matcher matcher = pattern.matcher(text);
	boolean artBreak = true;
	   while (matcher.find() && artBreak) {// this artificially stops the loop after finding the first instance. this is because we can assume that the text "rating ..." only occurs at the start of the string
	      String out= text.substring(0,matcher.end()-1); 
	      artBreak = false;
	      return out;
	   }
	   return text;
	}

public String escapeMetaCharacters(String inputString){
    final String[] metaCharacters = {"\\","'","\"","%","_"};

    for (int i = 0 ; i < metaCharacters.length ; i++){
        if(inputString.contains(metaCharacters[i])){
            inputString = inputString.replace(metaCharacters[i],"\\"+metaCharacters[i]);
        }
    }
    return inputString;
}
}//end class


