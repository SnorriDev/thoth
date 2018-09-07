package snorri.world;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Array;

import snorri.audio.ClipWrapper;
import snorri.events.SpellEvent;
import snorri.main.Debug;
import snorri.main.Util;
import snorri.semantics.Nominal;

public interface TileType extends Nominal {
	
	public static class Param<T> {
		
		protected enum Key {
			IS_NOT_SURFACE, //TODO, we need to change this so it's not a double negative
			SWIMMABLE,
			CHANGABLE,
			AT_TOP,
			CAN_SHOOT_OVER,
			BLEND_ORDER,
			REPLACEMENT_TYPE,
			OPEN_TYPE;
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
		
		public static Param<Boolean> isNotSurface(Boolean value) {
			return new Param<>(Key.IS_NOT_SURFACE, value);
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
		
	int getId();
	
	BufferedImage[] getTextures();
	
	BufferedImage getTexture(int index);
	
	int getNumberStyles();
	
	@Override
	String toString();
	
	ArrayList<Tile> getAllStyles();
		
	default boolean hasSounds() {
		return Tile.sounds.length >= 1;
	}
	
	default int getNumSounds() {
		return Tile.sounds.length;
	}
	
	default ClipWrapper[] getSounds() {
		return Tile.sounds;
	}
	
	default ClipWrapper getSound(int x) {
		if (x < Tile.sounds.length)
			return Tile.sounds[x];
		else {
			Debug.logger.warning("Audio clip index out of bounds.");
			return null;
		}
	}
	
	String name();
	
	boolean isNotSurface();
	
	boolean canShootOver();
	
	boolean isAtTop();
	
	boolean isChangable();
	
	public boolean isSwimmable();
	
	public int ordinal();
	
	@Deprecated
	default int getOrdinal() {
		return ordinal();
	}
	
	@Override
	public default Nominal get(AbstractSemantics attr, SpellEvent e) {
		if (attr == AbstractSemantics.FLOOD && isSwimmable()) {
			return new Tile(this);
		}
		
		if (attr == AbstractSemantics.STORM && this == UnifiedTileType.SAND) {
			return new Tile(this, 1);
		}
		
		return Nominal.super.get(attr, e);
	}
	
	static BufferedImage[] getReflections(BufferedImage image) {
		
		List<BufferedImage> out = new ArrayList<>();
		
		final boolean[] FLIP_OPTIONS = new boolean[] { false, true };
		for (boolean flipY : FLIP_OPTIONS) {
			for (boolean flipX : FLIP_OPTIONS) {
				out.add(Util.getFlipped(image, flipX, flipY));
			}
		}
		
		return out.toArray(new BufferedImage[0]);
		
	}
	
	public static BufferedImage[] getRotations(BufferedImage image) {
		
		List<BufferedImage> out = new ArrayList<>();
		
		for (double theta = 0; theta < Math.PI * 2; theta += Math.PI / 2) {
			out.add(Util.getRotated(image, theta));
		}
		
		return out.toArray(new BufferedImage[0]);
		
	}
	
	public static BufferedImage[] getTwoRotations(BufferedImage image) {
		
		List<BufferedImage> out = new ArrayList<>();
		
		for (double theta = 0; theta < Math.PI; theta += Math.PI / 2) {
			out.add(Util.getRotated(image, theta));
		}
		
		return out.toArray(new BufferedImage[0]);
		
	}
	
	public TileType getReplacement();
	
	public double getBlendOrder();
	
	/**
	* <p>Adds all the elements of the given arrays into a new array.</p>
	* <p>The new array contains all of the element of {@code array1} followed
	* by all of the elements {@code array2}. When an array is returned, it is always
	* a new array.</p>
	*
	* <pre>
	* ArrayUtils.addAll(null, null)     = null
	* ArrayUtils.addAll(array1, null)   = cloned copy of array1
	* ArrayUtils.addAll(null, array2)   = cloned copy of array2
	* ArrayUtils.addAll([], [])         = []
	* ArrayUtils.addAll([null], [null]) = [null, null]
	* ArrayUtils.addAll(["a", "b", "c"], ["1", "2", "3"]) = ["a", "b", "c", "1", "2", "3"]
	* </pre>
	*
	* @param <T> the component type of the array
	* @param array1  the first array whose elements are added to the new array, may be {@code null}
	* @param array2  the second array whose elements are added to the new array, may be {@code null}
	* @return The new array, {@code null} if both arrays are {@code null}.
	*      The type of the new array is the type of the first array,
	*      unless the first array is null, in which case the type is the same as the second array.
	* @since 2.1
	* @throws IllegalArgumentException if the array types are incompatible
	*/
	@SuppressWarnings("unchecked")
	public static <T> T[] addAll(T[] array1, T... array2) {
		if (array1 == null) {
			return addAll(array2);
		}
		else if (array2 == null) {
			return array1;
		}
		final Class<?> type1 = array1.getClass().getComponentType();
		T[] joinedArray = (T[]) Array.newInstance(type1, array1.length + array2.length);
		System.arraycopy(array1, 0, joinedArray, 0, array1.length);
		try {
			System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
		}
		catch (ArrayStoreException ase) {
			// Check if problem was due to incompatible types
			/*
			  * We do this here, rather than before the copy because:
			   * - it would be a wasted check most of the time
			   * - safer, in case check turns out to be too strict
			   */
			final Class<?> type2 = array2.getClass().getComponentType();
			if (!type1.isAssignableFrom(type2)) {
				throw new IllegalArgumentException("Cannot store " + type2.getName() + " in an array of " + type1.getName(), ase);
			}
			throw ase; // No, so rethrow original
		}
		return joinedArray;
	}
	
}
