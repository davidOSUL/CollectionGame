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
	private SerializableConsumer<Board> regularOnPlace; //the "default" on place for this event
	public ActOnHolderEvent(final SerializableConsumer<Board> onPeriod, final double periodInMinutes, final SerializableTriConsumer<Thing, Event, Board> doOnTickToCreator) {
		super(onPeriod, periodInMinutes);
		setOnTickToCreator(doOnTickToCreator);
	}
	public ActOnHolderEvent(final SerializableConsumer<Board> onPeriod, final double periodInMinutes, final SerializableTriConsumer<Thing, Event, Board> doOnTickToCreator, final Thing creator) {
		this(onPeriod, periodInMinutes, doOnTickToCreator);
		setCreator(creator);
	}
	public ActOnHolderEvent(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onRemove, final SerializableTriConsumer<Thing, Event, Board> doOnTickToCreator) {
		super(onPlace, onRemove);
		setOnTickToCreator(doOnTickToCreator);
		
	}
	public ActOnHolderEvent(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onRemove, final SerializableTriConsumer<Thing, Event, Board> doOnTickToCreator, final SerializableTriConsumer<Thing, Event, Board> doOnPlaceToCreator) {
		setOnTickToCreator(doOnTickToCreator);
		setDoOnPlaceToCreator(onPlace, doOnPlaceToCreator);
		setOnRemove(onRemove);
		
	}
	public ActOnHolderEvent(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onPeriod, final SerializableConsumer<Board> onRemove,final SerializableTriConsumer<Thing, Event, Board> doOnTickToCreator, final SerializableTriConsumer<Thing, Event, Board> doOnPlaceToCreator, final double periodInMinutes) {
		this(onPlace, onRemove, doOnTickToCreator, doOnPlaceToCreator);
		setOnPeriod(onPeriod);
		setPeriod(periodInMinutes);
		
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
		this(event.regularOnPlace, event.getOnPeriod(), event.getOnRemove(), event.doOnTickToCreator, event.doOnPlaceToCreator, event.getPeriodInMinutes());
		if (event.hasCreator())
			setCreator(event.getCreator());
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
