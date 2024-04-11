/*
 * Description: this is the defining class of the MainGUI, where the main thread of the program is located and utilizes the other classes
 * Author: Jordan Fitzenberger
 * Date: 10/21/2021
 */
package scpPackage;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import javax.swing.JLabel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.DropMode;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;

import net.miginfocom.swing.MigLayout;
import scpPackage.scpList;

@SuppressWarnings("serial")
public class MainGUI extends JFrame implements ActionListener{
	
	public static String curDir = System.getProperty("user.dir");//saves the current directory of the program so it can reference outside files in the "SCPs" directory
	public static String fs = FileSystems.getDefault().getSeparator();//saves the default file separator for the computer
	
	public static scpList mainList;// the MainGUI will use this scpList instance to hold all 5999 scp instances in order to be manipulated
	private final DefaultListModel<Integer> dataModel; //a default list model set to use integers, this will be used as a model for the JList to use in the GUI
	private final JList<Integer> imageGUIList;//list of corresponding images (if any) for each scp, this list will be displayed to the user to view the images
	private final ArrayList<ImageObj> imageArray;
	private final JButton searchButton;//search button to execute a method that processes the entered text in the scp text field
	private final JTextField scpField;// text field for the user to enter a number of an SCP to see its content
	private final JScrollPane scrollPane;//scrollpane attached to the content area so the user can scroll through the article if it is long
	private final JTextArea contentArea;//displays the article content for the user to read

	
	/**
	 * Create the frame.
	 */
	public MainGUI() {
	
		JFrame frame = new JFrame("SCP Archive");//create and set title of main JFrame
		frame.setSize(1200,850);//sets the size of th JFrame
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(PBar.class.getResource("/scpPackage/scp-3d-logo.png")));//sets the icon image for the frame
		frame.setVisible(true);//sets the visibility of the frame to true
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//sets the program to close on clicking the X
		frame.setLocationRelativeTo(null);//sets the frame to load in the center of the screen
		imageArray = new ArrayList<ImageObj>();
		dataModel = new DefaultListModel<Integer>();//list model for creating a Jlist to display pictures
		imageGUIList = new JList<Integer>(dataModel);//jlist to show a list of pictures associated with the scp currently being accessed
		
		imageGUIList.setFont(new Font("Verdana", Font.PLAIN, 11));//sets the font of the imageList
		
		MouseListener mouseListener = new MouseAdapter() {//executes when a number of the image list is clicked on, retrieves the image and caption and displays them both
		    public void mouseClicked(MouseEvent e) {
		        if (e.getClickCount() == 1 || e.getClickCount() == 2) {//avoid user double clicking, and 2 instances of the picture pop 
		        	int selectedItem =  imageGUIList.getSelectedValue();//gets selected list member and its associated text type number to a local variable
		        	imageArray.get(selectedItem-1).showPic();
		         }//end if
		    }//end mouseClicked
		};//end mouse Listener initialization
		
		imageGUIList.addMouseListener(mouseListener);//assign the mouse listener to the imageList
		
		JLabel logoLabel = new JLabel("");//creates new Jlabel to be used for the logo in the top left corner of the GUI
		logoLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(PBar.class.getResource("/scpPackage/scp-logo-small.png"))));//sets the image for the JLabel		
		frame.getContentPane().setLayout(new MigLayout("", "[][88.00][68.00][grow]", "[top][][][][grow,top]"));//sets the MigLayout dimensions for the content pane of the frame
		frame.getContentPane().add(logoLabel, "cell 0 0 3 1,alignx center");//add the logo JLabel to the content pane of the frame
		
		//initializes the contentArea to display the SCP article. sets the words to wrap to the next line when needed, after a white space. Its set so the user cannot edit it and the font is set.
		contentArea = new JTextArea();
		contentArea.setWrapStyleWord(true);
		contentArea.setFont(new Font("Verdana", Font.PLAIN, 13));
		contentArea.setLineWrap(true);
		contentArea.setEditable(false);
		contentArea.setBackground(new Color(0,0,0));
		contentArea.setForeground(new Color(255,255,255));
		
		//initializes the scrollPane and is added to the contentArea so it can be used to scroll through the article. the scrollPane is set to never have a horizontal scroll bar, and always have a vertical scroll bar. and is placed in the content pane of the frame
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setViewportView(contentArea);
		frame.getContentPane().add(scrollPane, "cell 3 0 1 5,grow");

		//creates a JLabel to inform the user to enter an SCP number in the text field below it, it's set the the center of the column its in, the font is set, and its placed in the content pane of the frame
		JLabel scpFieldLabel = new JLabel("Enter an SCP Number");
		scpFieldLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scpFieldLabel.setFont(new Font("Verdana", Font.BOLD, 11));
		frame.getContentPane().add(scpFieldLabel, "cell 0 1 3 1,growx");
		
		//initializes the text field for the user to enter an SCP number. its width in columns is set as well as the font. and its placed in the content pane of the frame
		scpField = new JTextField();
		scpField.setColumns(10);
		scpField.setFont(new Font("Verdana", Font.PLAIN, 11));
		frame.getContentPane().add(scpField, "cell 1 2,grow");
		
		//initializes the search button, adds an action listener to it, requests the focus to be set so the enter key will correspond with it, the font is set as well as it's position inside the content pane of the frame. lastly the default button of the frame is set to the search button
		searchButton = new JButton("Search");
		searchButton.addActionListener(this);
		searchButton.requestFocus();
		searchButton.setFont(new Font("Verdana", Font.PLAIN, 11));	
		frame.getContentPane().add(searchButton, "cell 2 2,alignx center");
		frame.getRootPane().setDefaultButton(searchButton);
		
		//creates a JLabel to inform the user that the list that may appear below it, are of the pictures corresponding with the selected SCP. the label is centered to the list below it and is placed in the content pane of the frame
		JLabel picLabel = new JLabel("Pictures");
		picLabel.setHorizontalAlignment(SwingConstants.CENTER);
		picLabel.setFont(new Font("Verdana", Font.BOLD, 11));
		frame.getContentPane().add(picLabel, "cell 0 3 3 1,alignx center");
		
		//adds the image list to the content pane of the frame, sets the minimum size of the frame, sets the frame to be centered on the screen
		frame.getContentPane().add(imageGUIList, "cell 0 4 3 1,growx,aligny top");
		frame.setMinimumSize(new Dimension(1200, 900));
		frame.setLocationRelativeTo(null);
		//frame.pack();//sets the dimensions of the windows to the preferred sizes because the above dimensions are possible
	}

	@Override
	public void actionPerformed(ActionEvent e) {//executes proccessing method if the showButton is pressed
		if(e.getSource()==searchButton) {
			 processScpField();
			}	
	}

	public void processScpField() {//executes the main function of the GUI, displaying new content

		 Statement statement = null;
	     ResultSet resultSet = null;
		
		
		try {
			
			 dataModel.removeAllElements();//this resets the list every time you click the button, so the new list doesn't append to the old one
			imageArray.removeAll(imageArray);//this resets the array list of pictures belonging to the GUI so only the pictures in use stay in memory
			 String text = scpField.getText();//gets text from scpfield for processing
			if(text.equals("")) {warningMessage();}//sends a warning message if there is no index in the text field, or the index is out of bounds
			 else if(Integer.parseInt(text) <1 || Integer.parseInt(text)> SCPConstants.MAX_SCP_INDEX){warningMessage();}
			 else{ 
				int parsed = Integer.parseInt(text);//parses scpfield contents from text to integer

				Connection connect = SCPUtils.connect();

				    statement = connect.createStatement();
				    resultSet = statement.executeQuery("select * from instances where id = "+parsed+";");
				    while(resultSet.next())	{
				    	int name = resultSet.getInt("id");
			        	int rating = resultSet.getInt("rating");
			        	String level = resultSet.getString("level");
			        	String content = resultSet.getString("content");
			        	content = unEscapeMetaCharacters(content);
			        	String paddedName;
			        	if(name<10) {		 paddedName = "00"+ name;}
						else if (name<100) {paddedName = "0"+ name;}
						else {			 paddedName = Integer.toString(name);}
			        	contentArea.setText("SCP-"+paddedName+ "\nSCP Class: "+ level +"\nRating: "+rating+"\n"+content);//set the contentArea to the right SCP content, including its number and class level (class as in danger level)
						
				    }
				//connect.close();
				contentArea.setCaretPosition(0);//this forces the scroll bar to go to the top of the text area to the user doesnt have to scroll up to read it when the contentArea refreshes with new content

				    statement = connect.createStatement();
				    resultSet = statement.executeQuery("select * from pictures where scp_id = "+parsed+";");
				    int i =1;
				    while(resultSet.next()) {
				    	int index = resultSet.getInt("scp_id");
				    	String caption = resultSet.getString("caption");
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
	
	public void warningMessage() {//informs the user with a JOptionPane to enter a valid number in the scp field
		 JOptionPane.showMessageDialog(null,"Enter a valid SCP index.","Alert",JOptionPane.OK_OPTION);     
	}
	public static void customWarning(String str) {
		 JOptionPane.showMessageDialog(null,str,"Alert",JOptionPane.OK_OPTION);     
	}
	public String unEscapeMetaCharacters(String inputString){
	    final String[] metaCharacters = {"\\\\","\\'","\\\"","%","\\_"};

	    for (int i = 0 ; i < metaCharacters.length ; i++){
	        if(inputString.contains(metaCharacters[i])){
	        	String tempChar = metaCharacters[i];
	        	inputString = inputString.replace(metaCharacters[i],tempChar.substring(1 ));
	        }
	    }
	    return inputString;
	}
//get setters and getters
	public scpList getMainList() {
		return mainList;

	}

	
}
