/**
 * 
 */
package effects;

import game.Board;
import interfaces.SerializableConsumer;
import interfaces.SerializableTriConsumer;
import thingFramework.Thing;

/**
 * @author David O'Sullivan
 *
 */
public class ActOnHolderEvent extends Event {
	private SerializableTriConsumer<Thing, Event, Board> doOnTickToCreator = (x, y, z) -> {};
	private SerializableTriConsumer<Thing, Event, Board> doOnPlaceToCreator = (x, y, z) -> {};
	private SerializableConsumer<Board> regularOnPlace = x -> {}; //the "default" on place for this event
	public ActOnHolderEvent(final SerializableConsumer<Board> onPeriod, final double periodInMinutes, final SerializableTriConsumer<Thing, Event, Board> doOnTickToCreator) {
		super(onPeriod, periodInMinutes);
		setOnTickToCreator(doOnTickToCreator);
	}
	public ActOnHolderEvent(final SerializableConsumer<Board> onPeriod, final double periodInMinutes, final SerializableTriConsumer<Thing, Event, Board> doOnTickToCreator, final Thing creator) {
		this(onPeriod, periodInMinutes, doOnTickToCreator);
		setCreator(creator);
	}
	public ActOnHolderEvent(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onRemove, final SerializableTriConsumer<Thing, Event, Board> doOnTickToCreator, final SerializableTriConsumer<Thing, Event, Board> doOnPlaceToCreator) {
		setOnTickToCreator(doOnTickToCreator);
		setDoOnPlaceToCreator(onPlace, doOnPlaceToCreator);
		setOnRemove(onRemove);
	}
	private void setDoOnPlaceToCreator(final SerializableConsumer<Board> onPlace, final SerializableTriConsumer<Thing, Event, Board> doOnPlaceToCreator) {
		this.doOnPlaceToCreator = doOnPlaceToCreator;
		regularOnPlace = onPlace;
		setOnPlace(board -> {
			regularOnPlace.accept(board);
			doOnPlaceToCreator.accept(getCreator(), this, board);
		});
	}
	private ActOnHolderEvent(final ActOnHolderEvent event) {
		super(event);
		setDoOnPlaceToCreator(event.regularOnPlace, event.doOnPlaceToCreator);
		setOnTickToCreator(event.doOnTickToCreator);
	}
	private void setOnTickToCreator(final SerializableTriConsumer<Thing, Event, Board> doOnTickToCreator) {
		this.doOnTickToCreator = doOnTickToCreator;
		setOnTick(board -> {
			doOnTickToCreator.accept(getCreator(), this, board);
		});
	}
	@Override
	public ActOnHolderEvent makeCopy() {
		return new ActOnHolderEvent(this);
	}
	
}
