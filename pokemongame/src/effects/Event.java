package effects; 
import static gameutils.Constants.DEBUG;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

import game.Board;
import interfaces.SerializableConsumer;
/**
 * various things may events which affect the state of the board at different times. Events have an
 * "onPlace", "onRemove" functions which happen when they are created/destroyed. Some events also have an
 * "onPeriod" function which happens at a regular interval
 * @author David O'Sullivan
 *
 */
public class Event implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SerializableConsumer<Board> onPlace =  x -> {};
	private SerializableConsumer<Board> onPeriod = x -> {};
	private SerializableConsumer<Board> onRemove =  x -> {};
	private double period = -1; //period in minutes
	private long timeCreated;
	private long inGameTimeCreated;
	private volatile long numPeriodsElapsed = 0;
	private boolean isPeriodic = false;
	private volatile AtomicBoolean onPlaceExecuted =new AtomicBoolean(false);
	private final boolean keepTrackWhileOff = false;
	private final static double MIN_PERIOD = .01;
	private String name = "Event ";
	private boolean shouldBeRemoved = false;
	private boolean shouldBeReset = false;
	private SerializableConsumer<Board> newOnRemove = null;
	public Event() {
	}
	public Event(final SerializableConsumer<Board> onPlace) {
		this.onPlace = onPlace;
	}
	public Event(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onRemove) {
		this(onPlace);
		this.onRemove = onRemove;
	}
	public Event(final SerializableConsumer<Board> onPeriod, final int periodInMinutes) {
		this.onPeriod = onPeriod;
		this.period = periodInMinutes;
		isPeriodic = periodInMinutes > 0L;


	}
	public Event(final SerializableConsumer<Board> onPeriod, final double periodInMinutes) {
		this.onPeriod = onPeriod;
		this.period = periodInMinutes;
		isPeriodic = periodInMinutes > 0.0;
		if (isPeriodic)
			this.period = Math.max(periodInMinutes, MIN_PERIOD);

	}
	public Event(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onPeriod, final int periodInMinutes) {
		this(onPeriod, periodInMinutes);
		this.onPlace = onPlace;
	}
	public Event(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onPeriod, final SerializableConsumer<Board> onRemove, final int periodInMinutes) {
		this(onPeriod, periodInMinutes);
		this.onPlace = onPlace;
		this.onRemove = onRemove;
	}
	public Event(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onPeriod, final double periodInMinutes) {
		this(onPeriod, periodInMinutes);
		this.onPlace = onPlace;
	}
	public Event(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onPeriod, final SerializableConsumer<Board> onRemove, final double periodInMinutes) {
		this(onPeriod, periodInMinutes);
		this.onPlace = onPlace;
		this.onRemove = onRemove;
	}
	private synchronized void runOnPlace(final Board b) {
		if (!onPlaceExecuted()) {
			timeCreated = b.getTotalTimeSinceStart();
			inGameTimeCreated = b.getTotalInGameTime();
		}
		onPlaceExecuted.set(true);
		onPlace.accept(b);
		if (DEBUG)
			System.out.println("runOnPlace from " + this);
	}
	private synchronized void runRemove(final Board b) {
		onPlaceExecuted.set(false);
		onRemove.accept(b);
		if (DEBUG)
			System.out.println("runOnRemove from " + this);
	}
	public synchronized boolean onPlaceExecuted() {
		return onPlaceExecuted.get();
	}
	private synchronized void executeIfTime(final Board b) {
		if (!keepTrackWhileOff) {
			if (hasPeriodicity() && millisAsMinutes(b.getTotalInGameTime()-inGameTimeCreated) / period >= (numPeriodsElapsed+1)) {
				if (DEBUG)
					System.out.println("perioddontkeeptrackwhile off from " + this);
				onPeriod.accept(b);
				numPeriodsElapsed++;
			}
		}
		else {
			if (hasPeriodicity() && millisAsMinutes(b.getTotalTimeSinceStart()-timeCreated) / period >= (numPeriodsElapsed+1)) {
				if (DEBUG)
					System.out.println("periodkeeptrackwhile off from " + this);
				onPeriod.accept(b);
				numPeriodsElapsed++;
			}

		}
	}
	/**
	 * set the onPlace consumer of this event
	 * @param onPlace the consumer to set to onPlace
	 */
	public void setOnPlace(final SerializableConsumer<Board> onPlace) {
		this.onPlace = onPlace;
	}
	/**
	 * @return true if this event was marked for rest
	 */
	public boolean shouldBeReset() {
		return shouldBeReset;
	}
	/**
	 * Execute the rest of this event. this entails calling onRemove, onPlace, and then setting
	 * onRemove to the newOnRemove that was passed in from markForRest.
	 * @param b the Board to act upon
	 * @return the Runnable to run
	 */
	public Runnable executeOnReset(final Board b) {
		return () -> {
			if (shouldBeReset) {
				shouldBeReset = false; 
				onRemove.accept(b);
				onPlace.accept(b);
				onRemove = newOnRemove; 
				newOnRemove = null;
			}
		};
	}
	/**
	 * The purpose of "resetting" an event is to perform actions similar to what would have happened if the 
	 * event was removed and then placed back down again. 
	 * If the on place Consumer of this event has already happened, then mark this event for reset, and 
	 * change the onRemove function to the "newOnRemove" function upon "executeOnReset" being called.
	 * If the on place Consumer has NOT happened, then don't mark this event for reset, just simply change the onRemove 
	 * function to the new onRemove function.
	 * @param newOnRemove the new on Remove
	 */
	public void markForReset(final SerializableConsumer<Board> newOnRemove) {
		if (onPlaceExecuted())
		{
			shouldBeReset = true;
			this.newOnRemove = newOnRemove;
		}
		else {
			this.onRemove = newOnRemove;
		}
	}
	public boolean hasPeriodicity() {
		return isPeriodic;
	}
	public long numPeriodsElapsed() {
		return numPeriodsElapsed;
	}

	protected double millisAsMinutes(final long x) {
		double y = x/1000.0;
		y=y/60.0;
		return y;
	}

	public synchronized void setOnPeriod(final SerializableConsumer<Board> onPeriod) {
		this.onPeriod = onPeriod;
	}
	public synchronized void setPeriod(final double period) {
		this.period = period;
	}
	public synchronized void setOnPeriod(final SerializableConsumer<Board> onPeriod, final double newPeriod) {
		setOnPeriod(onPeriod);
		setPeriod(newPeriod);
	}
	protected SerializableConsumer<Board> getOnPeriod() {
		return onPeriod;
	}
	public SerializableConsumer<Board> getOnPlace() {
		return onPlace;
	}
	protected synchronized void addToTotalPeriods() {
		numPeriodsElapsed++;
	}
	public Runnable executePeriod(final Board b) {
		return () -> executeIfTime(b);
	}
	public boolean keepTrackWhileOff() {
		return keepTrackWhileOff;
	}
	public void setName(final String name) {
		this.name = name;
	}
	public synchronized void markWasRemoved() {
		shouldBeRemoved = true;
	}
	public synchronized boolean wasRemoved() {
		return shouldBeRemoved;
	}
	@Override
	public String toString() {
		return name;
	}
	public void addToName(final String name) {
		final StringBuilder sb = new StringBuilder(this.name);
		sb.append(name);
		this.name = sb.toString();
	}
	public Runnable executeOnPlace(final Board b) {
		return () -> runOnPlace(b);
	}
	public Runnable executeOnRemove(final Board b) {
		return () -> runRemove(b);
	}


}
