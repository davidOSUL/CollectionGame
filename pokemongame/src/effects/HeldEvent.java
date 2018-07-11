package effects;

import game.Board;
import interfaces.SerializableConsumer;

public abstract class HeldEvent<C> extends Event {
	private C creator;
	public HeldEvent() {
		// TODO Auto-generated constructor stub
	}

	public HeldEvent(final SerializableConsumer<Board> onPlace) {
		super(onPlace);
		// TODO Auto-generated constructor stub
	}

	public HeldEvent(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onRemove) {
		super(onPlace, onRemove);
		// TODO Auto-generated constructor stub
	}

	public HeldEvent(final SerializableConsumer<Board> onPeriod, final int periodInMinutes) {
		super(onPeriod, periodInMinutes);
		// TODO Auto-generated constructor stub
	}

	public HeldEvent(final SerializableConsumer<Board> onPeriod, final double periodInMinutes) {
		super(onPeriod, periodInMinutes);
		// TODO Auto-generated constructor stub
	}

	public HeldEvent(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onPeriod, final int periodInMinutes) {
		super(onPlace, onPeriod, periodInMinutes);
		// TODO Auto-generated constructor stub
	}

	public HeldEvent(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onPeriod,
			final SerializableConsumer<Board> onRemove, final int periodInMinutes) {
		super(onPlace, onPeriod, onRemove, periodInMinutes);
		// TODO Auto-generated constructor stub
	}

	public HeldEvent(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onPeriod,
			final double periodInMinutes) {
		super(onPlace, onPeriod, periodInMinutes);
		// TODO Auto-generated constructor stub
	}

	public HeldEvent(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onPeriod,
			final SerializableConsumer<Board> onRemove, final double periodInMinutes) {
		super(onPlace, onPeriod, onRemove, periodInMinutes);
		// TODO Auto-generated constructor stub
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

}
