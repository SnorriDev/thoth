package snorri.entities;

import snorri.animations.Animation;
import snorri.dialog.Dialog;
import snorri.events.InteractEvent;
import snorri.main.FocusedWindow;
import snorri.main.GamePanel;
import snorri.main.Main;
import snorri.world.Vector;

public class NPC extends AIUnit {
	
	private static final long serialVersionUID = 1L;
	private static final Dialog DEFAULT_DIALOG = new Dialog();
	private static final Animation IDLE = new Animation("/textures/animations/unit/idle");
	private static final Animation WALKING = new Animation("/textures/animations/unit/walking");
	
	static {
		DEFAULT_DIALOG.text = "<p>After you!</p>";
		DEFAULT_DIALOG.image = "priest";
		DEFAULT_DIALOG.name = "Imhotep";
	}
		
	private Dialog dialog;

	public NPC(Vector pos) {
		this(pos, IDLE, WALKING, DEFAULT_DIALOG);
	}
	
	public NPC(Vector pos, Animation idle, Animation walking, Dialog dialog) {
		super(pos, null, idle, walking);
		this.dialog = dialog;
	}
	
	public void setDialog(Dialog dialog) {
		this.dialog = dialog;
	}
	
	public Dialog getDialog() {
		return dialog;
	}
	
	public void speak() {
		GamePanel window = Main.getWindow();
		if (window instanceof FocusedWindow) {
			((FocusedWindow) window).showDialog(dialog);
		}
	}
	
	@Override
	public void onInteract(InteractEvent e) {
		speak();
	}
	
	@Override
	public Mode getDefaultMode() {
		return Mode.SEEK;
	}
	
	@Override
	public int getBaseSpeed() {
		return Player.PLAYER_BASE_SPEED;
	}
	
	@Override
	public void updateEntityStats() {
		super.updateEntityStats();
		stopRange = 200;
	}
}
