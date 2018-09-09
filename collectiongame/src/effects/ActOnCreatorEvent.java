/**
 * 
 */
package effects;

import interfaces.SerializableConsumer;
import interfaces.SerializableTriConsumer;
import model.ModelInterface;
import thingFramework.Thing;

/**
 * An event that performs an action on its creator
 * @author David O'Sullivan
 *
 */
public class ActOnCreatorEvent extends Event {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SerializableTriConsumer<Thing, Event, ModelInterface> doOnTickToCreator = (x, y, z) -> {};
	private SerializableTriConsumer<Thing, Event, ModelInterface> doOnPlaceToCreator = (x, y, z) -> {};
	private SerializableConsumer<ModelInterface> regularOnPlace = x -> {}; //the "default" on place for this event
	/**
	 * Creates a new ActOnCreatorEvent
	 * @param onPeriod what should happen on the period of this event
	 * @param periodInMinutes the length of the period
	 * @param doOnTickToCreator what should happen every tick to this event's creator. 
	 */
	public ActOnCreatorEvent(final SerializableConsumer<ModelInterface> onPeriod, final double periodInMinutes, final SerializableTriConsumer<Thing, Event, ModelInterface> doOnTickToCreator) {
		super(onPeriod, periodInMinutes);
		setOnTickToCreator(doOnTickToCreator);
	}
	/**
	 * Creates a new ActOnCreatorEvent and sets the creator of this event
	 * @param onPeriod what should happen on the period of this event
	 * @param periodInMinutes the length of the period
	 * @param doOnTickToCreator what should happen every tick to this event's creator. 
	 * @param creator the creator of this event
	 */
	public ActOnCreatorEvent(final SerializableConsumer<ModelInterface> onPeriod, final double periodInMinutes, final SerializableTriConsumer<Thing, Event, ModelInterface> doOnTickToCreator, final Thing creator) {
		this(onPeriod, periodInMinutes, doOnTickToCreator);
		setCreator(creator);
	}
	/**
	 * Creates a new ActOnCreatorEvent 
	 * @param onPlace what should happen when this event is placed
	 * @param onRemove what should happen when this event is removed
	 * @param doOnTickToCreator what should happen every tick to this event's creator
	 * @param doOnPlaceToCreator what should happen to this event's creator when this event is placed
	 */
	public ActOnCreatorEvent(final SerializableConsumer<ModelInterface> onPlace, final SerializableConsumer<ModelInterface> onRemove, final SerializableTriConsumer<Thing, Event, ModelInterface> doOnTickToCreator, final SerializableTriConsumer<Thing, Event, ModelInterface> doOnPlaceToCreator) {
		setOnTickToCreator(doOnTickToCreator);
		setDoOnPlaceToCreator(onPlace, doOnPlaceToCreator);
		setOnRemove(onRemove);
	}
	private void setDoOnPlaceToCreator(final SerializableConsumer<ModelInterface> onPlace, final SerializableTriConsumer<Thing, Event, ModelInterface> doOnPlaceToCreator) {
		this.doOnPlaceToCreator = doOnPlaceToCreator;
		regularOnPlace = onPlace;
		setOnPlace(ModelInterface -> {
			regularOnPlace.accept(ModelInterface);
			doOnPlaceToCreator.accept(getCreator(), this, ModelInterface);
		});
	}
	private ActOnCreatorEvent(final ActOnCreatorEvent event) {
		super(event);
		setDoOnPlaceToCreator(event.regularOnPlace, event.doOnPlaceToCreator);
		setOnTickToCreator(event.doOnTickToCreator);
	}
	private void setOnTickToCreator(final SerializableTriConsumer<Thing, Event, ModelInterface> doOnTickToCreator) {
		this.doOnTickToCreator = doOnTickToCreator;
		setOnTick(ModelInterface -> {
			doOnTickToCreator.accept(getCreator(), this, ModelInterface);
		});
	}
	/** 
	 * @see effects.Event#makeCopy()
	 */
	@Override
	public ActOnCreatorEvent makeCopy() {
		return new ActOnCreatorEvent(this);
	}
	
}
