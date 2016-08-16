package snorri.overlay;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
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
		GridBagConstraints c = new GridBagConstraints();
		
		if (dialog.image != null) {
			add(new JLabel(new ImageIcon(Main.getImage(dialog.image))));
		}
		
		c.gridx = 0;
		c.gridy = 0;
		TextPane pane = this.new TextPane();
		pane.setPreferredSize(new Dimension(323, 200));
		pane.setText(dialog.text);
		add(pane, c);
		
		c.gridx = 0;
		c.gridy = 1;
		add(createButton("Okay"), c);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Okay")) {
			window.unpause();
		}
	}

}
