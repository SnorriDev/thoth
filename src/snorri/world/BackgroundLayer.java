package snorri.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;

import snorri.main.FocusedWindow;
import snorri.main.Main;

public class BackgroundLayer implements Layer {
	
	protected BufferedImage bitmap;
	protected Vector dimensions;
	
	public static final BufferedImage DEFAULT_BACKGROUND = Main.getImage("/textures/tiles/default_background00.png");
	public static final int CUSHION = 0;
	
	public BackgroundLayer() {
		this(DEFAULT_BACKGROUND);
	}
	
	public BackgroundLayer(BufferedImage bitmap) {
		this.bitmap = bitmap;
		this.dimensions = new Vector(bitmap.getWidth(), bitmap.getHeight());
	}
	
	public static Layer fromYAML(Map<String, Object> params) {
		String path = (String) params.get("path");
		BufferedImage bitmap = Main.getImage(path);
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
