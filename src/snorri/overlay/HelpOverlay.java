package snorri.overlay;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;

import snorri.main.FocusedWindow;
import snorri.main.Main;

public class HelpOverlay extends Overlay {

	private static final long serialVersionUID = 1L;
	
	public HelpOverlay(FocusedWindow window) {
		
		super(window);
		
		setLayout(new GridBagLayout());
		
		TextPane display = this.new TextPane();
		try {
			display.setPage(Main.getFile("/info/index.html").toURI().toURL());
		} catch (IOException e) {
			Main.error("could not find HTML info page");
			e.printStackTrace();
		}
		add(display);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Okay")) {
			window.unpause();
		}
	}

}
