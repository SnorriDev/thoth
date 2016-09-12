package snorri.overlay;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.JComponent;

import snorri.dialog.Dialog;
import snorri.main.FocusedWindow;

public class DialogOverlay extends Overlay {

	private static final long serialVersionUID = 1L;
	
	private static final Point POSITION = new Point(30, 40);
	
	public DialogOverlay(FocusedWindow focusedWindow) {
		
		super(focusedWindow);
		setLayout(null);
		
		Dialog test = new Dialog();
		test.image = "thoth";
		
		JComponent pane = new DialogPane(test);
		Dimension bounds = pane.getPreferredSize();
		pane.setBounds(POSITION.x, POSITION.y, bounds.width, bounds.height);
		add(pane);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

}
