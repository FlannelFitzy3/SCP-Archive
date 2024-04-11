/*
 * Description:this class defines the scpList, it also contains some of the most important methods for the functioning of the program such as downloading and assembling the scpList
 * Author: Jordan Fitzenberger
 * Date: 10/21/2021
 */
package scpPackage;

import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;

//THIS IS THE COPY

//THIS IS THE COPY
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.security.auth.Subject;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



import java.io.File;
import java.io.FileFilter;

public class scpList implements Runnable {

	private static ArrayList<scp> scps;//arrayList of scp instances that will be manipulated and created using this class
	private int index1;//used to keep track of which instance in the array list the scp instance is currently on due to their numerical names
	private static double index2 = 0;//used in the process of updating the progress bar value
	private static final PBar pBar = new PBar();//initialized custom progress bar, declared here and as static to minimize errors during the multi-threaded work in the run method
	public static String curDir = System.getProperty("user.dir");//saves the current directory of the program so it can reference outside files in the "SCPs" directory
	public static String fs = FileSystems.getDefault().getSeparator();//saves the default file separator for the computer
	
	//default constructor
	public scpList() {
		pBar.setVisibility(false);//sets the progress bar frame to be invisible because when it is initialized outside a constructor, it would appear too early
		scps = new ArrayList<scp>();//initializes an Array List of type scp
	}
	//alt constructor
	public scpList(int index1) {//creates scpList instance and saves the index of the given integer for later use
		this.index1=index1;
		scps = new ArrayList<scp>();//initializes an Array List of type scp
	}
	

public List<scp> getList() {//returns the Array list of scps
	return scps;
}
public scp get(int i) {//returns a specific scp instances inside of the scpList
	scp scptemp = scps.get(i-1);
	return scptemp;
}
public static void initWeb() {

}
	public void web() throws IOException //deletes all local files in the SCP directory and rebuilds it
{
		pBar.setVisibility(true);//makes the progress bar frame visible
		File file1 = new File(curDir+fs+"SCPs"+fs+"pictures"+fs);//starting directory for deleting subdirectories contents
		//finds the number of sub directories in the pictures directory, so it can easily change when more SCPs are added later
		File[] files1 = file1.listFiles(new FileFilter() 
		{
		public boolean accept(File f) 
			{
		        return (f.isDirectory() || f.isFile());
		    }
		});
		for(int i=1;i<files1.length+1;i++ ) //iterates through each sub directory in pictures, padding the right amount of "0"s for standard SCP number scheme
		{
			String dirstr;
			if(i<10) {		 dirstr = "00"+ i;}
			else if (i<100) {dirstr = "0"+ i;}
			else {			 dirstr = Integer.toString(i);}

			File file2 = new File(curDir+fs+"SCPs"+fs+"pictures"+fs+dirstr+fs);//sets the current subdirectory to manipulate
			//System.out.println(file2.toString());
					for(File file: file2.listFiles()) //deletes each file in the sub directory so when the user requests to build the archive again, there are no duplicate files
					    if (!file.isDirectory()) 
					        file.delete();
			File file3 = new File(curDir+fs+"SCPs"+fs+"captions"+fs+dirstr+fs);
					for(File file: file3.listFiles()) //deletes each file in the sub directory so when the user requests to build the archive again, there are no duplicate files
					    if (!file.isDirectory()) 
					        file.delete();
		}//end for
		
		int scpMax = SCPConstants.MAX_SCP_INDEX;//set to the current number of SCPs on the web site, this changes very rarely when they add more

		// create instance of each scp and adds them to the scpList, uses 8 threads to speed up rebuilding, more than 8 threads can cause issues such as the connection to the scp website to reset due to too many page requests
		ExecutorService executor = Executors.newFixedThreadPool(8);	
				for (int i =1; i <=scpMax; i++) //executes for every one of the 5999 scps currently on the web site
				{
					executor.execute(new scpList(i));//executes the run method with the given scp numeric name
				}
				executor.shutdown();//asks to stop the run method with all running threads in the thread pool
				
				try 
				{
				    if (!executor.awaitTermination(60, TimeUnit.MINUTES)) //unless the executor has shutdown already, after 20 minutes it will terminate remaining threads, this try/catch also prevents the "executor.shutdown()" in the for loop to execute too early
				    {
				        executor.shutdownNow();
				       
				    }
				} catch (InterruptedException ex) //print error to console
				{
				    System.out.println(ex);
				}
				renamePics();
				pBar.setVisibility(false);
}//end web

	@Override
	public synchronized void run() {//run method that is executed with each call of the executor in the thread pool from the web method

		 try { Thread.sleep(40); } catch(InterruptedException e) { System.out.println(e); }//thread sleeps for 40 milliseconds in order to not over burden the webserver
		Document doc1;//HTML document declaration for the Jsoup methods to utilize
		synchronized (this) {//Synchronized method to ensure that the updating of the progress bar is done sequentially 
			++index2;
			pBar.updateBar((int)index2);//updates the progress bar with a new value, its casted as an integer and not declared as one for more precise percentages being printed to the console when the lines below are uncommented
			//Double percentage = (index2/5999)*100;	
			//System.out.printf("%.2f%%     \r",percentage);
			//System.out.println(index2);
		}

		String strLine;//string for parsing the scp number back to a numerical name ex. SCP-087
		if(index1<10) {		  strLine = "00"+ index1;}//pads the string with "0"s to accommodate the intended/original naming scheme
		else if (index1<100) {strLine = "0"+ index1;}
		else {				  strLine = Integer.toString(index1);}
	  String name = "SCP-" + strLine;
	  scp tempSCP = new scp(name);//create new scp with the given string of its numerical name, this is to be used as an identifier later
	 System.out.println(strLine);
	  scps.add(tempSCP);//adds the new scp into the scpList
	  
	  try {
		tempSCP.getInfo();//executes the getInfo method for the specific scp (see getInfo method in the scp class)
	} catch (IOException e) {//print any errors to the console
		e.printStackTrace();
	}

	  //connect to web site
	  try {
		  doc1 = Jsoup.connect(("https://scp-wiki.wikidot.com/scp-"+strLine)).get();//connects to the specific webpage of that SCP
		  Element rating = doc1.select("span[class=number prw54353]").first();//gets scp rating from webpage
		  String str;//string used when manipulating the rating
			  if(rating == null) {//sets an uncommon rating for pages that a rating cannot be found, because it cannot be null
				  str = "-999";
			  }
			  else {//executes if the rating is not null
			  str = rating.text();// gets rating element and saves it in a variable as text
			  }
		  Elements content = doc1.select("#page-content");//selects and saves everything tagged with the "page-content" ID in the html code of the page
		  String strContent = content.text();//removes all html code from the content element and saves it as a usable string
		  
			  if (str.length()  != 0) 
			  {// some pages don't have the rating element of the page, so this statement works around the error this problem would produce
				  if ( str.equals("+"))
				  { //gets rid of "+" at the beginning of the rating if its there, if not then "-" will be parsed fine
					  str = str.substring(1 );
				  }
				  //parse the string to an integer and set the rating for the current scp
				  int ratingParsed = Integer.parseInt(str);
				  tempSCP.setRating(ratingParsed);
		
				  String level = scp.parseLevel(strContent);//extracts the danger class (level) and saves it as a string
				  DownloadImages(strLine);//downloads the pictures and their captions (if any) for the specific scp
				  
				  //save the rating, content, and danger class (level) on 3 seperate lines of its self named text file in the entries directory
				  String txtInput = str + "\n" + strContent + "\n" + level;
				  File file = new File (curDir+fs+"SCPs"+fs+"entries"+fs+strLine+".txt");
				  FileWriter fw = new FileWriter(file.getAbsoluteFile());
				  BufferedWriter bw = new BufferedWriter(fw);
				  bw.write(txtInput);
				  bw.close();
				  
			  }
		
		  else {System.out.println("no rating in page"); }//returns this string if no rating is found on the page
	  } catch (IOException e) {//catches errors while downloading content and pictures from the web site and saves them to a log named after the days date 
		  
						try {
							//gets an appropriate time stamp of the day to use as a file name
							long millis=System.currentTimeMillis();  
							java.sql.Date date=new java.sql.Date(millis);  
							String formattedString = date.toString();
							
							//if it doesn't already exist, a log file named as the days date is created
							File tempLog = new File(curDir+fs+"SCPs"+fs+"logs"+fs+formattedString+".txt");		
							if(!tempLog.exists()) {tempLog.createNewFile();}

                            //print the error stream to the days log file
							PrintStream out = new PrintStream(new FileOutputStream(tempLog, true));
							System.setErr(out);
							
						} catch (FileNotFoundException e1) {//if printing to error log doesn't work
							e1.printStackTrace();
						} catch (IOException e1) {// if create file doesn't work
							e1.printStackTrace();
						}		  
			e.printStackTrace();//also prints error out to the console
		}
	}//end run method
	
public void noWeb() throws IOException //executes when the user wants to build the archive into memory from local files
, ClassNotFoundException
{ 
	 Statement statement = null;
     ResultSet resultSet = null;
     boolean DBwrite = false;
	try {
		Connection connect = SCPUtils.connect();
    statement = connect.createStatement();
    resultSet = statement.executeQuery("select count(*) from instances;");
    int rowCount=0;
    while(resultSet.next()) {
    	rowCount = resultSet.getInt(1);
    }
    if(rowCount<SCPConstants.MAX_SCP_INDEX) {
    	DBwrite=true;
    }else {
    	 JOptionPane.showMessageDialog(null,"The maximum number of SCPs has already been reached.","Alert",JOptionPane.OK_OPTION);     
    }
	} catch (SQLException ex) {ex.printStackTrace();}
	if (DBwrite) {
		PBar DBPbar = new PBar();
		DBPbar.setMessage("		Uploading files to SCP database");
		DBPbar.setVisibility(true);

		String strLine;//allocates memory for the string that is to hold the current line in the buffer

		for ( int i = 0; i < SCPConstants.MAX_SCP_INDEX; i++ ) {

		  strLine = SCPUtils.padSCP( i );
		  String name = "SCP-" + strLine;//creates numerical name for the scp
		  scp tempSCP = new scp(name);//create new scp for each line in the file

			try {writeDB(tempSCP);} catch (Exception e) {e.printStackTrace();} //calls method to insert each scp into the SCP database, specifically the instances table.
			DBPbar.updateBar(++i);
			
		}
	}
		//for each scp in the scps array list, it will run the Get info method to set the rating and content


}//end noWeb
public static void getComponents(int i) {
	
}
public static void DownloadImages(String strLine) throws IOException // starts the downloading process and organization of pictures for the given scp
{
	        String strURL = "https://scp-wiki.wikidot.com/scp-"+strLine;//generate the correct URL for the scp
	        
	        Document document = Jsoup.connect(strURL).userAgent("Mozilla/5.0").timeout(10000).get();//connect to the given website using the URL string, uses FireFox Browser and times out after 10 seconds
	        Elements div = document.select("div[id=main-content]");// gets the main text and pictures from the page
	        Elements captionElement1 = div.select("div[class=scp-image-caption]");//saves the image caption for each image on the page in the main content
	        if(captionElement1.size() ==0) {// some of the image captions are under a different class element on the web page, so if the first class is not found, it will look for this other one as well.
	        	captionElement1 = div.select("div[class=scp-image-caption show]");
	        }
	        Elements imageElements = div.select("img[class=image]");//gets each image from the main page 
	        int index =0;//index for the number of captions in the page
	        int imageCount=0;//index for the number of pictures in the page

	        
	        for(Element imageElement : imageElements){//iterate over each image

	            String strImageURL = imageElement.attr("abs:src");//gets the absolute source URL for the website that is hosting the image
	            String caption ="";//initialize the caption
	            if(captionElement1.text()!="") {//if the pictures have a caption
	            	if(captionElement1.size()<imageElements.size()) {// if the number of captions is less than the number of pictures	     
	            		caption = "none";//set to none to differentiate from when the caption is simply blank, used in the helper method
	            	}//end inner if
	            	else {
	            caption = captionElement1.get(index).text();//saves the caption element at that index to a string
	            index++;//index increments for the next number
	            	}//end else
	            }//end outer if
	           if (caption=="") {// if the caption is blank, it will be assigned the numerical name as its caption
	        	   caption = "SCP-"+strLine;
	           }
	           
	            downloadImageHelper(strImageURL, strLine, caption, imageCount); //uses info generated from this method, and passes it to the helper method to break up the complexity
	            imageCount++;//image count increments 
	    }//end for loop

}

private static void downloadImageHelper(String strImageURL,String strLine, String caption, int imageCount) throws IOException//prepares pictures and their captions to then be saved to the appropriate files
{
	
	String picFolder = curDir+fs+"SCPs"+fs+"pictures"+fs+strLine;//generates the filepath to that specific scp
	String strImageName = strImageURL.substring( strImageURL.lastIndexOf("/") + 1 );//gets the image name at the end of its URL
	if(strImageName.contains("?")) {// if the image name contains a questionmark (like scp 1045 does) it removes it and all thats after it to generate an appropriate name
	strImageName = strImageName.substring(0,strImageName.lastIndexOf("?"));
	}
	if(strImageName.contains("%")) {
		strImageName.replaceAll("%","-");
	}
	String fileType = strImageURL.substring( strImageURL.lastIndexOf(".")+1,strImageURL.lastIndexOf(".") + 4);//get s the file type of the image
	if(fileType.equals("com")) { // corrects a parsing error when there is no ".jpeg" or similar at the end of the photo name
		fileType="jpg";
	}//end if equals
	if(fileType.equals("jpe")) {
		fileType = "jpg";
	}
	if(caption!="") //if there is a caption to the picture
	{
		caption = caption.replaceAll("\\/", "-");//replace slashes with dashes to avoid file saving issues due to slashes commonly being used as file seperators
	}//end if caption

	File tempFile = new File(curDir+fs+"SCPs"+fs+"pictures"+fs+strLine+fs+imageCount+"."+fileType);//generates the final name and path of the image to be saved
	boolean exists = tempFile.exists();//true if file already exists
	int incase = 0;//if image name already exists, this adds a number in parentheses to the end of the file name
	if(exists) //executes if the file name already exists
	{
		incase++;//increment a number to add at the end of the file name
		fileType = strImageURL.substring( strImageURL.lastIndexOf(".") + 1 );//gets the file type
		strImageName = strImageURL.substring( strImageURL.lastIndexOf("/")+1, strImageURL.lastIndexOf(".")+1);//gets the image name
		strImageName += "("+incase+")."+fileType;//re assembles the file's name with a number after it to avoid duplicates
	}
     String capFolder = curDir+fs+"SCPs"+fs+"captions"+fs+strLine+fs;//generates the path to the captions directory
     String paddedImageCount = SCPUtils.padSCP(imageCount);
     File file = new File (capFolder+paddedImageCount+".txt");//generates the file name which is the index number of the picture it corresponds to and its position in that scp's picture directory ( caption file "0.txt" would be the caption of the first/top picture in the scp's picture directory)
     if(!file.exists()) {//creates the file if it doesnt exist already
    	 file.createNewFile();
     }
     //writes to the file and closes the buffer
	  FileWriter fw = new FileWriter(file.getAbsoluteFile());
	  BufferedWriter bw = new BufferedWriter(fw);
	  String temp = EditorGUI.escapeMetaCharacters(caption);
	  bw.write(temp);
	  bw.close();
	  
	  int fileCounter = 0;
	  File picDir = new File(picFolder);
	    for(File file1: picDir.listFiles()) //deletes each file in the sub directory so when the user requests to build the archive again, there are no duplicate files
		    if (!file1.isDirectory()) 
		        fileCounter++;
	    strImageName = fileCounter+"."+fileType;
	    
    try {//attempts to download the image using its URL and saving it in the corresponding scp's picture directory
        
        //open the stream from URL
        URL urlImage = new URL(strImageURL);
        InputStream in = urlImage.openStream(); 
        byte[] buffer = new byte[4096];
        int n = -1;
        
        OutputStream os = new FileOutputStream( picFolder + fs + strImageName );
        
        //write bytes to the output stream
        while ( (n = in.read(buffer)) != -1 ){os.write(buffer, 0, n); }
        
        //close the stream
        os.close();

    } catch (IOException e) {
        e.printStackTrace();
    }
}//end downloadImageHelper

public static void renamePics() {
	String picFolder = curDir+fs+"SCPs"+fs+"pictures"+fs;
	File picDir = new File(picFolder);
	System.out.println("Picture root folder: "+ picFolder );
	for(File folder: picDir.listFiles()) {
		System.out.println("Current Picture Folder:"+folder.toString());
		int x=0;
		for(File file: folder.listFiles()) {
			
			String FileStr = file.toString();
			System.out.println("before: "+FileStr);
			if(FileStr.contains("%")) {
				FileStr.replace("%","_");
			}
			System.out.println("Full file name: "+FileStr);
			String filePathtoFolder="";
			try {
			filePathtoFolder = FileStr.substring(0,FileStr.lastIndexOf(fs));
			} catch(IndexOutOfBoundsException e) {
				filePathtoFolder = FileStr+".jpg";
			}
			try {
			System.out.println("File path: "+filePathtoFolder);
			String fileType = FileStr.substring(FileStr.lastIndexOf(".") );
			System.out.println("Full type: "+fileType);
			String xStr = SCPUtils.padSCP(x);
			String newFile =filePathtoFolder+fs+xStr+fileType;
			System.out.println("New File: "+newFile);
			boolean result = file.renameTo(new File(newFile));
			System.out.println(result);
			x++;
			}catch(IndexOutOfBoundsException e) {e.printStackTrace();}
        }
	}
}

public static void writeDB(scp scpTemp) throws Exception {
	 Statement statement = null;
     ResultSet resultSet = null;
     
     int name = Integer.parseInt(scpTemp.getItem().substring(4));//gets scp item name and parses out the "SCP-" just leaving the integer.
     int rating = scpTemp.getRating();
     String level = scpTemp.getLevel();
     String content = scpTemp.getContent();
     String inputString = "insert into instances values ( ?, ?, ?, ?);";
     try {

		 Connection connect = SCPUtils.connect();

	 	    PreparedStatement prepStmt = connect.prepareStatement(inputString);
	 	    prepStmt.setInt(1, name);
	 	    prepStmt.setInt(2, rating);
	 	    prepStmt.setString(3, level);
	 	    prepStmt.setString(4, content);
	 	    
	 	    prepStmt.execute();
	 	    connect.close();
   
     } catch (SQLException ex) {ex.printStackTrace();}
}


public static void readDB() throws Exception {
	 Statement statement = null;
     ResultSet resultSet = null;
	try {
		Connection connect = SCPUtils.connect();

	    // Do something with the Connection

        // Statements allow to issue SQL queries to the database
        statement = connect.createStatement();
        // Result set get the result of the SQL query
        resultSet = statement.executeQuery("select * from pictures;");
        while(resultSet.next()) {
        	//for instances table
        	
        	int name = resultSet.getInt("name");
        	int rating = resultSet.getInt("rating");
        	String level = resultSet.getString("level");
        	String content = resultSet.getString("content");
        	System.out.println(name + "\n" + rating + "\n" + level + "\n" + content);
        	
        	//for pics table
        	/*
        	int id = resultSet.getInt("id");
        	int name = resultSet.getInt("pics.name");
        	String picture = resultSet.getString("pic");
        	String caption = resultSet.getString("caption");
        	System.out.println(id + "\n" + name + "\n" + picture + "\n" + caption + "\n");
        	*/
        }

	} catch (SQLException ex) {
	    // handle any errors
		ex.printStackTrace();
	}
}

}//end class
