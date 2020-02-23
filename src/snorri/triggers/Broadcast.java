package snorri.triggers;

public class Broadcast {

	public static void send(String msg) {
		TriggerType.BROADCAST.activate(msg);
	}
	
}
