package snorri.world;

import java.awt.Graphics2D;
import java.util.List;

import snorri.entities.Entity;
import snorri.main.FocusedWindow;

public interface Editable extends Savable {

	public void render(FocusedWindow<?> levelEditor, Graphics2D gr, double deltaTime, boolean b);

	public Level getLevel();
	
	public Level getLevel(int layer);
	
	public Level getLevel(Class<? extends TileType> c);

	public void resize(int newWidth, int newHeight);
	
	public Editable getTransposed();
	
	public Editable getXReflected();

	public List<Entity> getEntities();
	
	public Vector getDimensions();
	
	public WorldGraph getWorldGraph();
		
}