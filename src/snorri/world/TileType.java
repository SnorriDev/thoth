package snorri.world;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import snorri.audio.ClipWrapper;
import snorri.main.Util;
import snorri.semantics.Nominal;

public interface TileType extends Nominal {
	
	public static class Param<T> {
	  
		protected enum Key {
			PATHABLE, SWIMMABLE, CHANGABLE, AT_TOP, CAN_SHOOT_OVER, BLEND_ORDER, REPLACEMENT_TYPE, OPEN_TYPE;
			//TODO use REPLACEMENT_TYPE == null instead of CHANGABLE
		}
		
		protected final Key key;
		protected final T value;
		
		protected Param(Key key, T value) {
			this.key = key;
			this.value = value;
		}
		
		public Key getKey() {
			return key;
		}
		
		public T getValue() {
			return value;
		}
		
		public static Param<Boolean> pathable(Boolean value) {
			return new Param<>(Key.PATHABLE, value);
		}
		
		public static Param<Boolean> swimmable(Boolean value) {
			return new Param<>(Key.SWIMMABLE, value);
		}
		
		public static Param<Boolean> changable(Boolean value) {
			return new Param<>(Key.CHANGABLE, value);
		}
		
		public static Param<Boolean> atTop(Boolean value) {
			return new Param<>(Key.AT_TOP, value);
		}
		
		public static Param<Boolean> canShootOver(Boolean value) {
			return new Param<>(Key.CAN_SHOOT_OVER, value);
		}
		
		public static Param<Double> blendOrder(Double value) {
			return new Param<>(Key.BLEND_ORDER, value);
		}
		
		public static Param<TileType> replacementType(TileType value) {
			return new Param<>(Key.REPLACEMENT_TYPE, value);
		}
		
		public static Param<TileType> openType(TileType value) {
			return new Param<>(Key.OPEN_TYPE, value);
		}
	}
	
	// use a method like this to set parameters on TileType
	public void setParam(Param<?> param);
	
	//BufferedImage getImage(String string);
	
	TileType byId(int id);
	
	int getId();
	
	BufferedImage[] getTextures();
	
	BufferedImage getTexture(int index);
	
	int getNumberStyles();
	
	@Override
	String toString();
		
	ArrayList<Tile> getSubTypes();
	
	boolean hasSounds();
	
	int getNumSounds();
	
	ClipWrapper[] getSounds();
	
	public ClipWrapper getSound(int x);
	
	String name();

	boolean isPathable();

	boolean canShootOver();

	boolean isAtTop();
	
	boolean isChangable();
	
	int getLayer();

	public static TileType lookup(Class<? extends TileType> c, int id) {
		
		if (c.equals(BackgroundElement.class)) {
			return BackgroundElement.byIdStatic(id);
		}
		
		if (c.equals(MidgroundElement.class)) {
			return MidgroundElement.byIdStatic(id);
		}
		
		if (c.equals(ForegroundElement.class)) {
			return ForegroundElement.byIdStatic(id);
		}
		
		return null;
		
	}
	
	public static TileType lookup(int layer, int id) {
		
		if (layer == 0) {
			return BackgroundElement.byIdStatic(id);
		}
		
		if (layer == 1) {
			return MidgroundElement.byIdStatic(id);
		}
		
		if (layer == 2) {
			return ForegroundElement.byIdStatic(id);
		}
		
		return null;
		
	}
	
	public boolean isLiquid();
	
	public int ordinal();
	
	@Deprecated
	default int getOrdinal() {
		return ordinal();
	}
	
	@Override
	public default Object get(World world, AbstractSemantics attr) {
		if (attr == AbstractSemantics.FLOOD && isLiquid()) {
			return new Tile(this);
		}
		
		if (attr == AbstractSemantics.STORM && this == BackgroundElement.SAND) {
			return new Tile(this, 1);
		}
		
		return Nominal.super.get(world, attr);
	}
	
	public static TileType[] getValues(Class<? extends TileType> c) {
		
		if (BackgroundElement.class.isAssignableFrom(c)) {
			return BackgroundElement.values();
		}
				
		if (MidgroundElement.class.isAssignableFrom(c)) {
			return MidgroundElement.values();
		}
		
		if (ForegroundElement.class.isAssignableFrom(c)) {
			return ForegroundElement.values();
		}
		
		return null;
		
	}
	
	static BufferedImage[] getReflections(BufferedImage image) {
		
		List<BufferedImage> out = new ArrayList<>();
		
		final boolean[] FLIP_OPTIONS = new boolean[] {false, true};
		for (boolean flipY : FLIP_OPTIONS) {
			for (boolean flipX : FLIP_OPTIONS) {
				out.add(Util.getFlipped(image, flipX, flipY));
			}
		}
		
		return out.toArray(new BufferedImage[0]);
		
	}
	
	static BufferedImage[] getRotations(BufferedImage image) {
		
		List<BufferedImage> out = new ArrayList<>();
		
		for (double theta = 0; theta < Math.PI * 2; theta += Math.PI / 2) {
			out.add(Util.getRotated(image, theta));
		}
		
		return out.toArray(new BufferedImage[0]);
		
	}

	public TileType getReplacement();
	
	public double getBlendOrder();
	
}
