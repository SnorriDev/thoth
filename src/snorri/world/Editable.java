package snorri.world;

import java.awt.Graphics;

import snorri.main.FocusedWindow;

public interface Editable extends Savable {

	public void render(FocusedWindow levelEditor, Graphics gr, boolean b);

	public Level getLevel();

	public void resize(int newWidth, int newHeight);
	
//	public Editable getTransposed();
//	
//	public Editable getXReflected();
	
}