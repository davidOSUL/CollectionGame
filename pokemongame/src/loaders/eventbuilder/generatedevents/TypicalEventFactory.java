package loaders.eventbuilder.generatedevents;

import effects.Event;
import gameutils.GameUtils;
import loaders.ThingLoadException;
import loaders.eventbuilder.generatedevents.globalmodification.GlobalModifierEventFactory;

public abstract class TypicalEventFactory {
	public abstract Event generateEvent();
	public abstract String getDescription();
	private final String[] inputs;
	private final static int EVENTTYPE_LOC = 0;
	protected TypicalEventFactory(final String[] inputs) {
		this.inputs = inputs;
	}
	public static TypicalEventFactory getTypicalEventFactory(final String[] inputs) {
		final EventType eventType = EventType.valueOf(inputs[EVENTTYPE_LOC].toUpperCase().trim());
		switch(eventType) {
		case DECREASE_SPAWN_PERIOD:
			return new DecreaseSpawnPeriodEventFactory(inputs);
		case GLOBAL_MODIFIER:
			return GlobalModifierEventFactory.generateGlobalModifierEventFactory(inputs);
		case LEGENDARY_CHANCE_INCREASE:
			return new LegendaryChanceIncreaseEventFactory(inputs);
		case RANDOM_GOLD:
			return new RandomGoldEventFactory(inputs);
		default:
			throw new ThingLoadException("invalid event type: " + eventType);
		
		}
		
	}
	protected static String getTimeDisplayDescription(final long timeToExist) {
		final StringBuilder sb = new StringBuilder();
		if (timeToExist > 0) {
			sb.append("For the next ");
			final String timeVal = GameUtils.millisecondsToWrittenOutTime(timeToExist);
			sb.append(timeVal);
			sb.append(" all");
		}
		else {
			sb.append("All");
		}
		return sb.toString();
	}
	protected static long parseLong(final String myLong) {
		return Double.valueOf(myLong).longValue();
	}
	enum EventType {
		RANDOM_GOLD, //of the format randomgold:x:y:z, so x (the first) will be at 1 and y (the last) will be at 3
		LEGENDARY_CHANCE_INCREASE,
		DECREASE_SPAWN_PERIOD,
		GLOBAL_MODIFIER;
	}
	protected String[] getInputs() {
		return inputs;
	}
}
