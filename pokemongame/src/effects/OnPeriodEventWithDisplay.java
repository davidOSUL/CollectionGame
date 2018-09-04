package effects;

import game.Board;
import interfaces.SerializableConsumer;
import interfaces.SerializableTriConsumer;
import thingFramework.Thing;

/**
 * An event that has an onPeriod consumer, and which also updates the extra description of a provided attribute name for 
 * the creator this event with the time until the next period of this event.
 * @author David O'Sullivan
 *
 */
public class OnPeriodEventWithDisplay extends ActOnCreatorEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Creates a new "OnPeriodEventWithDisplay" event. This event has a period, and will also update a given attribute's extra description with 
	 * the time to next period
	 * @param onPeriod the onPeriod event
	 * @param periodInMinutes the period in minutes
	 * @param attributeName the attribute to display the time to next period next to 
	 * @param creator the holder of this event
	 */
	public OnPeriodEventWithDisplay(final SerializableConsumer<Board> onPeriod, final double periodInMinutes, final String attributeName, final Thing creator) {
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
				t.setExtraDescription(attributeName, " (" + e.getTimeToNextPeriod(b) + ")");
			}
		};
		return update;
	}

}
