/*
 * Description:
 * Author: Jordan Fitzenberger
 * Date: 10/21/2021
 */
package scpPackage;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.table.TableModel;
import javax.swing.*;


public class PicObj{
	private File picture;
	private String caption;

	
	//constructor
	public PicObj() {
		//this.picture = ;
		this.caption="";
	}
	
	//alt constructor 
	public PicObj (File picture,String caption) {
		this.picture = picture;
		this.caption = caption;
	}	
	

	public File getPicture() {
		return picture;
	}

	public void setPicture(File picture) {
		this.picture = picture;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	public void showPic() {
		File tempPic = this.getPicture();
		String captionstr = this.getCaption();

        JFrame frame = new JFrame();
    	frame.setLayout(new GridBagLayout());
    	frame.setIconImage(Toolkit.getDefaultToolkit().getImage(PBar.class.getResource("/scpPackage/SCP-Logo-2.png")));
    	GridBagConstraints c = new GridBagConstraints();
    	
        JPanel leftPanel = new JPanel();
        JLabel label = new JLabel(new ImageIcon(tempPic.toString()));
        
    	leftPanel.add(label);
    	leftPanel.setBackground(new Color(25,0,0));
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        frame.add(leftPanel,c);
        
        JPanel rightPanel = new JPanel();
    	JTextArea caption = new JTextArea(captionstr);
    	
    	caption.setEditable(false);
    	caption.setLineWrap(true);
    	caption.setWrapStyleWord(true);
    	caption.setFont(new Font("Veranda", Font.PLAIN,12));
    	caption.setBackground(new Color(25,0,0));
    	caption.setForeground(Color.WHITE);
    	rightPanel.add(caption);
    	c.fill = GridBagConstraints.VERTICAL;
        c.gridx = 0;
        c.gridy = 3;//sets the right panel to start in the 3rd column, so its next to the picture taking up roughly half the space
        frame.add(rightPanel);
        rightPanel.setBackground(new Color(25,0,0));
        rightPanel.setForeground(new Color(25,0,0));
        frame.getContentPane().setBackground(new Color(25,0,0) );
        frame.setVisible(true);
    	frame.pack();
	}
}
