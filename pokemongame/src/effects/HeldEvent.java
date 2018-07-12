package effects;

import game.Board;
import interfaces.SerializableConsumer;

public abstract class HeldEvent<C> extends Event {
	private C creator;
	public HeldEvent() {}
	/**
	 * @param onPeriod what should happen on a given period
	 * @param periodInMinutes the length of that period
	 */
	public HeldEvent(final SerializableConsumer<Board> onPeriod,
			final double periodInMinutes) {
		super(onPeriod, periodInMinutes);
	}
	public abstract HeldEvent<C> makeCopy();
	public C getCreator() {
		if (this.creator == null)
			throw new IllegalStateException("Attempted to access null creator");
		return creator;
	}
	public void setCreator(final C creator) {
		if (onPlaceExecuted())
			throw new IllegalStateException("Attempted to change creator after placing!");
		this.creator = creator;
	}
	protected boolean hasCreator() {
		return creator != null;
	}

}
