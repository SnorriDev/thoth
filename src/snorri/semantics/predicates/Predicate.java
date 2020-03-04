package snorri.semantics.predicates;

import snorri.events.CastEvent;
import snorri.semantics.CommandStatus;
import snorri.semantics.Lambda;

public interface Predicate extends Lambda<CastEvent, CommandStatus> {

}
