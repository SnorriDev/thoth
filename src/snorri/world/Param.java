package snorri.world;

public class Param<T> {
	
	/**Parameters for creating UnifiedTileTypes.*/
	
	protected enum Key {
		IS_OCCUPIED, SWIMMABLE, CHANGABLE, AT_TOP, CAN_SHOOT_OVER, BLEND_ORDER, REPLACEMENT_TYPE, REPLACEMENT_TILE, DAMAGE;
	}
	
	protected final Param.Key key;
	protected final T value;
	
	protected Param(Param.Key key, T value) {
		this.key = key;
		this.value = value;
	}
	
	public Param.Key getKey() {
		return key;
	}
	
	public T getValue() {
		return value;
	}
	
	public static Param<Boolean> isOccupied(Boolean value) {
		return new Param<>(Key.IS_OCCUPIED, value);
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
	
	public static Param<Tile> replacementTile(Tile value) {
		return new Param<>(Key.REPLACEMENT_TILE, value);
	}
	
	public static Param<Double> damage(double value) {
		return new Param<>(Key.DAMAGE, value);
	}
}