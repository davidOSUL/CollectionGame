package loaders.eventbuilder;

import effects.Event;
import gameutils.GameUtils;

public final class TypicalEvent {
	private Event e;
	private String description;
	private TypicalEvent() {}
	String getDescription() { return description;}
	Event getEvent() {return e;}
	static TypicalEvent generateEvent(final String[] inputs) {
		final String type = inputs[0]; //the type of event
		final EventType eventType = EventType.valueOf(type.toUpperCase().trim());
		final int lower = eventType.getLower();
		final int upper = eventType.getUpper();
		final TypicalEvent te = new TypicalEvent();
		switch (eventType) {
		case RANDOMGOLD:
			final int[] integerInputs = GameUtils.parseAllInRangeToInt(inputs, lower, upper-1); //upper-1 because the last input will be a double that we have to parse seperately
			te.e = generateRandomGoldEvent(integerInputs[0], integerInputs[1], Double.parseDouble(inputs[upper]));
			te.description = eventType.getDescription(integerInputs[0], integerInputs[1], Double.parseDouble(inputs[upper]));
			break;
		case INCREASE_LEGENDARY_CHANCE:
			te.e = generateLegendaryChanceIncreaseEvent(Integer.parseInt(inputs[0]));
			te.description = eventType.getDescription(inputs[0]);
			break;
		default:
			break;
		}
		return te;
	}
	/**
	 * Generates an event that every periodInMinute minutes will with a percentChance chance add the specified amount of gold to the board
	 * @param percentChance the chance that gold is added
	 * @param gold the amount of gold to add
	 * @param periodInMinutes the frequency of checking if gold is added
	 * @return the created event
	 */
	private static Event generateRandomGoldEvent(final int percentChance, final int gold, final double periodInMinutes) {
		final Event randomGold = new Event( board -> {
			if (GameUtils.testPercentChance(percentChance))
				board.addGold(gold);
		}, periodInMinutes);
		return randomGold;
	}
	/**
	 * Generates an event that increases the % chance of legendary pokemon spawning by increase (% 0-100)
	 * @param increase the percentage to increase by (0-100)
	 * @return the created event
	 */
	private static Event generateLegendaryChanceIncreaseEvent(final int increase) {
		return new Event(board -> board.increaseLegendaryChance(increase), board -> board.decreaseLegendaryChance(increase));
		
	}

	/**
	 * All TypicalEvents. Contains the lower index and the upper index of the parsed input line, where the inputs to the corresponding method that generates the event can be found
	 * @author David O'Sullivan
	 *
	 */
	enum EventType {
		RANDOMGOLD(1, 3, "Has a %d%% Chance of Generating %d PokeCash Every %.2f Minutes"), //of the format randomgold:x:y:z, so x (the first) will be at 1 and y (the last) will be at 3
		INCREASE_LEGENDARY_CHANCE(1,1, "Increases chance of legendary pokemon spawning by %d%%");
		
		/**
		 * The lower index of the set of parameters for the event's generator function
		 */
		private final int lower;
		/**
		 * The upper index of the set of parameters for the event's generator function
		 */
		private final int upper;
		private final String descriptionTemplate;
		private EventType(final int lower, final int upper, final String descriptionTemplate) {
			this.lower = lower;
			this.upper = upper;
			this.descriptionTemplate = descriptionTemplate;
		}
		private int getLower() {
			return lower;
		}
		private int getUpper() {
			return upper;
		}
		private String getDescription(final Object ...args) {
			return String.format(descriptionTemplate, args);
		}
	}
}
