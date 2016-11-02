package snorri.overlay;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import snorri.main.FocusedWindow;
import snorri.main.GameWindow;
import snorri.main.Main;

public class ObjectiveOverlay extends Overlay {

	private static final long serialVersionUID = 1L;

	protected ObjectiveOverlay(FocusedWindow focusedWindow) {
		super(focusedWindow);
		setLayout(new GridBagLayout());
		TextPane display = this.new TextPane();
		if (Main.getWindow() instanceof GameWindow) {
			display.setText(((GameWindow) Main.getWindow()).getObjectiveInfo());
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
