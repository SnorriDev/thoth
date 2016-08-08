package snorri.overlay;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JTextPane;

import snorri.main.FocusedWindow;
import snorri.main.Main;

public class HelpOverlay extends Overlay {

	private static final long serialVersionUID = 1L;
	
	public HelpOverlay(FocusedWindow window) {
		
		super(window);
		
		setLayout(new GridBagLayout());
		
		JTextPane display = new JTextPane();
		display.setContentType("text/html");
		display.setEditable(false);
		display.addKeyListener(this);
		display.setBorder(BorderFactory.createLineBorder(BORDER));
		display.setBackground(NORMAL_BG);
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
	}

}
