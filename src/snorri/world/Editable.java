package snorri.world;

import java.awt.Graphics;
import java.util.List;

import snorri.entities.Entity;
import snorri.main.FocusedWindow;

public interface Editable extends Savable {

	public void render(FocusedWindow levelEditor, Graphics gr, boolean b);

	public Level getLevel();

	public void resize(int newWidth, int newHeight);
	
	public Editable getTransposed();
	
	public Editable getXReflected();

	public List<Entity> getEntities();
		
}