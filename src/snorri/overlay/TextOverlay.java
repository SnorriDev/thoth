package snorri.overlay;

import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import snorri.dialog.Dialog;
import snorri.main.FocusedWindow;
import snorri.main.Main;

public class TextOverlay extends Overlay {

	private static final long serialVersionUID = 1L;
	
	public TextOverlay(FocusedWindow<?> focusedWindow, Dialog dialog) {
		super(focusedWindow);
		
		setLayout(new GridBagLayout());
		
		if (dialog.image != null) {
			add(new JLabel(new ImageIcon(Main.getImage(dialog.image))));
		}
		
		TextPane pane = this.new TextPane();
		pane.setText(dialog.text);
		add(pane);
		
	}

}
