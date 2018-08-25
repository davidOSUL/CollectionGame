package loaders.eventbuilder.generatedevents;

import effects.Event;

public abstract class TypicalEventFactory {
	public abstract Event generateEvent();
	public abstract String getDescription();
	private final String[] inputs;
	private final static int EVENTTYPE_LOC = 0;
	protected static long parseLong(final String myLong) {
		return Double.valueOf(myLong).longValue();
	}
	protected TypicalEventFactory(final String[] inputs) {
		this.inputs = inputs;
	}
	public static TypicalEventFactory getTypicalEventFactory(final String[] inputs) {
		final EventType eventType = EventType.valueOf(inputs[EVENTTYPE_LOC].toUpperCase().trim());
		switch(eventType) {
		case ALLPOKEADD:
			break;
		case ALLPOKEMULT:
			break;
		case DECRSPAWNPERIOD:
			return new DecreaseSpawnPeriod(inputs);
		case LEGCHANCEINCR:
			break;
		case POKEOFCATEGORYADD:
			break;
		case POKEOFCATEGORYMULT:
			break;
		case RANDOMGOLD:
			break;
		default:
			break;
		
		}
		return null;
		
	}
	enum EventType {
		RANDOMGOLD, //of the format randomgold:x:y:z, so x (the first) will be at 1 and y (the last) will be at 3
		LEGCHANCEINCR,
		POKEOFCATEGORYADD, //e.g. For the next 10 minutes, all water types get +1 $/min
		ALLPOKEADD,
		POKEOFCATEGORYMULT,
		ALLPOKEMULT,
		DECRSPAWNPERIOD;
	}
}
