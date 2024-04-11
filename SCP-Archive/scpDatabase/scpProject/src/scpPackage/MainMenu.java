package scpPackage;

import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import javax.swing.JPanel;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;


public class MainMenu implements ActionListener{

	private JFrame f;
	private JButton buildButton, uploadButton, browseButton, editButton;

	private static ArrayList<scp> scps;
	private static scpList myScpList;
	private static final double index2 = 0;//used in the process of updating the progress bar value
	//private static PBar pBar;//initialized custom progress bar, declared here and as static to minimize errors during the multi-threaded work in the run method 
	private int index1;//used to keep track of which instance in the array list the scp instance is currently on due to their numerical names
	public static String curDir = System.getProperty("user.dir");//saves the current directory of the program so it can reference outside files in the "SCPs" directory
	public static String fs = FileSystems.getDefault().getSeparator();//saves the default file separator for the computer
	
	File theDir = new File(curDir+fs+"SCPs"+fs);//saves the top working directory of the program, known as SCPs
	/**
	 * Launch the application.
	 */
	public static void main (String[] args) {
		new MainMenu();
	}

	/**
	 * Create the application.
	 */
	public MainMenu() {
		myScpList = new scpList();
		//scpList.renamePics();
		//pBar = new PBar();
		//pBar.setVisibility(false);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		f = new JFrame();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		f.setFont(new Font("Verdana", Font.PLAIN, 12));
		f.setIconImage(Toolkit.getDefaultToolkit().getImage(MainMenu.class.getResource("/scpPackage/scp-3d-logo.png")));
		f.setTitle("SCP Archive");
		f.setSize(425,500);
		//frame.setBounds(100, 100, 450, 300);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setIcon(new ImageIcon(MainMenu.class.getResource("/scpPackage/scp-3d-logo-small.png")));
		f.getContentPane().add(lblNewLabel, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		f.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[-1.00][][][321.00]", "[][][][][][][][][]"));
		
		JLabel lblNewLabel_1 = new JLabel("SCP Archive Menu");
		lblNewLabel_1.setFont(new Font("Verdana", Font.BOLD, 12));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblNewLabel_1, "cell 1 0 3 1,alignx center");
		
		buildButton = new JButton("Build");
		panel.add(buildButton, "cell 1 1,grow");
		buildButton.addActionListener(this);
		
		JLabel lblNewLabel_2 = new JLabel("Build local SCP files from the Internet.");
		lblNewLabel_2.setFont(new Font("Verdana", Font.PLAIN, 11));
		panel.add(lblNewLabel_2, "cell 3 1");
		
		uploadButton = new JButton("Upload");
		panel.add(uploadButton, "cell 1 3,grow");
		uploadButton.addActionListener(this);
		
		JLabel lblNewLabel_4 = new JLabel("Upload SCP files to database.");
		lblNewLabel_4.setFont(new Font("Verdana", Font.PLAIN, 11));
		panel.add(lblNewLabel_4, "cell 3 3");
		
		browseButton = new JButton("Browse");
		panel.add(browseButton, "cell 1 5,grow");
		browseButton.addActionListener(this);
		
		JLabel lblNewLabel_3 = new JLabel("Browse SCP Database.");
		lblNewLabel_3.setFont(new Font("Verdana", Font.PLAIN, 11));
		panel.add(lblNewLabel_3, "cell 3 5");
		
		editButton = new JButton("Edit");
		panel.add(editButton, "cell 1 7,grow");
		editButton.addActionListener(this);
		
		JLabel editLabel = new JLabel("Edit SCP entries.");
		editLabel.setFont(new Font("Verdana", Font.PLAIN, 11));
		panel.add(editLabel, "cell 3 7");
		f.pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==buildButton) {
			try {
				if (!theDir.exists())//if the top directory doesn't exist
				{					
					makeDirs(theDir);//creates the top directory and its sub directories needed for the program
			}
			} catch (IOException e1) {e1.printStackTrace();}
					new Thread(new Runnable() {//swing runs on one thread so web method needs to be done with a seperate thread to avoid entire program to be frozen and unresponsive
					     @Override
					     public void run() {
					    	 try {
								myScpList.web();
							} catch (IOException e) {e.printStackTrace();}
					     }
					}).start();
		}
		else if(e.getSource()==uploadButton){		
				new Thread(new Runnable() {
				@Override
				public void run() {
						try {
						myScpList.noWeb();
						} catch (IOException | ClassNotFoundException e1) {e1.printStackTrace();}
					}
				}).start();	
		}
		else if(e.getSource()==browseButton) {
			new Thread(new Runnable() {
				@Override
				public void run() {
						new MainGUI();
					}
				}).start();
			f.dispose();
		}
		else if(e.getSource()==editButton) {
			new Thread(new Runnable() {
				@Override
				public void run() {
						new EditorGUI();
					}
				}).start();
			f.dispose();
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
		for ( int i = 1; i < SCPConstants.MAX_SCP_INDEX; i++ ) {


		try {
				strLine = SCPUtils.padSCP( i );
				File tempPicDir = new File(pictures+fs+strLine);//generates the file path for the specific scp's picturees
				File tempEntryFile = new File(entries+fs+strLine+".txt");//generates the file path for an entry text file for the specific scp
				File tempCaptionFile = new File(captions+fs+strLine);//generates the file path for for the specific scp's caption directory
				
				//creates the picture and caption directory for the specific scp, and its entry text file 
				tempPicDir.mkdir();
				tempCaptionFile.mkdir();
				tempEntryFile.createNewFile();


		} catch (IOException e) {
			e.printStackTrace();
		}
		}

	}//end makeDirs
	}
