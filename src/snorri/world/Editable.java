package snorri.world;

import java.awt.Graphics2D;

import snorri.main.FocusedWindow;

/**
 * A class that can be viewed and edited in the level editor.
 * @author snorri
 * 
 * This interface intersects (but is separate from) Layer. For example, World is an Editable but not a Layer.
 * 
 * It describes objects that can be:
 * 	* Loaded in the level editor.
 *  * Saved in the level editor.
 *  * Displayed in the level editor.
 * 	* Edited via various transforms (resizing, reflection, transposition).
 *
 */
public interface Editable extends Loadable, Savable, Renderable {
	
	/** This method is declared in Renderable but written here to be explicit. */
	@Override
	public void render(FocusedWindow<?> levelEditor, Graphics2D gr, double deltaTime, boolean b);
	
	/** Return the main tile layer in which intersections are checked. */
	public TileLayer getTileLayer();
		
	public WorldGraph getWorldGraph();
	
	@Override
	public Editable getTransposed();
	@Override
	public Editable getXReflected();
	@Override
	public Editable getResized(int newWidth, int newHeight);
		
}