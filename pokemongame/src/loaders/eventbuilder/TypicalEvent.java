package loaders.eventbuilder;

import java.util.ArrayList;
import java.util.List;

import effects.Event;
import effects.GlobalPokemonModifierEvent;
import effects.HeldEvent;
import effects.OnPeriodEventWithDisplay;
import gameutils.GameUtils;
import interfaces.SerializableConsumer;
import interfaces.SerializablePredicate;
import modifiers.Modifier;
import thingFramework.Attribute;
import thingFramework.Pokemon;
import thingFramework.Thing;

/**
 * Class used to construct typical events
 * @author David O'Sullivan
 *
 */
public final class TypicalEvent {
	private Event e;
	private HeldEvent<Thing> he;
	private String description;
	private TypicalEvent() {}
	static TypicalEvent generateEvent(final String[] inputs) {
		final String type = inputs[0]; //the type of event
		final EventType eventType = EventType.valueOf(type.toUpperCase().trim());
		final int lower = eventType.getLower();
		final int upper = eventType.getUpper();
		final TypicalEvent te = new TypicalEvent();
		switch (eventType) {
		case RANDOMGOLD:
			final int[] integerInputs = GameUtils.parseAllInRangeToInt(inputs, lower, upper-1); //upper-1 because the last input will be a double that we have to parse seperately
			te.he = generateRandomGoldEvent(integerInputs[0], integerInputs[1], Double.parseDouble(inputs[upper]));
			te.description = eventType.getDescription(integerInputs[0], integerInputs[1], Double.parseDouble(inputs[upper]));
			break;
		case LEGCHANCEINCR:
			final int amount = Integer.parseInt(inputs[lower]);
			te.e= generateLegendaryChanceIncreaseEvent(amount);
			te.description = eventType.getDescription(amount);
			break;
		case ALLPOKEADD:
			String attributeToAdd = inputs[lower];
			int amountToAdd =  Integer.parseInt(inputs[lower+1]);
			long timeToExist = parseLong(inputs[lower+2]);
			String displayAttribute = Attribute.generateAttribute(attributeToAdd, Integer.toString(amountToAdd)).toReverseString();
			te.he = generateIntegerAddToAllPokemonEvent(attributeToAdd, amountToAdd, timeToExist, Boolean.parseBoolean(inputs[lower+3]), Boolean.parseBoolean(inputs[lower+4]));
			te.description = eventType.getDescription(getTimeDisplayDescription(timeToExist), getPositive(amountToAdd), displayAttribute);
			break;
		case POKEOFCATEGORYADD:
			amountToAdd =  Integer.parseInt(inputs[lower+3]);
			timeToExist = parseLong(inputs[lower+4]);
			String verbalDescription = inputs[upper];
			attributeToAdd = inputs[lower+2];
			displayAttribute = Attribute.generateAttribute(attributeToAdd, Integer.toString(amountToAdd)).toReverseString();
			te.he = generateIntegerAddToAllOfPokemonCategoryEvent(inputs[lower], inputs[lower+1], attributeToAdd, amountToAdd, timeToExist, Boolean.parseBoolean(inputs[lower+5]), Boolean.parseBoolean(inputs[lower+6]));
			te.description = eventType.getDescription(getTimeDisplayDescription(timeToExist), verbalDescription, getPositive(amountToAdd), displayAttribute);
			break;
		case ALLPOKEMULT:
			String attributeToMultiply = inputs[lower];
			int amountToMultiply =  Integer.parseInt(inputs[lower+1]);
			timeToExist = parseLong(inputs[lower+2]);
			displayAttribute = Attribute.generateAttribute(attributeToMultiply, Integer.toString(amountToMultiply)).toReverseString();
			te.he = generateIntegerMultiplyToAllPokemonEvent(attributeToMultiply, amountToMultiply, timeToExist, Boolean.parseBoolean(inputs[lower+3]), Boolean.parseBoolean(inputs[lower+4]));
			te.description = eventType.getDescription(getTimeDisplayDescription(timeToExist), displayAttribute);
			break;
		case POKEOFCATEGORYMULT:
			amountToMultiply =  Integer.parseInt(inputs[lower+3]);
			timeToExist = parseLong(inputs[lower+4]);
			verbalDescription = inputs[upper];
			attributeToMultiply = inputs[lower+2];
			displayAttribute = Attribute.generateAttribute(attributeToMultiply, Integer.toString(amountToMultiply)).toReverseString();
			te.he = generateIntegerMultiplyToAllPokemonOfCategoryEvent(inputs[lower], inputs[lower+1], attributeToMultiply, amountToMultiply, timeToExist, Boolean.parseBoolean(inputs[lower+5]), Boolean.parseBoolean(inputs[lower+6]));
			te.description = eventType.getDescription(getTimeDisplayDescription(timeToExist), verbalDescription, displayAttribute);
			break;
		}
		return te;
	}
	private static long parseLong(final String myLong) {
		return Double.valueOf(myLong).longValue();
	}
	private static String getTimeDisplayDescription(final long timeToExist) {
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
	private static String getPositive(final int amount) {
		return amount >= 0 ? "+" : "";
	}
	/**
	 * Generates an event that every periodInMinute minutes will with a percentChance chance add the specified amount of gold to the board
	 * @param percentChance the chance that gold is added
	 * @param gold the amount of gold to add
	 * @param periodInMinutes the frequency of checking if gold is added
	 * @return the created event
	 */
	private static HeldEvent<Thing> generateRandomGoldEvent(final int percentChance, final int gold, final double periodInMinutes) {
		final HeldEvent<Thing> randomGold = new OnPeriodEventWithDisplay<Thing>( board -> {
			if (GameUtils.testPercentChance(percentChance))
				board.addGold(gold);
		}, periodInMinutes, "event description");
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
	 * Generates a held event that adds to a given attribute that is an integer the provided amount for all pokemon
	 * with the provided categoryAttributeValue of the attribute with the name categoryAttribute name.
	 * Can also specify lifetimeInMillis (-1 for never goes away), whether or not the countdown should be displayed,
	 * whether or not it should be removed when done.
	 * @param categoryAttributeName
	 * @param categoryAttributeValue
	 * @param attributeToAdd
	 * @param amountToAdd
	 * @param lifetimeInMillis
	 * @param removeWhenDone
	 * @param displayCountdown
	 * @return the created held event
	 */
	private static HeldEvent<Thing> generateIntegerAddToAllOfPokemonCategoryEvent(final String categoryAttributeName, final String categoryAttributeValue, final String attributeToAdd, final int amountToAdd, final long lifetimeInMillis, final boolean removeWhenDone, final boolean displayCountdown) {
		final SerializablePredicate<Pokemon> shouldModify = p -> {
			return p.containsAttribute(categoryAttributeName) &&
					p.getAttribute(categoryAttributeName).valEqualsParse(categoryAttributeValue);
		};
		final List<SerializableConsumer<Pokemon>> mods = getAddToIntModifiers(attributeToAdd, amountToAdd);
		final Modifier<Pokemon> m = new Modifier<Pokemon>(lifetimeInMillis, shouldModify, mods.get(0), mods.get(1));
		return new GlobalPokemonModifierEvent<Thing>(m, removeWhenDone, displayCountdown);
	}

	/**
	 * Generates a held event that adds to a given attribute that is an integer the provided amount for all pokemon.
	 * Can also specify lifetimeInMillis (-1 for never goes away), whether or not the countdown should be displayed,
	 * whether or not it should be removed when done.
	 * @param attributeToAdd
	 * @param amountToAdd
	 * @param lifetimeInMillis
	 * @param removeWhenDone
	 * @param displayCountdown
	 * @return
	 */
	private static HeldEvent<Thing> generateIntegerAddToAllPokemonEvent(final String attributeToAdd, final int amountToAdd, final long lifetimeInMillis, final boolean removeWhenDone, final boolean displayCountdown) {
		final List<SerializableConsumer<Pokemon>> mods = getAddToIntModifiers(attributeToAdd, amountToAdd);
		final Modifier<Pokemon> m = new Modifier<Pokemon>(lifetimeInMillis, mods.get(0), mods.get(1));
		return new GlobalPokemonModifierEvent<Thing>(m, removeWhenDone, displayCountdown);
	}
	private static List<SerializableConsumer<Pokemon>> getAddToIntModifiers(final String attributeToAdd, final int amountToAdd) {
		final SerializableConsumer<Pokemon> modification = p -> {
			p.addToIntegerAttribute(attributeToAdd, amountToAdd, true);
		};
		final SerializableConsumer<Pokemon> reverseModification =  p -> {
			p.addToIntegerAttribute(attributeToAdd, -amountToAdd, true);
		};
		final List<SerializableConsumer<Pokemon>> list = new ArrayList<SerializableConsumer<Pokemon>>();
		list.add(modification);
		list.add(reverseModification);
		return list;
	}

	/**
	 * Generates a held event that multiples the provided attribute by the provided value for the length of time given (-1 for infinte)
	 * @param attributeToMultiply
	 * @param amountToMultiply
	 * @param lifetimeInMillis
	 * @param removeWhenDone
	 * @param displayCountdown
	 * @return
	 */
	private static HeldEvent<Thing> generateIntegerMultiplyToAllPokemonEvent(final String attributeToMultiply, final int amountToMultiply, final long lifetimeInMillis, final boolean removeWhenDone, final boolean displayCountdown) {
		final List<SerializableConsumer<Pokemon>> mods = getMultiplyToIntModifiers(attributeToMultiply, amountToMultiply);
		final Modifier<Pokemon> m = new Modifier<Pokemon>(lifetimeInMillis, mods.get(0), mods.get(1));
		return new GlobalPokemonModifierEvent<Thing>(m, removeWhenDone, displayCountdown);
	}
	/**
	 *  Generates a held event that multiples the provided attribute by the provided value for the length of time given (-1 for infinte)
	 *  for all pokemon that have the attribute categoryAttributeName and for that attribute have the value categoryAttributeValue
	 * @param categoryAttributeName
	 * @param categoryAttributeValue
	 * @param attributeToMultiply
	 * @param amountToMultiply
	 * @param lifetimeInMillis
	 * @param removeWhenDone
	 * @param displayCountdown
	 * @return
	 */
	private static HeldEvent<Thing> generateIntegerMultiplyToAllPokemonOfCategoryEvent(final String categoryAttributeName, final String categoryAttributeValue, final String attributeToMultiply, final int amountToMultiply, final long lifetimeInMillis, final boolean removeWhenDone, final boolean displayCountdown) {
		final SerializablePredicate<Pokemon> shouldModify = p -> {
			return p.containsAttribute(categoryAttributeName) &&
					p.getAttribute(categoryAttributeName).valEqualsParse(categoryAttributeValue);
		};
		final List<SerializableConsumer<Pokemon>> mods = getMultiplyToIntModifiers(attributeToMultiply, amountToMultiply);
		final Modifier<Pokemon> m = new Modifier<Pokemon>(lifetimeInMillis, shouldModify, mods.get(0), mods.get(1));
		return new GlobalPokemonModifierEvent<Thing>(m, removeWhenDone, displayCountdown);
	}
	private static List<SerializableConsumer<Pokemon>> getMultiplyToIntModifiers(final String attributeToAdd, final int amountToMult) {
		final SerializableConsumer<Pokemon> modification = p -> {
			p.multiplyIntegerAttribute(attributeToAdd, amountToMult, true);
		};
		final SerializableConsumer<Pokemon> reverseModification =  p -> {
			p.divideIntegerAttribute(attributeToAdd, amountToMult, false);
		};
		final List<SerializableConsumer<Pokemon>> list = new ArrayList<SerializableConsumer<Pokemon>>();
		list.add(modification);
		list.add(reverseModification);
		return list;
	}
	/**
	 * All TypicalEvents. Contains the lower index and the upper index of the parsed input line, where the inputs to the corresponding method that generates the event can be found
	 * @author David O'Sullivan
	 *
	 */
	//TODO: PUT THESE IN THEIR OWN CLASS
	enum EventType {
		RANDOMGOLD(1, 3, "Has a %d%% Chance of Generating %d PokeCash Every %.2f Minutes"), //of the format randomgold:x:y:z, so x (the first) will be at 1 and y (the last) will be at 3
		LEGCHANCEINCR(1,1, "Increases chance of legendary pokemon spawning by %d%%"),
		POKEOFCATEGORYADD(1, 8, "%s %s get %s%s"), //e.g. For the next 10 minutes, all water types get +1 $/min
		ALLPOKEADD(1, 5, "%s pokemon get %s%d"),
		POKEOFCATEGORYMULT(1, 8, "%s %s get x%s"),
		ALLPOKEMULT(1, 5, "%s pokemon get x%s");

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
	String getDescription() { return description;}
	Event getRegularEvent() {return e;}
	HeldEvent<Thing> getHeldEvent(){return he;}
	public boolean isHeldEvent() { return this.he != null;}
}
