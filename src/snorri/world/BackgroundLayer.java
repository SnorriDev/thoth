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
	
	protected World world;
	protected BufferedImage bitmap;
	
	public static final BufferedImage DEFAULT_BACKGROUND = Main.getImage("/textures/backgrounds/splash.png");
	public static final int CUSHION = 0;
	
	public BackgroundLayer(World world, BufferedImage bitmap) {
		this.bitmap = bitmap;
		this.world = world;
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
		return new BackgroundLayer(world, bitmap);
	}
	
	@Override
	public void render(FocusedWindow<?> window, Graphics2D gr, double deltaTime, boolean renderOutside) {
		Vector center = window.getCenterObject().getPos();
		Vector windowDimensions = window.getDimensions();
		Vector origin = windowDimensions.divide(2).sub(center);
		
		BufferedImage clipped = bitmap.getSubimage(0, 0, bitmap.getWidth(), world.getHeight());
		
		for (int x = 0; x < world.getWidth(); x += bitmap.getWidth()) {
			if (x + bitmap.getWidth() >= world.getWidth()) {
				clipped = clipped.getSubimage(0, 0, world.getWidth() - x, clipped.getHeight());
			}
			gr.drawImage(clipped, origin.getX() + x, origin.getY(), null);
		}
	}

	public int getWidth() {
		return world.getWidth();
	}
	
	public int getHeight() {
		return world.getHeight();
	}

	@Override
	public boolean canShootOver(Vector position) {
		return true;
	}

	/** Background layers are not modified by transforms. */
	@Override
	public BackgroundLayer getTransposed() {
		return new BackgroundLayer(this.world, this.bitmap);
	}

	/** Background layers are not modified by transforms. */
	@Override
	public BackgroundLayer getXReflected() {
		return new BackgroundLayer(this.world, this.bitmap);
	}

	/** Background layers are not modified by transforms. */
	@Override
	public BackgroundLayer getResized(int newWidth, int newHeight) {
		return new BackgroundLayer(this.world, this.bitmap);
	}

}
