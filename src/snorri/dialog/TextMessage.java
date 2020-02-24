package snorri.dialog;

import java.awt.Graphics;
import java.awt.Image;

import snorri.windows.GameWindow;

public class TextMessage extends Message {

	private final String text;
	
	public TextMessage(Image image, String text, boolean success, Runnable onClear) {
		super(image);
		this.text = text;
		this.success = success;
		this.onClear = onClear;
	}
	
	public TextMessage(String text) {
		this(null, text, true, null);
	}
	
	@Override
	public String toString() {
		return text;
	}
	
	/**
	 * @return the height of the line
	 */
	public int render(GameWindow window, Graphics gr, int xTrans) {
		if (hasIcon()) {
			return drawLineWithIcon(toString(), gr, window, xTrans);
		}
		return super.render(window, gr, xTrans);
	}

}
