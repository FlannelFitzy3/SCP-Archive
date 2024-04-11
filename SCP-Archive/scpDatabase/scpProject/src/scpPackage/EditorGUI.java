package scpPackage;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.FileSystems;
import java.sql.*;
import java.util.ArrayList;

public class EditorGUI extends JFrame implements ActionListener{
	
	public static String curDir = System.getProperty("user.dir");//saves the current directory of the program so it can reference outside files in the "SCPs" directory
	public static String fs = FileSystems.getDefault().getSeparator();//saves the default file separator for the computer
	
	private final DefaultListModel<Integer> dataModel; //a default list model set to use integers, this will be used as a model for the JList to use in the GUI
	private final ArrayList<ImageObj> imageArray;
	private final JButton searchButton;//search button to execute a method that processes the entered text in the scp text field
	private final JTextField scpField;// text field for the user to enter a number of an SCP to see its content
	private final JScrollPane scrollPane;//scrollpane attached to the content area so the user can scroll through the article if it is long
	private final JTextArea contentArea;//displays the article content for the user to read
	private final JTextField levelField;
	private final JLabel levelLabel;
	private final JLabel ratingLabel;
	private final JTextField ratingField;
	private final JButton updateButton;
	private final JLabel currentSCPLabel;
	private final JLabel currentSCP;
	private final JList<Integer> imageGUIList;
	private final JButton addImageButton;
	private final JButton deleteImageButton;
	

	/**
	 * Create the application.
	 */
	public EditorGUI() {
		JFrame frame = new JFrame("SCP Editor");
		frame.getContentPane().setFont(new Font("Verdana", Font.BOLD, 11));
		frame.setVisible(true);
		setSize(1200,850);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(PBar.class.getResource("/scpPackage/scp-3d-logo.png")));//sets the icon image for the frame
		frame.setLocationRelativeTo(null);//sets the frame to load in the center of the screen
		JLabel logoLabel = new JLabel("");//creates new Jlabel to be used for the logo in the top left corner of the GUI
		logoLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(PBar.class.getResource("/scpPackage/scp-logo-small.png"))));//sets the image for the JLabel		
		frame.getContentPane().setLayout(new MigLayout("", "[70.00,grow][60.00][][233.00][739.00,grow][]", "[top][][25.00][25.00][][][][][][grow][][][grow,top]"));//sets the MigLayout dimensions for the content pane of the frame
		frame.getContentPane().add(logoLabel, "cell 0 0 2 5,alignx right,growy");//add the logo JLabel to the content pane of the frame
		imageArray = new ArrayList<ImageObj>();
		
		/*
		 * the data model, image list, and mouse listener will function like the browser until a lter version
		 * that enables editing of the captions and possilbly the pictures
		 */
		dataModel = new DefaultListModel<Integer>();//list model for creating a Jlist to display pictures
		imageGUIList = new JList<Integer>(dataModel);//jlist to show a list of pictures associated with the scp currently being accessed
		
		imageGUIList.setFont(new Font("Verdana", Font.PLAIN, 11));//sets the font of the imageList
		
		MouseListener mouseListener = new MouseAdapter() {//executes when a number of the image list is clicked on, retrieves the image and caption and displays them both
		    public void mouseClicked(MouseEvent e) {
		        if (e.getClickCount() == 1 || e.getClickCount() == 2) {//avoid user double clicking, and 2 instances of the picture pop 
		        	int selectedItem =  imageGUIList.getSelectedValue();//gets selected list member and its associated text type number to a local variable
		        	ImageObj tempImage = imageArray.get(selectedItem-1);
		        	tempImage.setCaptionEditable(true);
		        	tempImage.showPic();
		         }//end if
		    }//end mouseClicked
		};//end mouse Listener initialization
		
		imageGUIList.addMouseListener(mouseListener);//assign the mouse listener to the imageList
		
		currentSCPLabel = new JLabel("Current SCP:");
		currentSCPLabel.setFont(new Font("Verdana", Font.BOLD, 11));
		frame.getContentPane().add(currentSCPLabel, "cell 2 1");
		
		currentSCP = new JLabel("");
		frame.getContentPane().add(currentSCP, "cell 3 1");
		
		
		levelLabel = new JLabel("SCP Class :");
		levelLabel.setFont(new Font("Verdana", Font.BOLD, 11));
		frame.getContentPane().add(levelLabel, "cell 2 2,alignx trailing,aligny center");
				
		levelField = new JTextField();
		frame.getContentPane().add(levelField, "cell 3 2,growx");
		levelField.setColumns(10);
				
		updateButton = new JButton("Save Changes");
		frame.getContentPane().add(updateButton, "cell 5 2 1 2,grow");
		updateButton.addActionListener(this);
				
		ratingLabel = new JLabel("SCP Rating :");
		ratingLabel.setFont(new Font("Verdana", Font.BOLD, 11));
		frame.getContentPane().add(ratingLabel, "cell 2 3,alignx trailing");
				
		ratingField = new JTextField();
		ratingField.setColumns(10);
		frame.getContentPane().add(ratingField, "cell 3 3,growx");
		
				//creates a JLabel to inform the user to enter an SCP number in the text field below it, it's set the the center of the column its in, the font is set, and its placed in the content pane of the frame
		JLabel scpLabel = new JLabel("Enter an SCP Number");
		scpLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scpLabel.setFont(new Font("Verdana", Font.BOLD, 11));
		frame.getContentPane().add(scpLabel, "cell 0 5 2 1,growx");
		
		//initializes the text field for the user to enter an SCP number. its width in columns is set as well as the font. and its placed in the content pane of the frame
		scpField = new JTextField();
		scpField.setColumns(10);
		scpField.setFont(new Font("Verdana", Font.PLAIN, 11));
		frame.getContentPane().add(scpField, "cell 0 6,grow");
		
		//initializes the search button, adds an action listener to it, requests the focus to be set so the enter key will correspond with it, the font is set as well as it's position inside the content pane of the frame. lastly the default button of the frame is set to the search button
		searchButton = new JButton("Search");
		searchButton.addActionListener(this);
		searchButton.requestFocus();
		searchButton.setFont(new Font("Verdana", Font.PLAIN, 11));	
		frame.getContentPane().add(searchButton, "cell 1 6,alignx center");
		frame.getRootPane().setDefaultButton(searchButton);
		searchButton.addActionListener(this);
		
		//creates a JLabel to inform the user that the list that may appear below it, are of the pictures corresponding with the selected SCP. the label is centered to the list below it and is placed in the content pane of the frame
		JLabel picLabel = new JLabel("Pictures");
		picLabel.setHorizontalAlignment(SwingConstants.CENTER);
		picLabel.setFont(new Font("Verdana", Font.BOLD, 11));
		frame.getContentPane().add(picLabel, "cell 0 7 2 1,alignx center");
		
		//initializes the contentArea to display the SCP article. sets the words to wrap to the next line when needed, after a white space. Its set so the user cannot edit it and the font is set.
		contentArea = new JTextArea();
		contentArea.setWrapStyleWord(true);
		contentArea.setFont(new Font("Verdana", Font.PLAIN, 13));
		contentArea.setLineWrap(true);
		
		//initializes the scrollPane and is added to the contentArea so it can be used to scroll through the article. the scrollPane is set to never have a horizontal scroll bar, and always have a vertical scroll bar. and is placed in the content pane of the frame
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setViewportView(contentArea);
		frame.getContentPane().add(scrollPane, "cell 2 4 4 9,grow");
		
		
		imageGUIList.setFont(new Font("Verdana", Font.PLAIN, 11));
		frame.getContentPane().add(imageGUIList, "cell 0 8 2 2,growx,aligny center");
		
		addImageButton = new JButton("Add Image");
		addImageButton.setFont(new Font("Verdana", Font.PLAIN, 11));
		frame.getContentPane().add(addImageButton, "cell 0 10 2 1,growx");
		
		deleteImageButton = new JButton("Delete Image");
		deleteImageButton.setFont(new Font("Verdana", Font.PLAIN, 11));
		frame.getContentPane().add(deleteImageButton, "cell 0 11 2 1,growx");
		frame.setMinimumSize(new Dimension(1200, 900));
		frame.setLocationRelativeTo(null);
		addImageButton.addActionListener(this);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==searchButton) {
			 processScpField();
			}	
		if(e.getSource()==updateButton) {
			updateCheck();	
		}
		if(e.getSource()==addImageButton) {
			if(currentSCP.getText()=="") {
				MainGUI.customWarning("Select an SCP first.");
			}else {
				String temp = currentSCP.getText();
				new ImageAdder(temp);
			}
		}
	}

	public void processScpField()	{
		 Statement statement = null;
	     ResultSet resultSet = null;
		
		
		try {
			
			 dataModel.removeAllElements();//this resets the list every time you click the button, so the new list doesn't append to the old one
			imageArray.removeAll(imageArray);//this resets the array list of pictures belonging to the GUI so only the pictures in use stay in memory
			 String text = scpField.getText();//gets text from scpfield for processing
			if(text.equals("")) {warningMessage();}//sends a warning message if there is no index in the text field, or the index is out of bounds
			 else if(Integer.parseInt(text) <1 || Integer.parseInt(text)>SCPConstants.MAX_SCP_INDEX){warningMessage();}
			 else{ 
				int parsed = Integer.parseInt(text);//parses scpfield contents from text to integer
				 Connection connect = SCPUtils.connect();

				    statement = connect.createStatement();
				    resultSet = statement.executeQuery("select * from instances where name = "+parsed+";");
				    while(resultSet.next())	{
				    	int name = resultSet.getInt("name");
			        	int rating = resultSet.getInt("rating");
			        	String level = resultSet.getString("level");
			        	String content = resultSet.getString("content");
			        	content = unEscapeMetaCharacters(content);
			        	String paddedName;
			        	if(name<10) {		 paddedName = "00"+ name;}
						else if (name<100) {paddedName = "0"+ name;}
						else {			 paddedName = Integer.toString(name);}
			        	currentSCP.setText(paddedName);
			        	levelField.setText(level);
			        	ratingField.setText(String.valueOf(rating));
			        	contentArea.setText(content);//set the contentArea to the right SCP content, including its number and class level (class as in danger level)
						
				    }
				connect.close();
				contentArea.setCaretPosition(0);//this forces the scroll bar to go to the top of the text area to the user doesnt have to scroll up to read it when the contentArea refreshes with new content
				
				    statement = connect.createStatement();
				    resultSet = statement.executeQuery("select * from pictures where name = "+parsed+";");
				    int i =1;
				    while(resultSet.next()) {
				    	int index = resultSet.getInt("id");
				    	String caption = resultSet.getString("captions");
				    	caption = unEscapeMetaCharacters(caption);
				    	Blob myBlob = resultSet.getBlob(3);
				    	byte[] barr =myBlob.getBytes(1,(int)myBlob.length());//1 means first image
				    	Image myImage= new ImageIcon(barr).getImage();
				    	ImageObj tempImage = new ImageObj(index,myImage,caption);
				    	imageArray.add(tempImage);
				    	dataModel.addElement(i++);
				    }
				    connect.close();
				
				//ListChange(parsed);//changes the image list to the right number and image/caption instances
				}
			
			}catch (Exception ex) {//catch all for errors with the scpfield and its contents
		        ex.printStackTrace();
		        warningMessage();
		    }
	}
	public void updateCheck() {
		String current = currentSCP.getText();
		String ratingStr = ratingField.getText();
		int rating = -999;
		String levelStr = levelField.getText();
		String content = contentArea.getText();
		boolean passTest = true;
		
		//checks if there is currently an SCP able to be edited
		if(current.equals("")) {
			MainGUI.customWarning("Select an SCP first.");	
			passTest = false;
		}
		//checks if the rating field is empty, if not, it also checks if it is a number. if either of these tests fail then the passTest variable will be set to false;
		boolean first = true;
		boolean second = true;
		if(ratingStr.equals("")) {
			MainGUI.customWarning("Enter a rating first.");
			first = false;
		}
			try {
				rating = Integer.parseInt(ratingStr);
			} catch (NumberFormatException e) {
				MainGUI.customWarning("Rating must be a whole number.");
				second = false;
			}
		
		if(!first || !second) {
			passTest = false;
		}
		
		//tests to see if the text in the SCP class field is one of the possible SCP classes, if not the passTest variable will be set to false;
		switch(levelStr.toUpperCase()) {
		case "SAFE": levelStr="Safe"; break;
		case "EUCLID": levelStr= "Euclid"; break;
		case "KETER":levelStr="Keter"; break;
		case "THAUMIEL":levelStr="Thaumiel"; break;
		case "NEUTRALIZED":levelStr="Neutralized"; break;
		case "APOLLYON":levelStr="Apollyon"; break;
		case "ARCHON":levelStr="Archon"; break;
		case "EXPLAINED":levelStr="Explained"; break;
		case "ESOTERIC":levelStr="Esoteric"; break;
		case "DECOMMISSIONED": levelStr="Decommissioned"; break;
		case "ANOMALOUS SAFE":levelStr="Anomalous"; break;
		case "SCP CLASS NOT FOUND": levelStr="SCP class not found"; break;
		default: passTest = false; MainGUI.customWarning("Enter a valid SCP Class.");
		}
		
		//tests to see if the content area has any text in it, if not, the passTest variable is set to false;
		if(contentArea.getText().equals("")) {
			MainGUI.customWarning("The content cannot be blank.");
			passTest = false;
		}
		for(int i=0; i <imageArray.size();i++) {
			if(imageArray.get(i).getCaptionAreaText().equals("")) {
				passTest = false;
				MainGUI.customWarning("Image captions cannot be blank");
				break;
			}
			
		}
		
		if(passTest) {
			updateDB();
		}
	}	
	public void updateDB() {

		Connection connect = null;
		 Statement statement = null;
	     ResultSet resultSet = null;
		try {
			
			String currentStr = currentSCP.getText();
			int current = Integer.parseInt(currentStr);
			
			String ratingStr = ratingField.getText();
			int rating = Integer.parseInt(ratingStr);
			
			String level = levelField.getText();
			
			String content = contentArea.getText();
			 content = escapeMetaCharacters(content);
			 
			String inputString = "update instances set name = ? , rating = ? , level = ? , content = ? where name = ?;";
		     try {

			    	 Class.forName("com.mysql.cj.jdbc.Driver");
				 connect = DriverManager.getConnection("jdbc:mysql://"+ SCPConstants.SERVER_IP+"/SCP?" +
						 "user="+ SCPConstants.DATABASE_USER+"&password="+ SCPConstants.DATABASE_PASSWORD);

			 	    PreparedStatement prepStmt = connect.prepareStatement(inputString);
			 	    prepStmt.setInt(1, current);
			 	    prepStmt.setInt(2, rating);
			 	    prepStmt.setString(3, level);
			 	    prepStmt.setString(4, content);
			 	    prepStmt.setInt(5, current);
			 	    
			 	   prepStmt.execute();
			 	    
			 	   for(int i=0; i <imageArray.size();i++) {
			 		  inputString = "update pictures set captions = ? where id = ?;";
			 		   ImageObj tempImage = imageArray.get(i);
			 		   
			 		   		String caption = tempImage.getCaptionAreaText();
							int index = tempImage.getIndex();
							
							prepStmt = connect.prepareStatement(inputString);
							prepStmt.setString(1,caption);
							prepStmt.setInt(2,index);
							prepStmt.execute();
						
					}
			 	    
			 	    connect.close();
		   
		     } catch (SQLException ex) {ex.printStackTrace();}
			
			}catch (Exception ex) {//catch all for errors with the scpfield and its contents
		        ex.printStackTrace();
		        warningMessage();
		    }
	}
	public void warningMessage() {//informs the user with a JOptionPane to enter a valid number in the scp field
		 JOptionPane.showMessageDialog(null,"Enter a valid SCP index.","Alert",JOptionPane.OK_OPTION);     
	}

	public static String unEscapeMetaCharacters(String inputString){
	    final String[] metaCharacters = {"\\\\","\\'","\\\"","%","\\_"};

	    for (int i = 0 ; i < metaCharacters.length ; i++){
	        if(inputString.contains(metaCharacters[i])){
	        	String tempChar = metaCharacters[i];
	        	inputString = inputString.replace(metaCharacters[i],tempChar.substring(1 ));
	        }
	    }
	    return inputString;
	}
	public static String escapeMetaCharacters(String inputString){
	    final String[] metaCharacters = {"\\","'","\"","%","_"};

	    for (int i = 0 ; i < metaCharacters.length ; i++){
	        if(inputString.contains(metaCharacters[i])){
	            inputString = inputString.replace(metaCharacters[i],"\\"+metaCharacters[i]);
	        }
	    }
	    return inputString;
	}
}
