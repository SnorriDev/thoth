package snorri.overlay;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;

import snorri.main.Main;
import snorri.windows.FocusedWindow;
import snorri.windows.MenuPanel;

public class PauseOverlay extends Overlay {

	private static final long serialVersionUID = 1L;
	
	public PauseOverlay(FocusedWindow<?> focusedWindow) {
		super(focusedWindow);
		setLayout(new GridBagLayout());
		JPanel menu = new MenuPanel();
		menu.setOpaque(false);
		menu.add(createButton("SAVE"));
		menu.add(createButton("BACK"));
		menu.add(createButton("OBJECTIVE"));
		menu.add(createButton("HELP"));
		menu.add(createButton("QUIT"));
		
		add(menu, new GridBagConstraints());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("SAVE")) {
			window.getUniverse().wrapSave();
		}
		if (e.getActionCommand().equals("BACK")) {
			window.unpause();
		}
		if (e.getActionCommand().equals("HELP")) {
			Main.setOverlay(new HelpOverlay(window));
		}
		if (e.getActionCommand().equals("QUIT")) {
			//TODO save
			Main.launchMenu();
		}
	}
	
}
