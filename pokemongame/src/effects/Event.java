package effects; 
import java.io.Serializable;
import java.util.function.Consumer;

import game.Board;
public class Event implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Consumer<Board> onPlace = x -> {};
	private Consumer<Board> onPeriod = x -> {};
	private Consumer<Board> onRemove = x -> {};
	private double period = -1; //period in minutes
	private long timeCreated;
	private volatile long numPeriodsElapsed = 0;
	private boolean isPeriodic = false;
	private boolean onPlaceExecuted = false;
	private boolean keepTrackWhileOff = false;
	private final static double MIN_PERIOD = .01;
	public Event() {
	}
	public Event(Consumer<Board> onPlace) {
		this.onPlace = onPlace;
	}
	public Event(Consumer<Board> onPlace, Consumer<Board> onRemove) {
		this(onPlace);
		this.onRemove = onRemove;
	}
	public Event(Consumer<Board> onPeriod, int periodInMinutes) {
		this.onPeriod = onPeriod;
		this.period = periodInMinutes;
		isPeriodic = periodInMinutes > 0L;
		
		
	}
	public Event(Consumer<Board> onPeriod, double periodInMinutes) {
		this.onPeriod = onPeriod;
		this.period = periodInMinutes;
		isPeriodic = periodInMinutes > 0.0;
		if (isPeriodic)
			this.period = Math.max(periodInMinutes, MIN_PERIOD);
		
	}
	public Event(Consumer<Board> onPlace, Consumer<Board> onPeriod, int periodInMinutes) {
		this(onPeriod, periodInMinutes);
		this.onPlace = onPlace;
	}
	public Event(Consumer<Board> onPlace, Consumer<Board> onPeriod, Consumer<Board> onRemove, int periodInMinutes) {
		this(onPeriod, periodInMinutes);
		this.onPlace = onPlace;
		this.onRemove = onRemove;
	}
	public Event(Consumer<Board> onPlace, Consumer<Board> onPeriod, double periodInMinutes) {
		this(onPeriod, periodInMinutes);
		this.onPlace = onPlace;
	}
	public Event(Consumer<Board> onPlace, Consumer<Board> onPeriod, Consumer<Board> onRemove, double periodInMinutes) {
		this(onPeriod, periodInMinutes);
		this.onPlace = onPlace;
		this.onRemove = onRemove;
	}
	private synchronized void runOnPlace(Board b) {
		if (!onPlaceExecuted)
			timeCreated = System.currentTimeMillis();
		onPlaceExecuted = true;
		onPlace.accept(b);
	}
	private synchronized void runRemove(Board b) {
		onPlaceExecuted = false;
		onRemove.accept(b);
	}
	public boolean onPlaceExecuted() {
		return onPlaceExecuted;
	}
	private synchronized void executeIfTime(Board b) {
		if (!keepTrackWhileOff) {
			long gameTime = b.getTotalGameTime();
			if (hasPeriodicity() && difAsMinutes(gameTime) / period >= (numPeriodsElapsed+1)) {
				onPeriod.accept(b);
				numPeriodsElapsed++;
			}
		}
		else {
			long currentTime = System.currentTimeMillis();
			if (hasPeriodicity() && difAsMinutes(currentTime-timeCreated) / period >= (numPeriodsElapsed+1)) {
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
	public long getTimeCreated() {
		return timeCreated;
	}
	protected double difAsMinutes(long x) {
		double y = x/1000.0;
		y=y/60.0;
		return y;
	}
	protected Consumer<Board> getOnPeriod() {
		return onPeriod;
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
