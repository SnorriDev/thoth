package snorri.inventory;

import java.awt.Image;
import java.io.Serializable;

public class ManaManager implements Serializable, Droppable {
	
	/** Any object that uses mana should create an instance of this.
	 * 
	 * Example usage:
	 * 
	 * ```
	 * ManaManager mana = new ManaManager();
	 * if (mana.spendIfHasMana(50)) {
	 *     // Cast the spell.
	 * }
	 * ```
	 * */
	
	private static final long serialVersionUID = 1L;
	private double maxMana;
	private double manaRegen;
	private double mana;

	public ManaManager(double maxMana, double manaRegen) {
		this.maxMana = maxMana;
		this.manaRegen = manaRegen;
		mana = this.maxMana;
	}
	
	public ManaManager() {
		this(100d, 20d);
	}
	
	public void setMaxMana(final double maxMana) {
		this.maxMana = maxMana;
	}
	
	public void setManaRegen(final double manaRegen) {
		this.manaRegen = manaRegen;
	}
	
	public double getMana() {
		return mana;
	}
	
	public boolean spendIfHasMana(double cost) {
		/** Return true if mana was spent, otherwise return false. */
		if (mana >= cost) {
			mana -= cost;
			return true;
		}
		return false;
	}
	
	public void update(double deltaTime) {
		mana = Math.max(mana + deltaTime * manaRegen, maxMana);
	}

	@Override
	public int getMaxQuantity() {
		return 1;
	}

	@Override
	public boolean stack(Droppable other) {
		return false;
	}

	@Override
	public Image getTexture() {
		return null;
	}

	@Override
	public Droppable copy() {
		return new ManaManager(maxMana, manaRegen);
	}

	public static Droppable fromString(String raw) {
		if (raw == "!mana") {
			return new ManaManager();
		}
		else if (raw.startsWith("!mana")) {
			String[] pieces = raw.split(":");
			int maxMana = Integer.parseInt(pieces[1]);
			int manaRegen = Integer.parseInt(pieces[2]);
			return new ManaManager(maxMana, manaRegen);
		}
		else {
			return null;
		}
	}
	
}
