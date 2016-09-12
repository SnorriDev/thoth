package snorri.dialog;

import java.awt.Image;

import javax.swing.ImageIcon;

/**
 * Wrapper class to read dialog and other HTML text from YAML file
 */

public class Dialog {
	
	/**HTML text to show*/
	public String text = "";
	/**Image for actual dialog*/
	public String image = null;
		
	public Dialog() {
	}
	
	public Image getImage() {
		return Portraits.get(image);
	}

	public ImageIcon getIcon() {
		return new ImageIcon(getImage());
	}
		
}
