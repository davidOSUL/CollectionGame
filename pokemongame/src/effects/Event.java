package effects;
import java.util.Date;
import java.util.function.*;
import game.Board;
public class Event  {
	private Consumer<Board> onPlace = x -> {};
	private Consumer<Board> onPeriod = x -> {};
	private int period = -1; //period in minutes
	private long timeCreated;
	private long numPeriodsElapsed = 0;
	private boolean isPeriodic = false;
	public Event() {
		
	}
	public Event(Consumer<Board> onPlace) {
		this.onPlace = onPlace;
	}
	public Event(Consumer<Board> onPlace, Consumer<Board> onPeriod, int periodInMinutes) {
		this(onPlace);
		this.onPeriod = onPeriod;
		this.period = periodInMinutes;
		isPeriodic = periodInMinutes > 0L;
		this.timeCreated = System.currentTimeMillis();
	}
	public void executeOnPlace(Board b) {
		onPlace.accept(b);
	}
	public void executeIfTime(long millis, Board b) {
		
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
	
}
