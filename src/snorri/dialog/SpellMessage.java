package snorri.dialog;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import snorri.main.Main;
import snorri.windows.GameWindow;

public class SpellMessage extends Message {

	private static final BufferedImage ICON = Main.getImage("/textures/hud/horus.png");
	
	private final Object output;
	private final String header;
	
	public SpellMessage(String header, Object output, boolean isStatement) {
		super(ICON);
		this.output = output;
		//TODO(#20): It might be useful to have an enum return value here.
		this.header = header + (isStatement ? " output" : " result");
		success = output != null && !new Boolean(false).equals(output);
	}
	
	public SpellMessage(Object output, boolean isStatement) {
		this("", output, isStatement);
	}
	
	@Override
	public String toString() {
		return success ? format(header, output) : "nothing";
	}
	
	@Override
	public int render(GameWindow window, Graphics gr, int xTrans) {
		if (output == null) {
			return 0;
		}
		return drawLineWithIcon(output.toString(), gr, window, xTrans, getSuccessColor());
	}
	
	/**
	 * Format information to be printed in the game log
	 * @param header
	 * 	The header to go in front of the spell
	 * @param output
	 * 	The result of the evaluated spell
	 * @return formatted text
	 */
	public static final String format(String header, Object output) {
		StringBuilder formatted = new StringBuilder(header);
		formatted.append(": ");
		formatted.append(output.toString());
		return formatted.toString();
	}

}
