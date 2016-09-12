package snorri.overlay;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.JComponent;

import snorri.dialog.Dialog;
import snorri.main.FocusedWindow;
import snorri.main.GamePanel;
import snorri.main.Main;

public class DialogOverlay extends Overlay {

	private static final long serialVersionUID = 1L;
		
	public DialogOverlay(FocusedWindow focusedWindow) {
		
		super(focusedWindow);
		setLayout(null);
		
		Dialog test = new Dialog();
		test.image = "thoth";
		
		JComponent pane = new DialogPane(test);
		Dimension bounds = pane.getPreferredSize();
		
		GamePanel window = Main.getWindow();
		pane.setBounds((window.getWidth() - bounds.width) / 2, window.getHeight() - bounds.height - GamePanel.MARGIN, bounds.width, bounds.height);
		add(pane);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

}
