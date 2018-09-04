package loaders.eventbuilder.generatedevents;

import effects.Event;
import gameutils.GameUtils;
import gui.guiutils.GuiUtils;

/**
 * Constructs Events that decrease the period at which new Creatures spawn 
 * @author David O'Sullivan
 *
 */
public class DecreaseSpawnPeriodEventFactory extends TypicalEventFactory {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final long millis;
	/**
	 * Creates a new DecreaseSpawnPeriodEventFactory
	 * @param inputs the inputs to use to construct the event
	 */
	public DecreaseSpawnPeriodEventFactory(final String[] inputs) {
		super(inputs);
		millis = parseLong(inputs[1]);
	};
	/** 
	 * @see loaders.eventbuilder.generatedevents.TypicalEventFactory#generateEvent()
	 */
	@Override
	public Event generateEvent() {
		return new Event(board -> board.addToPeriodDecrease(millis), board -> board.addToPeriodDecrease(-millis));
	}

	/** 
	 * @see loaders.eventbuilder.generatedevents.TypicalEventFactory#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Decrease wait time between new " + GuiUtils.getCreatureName() + " spawning by " + GameUtils.millisecondsToWrittenOutTime(millis);
	}


}
