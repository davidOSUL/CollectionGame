package effects;

import game.Board;
import interfaces.SerializableConsumer;
import interfaces.SerializableTriConsumer;
import thingFramework.Thing;

public class OnPeriodEventWithDisplay<C extends Thing> extends ActOnHolderEvent<C> {

	/**
	 * Creates a new "OnPeriodEventWithDisplay" event. This event has a period, and will also update a given attribute's extra description with 
	 * the time to next period
	 * @param onPeriod the onPeriod event
	 * @param periodInMinutes the period in minutes
	 * @param attributeName the attribute to display the time to next period next to 
	 * @param creator the holder of this event
	 */
	public OnPeriodEventWithDisplay(final SerializableConsumer<Board> onPeriod, final double periodInMinutes, final String attributeName, final C creator) {
		super(onPeriod, periodInMinutes, getDoOnTick(attributeName), creator);
	}
	/**
	 * Creates a new "OnPeriodEventWithDisplay" event. This event has a period, and will also update a given attribute's extra description with 
	 * the time to next period
	 * @param onPeriod the onPeriod event
	 * @param periodInMinutes the period in minutes
	 * @param attributeName the attribute to display the time to next period next to 
	 */
	public OnPeriodEventWithDisplay(final SerializableConsumer<Board> onPeriod, final double periodInMinutes, final String attributeName) {
		super(onPeriod, periodInMinutes, getDoOnTick(attributeName));
	}
	private static <T extends Thing> SerializableTriConsumer<T, Event, Board> getDoOnTick(final String attributeName) {
		final SerializableTriConsumer<T, Event, Board> update = (t, e, b) -> {
			if (t.containsAttribute(attributeName)) { //TODO: less hacky fix
				t.getAttribute(attributeName).setExtraDescription(" (" + e.getTimeToNextPeriod(b) + ")");
				t.updateDescription();
			}
		};
		return update;
	}

}
