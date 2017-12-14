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
	private long numCurrentPeriodsElapsed = 0;
	private long timeOfLastChange = System.currentTimeMillis();
	private double currentPeriodVal = -1;
	public CustomPeriodEvent(Consumer<Board> onPeriod, Function<Board, Double> generatePeriod) {
		super(onPeriod, Integer.MAX_VALUE);
		this.generatePeriod = generatePeriod;
	}
	private synchronized void executeIfTime(Board b) {
		double period = generatePeriod.apply(b);
		if (currentPeriodVal != period) {
			timeOfLastChange = System.currentTimeMillis();
			currentPeriodVal = period;
			numCurrentPeriodsElapsed = 0;
		}
		if (!keepTrackWhileOff()) {
			if (difAsMinutes(b.getTotalGameTime()) / period > (numCurrentPeriodsElapsed+1)*period) {
				getOnPeriod().accept(b);
				numCurrentPeriodsElapsed++;
				addToTotalPeriods();
			}
		}
		else {
			long currentTime = System.currentTimeMillis();
			if (difAsMinutes(currentTime-getTimeCreated()) / period > (numCurrentPeriodsElapsed+1)*period) {
				getOnPeriod().accept(b);
				numCurrentPeriodsElapsed++;
				addToTotalPeriods();
			}
		}
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
