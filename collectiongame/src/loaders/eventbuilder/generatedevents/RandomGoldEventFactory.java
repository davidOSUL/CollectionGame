package loaders.eventbuilder.generatedevents;

import effects.Event;
import effects.OnPeriodEventWithDisplay;
import gameutils.GameUtils;
import gui.guiutils.GuiUtils;

/**
 * Constructs events that periodically have a certain percent chance of generating more gold
 * @author David O'Sullivan
 *
 */
public class RandomGoldEventFactory extends TypicalEventFactory{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int percentChance;
	private final int gold;
	private final double periodInMinutes;
	private static final int PERCENT_LOC = 1;
	private static final int GOLD_LOC = 2;
	private static final int PERIOD_LOC =3;
	/**
	 * Constructs a new RandomGoldEventFactory
	 * @param inputs the inputs to use to construct the  RandomGoldEventFactory
	 */
	protected RandomGoldEventFactory(final String[] inputs) {
		super(inputs);
		percentChance = Integer.parseInt(inputs[PERCENT_LOC]);
		gold = Integer.parseInt(inputs[GOLD_LOC]);
		periodInMinutes = Double.parseDouble(inputs[PERIOD_LOC]);
		
	}

	/** 
	 * @see loaders.eventbuilder.generatedevents.TypicalEventFactory#generateEvent()
	 */
	@Override
	public Event generateEvent() {
		final Event randomGold = new OnPeriodEventWithDisplay( model -> {
			if (GameUtils.testPercentChance(percentChance))
				model.addGold(gold);
		}, periodInMinutes, "event description");
		return randomGold;
	}

	/** 
	 * @see loaders.eventbuilder.generatedevents.TypicalEventFactory#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Has a " + percentChance + "% Chance of Generating " + getRandomGoldString(gold) + " every " + GameUtils.minutesToWrittenOutTime(periodInMinutes);
	}
	private static String getRandomGoldString(final int amountOfGold) {
		return  GuiUtils.getSignedColorFormat(amountOfGold, '+') + GuiUtils.getMoneySymbol() + amountOfGold + "</font>";
	}

}
