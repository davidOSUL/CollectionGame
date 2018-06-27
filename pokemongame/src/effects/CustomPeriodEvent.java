package effects;

import java.util.function.Consumer;
import java.util.function.Function;

import game.Board;

public class CustomPeriodEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Function<Board, Double> generatePeriod = x -> {return -1.0;};
	private volatile long numCurrentPeriodsElapsed = 0;
	private volatile long timeOfLastChange = System.currentTimeMillis();
	private volatile double currentPeriodVal = -1;
	private static final double MIN_PERIOD = .01;
	public CustomPeriodEvent(Consumer<Board> onPeriod, Function<Board, Double> generatePeriod) {
		super(onPeriod, Integer.MAX_VALUE);
		this.generatePeriod = generatePeriod;
	}
	public CustomPeriodEvent(Consumer<Board> onPlace, Consumer<Board> onPeriod, Function<Board, Double> generatePeriod) {
		super(onPlace, onPeriod, Integer.MAX_VALUE);
		this.generatePeriod = generatePeriod;
	}
	private synchronized void executeIfTime(Board b) {
		double period = generatePeriod.apply(b);
		period = Math.max(MIN_PERIOD, period);
		if (currentPeriodVal != period) {
			timeOfLastChange = keepTrackWhileOff() ? System.currentTimeMillis() : b.getTotalGameTime();
			currentPeriodVal = period;
			numCurrentPeriodsElapsed = 0;
		}
		if (!keepTrackWhileOff()) {
			if (difAsMinutes(b.getTotalGameTime()-timeOfLastChange) / period > (numCurrentPeriodsElapsed+1)) {
				getOnPeriod().accept(b);
				numCurrentPeriodsElapsed++;
				addToTotalPeriods();
			}
		}
		else {
			long currentTime = System.currentTimeMillis();
			if (difAsMinutes(currentTime-timeOfLastChange) / period > (numCurrentPeriodsElapsed+1)) {
				getOnPeriod().accept(b);
				numCurrentPeriodsElapsed++;
				addToTotalPeriods();
			}
		}
	}
	@Override
	public Event createNewEventCopy() {
		return new CustomPeriodEvent(getOnPlace(), getOnPeriod(), generatePeriod);
	}
	@Override
	public Runnable executePeriod(Board b) {
		return new Runnable() {
			public void run() {
				executeIfTime(b);
			}
		};
	}
	public long numCurrentPeriodsElapsed() {
		return numCurrentPeriodsElapsed;
	}
}
