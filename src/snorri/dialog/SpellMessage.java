package snorri.dialog;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import snorri.main.GameWindow;
import snorri.main.Main;

public class SpellMessage extends Message {

	private static final BufferedImage ICON = Main.getImage("/textures/hud/horus.png");
	
	private final Object output;
	
	public SpellMessage(Object output) {
		super(ICON);
		this.output = output;
		success = output != null;
	}
	
	@Override
	public String toString() {
		return success ? ("spell: " + output) : "nothing";
	}
	
	@Override
	public int render(GameWindow window, Graphics gr, int xTrans) {
		if (output == null) {
			Main.error("null spell message");
			return 0;
		}
		return drawLineWithIcon(output.toString(), gr, window, xTrans);
	}

}
