package snorri.inventory;

import java.awt.Color;
import java.awt.Graphics;

import snorri.dialog.SpellMessage;
import snorri.dialog.TextMessage;
import snorri.entities.BossAIUnit;
import snorri.entities.Player;
import snorri.events.CastEvent;
import snorri.events.CastEvent.Caster;
import snorri.main.Main;
import snorri.windows.GameWindow;
import snorri.world.Vector;
import snorri.world.World;

public class Papyrus extends Item {

	private static final long serialVersionUID = 1L;
	private static final Color PAPYRUS_COOLDOWN_COLOR = new Color(118, 45, 50, 150);
	private static final double PAPYRUS_COOLDOWN = 1;
	
	private boolean ignoreMessages = false;
	/** This variable is unused, but not removed, because it would fuck with djanky serialization scheme. */
	private int numPapyri;
	
	public Papyrus(ItemType t) {
		super(t);
		timer = new Timer(PAPYRUS_COOLDOWN);
	}

	public void cast(World world, Caster player, Vector castPos) {
		GameWindow gameWindow = (GameWindow) Main.getWindow();
		CastEvent event = new CastEvent(world, player, null);
		if (spell == null || !getTimer().isOffCooldown() || !(Main.getWindow() instanceof GameWindow)) {
			return;
		}
		Object spellResult = spell.cast(event);
		String orthography = spell.getOrthography();
		if (player instanceof Player) {
			gameWindow.showMessage(new SpellMessage(orthography, spellResult, false));
		}
		getTimer().activateIfPossible();
	}

	/**
	 * This function has been repurposed for casting AI spells
	 * @param world
	 * @param caster The BossAIUnit caster
	 * @param subject
	 * @return true iff successfully activated
	 */
	public boolean castPrewritten(World world, BossAIUnit caster) {

		String orthography; //TODO calculate firstWord here?
		if (spell == null || "".equals(orthography = spell.getOrthography())) {
			if (!ignoreMessages && Main.getWindow() instanceof GameWindow) {
				((GameWindow) Main.getWindow()).showMessage(new TextMessage(null, "write on your papyrus before casting it!", false, new Runnable() {
					@Override
					public void run() {
						ignoreMessages = false;
					}
				}));
				ignoreMessages = true;
			}
			return false;
		}
		
		if (timer.activateIfPossible()) {
			Object o = wrapCastSpell(new CastEvent(world, caster, null));
			if (Main.getWindow() instanceof GameWindow) {
				((GameWindow) Main.getWindow()).showMessage(new SpellMessage(orthography, o, false));
			}
			return true;
		}

		if (!ignoreMessages && Main.getWindow() instanceof GameWindow) {
			((GameWindow) Main.getWindow()).showMessage(new TextMessage(null, "papyrus is on cooldown!", false, new Runnable() {
				@Override
				public void run() {
					ignoreMessages = false;
				}
			}));
			ignoreMessages = true;
		}
		return false;

	}

	@Override
	public Color getArcColor() {
		return PAPYRUS_COOLDOWN_COLOR;
	}

	@Override
	public int drawThumbnail(Graphics g, int i, boolean top, boolean selected) {
		int width = super.drawThumbnail(g, i, top, selected);
		Vector pos = getPos(i, top);
		g.drawString(String.valueOf(numPapyri), pos.getX(), pos.getY());
		return width;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString());
		builder.append(" (");
		builder.append(numPapyri);
		builder.append(")");
		return builder.toString();
	}

}