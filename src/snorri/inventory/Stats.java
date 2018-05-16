package snorri.inventory;

import java.io.Serializable;

import snorri.entities.Entity;

public class Stats implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Entity ent;
	
	private double strength;
	private double defense;
	private double intelligence;
	private double attention;
	
	public static final double DEFAULT_STRENGTH = 0;
	public static final double DEFAULT_DEFENSE = 0;
	public static final double DEFAULT_INTELLIGENCE = 0;
	public static final double DEFAULT_ATTENTION = 0;
	
	public Stats(Entity ent, double strength, double defense, double intelligence, double attention) {
		this.ent = ent;
		this.strength = strength;
		this.defense = defense;
		this.intelligence = intelligence;
		this.attention = attention;
	}
	
	public Stats(Entity ent) {
		this(ent, DEFAULT_STRENGTH, DEFAULT_DEFENSE, DEFAULT_INTELLIGENCE, DEFAULT_ATTENTION);
	}

	public double getStrength() {
		return strength;
	}
	
	public double getDefense() {
		return defense;
	}
	
	public double getIntelligence() {
		return intelligence;
	}
	
	public double getAttention() {
		return attention;
	}
	
	public void incrStrength() {
		strength++;
	}
	
	public void incrDefense() {
		defense++;
	}
	
	public void incrIntelligence() {
		intelligence++;
	}
	
	public void incrAttention() {
		attention++;
	}

	public Entity getEnt() {
		return ent;
	}

	public double getMaxHealth() {
		return 100 + 10 * defense;
	}
	
	public double getMaxMana() {
		return 100 + 10 * intelligence;
	}
	
	public double getManaRegen() {
		return attention;
	}

}
