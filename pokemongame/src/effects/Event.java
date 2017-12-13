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
	private int period = -1; //period in minutes
	private long timeCreated;
	private long numPeriodsElapsed = 0;
	private boolean isPeriodic = false;
	private boolean onPlaceExecuted = false;
	public Event() {
	}
	public Event(Consumer<Board> onPlace) {
		this.onPlace = onPlace;
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
	private void runOnPlace(Board b) {
		onPlaceExecuted = true;
		onPlace.accept(b);
	}
	public boolean onPlaceExecuted() {
		return onPlaceExecuted;
	}
	private synchronized void executeIfTime(long millis, Board b) {
		
		if (hasPeriodicity() && difAsMinutes(millis-timeCreated) / period > (numPeriodsElapsed+1)*period) {
			onPeriod.accept(b);
			numPeriodsElapsed++;
		}
	}
	public boolean hasPeriodicity() {
		return isPeriodic;
	}
	public long numPeriodsElapsed() {
		return numPeriodsElapsed;
	}
	private long difAsMinutes(long x) {
		x = x/1000;
		x=x/60;
		return x;
	}
	public Runnable executePeriod(long millis, Board b) {
		return new Runnable() {
			public void run() {
				executeIfTime(millis, b);
			}
		};
	}
	public Runnable executeOnPlace(Board b) {
		return new Runnable() {
			public void run() {
				runOnPlace(b);
			}
		};
	}
	
	
}
