package snorri.semantics.commands;

import snorri.events.CastEvent;
import snorri.semantics.CommandStatus;
import snorri.semantics.Lambda;

public interface Command extends Lambda<CastEvent, CommandStatus> {

}
