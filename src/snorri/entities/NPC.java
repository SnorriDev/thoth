package snorri.entities;

import snorri.animations.Animation;
import snorri.collisions.Collider;
import snorri.world.Vector;

public abstract class NPC extends Unit {
	
	private static final long serialVersionUID = 5405304168680948365L;
	
	private java.util.Vector<String> dialogue;

	public NPC(Vector pos, Animation idle, Animation walking, java.util.Vector<String> dialogue) {
		super(pos, idle, walking);
		this.dialogue = dialogue;
	}
	
	public NPC(Vector pos, Animation idle, Animation walking, Animation attack, java.util.Vector<String> dialogue) {
		super(pos, idle, walking, attack);
		this.dialogue = dialogue;
	}
	
	public NPC(Vector pos, Collider c, java.util.Vector<String> dialogue) {
		super(pos, c);
		this.dialogue = dialogue;
	}
	
	public NPC(Vector pos, Animation idle, Animation walking) {
		this(pos, idle, walking, new java.util.Vector<String>());
	}
	
	public NPC(Vector pos, Animation idle, Animation walking, Animation attack) {
		this(pos, idle, walking, attack, new java.util.Vector<String>());
	}
	
	public NPC(Vector pos, Collider c) {
		this(pos, c, new java.util.Vector<String>());
	}
	
	public java.util.Vector<String> getDialogue() {
		return dialogue;
	}
	
	public String getDialogue(int idx) {
		return dialogue.elementAt(idx);
	}
	
	public void speak() {
		
	}
}
