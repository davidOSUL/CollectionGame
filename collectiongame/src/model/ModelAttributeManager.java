package model;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import attributes.Attribute;
import attributes.AttributeManagerObserver;
import attributes.AttributeName;
import effects.Event;
import effects.OnPeriodEventWithDisplay;
import thingFramework.Thing;
/**
 * Implementation of an AttributeManagerObserver, whenever a thing gets a gph, gpm, or popularity attribute
 * the BaordAttributeManager creates or updates an event to affect the stats of the ModelInterface appropriately. 
 * @author David O'Sullivan
 */
public final class ModelAttributeManager implements AttributeManagerObserver<Integer>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Thing holder;
	private final Map<Attribute<?>, Event> addedEvents = new HashMap<Attribute<?>, Event>();
	/**
	 * Creates a new ModelAttributeManager
	 * @param holder the Thing that has this ModelAttributeManager
	 */
	public ModelAttributeManager(final Thing holder) {
		this.holder = holder;
	}
	/**
	 * Builds Model events based on input name
	 * @param nameOfEvent gph = gold Per Hour, gpm = Gold Per Minute, popularity boost = Increase popularity of model on place, decrease on remove
	 * @param valueOfEvent the amount to effect the attribute by
	 * @return the created event
	 */
	private Event eventBuilder(final String nameOfEvent, final int valueOfEvent) {
		Event event = null;
		switch (nameOfEvent) {
		case "gph":
			event = new OnPeriodEventWithDisplay(model -> model.addGold( valueOfEvent), 60, AttributeName.GPH, holder);
			event.addToName("GPH: ");
			break;
		case "gpm":
			event =  new OnPeriodEventWithDisplay(model -> model.addGold(valueOfEvent), 1, AttributeName.GPM, holder);
			event.addToName("GPM: ");
			break;
		case "popularity boost":
			event = new Event(model -> model.addPopularity(valueOfEvent), model -> model.subtractPopularity(valueOfEvent));
			event.addToName("POP: ");
			break;
		default:
			break;
		}
		return event;
	}
	private void modifyModelEvent(final String AttributeName, final Event e, final int newValue) {
		switch(AttributeName) {
		case "gph":
			e.setOnPeriod(model -> model.addGold(newValue));
			break;
		case "gpm":
			e.setOnPeriod(model -> model.addGold(newValue));
			break;
		case "popularity boost":
			e.setOnPlace(model -> model.addPopularity(newValue));
			e.markForReset( model -> model.subtractPopularity(newValue));
			break;
		default:
			break;
		}

	}
	
	
	/** 
	 * If the attribute generated was gpm, gph, or popularity, will add the appropriate event to the holder of this ModelAttributeManager
	 * @see attributes.AttributeManagerObserver#onAttributeGenerated(attributes.Attribute)
	 */
	@Override
	public void onAttributeGenerated(final Attribute<Integer> addedAttribute) {
		final Event e = eventBuilder(addedAttribute.getName(), addedAttribute.getValue() == null ? 0 : addedAttribute.getValue());
		if (e != null) {
			holder.addToEventList(e);
			addedEvents.put(addedAttribute, e);
		}
		
	}
	/** 
	 * If the attribute removed was gpm, gph, or popularity, will marks the appropriate event for removals
	 * @see attributes.AttributeManagerObserver#onAttributeRemoved(attributes.Attribute)
	 */
	@Override
	public void onAttributeRemoved(final Attribute<Integer> removedAttribute) {
		if (addedEvents.containsKey(removedAttribute)) {
			addedEvents.remove(removedAttribute).markForRemoval();
		}
		
	}
	/** 
	 * If the attribute whose value was changed was gpm, gph, or popularity, will add/reset the appropriate event(s) to the holder of this ModelAttributeManager
	 * @see attributes.AttributeManagerObserver#onAttributeValueChanged(attributes.Attribute)
	 */
	@Override
	public void onAttributeValueChanged(final Attribute<Integer> modifiedAttribute) {
		if (addedEvents.containsKey(modifiedAttribute)) {
			modifyModelEvent(modifiedAttribute.getName(), addedEvents.get(modifiedAttribute), modifiedAttribute.getValue());
		}
		
	}
	
	
}
