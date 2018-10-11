package effects;

import attributes.AttributeName;
import interfaces.SerializableConsumer;
import interfaces.SerializableTriConsumer;
import model.ModelInterface;
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
	public OnPeriodEventWithDisplay(final SerializableConsumer<ModelInterface> onPeriod, final double periodInMinutes, final AttributeName<?> attributeName, final Thing creator) {
		super(onPeriod, periodInMinutes, getDoOnTick(attributeName), creator);
	}
	/**
	 * Creates a new "OnPeriodEventWithDisplay" event. This event has a period, and will also update a given attribute's extra description with 
	 * the time to next period
	 * @param onPeriod the onPeriod event
	 * @param periodInMinutes the period in minutes
	 * @param attributeName the attribute to display the time to next period next to 
	 */
	public OnPeriodEventWithDisplay(final SerializableConsumer<ModelInterface> onPeriod, final double periodInMinutes, final AttributeName<?> attributeName) {
		super(onPeriod, periodInMinutes, getDoOnTick(attributeName));
	}
	private static <T extends Thing> SerializableTriConsumer<T, Event, ModelInterface> getDoOnTick(final AttributeName<?> attributeName) {
		final SerializableTriConsumer<T, Event, ModelInterface> update = (thing, event, model) -> {
			if (thing.containsAttribute(attributeName)) { //TODO: investigate timing
				thing.setExtraDescription(attributeName, " (" + event.getTimeToNextPeriod(model) + ")");
			}
		};
		return update;
	}

}
