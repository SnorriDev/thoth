package snorri.overlay;

import java.awt.GridBagLayout;
import java.io.IOException;
import java.util.logging.Level;

import snorri.main.Debug;
import snorri.main.Main;
import snorri.windows.FocusedWindow;

public class HelpOverlay extends Overlay {

	private static final long serialVersionUID = 1L;
	
	public HelpOverlay(FocusedWindow<?> window) {
		
		super(window);
		
		setLayout(new GridBagLayout());
		
		TextPane display = this.new TextPane();
		try {
			display.setPage(Main.getFile("/info/index.html").toURI().toURL());
		} catch (IOException e) {
			Debug.logger.log(Level.SEVERE, "Could not load help page.", e);
		}
		add(display);
		
	}

}
