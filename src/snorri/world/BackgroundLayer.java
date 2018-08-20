package snorri.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import snorri.entities.Entity;
import snorri.main.Debug;
import snorri.main.FocusedWindow;
import snorri.main.Main;

public class BackgroundLayer implements Editable {
	
	protected BufferedImage bitmap;
	protected Vector dimensions;
	
	public static final BufferedImage DEFAULT_BACKGROUND = Main.getImage("/textures/tiles/default_background00.png");
	public static final Vector DEFAULT_SIZE = World.DEFAULT_LEVEL_SIZE;
	public static final int CUSHION = 0;
	
	public BackgroundLayer() {
		this(DEFAULT_BACKGROUND, DEFAULT_SIZE);
	}
	
	public BackgroundLayer(BufferedImage bitmap, Vector size) {
		this.bitmap = bitmap;
		this.dimensions = size;
	}
	
	private static BufferedImage getImage(String string) {
		return Main.getImage("/textures/tiles/background/" + string);
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
	public void render(FocusedWindow<?> g, Graphics2D gr, double deltaTime, boolean renderOutside) {
		int minX, maxX, minY, maxY;
		
		Vector center = g.getCenterObject().getPos();
		Vector dim = g.getDimensions();
		
		if (bitmap == null) {
			return;
		}

		minX = center.getX() - dim.getX() / 2;
		minY = center.getY() - dim.getY() / 2;
		int adjMinX = Math.max(0, minX), adjMinY = Math.max(0, minY);
		BufferedImage image = bitmap.getSubimage(adjMinX, adjMinY, Math.min(bitmap.getWidth() - adjMinX, dim.getX()), Math.min(bitmap.getHeight() - adjMinY, dim.getY()));
		
		gr.drawImage(image, Math.max(0, -minX), Math.max(0, -minY), null);		
	}
	
	@Override
	public Level getLevel() {
		return null;
	}
	
	@Override
	public Level getLevel(int layer) {
		return null;
	}
	
	@Override
	public Level getLevel(Class<? extends TileType> c) {
		return null;
	}
	
	@Override
	public void resize(int newWidth, int newHeight) {
		dimensions = new Vector(newWidth, newHeight);
	}
	
	@Override
	public BackgroundLayer getTransposed() {
		return this;
	}
	
	@Override
	public BackgroundLayer getXReflected() {
		return this;
	}
	
	@Override
	public List<Entity> getEntities() {
		return null;
		//return new List<Entity>;
	}
	
	@Override
	public Vector getDimensions() {
		return dimensions;
	}
	
	@Override
	public WorldGraph getWorldGraph() {
		return null;
	}

	public BackgroundLayer getResized(int newWidth, int newHeight, int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getWidth() {
		return dimensions.getX();
	}
	
	public int getHeight() {
		return dimensions.getY();
	}
}
