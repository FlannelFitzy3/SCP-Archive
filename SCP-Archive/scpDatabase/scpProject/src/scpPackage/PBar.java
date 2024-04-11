/*
 * Description: creates a progress bar frame when rebuilding the SCP archive from the Internet
 * Author: Jordan Fitzenberger
 * Date: 10/21/2021
 */
package scpPackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import javax.swing.JSeparator;
import java.awt.FlowLayout;
import java.awt.Font;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;

import java.awt.Component;

import javax.imageio.ImageIO;
import javax.swing.Box;
import java.awt.Toolkit;

public class PBar extends JFrame implements ActionListener {
	public static String curDir = System.getProperty("user.dir");//saves the current directory of the program so it can reference outside files in the "SCPs" directory

	private final JFrame f;//the frame that will hold the user message, the progress bar, and the cancel button
	private final JLabel message;// label that will display a message to the user in the frame
	private final JButton cancelButton;// button to cancel the current operation of downloading the needed items from the Internet, and exit the program
	private final JProgressBar bar;//progress bar to show how far along the program is with downloading the needed items to rebuild the SCP archive
	private final boolean visible = false;//boolean set to false so when the scpList instance initializes, the PBar window will not pop up right away, only after the user clicks "Yes" when asked if they want to rebuild the archive from the Internet


	public PBar() {
		f = new JFrame("SCP Archive");//Initializes and names the frame
		f.setIconImage(Toolkit.getDefaultToolkit().getImage(PBar.class.getResource("/scpPackage/scp-3d-logo.png")));//sets the window icon to the scp logo
		//f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//sets the program to close when the user clicks the X
		f.getContentPane().setLayout(null);//sets the layout to null, this is because the frame uses absolute positioning, meaning each part of the frame is placed at the precise pixel its assigned
		f.setVisible(true);//sets the visibility of the frame to true
		f.setSize(400,200);//sets the dimensions of the frame
		f.setLocationRelativeTo(null);//sets the frame to null, placing it in the middle of the screen
		
		//Initializes the JLabel message to the given string, sets the font, and exact position in the frame it will be placed, adds the message to the content pane
		message = new JLabel("Rebuilding SCP archive, this will take a while.");
		message.setFont(new Font("Verdana", Font.PLAIN, 14));
		message.setBounds(30, 11, 324, 23);
		f.getContentPane().add(message);
		
		//initializes the JProgressBar starting at 0 and ending at the number of SCPs currently in the web site 5,999, sets the size and position of the bar. he colors of the percentage, foreground, and background, are set for the progress bar. its then added to the frame
		bar = new JProgressBar(0,SCPConstants.MAX_SCP_INDEX);
		bar.setStringPainted(true);
		bar.setBounds(10, 49, 364, 30);
		bar.setForeground(new Color(102, 0, 0));
		bar.setBackground(new Color(25,0,0));
		BasicProgressBarUI ui = new BasicProgressBarUI() {
		    protected Color getSelectionBackground() {
		        return Color.white;
		    }
		    protected Color getSelectionForeground() {
		        return Color.white;
		    }
		};
		bar.setUI(ui);
		f.getContentPane().add(bar);
		
		//initializes the cancel button with the given string, adds and action lister so it can be functional, sets the size and position in the frame and is added to the frame
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		cancelButton.setBounds(150, 100, 89, 35);
		f.getContentPane().add(cancelButton);



	}
	public PBar(int i) {
		f = new JFrame("SCP Archive");//Initializes and names the frame
		f.setIconImage(Toolkit.getDefaultToolkit().getImage(PBar.class.getResource("/scpPackage/scp-3d-logo.png")));//sets the window icon to the scp logo
		//f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//sets the program to close when the user clicks the X
		f.getContentPane().setLayout(null);//sets the layout to null, this is because the frame uses absolute positioning, meaning each part of the frame is placed at the precise pixel its assigned
		f.setVisible(true);//sets the visibility of the frame to true
		f.setSize(400,200);//sets the dimensions of the frame
		f.setLocationRelativeTo(null);//sets the frame to null, placing it in the middle of the screen
		
		//Initializes the JLabel message to the given string, sets the font, and exact position in the frame it will be placed, adds the message to the content pane
		message = new JLabel("Rebuilding SCP archive, this will take a while.");
		message.setFont(new Font("Verdana", Font.PLAIN, 14));
		message.setBounds(30, 11, 324, 23);
		f.getContentPane().add(message);
		
		//initializes the JProgressBar starting at 0 and ending at the number of SCPs currently in the web site 5,999, sets the size and position of the bar. he colors of the percentage, foreground, and background, are set for the progress bar. its then added to the frame
		bar = new JProgressBar(i,SCPConstants.MAX_SCP_INDEX);
		bar.setStringPainted(true);
		bar.setBounds(10, 49, 364, 30);
		bar.setForeground(new Color(102, 0, 0));
		bar.setBackground(new Color(25,0,0));
		BasicProgressBarUI ui = new BasicProgressBarUI() {
		    protected Color getSelectionBackground() {
		        return Color.white;
		    }
		    protected Color getSelectionForeground() {
		        return Color.white;
		    }
		};
		bar.setUI(ui);
		f.getContentPane().add(bar);
		
		//initializes the cancel button with the given string, adds and action lister so it can be functional, sets the size and position in the frame and is added to the frame
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		cancelButton.setBounds(150, 100, 89, 35);
		f.getContentPane().add(cancelButton);
	}
	public void setMessage(String message) {
		this.message.setText(message);
	}
	public void updateBar(int i) {//when called, it update the size and percentage of the progress bar. when the value gets to 599 (the last SCP) the frame is killed
		bar.setValue(i);
		if(i>=SCPConstants.MAX_SCP_INDEX)
			f.dispose();
	}
	public void setVisibility(boolean x) {//method for other classes to set when its appropriate to display the progress bar frame
		f.setVisible(x);
	}
	@Override
	public void actionPerformed(ActionEvent e) {//terminates the program when the cancel button is pressed
		if(e.getSource()==cancelButton) {
			f.dispose();
		}
		
	}
}
