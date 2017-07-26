package snorri.inventory;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import snorri.dialog.SpellMessage;
import snorri.dialog.TextMessage;
import snorri.entities.Entity;
import snorri.events.SpellEvent.Caster;
import snorri.hieroglyphs.Hieroglyphs;
import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.main.Util;
import snorri.nonterminals.Sentence;
import snorri.world.World;

public class Papyrus extends Item {

	private static final long serialVersionUID = 1L;
	private static final Color PAPYRUS_COOLDOWN_COLOR = new Color(118, 45, 50, 150);
	
	private boolean ignoreMessages = false;
	
	public Papyrus(ItemType t) {
		super(t);
		timer = new Timer(0); //no cooldown
	}

	public boolean tryToActivate(Entity subject) {
		GameWindow window = (GameWindow) Main.getWindow();
		return tryToActivate(window.getWorld(), window.getFocusAsCaster(), subject);
	}

	public boolean tryToActivate(World world, Caster caster, Entity subject) {

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
		
		if (timer.activate()) {
			Object o = useSpell(world, caster, subject);
			if (Main.getWindow() instanceof GameWindow) {
				((GameWindow) Main.getWindow()).showMessage(new SpellMessage(orthography, o, spellIsStatement()));
			}
			caster.getInventory().remove(this, true);
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

	private boolean spellIsStatement() {
		return spell instanceof Sentence && ((Sentence) spell).isStatement();
	}

	@Override
	public Color getArcColor() {
		return PAPYRUS_COOLDOWN_COLOR;
	}

	@Override
	public int getInvPos() {
		return 2;
	}
	
	@Override
	protected void computeTexture() {
		if (type.getTexture() == null) {
			return;
		}
		String firstWord;
		if (spell != null && (firstWord = spell.getFirstWord()) != null) {
			texture = Util.deepCopy(type.getTexture());
			Graphics2D gr = (Graphics2D) texture.getGraphics();
			BufferedImage hieroglyph = Hieroglyphs.getImage(firstWord);
			int sWidth = texture.getWidth();
			int hWidth = hieroglyph.getWidth();
			gr.drawImage(hieroglyph, sWidth / 2 - hWidth / 6, 1, hWidth / 3, hieroglyph.getHeight() / 3, null);
			gr.dispose();
		} else {
			super.computeTexture();
		}
	}

}
