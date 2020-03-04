package snorri.semantics.commands.trans;

import snorri.grammar.PartOfSpeech;
import snorri.main.Main;
import snorri.semantics.CommandStatus;
import snorri.semantics.Definition;
import snorri.semantics.Lambda;
import snorri.semantics.commands.Command;
import snorri.semantics.nouns.Noun;
import snorri.triggers.TriggerType;
import snorri.windows.GameWindow;

public class Write implements Definition<Lambda<Noun, Command>> {

	protected static final String NONE = "null";

	@Override
	public PartOfSpeech getPartOfSpeech() {
		return PartOfSpeech.TRANS_CMD;
	}

	@Override
	public Lambda<Noun, Command> getMeaning() {
		return noun -> {
			return e -> {
				Object obj = noun.apply(e);
				if (Main.getWindow() instanceof GameWindow && obj != null) {
					String objString = obj.toString();
					String text = objString != null ? objString : NONE;
					TriggerType.WRITE.activate(text);
					((GameWindow) Main.getWindow()).showMessage(text);
					return CommandStatus.DONE;
				}
				return CommandStatus.FAILED;
			};
		};
	}

	@Override
	public String getEnglish() {
		return "write";
	}

	@Override
	public String getDocumentation() {
		return "Print out an object in the dialog pane.";
	}

}
