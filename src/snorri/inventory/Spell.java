package snorri.inventory;

import java.io.Serializable;

import snorri.events.CastEvent;
import snorri.grammar.ChartParser;
import snorri.semantics.CommandStatus;
import snorri.semantics.commands.Command;

public class Spell implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private transient Command command;
	private String text;
	
	public Spell(String text) {
		this.text = text;
	}
	
	public static Spell fromString(String text) {
		Spell spell = new Spell(text);
		spell.compile();
		return spell;
	}
	
	public Spell copy() {
		return new Spell(text);
	}
	
	public void compile() {
		command = ChartParser.parseText(text);
	}
	
	public String getOrthography() {
		return text;
	}
	
	public CommandStatus cast(CastEvent event) {
		if (command == null) {
			compile();
		}
		return command.apply(event);
	}
	
	public boolean altersMovement() {
		// FIXME: Get this up and running again.
		return false;
	}

}
