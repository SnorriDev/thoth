package snorri.overlay;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import snorri.dialog.Dialog;
import snorri.main.FocusedWindow;
import snorri.main.Main;

public class DialogOverlay extends Overlay {

	private static final long serialVersionUID = 1L;
	
	public DialogOverlay(FocusedWindow focusedWindow, Dialog dialog) {
		super(focusedWindow);
		
		setLayout(new GridBagLayout());
		
		if (dialog.image != null) {
			add(new JLabel(new ImageIcon(Main.getImage(dialog.image))));
		}
		
		TextPane pane = this.new TextPane();
		pane.setText(dialog.text);
		add(pane);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Okay")) {
			window.unpause();
		}
	}

}
