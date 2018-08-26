package loaders.eventbuilder.generatedevents;

import effects.Event;
import effects.OnPeriodEventWithDisplay;
import gameutils.GameUtils;
import gui.guiutils.GuiUtils;

public class RandomGoldEventFactory extends TypicalEventFactory{
	private final int percentChance;
	private final int gold;
	private final double periodInMinutes;
	private static final int PERCENT_LOC = 1;
	private static final int GOLD_LOC = 2;
	private static final int PERIOD_LOC =3;
	protected RandomGoldEventFactory(final String[] inputs) {
		super(inputs);
		percentChance = Integer.parseInt(inputs[PERCENT_LOC]);
		gold = Integer.parseInt(inputs[GOLD_LOC]);
		periodInMinutes = Double.parseDouble(inputs[PERIOD_LOC]);
		
	}

	@Override
	public Event generateEvent() {
		final Event randomGold = new OnPeriodEventWithDisplay( board -> {
			if (GameUtils.testPercentChance(percentChance))
				board.addGold(gold);
		}, periodInMinutes, "event description");
		return randomGold;
	}

	@Override
	public String getDescription() {
		return "Has a " + percentChance + "% Chance of Generating " + getRandomGoldString(gold) + " every " + periodInMinutes + " Minutes";
	}
	private static String getRandomGoldString(final int amountOfGold) {
		return  GuiUtils.getSignedColorFormat(amountOfGold, '+') + GuiUtils.getMoneySymbol() + amountOfGold + "</font>";
	}

}
