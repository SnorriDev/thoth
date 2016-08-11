package snorri.dialog;

public class SpellMessage extends Message {

	private final Object output;
	
	public SpellMessage(Object output) {
		super();
		this.output = output;
		success = output != null;
	}
	
	@Override
	public String toString() {
		return success ? ("spell: " + output) : "nothing";
	}

}
