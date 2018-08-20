package snorri.world;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import snorri.entities.Entity;
import snorri.main.FocusedWindow;

public class BackgroundImageLayer implements Layer {
	
	public BackgroundImageLayer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void render(FocusedWindow<?> levelEditor, Graphics2D gr, double deltaTime, boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Level getLevel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Level getLevel(int layer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Level getLevel(Class<? extends TileType> c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resize(int newWidth, int newHeight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Entity> getEntities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector getDimensions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WorldGraph getWorldGraph() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void load(File folder) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(File f, boolean recomputeGraphs) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Layer getTransposed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Layer getXReflected() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Layer getResized(int newWidth, int newHeight, int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canShootOver(Vector g) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
