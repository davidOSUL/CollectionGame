package effects; 
import static gameutils.Constants.DEBUG;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

import game.Board;
import gameutils.GameUtils;
import interfaces.SerializableConsumer;
import thingFramework.Thing;
/**
 * Various things make events which affect the state of the board at different times. Events have an
 * "onPlace", "onRemove" functions which happen when they are created/destroyed. Some events also have an
 * "onPeriod" function which happens at a regular interval, and an "onTick" function that happens every time the game
 * is updated
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
	private SerializableConsumer<Board> onTick = x -> {};
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
	private Thing creator;
	/**
	 * Creates a new event that does nothing to the boad
	 */
	public Event() {
	}
	/**
	 * Creates a new event that does something to the board when it is put down
	 * @param onPlace what should happen to the board upon placing this event
	 */
	public Event(final SerializableConsumer<Board> onPlace) {
		setOnPlace(onPlace);
	}
	/**
	 * Creates a new event that does something to the board when it is put down and also when it is removed
	 * @param onPlace what should happen to the board upon placing this event
	 * @param onRemove what should happen to the board upon removing this event
	 */
	public Event(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onRemove) {
		this(onPlace);
		setOnRemove(onRemove);
	}
	/**
	 * Creates a new event that does something to the board periodically
	 * @param onPeriod what should happen to the board periodically
	 * @param periodInMinutes the length of the period (in minutes)
	 */
	public Event(final SerializableConsumer<Board> onPeriod, final int periodInMinutes) {
		setOnPeriod(onPeriod);
		setPeriod(periodInMinutes);
	}
	/**
	 * Creates a new event that does something to the board periodically
	 * @param onPeriod what should happen to the board periodically
	 * @param periodInMinutes the length of the period (in minutes)
	 */
	public Event(final SerializableConsumer<Board> onPeriod, final double periodInMinutes) {
		setOnPeriod(onPeriod);
		setPeriod(periodInMinutes);
		

	}
	/**
	 * Creates a new event that does something to the board when it is put down, and also periodically
	 * @param onPlace what should happen to the board upon placing this event
	 * @param onPeriod what should happen to the board periodically
	 * @param periodInMinutes the length of the period (in minutes)
	 */
	public Event(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onPeriod, final int periodInMinutes) {
		this(onPeriod, periodInMinutes);
		setOnPlace(onPlace);
	}
	/**
	 * Creates a new event that does something to the board when it is put down, and also periodically
	 * @param onPlace what should happen to the board upon placing this event
	 * @param onPeriod what should happen to the board periodically
	 * @param periodInMinutes the length of the period (in minutes)
	 */
	public Event(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onPeriod, final double periodInMinutes) {
		this(onPeriod, periodInMinutes);
		setOnPlace(onPlace);
	}
	/**
	 * Creates a new event that does something to the board when it is put down, when it is removed, and also periodically
	 * @param onPlace what should happen to the board upon placing this event
	 * @param onPeriod what should happen to the board periodically
	 * @param onRemove what should happen to the board upon removing this event
	 * @param periodInMinutes the length of the period (in minutes)
	 */
	public Event(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onPeriod, final SerializableConsumer<Board> onRemove, final int periodInMinutes) {
		this(onPeriod, periodInMinutes);
		setOnPlace(onPlace);
		setOnRemove(onRemove);
	}
	
	/**
	 * Creates a new event that does something to the board when it is put down, when it is removed, and also periodically
	 * @param onPlace what should happen to the board upon placing this event
	 * @param onPeriod what should happen to the board periodically
	 * @param onRemove what should happen to the board upon removing this event
	 * @param periodInMinutes the length of the period (in minutes)
	 */
	public Event(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onPeriod, final SerializableConsumer<Board> onRemove, final double periodInMinutes) {
		this(onPeriod, periodInMinutes);
		setOnPlace(onPlace);
		setOnRemove(onRemove);
	}
	/**
	 * Creates a new event that does something to the board when it is put down, when it is removed, periodically, and every game tick
	 * @param onPlace what should happen to the board upon placing this event
	 * @param onPeriod what should happen to the board periodically
	 * @param onRemove what should happen to the board upon removing this event
	 * @param onTick what should happen to the board every game tick
	 * @param periodInMinutes the length of the period (in minutes)
	 */
	public Event(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onPeriod, final SerializableConsumer<Board> onRemove, final SerializableConsumer<Board> onTick, final double periodInMinutes) {
		this(onPlace, onPeriod, onRemove, periodInMinutes);
		setOnTick(onTick);
	}
	/**
	 * Creates a new Event that copies over all the data from another event
	 * @param oldEvent the event to copy over from
	 */
	protected Event(final Event oldEvent) {
		this(oldEvent.onPlace, oldEvent.onPeriod, oldEvent.onRemove, oldEvent.onTick, oldEvent.period);
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
	private synchronized void runOnTick(final Board b) {
		onTick.accept(b);
	}
	/**
	 * Returns true if the "onPlace" consumer has been executed
	 * @return true if onPlace has been executed
	 */
	public synchronized boolean onPlaceExecuted() {
		return onPlaceExecuted.get();
	}
	/**
	 * Executes the onPeriod consumer if it should
	 */
	private synchronized void executeIfTime(final Board b) {
		if (!keepTrackWhileOff) {
			if (hasPeriodicity() && (long) (GameUtils.millisAsMinutes(b.getTotalInGameTime()-inGameTimeCreated) / period) > numPeriodsElapsed) {
				if (DEBUG)
					System.out.println("perioddontkeeptrackwhile off from " + this);
				onPeriod.accept(b);
				numPeriodsElapsed++;
			}
		}
		else {
			if (hasPeriodicity() && (long) (GameUtils.millisAsMinutes(b.getTotalTimeSinceStart()-timeCreated) / period) > numPeriodsElapsed) {
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
	 * The purpose of "resetting" an event is to perform actions similar to what would have happened if the 
	 * event was removed and then placed back down again. 
	 * One of two things will happen when calling this method depending on whether or no onPlace has exectued
	 * <p> If the on place Consumer of this event has already happened, then this event will be marked for reset, and 
	 * the onRemove function will be changed to the "newOnRemove" function UPON "executeOnReset" being called.
	 * <p> If the on place Consumer has NOT happened, then this event won't be marked for reset, and the onRemove function
	 * will be set to the newOnRemove
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
	/**
	 * Returns true if this event is periodic
	 * @return true if this event is peridic
	 */
	boolean hasPeriodicity() {
		return isPeriodic;
	}
	/**
	 * returns the number of periods that have elapsed
	 * @return the number of periods that have elapsed
	 */
	public long numPeriodsElapsed() {
		return numPeriodsElapsed;
	}

	

	/**
	 * Sets the onPeriod consumer for this event
	 * @param onPeriod the onPeriod consumer for this event
	 */
	public synchronized void setOnPeriod(final SerializableConsumer<Board> onPeriod) {
		this.onPeriod = onPeriod;
	}
	/**
	 * returns the period of this event in minutes
	 * @return the period of this event in minutes
	 */
	public synchronized double getPeriodInMinutes() {
		return this.period;
	}
	/**
	 * Sets the period of this event in minutes
	 * @param periodInMinutes the new period of this event in minutes
	 */
	public synchronized void setPeriod(final double periodInMinutes) {
		this.period = periodInMinutes;
		isPeriodic = period > 0;
		if (isPeriodic)
			this.period = Math.max(periodInMinutes, MIN_PERIOD);
	}
	/**
	 * Sets the onPeriod consumer for this event as well as the new period in minutes for this event
	 * @param onPeriod the onPeriod consumer for this event
	 * @param newPeriod the new period of this event in minutes
	 */
	public synchronized void setOnPeriod(final SerializableConsumer<Board> onPeriod, final double newPeriod) {
		setOnPeriod(onPeriod);
		setPeriod(newPeriod);
	}
	/**
	 * Returns the Consumer that is executed every event period
	 * @return the Consumer that is executed every event period
	 */
	protected SerializableConsumer<Board> getOnPeriod() {
		return onPeriod;
	}
	/**
	 * Returns the Consumer that is executed when this event is placed down
	 * @return the Consumer that is executed when this event is placed down
	 */
	public SerializableConsumer<Board> getOnPlace() {
		return onPlace;
	}
	/**
	 * Increases the total number of periods that have elapsed
	 */
	protected synchronized void addToTotalPeriods() {
		numPeriodsElapsed++;
	}
	/**
	 * Returns a runnable that when run, will execute onPeriod if it is time to do so
	 * This should be called every game tick.
	 * @param b the board to execute the onPeriod consumer upon
	 * @return the runnable to be run
	 */
	public Runnable executePeriod(final Board b) {
		return () -> executeIfTime(b);
	}
	/**
	 * Returns true if this event keep tracks of its lifetime/periods while the game is not running
	 * @return  true if this event keep tracks of its lifetime/periods while the game is not running
	 */
	public boolean keepTrackWhileOff() {
		return keepTrackWhileOff;
	}
	/**
	 * Sets the name of this event
	 * @param name the new name of this event
	 */
	public void setName(final String name) {
		this.name = name;
	}
	/**
	 * Marks this event for removal. Objects with references with this event when calling wasMarkedForRemoval(), will now
	 * know that this event should be removed when it can.
	 */
	public synchronized void markForRemoval() {
		shouldBeRemoved = true;
	}
	/**
	 * Returns true if this event was marked for removal
	 * @return  true if this event was marked for removal
	 */
	public synchronized boolean wasMarkedForRemoval() {
		return shouldBeRemoved;
	}
	/** 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
	/**
	 * Appends the provided text to the current name of this Event
	 * @param nameAddition the text to add to the name of this Event
	 */
	public void addToName(final String nameAddition) {
		final StringBuilder sb = new StringBuilder(this.name);
		sb.append(nameAddition);
		this.name = sb.toString();
	}
	/**
	 * Returns a runnable that when run, will execute onPlace. 
	 * @param b the board to execute the onPlace consumer upon
	 * @return the runnable to be run
	 */
	public Runnable executeOnPlace(final Board b) {
		return () -> runOnPlace(b);
	}
	/**
	 * Returns a runnable that when run, will execute onRemove. 
	 * @param b the board to execute the onRemove consumer upon
	 * @return the runnable to be run
	 */
	public Runnable executeOnRemove(final Board b) {
		return () -> runRemove(b);
	}
	/**
	 * Returns a runnable that when run, will execute onTick. This should be called every game tick
	 * @param b the board to execute the onTick consumer upon
	 * @return the runnable to be run
	 */
	public Runnable executeOnTick(final Board b) {
		return () -> runOnTick(b);
	}
	/**
	 * Sets the onRemove consumer for this event. 
	 * @param onRemove the new onRemove consumer for this event.
	 */
	protected void setOnRemove(final SerializableConsumer<Board> onRemove) {
		this.onRemove = onRemove;
	}
	/**
	 * Sets the onTick consumer for this event
	 * @param onTick the new onTick consumer for this event
	 */
	public void setOnTick(final SerializableConsumer<Board> onTick) {
		this.onTick = onTick;
	}
	/**
	 * Returns the onRemove consumer for this event
	 * @return  the onRemove consumer for this event
	 */
	protected SerializableConsumer<Board> getOnRemove() {
		return onRemove;
	}
	/**
	 * Returns a runnable that executes the reset procedure for this event. This entails calling onRemove, onPlace, and then setting
	 * onRemove to the newOnRemove that was passed in from markForRest. Finally, it will be unmarked as needing reset
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
	 * Returns a string representing the amount of time until the next period occurs
	 * @param b the board to get the time from
	 * @return the String representation (of the format : MM:SS)
	 */
	public String getTimeToNextPeriod(final Board b) {
		if (period < 0)
			return GameUtils.infinitySymbol();
		final long timeAliveAsMillis = keepTrackWhileOff ? b.getTotalTimeSinceStart() - timeCreated : b.getTotalInGameTime() - inGameTimeCreated;
		final long periodAsMillis = GameUtils.minutesToMillis(period);
		return GameUtils.millisecondsToTime(periodAsMillis - (timeAliveAsMillis % periodAsMillis));
		
	}
	/**
	 * Makes a copy of this event
	 * @return the copy of this event
	 */
	public Event makeCopy() {
		return new Event(this);
	}
	/**
	 * Gets the creator of this event
	 * @return the Thing that created this event
	 */
	public Thing getCreator() {
		if (this.creator == null)
			throw new IllegalStateException("Attempted to access null creator");
		return creator;
	}
	/**
	 * Sets the creator for this event
	 * @param creator the creator of this event
	 * @throws IllegalStateException if called after onPlace has been executed
	 */
	public void setCreator(final Thing creator) {
		if (onPlaceExecuted())
			throw new IllegalStateException("Attempted to change creator after placing!");
		this.creator = creator;
	}
	/**
	 * Returns true if this Event has a creator set, false otherwise
	 * @return true if this Event has a creator set, false otherwise
	 */
	public boolean hasCreator() {
		return creator != null;
	}

}
