package loaders.eventbuilder.generatedevents;

import effects.Event;
import gameutils.GameUtils;

public class DecreaseSpawnPeriodEventFactory extends TypicalEventFactory {
	private final long millis;
	public DecreaseSpawnPeriodEventFactory(final String[] inputs) {
		super(inputs);
		millis = parseLong(inputs[1]);
	};
	@Override
	public Event generateEvent() {
		return new Event(board -> board.addToPeriodDecrease(millis), board -> board.addToPeriodDecrease(-millis));
	}

	@Override
	public String getDescription() {
		return "Decrease wait time between new pokemon spawning by " + GameUtils.millisecondsToWrittenOutTime(millis);
	}


}
