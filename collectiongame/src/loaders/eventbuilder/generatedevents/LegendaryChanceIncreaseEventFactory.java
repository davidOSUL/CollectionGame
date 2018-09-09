package loaders.eventbuilder.generatedevents;

import effects.Event;
import gui.guiutils.GuiUtils;

/**
 * Constructs Events that increase the percent chance of legendary Creatures spawning
 * @author David O'Sullivan
 *
 */
public class LegendaryChanceIncreaseEventFactory extends TypicalEventFactory{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int INCREASE_LOC = 1;
	private final int increase;
	/**
	 * Constructs a new LegendaryChanceIncreaseEventFactory
	 * @param inputs the inputs to use to construct the LegendaryChanceIncreaseEventFactory
	 */
	protected LegendaryChanceIncreaseEventFactory(final String[] inputs) {
		super(inputs);
		increase = Integer.parseInt(inputs[INCREASE_LOC]);
	}

	/** 
	 * @see loaders.eventbuilder.generatedevents.TypicalEventFactory#generateEvent()
	 */
	@Override
	public Event generateEvent() {
		return new Event(model ->model.increaseLegendaryChance(increase), model -> model.decreaseLegendaryChance(increase));

	}

	/** 
	 * @see loaders.eventbuilder.generatedevents.TypicalEventFactory#getDescription()
	 */
	@Override
	public String getDescription() {
		return  "Increases chance of legendary " + GuiUtils.getPluralCreatureName() + " spawning by " + GuiUtils.getSignedColorFormat(increase) + increase + "%</font>";
	}

}
