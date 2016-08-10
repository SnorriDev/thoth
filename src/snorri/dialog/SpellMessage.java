package snorri.dialog;

import java.awt.Graphics;

import snorri.main.GameWindow;
import snorri.main.Main;

public class SpellMessage extends Message {

	private final Object output;
	
	public SpellMessage(Object output) {
		super();
		this.output = output;
	}
	
	@Override
	public int render(GameWindow window, Graphics gr, int xTrans) {
		// TODO Auto-generated method stub
		Main.log(output);
		return 0;
	}

}
