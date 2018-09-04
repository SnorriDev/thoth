package snorri.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

import snorri.main.Debug;
import snorri.main.FocusedWindow;
import snorri.main.Main;

public class BackgroundLayer implements Layer {
	
	// TODO(lambdaviking): Probably want to make this a SavableLayer.
	
	protected BufferedImage bitmap;
	protected Vector dimensions;
	
	public static final BufferedImage DEFAULT_BACKGROUND = Main.getImage("/textures/backgrounds/splash.png");
	public static final int CUSHION = 0;
	
	public BackgroundLayer() {
		this(DEFAULT_BACKGROUND);
	}
	
	public BackgroundLayer(BufferedImage bitmap) {
		this.bitmap = bitmap;
		this.dimensions = new Vector(bitmap.getWidth(), bitmap.getHeight());
	}
	
	public static Layer fromYAML(World world, Map<String, Object> params) {
		String path = (String) params.get("path");
		Debug.logger.info("Loading " + path + "...");
		BufferedImage bitmap;
		if (path.startsWith("/")) {
			// If the path is absolute, look in the game directory.
			bitmap = Main.getImage(path);
		} else {
			// If the path is not absolute, look in the world directory.
			File file = new File(world.getDirectory(), path);
			bitmap = Main.getImage(file);
		}
		return new BackgroundLayer(bitmap);
	}
	
	@Override
	public void render(FocusedWindow<?> g, Graphics2D gr, double deltaTime, boolean renderOutside) {
		int minX, minY;
		
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

	public int getWidth() {
		return dimensions.getX();
	}
	
	public int getHeight() {
		return dimensions.getY();
	}

	@Override
	public boolean canShootOver(Vector position) {
		return true;
	}

	/** Background layers are not modified by transforms. */
	@Override
	public BackgroundLayer getTransposed() {
		return new BackgroundLayer(this.bitmap);
	}

	/** Background layers are not modified by transforms. */
	@Override
	public BackgroundLayer getXReflected() {
		return new BackgroundLayer(this.bitmap);
	}

	/** Background layers are not modified by transforms. */
	@Override
	public BackgroundLayer getResized(int newWidth, int newHeight) {
		return new BackgroundLayer(this.bitmap);
	}

}
