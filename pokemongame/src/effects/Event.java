package effects; 
import static gameutils.Constants.DEBUG; 

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import interfaces.SerializableConsumer;

import game.Board;
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
	private boolean keepTrackWhileOff = false;
	private final static double MIN_PERIOD = .01;
	private String name = "Event ";
	public Event() {
	}
	public Event(SerializableConsumer<Board> onPlace) {
		this.onPlace = onPlace;
	}
	public Event(SerializableConsumer<Board> onPlace, SerializableConsumer<Board> onRemove) {
		this(onPlace);
		this.onRemove = onRemove;
	}
	public Event(SerializableConsumer<Board> onPeriod, int periodInMinutes) {
		this.onPeriod = onPeriod;
		this.period = periodInMinutes;
		isPeriodic = periodInMinutes > 0L;


	}
	public Event(SerializableConsumer<Board> onPeriod, double periodInMinutes) {
		this.onPeriod = onPeriod;
		this.period = periodInMinutes;
		isPeriodic = periodInMinutes > 0.0;
		if (isPeriodic)
			this.period = Math.max(periodInMinutes, MIN_PERIOD);

	}
	public Event(SerializableConsumer<Board> onPlace, SerializableConsumer<Board> onPeriod, int periodInMinutes) {
		this(onPeriod, periodInMinutes);
		this.onPlace = onPlace;
	}
	public Event(SerializableConsumer<Board> onPlace, SerializableConsumer<Board> onPeriod, SerializableConsumer<Board> onRemove, int periodInMinutes) {
		this(onPeriod, periodInMinutes);
		this.onPlace = onPlace;
		this.onRemove = onRemove;
	}
	public Event(SerializableConsumer<Board> onPlace, SerializableConsumer<Board> onPeriod, double periodInMinutes) {
		this(onPeriod, periodInMinutes);
		this.onPlace = onPlace;
	}
	public Event(SerializableConsumer<Board> onPlace, SerializableConsumer<Board> onPeriod, SerializableConsumer<Board> onRemove, double periodInMinutes) {
		this(onPeriod, periodInMinutes);
		this.onPlace = onPlace;
		this.onRemove = onRemove;
	}
	private synchronized void runOnPlace(Board b) {
		if (!onPlaceExecuted()) {
			timeCreated = b.getTotalTimeSinceStart();
			inGameTimeCreated = b.getTotalInGameTime();
		}
		onPlaceExecuted.set(true);
		onPlace.accept(b);
		if (DEBUG)
			System.out.println("runOnPlace from " + this);
	}
	private synchronized void runRemove(Board b) {
		onPlaceExecuted.set(false);
		onRemove.accept(b);
		if (DEBUG)
			System.out.println("runOnRemove from " + this);
	}
	public synchronized boolean onPlaceExecuted() {
		return onPlaceExecuted.get();
	}
	private synchronized void executeIfTime(Board b) {
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
	public boolean hasPeriodicity() {
		return isPeriodic;
	}
	public long numPeriodsElapsed() {
		return numPeriodsElapsed;
	}

	protected double millisAsMinutes(long x) {
		double y = x/1000.0;
		y=y/60.0;
		return y;
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
	public Runnable executePeriod(Board b) {
		return new Runnable() {
			public void run() {
				executeIfTime(b);
			}
		};
	}
	public boolean keepTrackWhileOff() {
		return keepTrackWhileOff;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return name;
	}
	public void addToName(String name) {
		StringBuilder sb = new StringBuilder(this.name);
		sb.append(name);
		this.name = sb.toString();
	}
	public Runnable executeOnPlace(Board b) {
		return new Runnable() {
			public void run() {
				runOnPlace(b);
			}
		};
	}
	public Runnable executeOnRemove(Board b) {
		return new Runnable() {
			public void run() {
				runRemove(b);
			}
		};
	}


}
