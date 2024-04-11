/*
 * Description: this defines the Image object class and its methods. unlike the Pic Obj. this object is different because the picture is set as an Image type based on AWT and is not a file. this is because
 * this class is designed for being used with the database version of the program. the image is not saved as a file on the computer but instead is saved into memory once retrieved from the database.
 */
package scpPackage;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ImageObj {
	private final int index;
	private Image picture;
	private String caption;
	private JTextArea captionArea;
	private JFrame frame;
	//constructor
	public ImageObj() {
		index = 0;
		picture = null;
		caption ="";
		initialize();
	}
	
	//alt constructor 
	public ImageObj (int index,Image picture,String caption) {
		this.index = index;
		this.picture = picture;
		this.caption = caption;
		initialize();
	}	
	public void initialize() {
		Image tempPic = this.getImage();
		String captionstr = this.getCaption();
		frame = new JFrame();
        
    	frame.setLayout(new GridBagLayout());
    	frame.setIconImage(Toolkit.getDefaultToolkit().getImage(PBar.class.getResource("/scpPackage/SCP-Logo-2.png")));
    	GridBagConstraints c = new GridBagConstraints();
    	
        JPanel leftPanel = new JPanel();
        JLabel label = new JLabel(new ImageIcon(tempPic));
        
    	leftPanel.add(label);
    	leftPanel.setBackground(new Color(25,0,0));
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        frame.add(leftPanel,c);
        
        JPanel rightPanel = new JPanel();
        captionArea = new JTextArea(captionstr);
    	
        captionArea.setEditable(false);
    	captionArea.setLineWrap(true);
    	captionArea.setWrapStyleWord(true);
    	captionArea.setFont(new Font("Veranda", Font.PLAIN,12));
    	captionArea.setBackground(new Color(25,0,0));
    	captionArea.setForeground(Color.WHITE);
    	rightPanel.add(captionArea);
    	c.fill = GridBagConstraints.VERTICAL;
        c.gridx = 0;
        c.gridy = 3;//sets the right panel to start in the 3rd column, so its next to the picture taking up roughly half the space
        frame.add(rightPanel);
        rightPanel.setBackground(new Color(25,0,0));
        rightPanel.setForeground(new Color(25,0,0));
        frame.getContentPane().setBackground(new Color(25,0,0) );
        frame.setVisible(false);
    	frame.pack();
		
	}
	
	public Image getImage() {
		return picture;
	}

	public void setPicture(Image picture) {
		this.picture = picture;
	}

	public String getCaption() {
		return caption;
	}
 public int getIndex() {
	 return index;
 }
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public String getCaptionAreaText() {
		String temp = EditorGUI.unEscapeMetaCharacters(captionArea.getText());
		return temp;
	}
	
	public void setCaptionEditable(boolean x) {
		captionArea.setEditable(x);
	}
	public void showPic() {
		frame.setVisible(true);
	}
}
