package loaders.eventbuilder.generatedevents;

import effects.Event;
import gui.guiutils.GuiUtils;

public class LegendaryChanceIncreaseEventFactory extends TypicalEventFactory{
	private static final int INCREASE_LOC = 1;
	private final int increase;
	protected LegendaryChanceIncreaseEventFactory(final String[] inputs) {
		super(inputs);
		increase = Integer.parseInt(inputs[INCREASE_LOC]);
	}

	@Override
	public Event generateEvent() {
		return new Event(board -> board.increaseLegendaryChance(increase), board -> board.decreaseLegendaryChance(increase));

	}

	@Override
	public String getDescription() {
		return  "Increases chance of legendary pokemon spawning by " + GuiUtils.getSignedColorFormat(increase) + increase + "%</font>";
	}

}
