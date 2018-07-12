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
public class ActOnHolderEvent<C extends Thing> extends HeldEvent<C> {
	private SerializableTriConsumer<C, Event, Board> doOnTickToCreator = (x, y, z) -> {};
	public ActOnHolderEvent(final SerializableConsumer<Board> onPeriod, final double periodInMinutes, final SerializableTriConsumer<C, Event, Board> doOnTickToCreator) {
		super(onPeriod, periodInMinutes);
		this.doOnTickToCreator = doOnTickToCreator;
		setOnTick(board -> {
			doOnTickToCreator.accept(getCreator(), this, board);
		});
	}
	public ActOnHolderEvent(final SerializableConsumer<Board> onPeriod, final double periodInMinutes, final SerializableTriConsumer<C, Event, Board> doOnTickToCreator, final C creator) {
		this(onPeriod, periodInMinutes, doOnTickToCreator);
		setCreator(creator);
	}
	private ActOnHolderEvent(final ActOnHolderEvent<C> event) {
		this(event.getOnPeriod(), event.getPeriod(), event.doOnTickToCreator);
		if (event.hasCreator())
			setCreator(event.getCreator());
	}
	
	@Override
	public HeldEvent<C> makeCopy() {
		return new ActOnHolderEvent(this);
	}
}
