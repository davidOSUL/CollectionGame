package loaders.eventbuilder.generatedevents;

import effects.Event;
import gameutils.GameUtils;

public class DecreaseSpawnPeriod extends TypicalEventFactory {
	private final long millis;
	public DecreaseSpawnPeriod(final String[] inputs) {
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
