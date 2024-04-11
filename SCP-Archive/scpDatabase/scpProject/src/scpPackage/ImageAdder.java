package scpPackage;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import scpPackage.EditorGUI;
import scpPackage.MainMenu;
import net.miginfocom.swing.MigLayout;

public class ImageAdder implements ActionListener{

	private JFrame frame;
	private JTextField filePathField;
	private JButton searchButton, saveButton;
	private String FileString;
	private File selectedFile;
	private JTextArea captionArea;
	private final String scpIndex;
	private final String curDir = System.getProperty("user.dir");//saves the current directory of the program so it can reference outside files in the "SCPs" directory
	private final String fs = FileSystems.getDefault().getSeparator();//saves the default file separator for the computer
	private final File theDir = new File(curDir+fs+"SCPs"+fs);
	/**
	 * Launch the application.
	 
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ImageAdder window = new ImageAdder("SCP-001");
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
*/
	/**
	 * Create the application.
	 */
	public ImageAdder(String scpIndex) {
		this.scpIndex = scpIndex;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setVisible(true);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JLabel titleLabel = new JLabel("Add an image to SCP-"+scpIndex);
		titleLabel.setFont(new Font("Verdana", Font.BOLD, 14));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(titleLabel, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[66.00][grow][grow][][][][][][][][][68.00][grow]", "[][][grow][]"));
		
		JLabel filePathLabel = new JLabel("Path to Image:");
		filePathLabel.setHorizontalAlignment(SwingConstants.CENTER);
		filePathLabel.setFont(new Font("Verdana", Font.PLAIN, 11));
		panel.add(filePathLabel, "cell 0 0,alignx trailing,growy");
		
		filePathField = new JTextField();
		filePathField.setFont(new Font("Verdana", Font.PLAIN, 11));
		filePathField.setEditable(false);
		panel.add(filePathField, "cell 1 0 11 1,growx");
		filePathField.setColumns(10);
		
		searchButton = new JButton("Search");
		searchButton.setFont(new Font("Verdana", Font.PLAIN, 11));
		panel.add(searchButton, "cell 12 0,growx");
		searchButton.addActionListener(this);
		
		JLabel captionLabel = new JLabel("Caption:");
		captionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		captionLabel.setFont(new Font("Verdana", Font.PLAIN, 11));
		panel.add(captionLabel, "cell 0 1,alignx right");
		
		captionArea = new JTextArea();
		panel.add(captionArea, "cell 1 1 11 2,grow");
		
		saveButton = new JButton("Add Image");
		saveButton.setFont(new Font("Verdana", Font.PLAIN, 11));
		panel.add(saveButton, "cell 12 3");
		saveButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==searchButton) {
			 JFileChooser myFC = new JFileChooser(new File(System.getProperty("user.home")));
			 myFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
			 myFC.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "jpg","jpeg", "png", "tif", "gif"));
			 int result = myFC.showOpenDialog(myFC);
			 if(result == JFileChooser.APPROVE_OPTION) {
					selectedFile = myFC.getSelectedFile();
					FileString = selectedFile.toString();
					filePathField.setText(FileString);	 
			}	
		}
		if(e.getSource()==saveButton) {
			boolean pass = true;
			if(!selectedFile.exists()) {
				MainGUI.customWarning("Select a valid file first.");
				pass = false;
			}
			if(captionArea.getText()=="") {
				MainGUI.customWarning("caption cannot be blank.");
				pass = false;
			}
			if(pass) {
				try {
					//scpIndex = scpIndex.substring(4,scpIndex.length());
					int index = Integer.parseInt(scpIndex);
					addImage(index,selectedFile,captionArea.getText());
				} catch (IOException | ClassNotFoundException e1) {e1.printStackTrace();}
			}
		}
	}
	//left off here trying to add picture to local files. adding to database works fine
	public void addImage(int index, File file, String caption) throws ClassNotFoundException, IOException {
		
		if(!theDir.exists()) {
			try {
				makeDirs(theDir);
			} catch (IOException e) {e.printStackTrace();}
		}
		String indexStr = SCPUtils.padSCP(index);
		//(theDir+fs+"pictures"+fs);
	    File captionFolder = new File(theDir+fs+"captions"+fs+indexStr+fs);
	    String  captionFolderStr = captionFolder.toString();
	    System.out.println("caption Folder: "+captionFolderStr);
	    int fileCounter = 0;
	    for(File file1: captionFolder.listFiles()) //deletes each file in the sub directory so when the user requests to build the archive again, there are no duplicate files
		    if (!file1.isDirectory()) 
		        fileCounter++;
	    String capCounter = SCPUtils.padSCP(fileCounter);
	    String newCaptionFile = captionFolderStr+fs+capCounter+".txt";
	    System.out.println("new caption file: "+newCaptionFile);
	    FileWriter fw = new FileWriter(newCaptionFile);
	    BufferedWriter bw = new BufferedWriter(fw);
		  bw.write(caption);
		  bw.close();
		  fw.close();
		  
		  String fileString = file.toString();
		  System.out.println("File String: "+fileString);
		  fileString = fileString.substring(fileString.lastIndexOf(fs)+1);
		  System.out.println("File String: "+fileString);
		  String fileType = fileString.substring( fileString.lastIndexOf(".")+1);//gets the file type
		  System.out.println("File type: "+fileType);
		  File pictureFolder = new File(theDir+fs+"pictures"+fs+indexStr+fs);
		  fileCounter = 0;
		    for(File file1: pictureFolder.listFiles()) //deletes each file in the sub directory so when the user requests to build the archive again, there are no duplicate files
			    if (!file1.isDirectory()) 
			        fileCounter++;
		  fileString = SCPUtils.padSCP(fileCounter);
		  String pictureFolderStr = pictureFolder.toString();
		  System.out.println("pictureFolder "+ pictureFolderStr);
		  String newPictureFile = pictureFolderStr+fs+fileString+"."+fileType;
		  System.out.println("new picture File "+ newPictureFile);
		  
		  
		 
		  try {
			  BufferedImage img = ImageIO.read(file);
			    ImageIO.write(img, fileType, new File(newPictureFile));
			} catch (IOException e) {
			    e.printStackTrace();
			}
		//String newPictureFile

	     FileInputStream myInputStream = new FileInputStream(file);
	     String inputString = "insert into pictures values ( ?, ?, ?, ?);";
	 	try {
	 		
	 		 Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connect = SCPUtils.connect();

	 	    // Do something with the Connection
	 	    PreparedStatement prepStmt = connect.prepareStatement(inputString);
	 	    prepStmt.setNull(1, Types.INTEGER);
	 	    prepStmt.setInt(2, index);
	 	    prepStmt.setBlob(3, myInputStream);
	 	    caption = EditorGUI.escapeMetaCharacters(caption);
	 	    prepStmt.setString(4, caption);

	 	    prepStmt.execute();
	 	    connect.close();
	 	    JOptionPane.showInternalMessageDialog(null,"Image added successfully.","Adding Image",JOptionPane.OK_OPTION);
	 	} catch (SQLException ex) {
	 	    // handle any errors
	 		ex.printStackTrace();
	 	}
	}
	public void makeDirs(File theDir) throws IOException {//creates the needed directories for the program to work
		theDir.mkdir();//makes SCPs directory
		//generates the file path for the entries, pictures, captions, and logs directories
	    File entries = new File(theDir+fs+"entries"+fs);
	    File pictures = new File(theDir+fs+"pictures"+fs);
	    File captions = new File(theDir+fs+"captions"+fs);
	    File logs = new File (theDir+fs+"logs"+fs);
	    //creates directories in the file system
	    entries.mkdir();
	    pictures.mkdir();
	    captions.mkdir();
	    logs.mkdir();
	    
	    String strLine;//allocate memory for the buffer to save the line to
		for ( int i = 0; i < SCPConstants.MAX_SCP_INDEX; i++ ) {

			try {
				strLine = SCPUtils.padSCP( i );
				File tempPicDir = new File( pictures + fs + strLine );//generates the file path for the specific scp's picturees
				File tempEntryFile = new File( entries + fs + strLine + ".txt" );//generates the file path for an entry text file for the specific scp
				File tempCaptionFile = new File( captions + fs + strLine );//generates the file path for for the specific scp's caption directory

				//creates the picture and caption directory for the specific scp, and its entry text file
				tempPicDir.mkdir( );
				tempCaptionFile.mkdir( );
				tempEntryFile.createNewFile( );

			} catch ( IOException e ) {
				e.printStackTrace( );
			}
		}
	}//end makeDirs
	
}
