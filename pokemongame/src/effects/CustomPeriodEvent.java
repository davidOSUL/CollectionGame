package effects;

import java.util.function.Consumer;
import java.util.function.Function;

import game.Board;
import interfaces.SerializableConsumer;
import interfaces.SerializableFunction;

public class CustomPeriodEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SerializableFunction<Board, Double> generatePeriod = x -> {return -1.0;};
	private volatile long numCurrentPeriodsElapsed = 0;
	private volatile long timeOfLastChange;
	private volatile double currentPeriodVal = -1;
	private static final double MIN_PERIOD = .01;
	public CustomPeriodEvent(SerializableConsumer<Board> onPeriod, SerializableFunction<Board, Double> generatePeriod) {
		super(onPeriod, Integer.MAX_VALUE);
		this.generatePeriod = generatePeriod;
	}
	public CustomPeriodEvent(SerializableConsumer<Board> onPlace, SerializableConsumer<Board> onPeriod, SerializableFunction<Board, Double> generatePeriod) {
		super(onPlace, onPeriod, Integer.MAX_VALUE);
		this.generatePeriod = generatePeriod;
	}
	private synchronized void executeIfTime(Board b) {
		double period = generatePeriod.apply(b);
		if (period <0)
			return;
		period = Math.max(MIN_PERIOD, period);
		if (currentPeriodVal != period) {
			timeOfLastChange = keepTrackWhileOff() ? b.getTotalGameTime() : b.getSessionGameTime();
			currentPeriodVal = period;
			numCurrentPeriodsElapsed = 0;
		}
		if (!keepTrackWhileOff()) {
			if (millisAsMinutes(b.getSessionGameTime()-timeOfLastChange) / period >= (numCurrentPeriodsElapsed+1)) {
				getOnPeriod().accept(b);
				numCurrentPeriodsElapsed++;
				addToTotalPeriods();
			}
		}
		else {
			if (millisAsMinutes(b.getTotalGameTime()-timeOfLastChange) / period >= (numCurrentPeriodsElapsed+1)) {
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
	@Override
	public void endSession() {
		super.endSession();
		if (!keepTrackWhileOff()) {
			timeOfLastChange = 0;
			numCurrentPeriodsElapsed = 0;
		}
	}
}
