package effects; 
import java.io.Serializable;
import java.util.function.*;
import game.Board;
public class Event implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Consumer<Board> onPlace = x -> {};
	private Consumer<Board> onPeriod = x -> {};
	private Consumer<Board> onRemove = x -> {};
	private int period = -1; //period in minutes
	private long timeCreated;
	private volatile long numPeriodsElapsed = 0;
	private boolean isPeriodic = false;
	private boolean onPlaceExecuted = false;
	private boolean keepTrackWhileOff = false;
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
		this.timeCreated = System.currentTimeMillis();
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
	private synchronized void runOnPlace(Board b) {
		onPlaceExecuted = true;
		onPlace.accept(b);
	}
	private synchronized void runRemove(Board b) {
		onRemove.accept(b);
	}
	public boolean onPlaceExecuted() {
		return onPlaceExecuted;
	}
	private synchronized void executeIfTime(Board b) {
		if (!keepTrackWhileOff) {
			long gameTime = b.getTotalGameTime();
			if (hasPeriodicity() && difAsMinutes(gameTime) / period >= (numPeriodsElapsed+1)*period) {
				onPeriod.accept(b);
				numPeriodsElapsed++;
			}
		}
		else {
			long currentTime = System.currentTimeMillis();
			if (hasPeriodicity() && difAsMinutes(currentTime-timeCreated) / period >= (numPeriodsElapsed+1)*period) {
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
	protected long difAsMinutes(long x) {
		x = x/1000;
		x=x/60;
		return x;
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
