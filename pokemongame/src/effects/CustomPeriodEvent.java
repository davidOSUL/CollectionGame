package effects;

import game.Board;
import gameutils.GameUtils;
import interfaces.SerializableConsumer;
import interfaces.SerializableFunction;

/**
 * Event whose period changes
 * @author David O'Sullivan
 *
 */
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
	public CustomPeriodEvent(final SerializableConsumer<Board> onPeriod, final SerializableFunction<Board, Double> generatePeriod) {
		super(onPeriod, Integer.MAX_VALUE);
		this.generatePeriod = generatePeriod;
	}
	public CustomPeriodEvent(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onPeriod, final SerializableFunction<Board, Double> generatePeriod) {
		super(onPlace, onPeriod, Integer.MAX_VALUE);
		this.generatePeriod = generatePeriod;
	}
	private CustomPeriodEvent(final CustomPeriodEvent event) {
		super(event);
		this.generatePeriod = event.generatePeriod;
	}
	private synchronized void executeIfTime(final Board b) {
		double period = generatePeriod.apply(b);
		if (period <0)
			return;
		period = Math.max(MIN_PERIOD, period);
		if (currentPeriodVal != period) {
			timeOfLastChange = keepTrackWhileOff() ? b.getTotalTimeSinceStart() : b.getTotalInGameTime();
			currentPeriodVal = period;
			numCurrentPeriodsElapsed = 0;
		}
		if (!keepTrackWhileOff()) {
			if (GameUtils.millisAsMinutes(b.getTotalInGameTime()-timeOfLastChange) / period >= (numCurrentPeriodsElapsed+1)) {
				getOnPeriod().accept(b);
				numCurrentPeriodsElapsed++;
				addToTotalPeriods();
			}
		}
		else {
			if (GameUtils.millisAsMinutes(b.getTotalTimeSinceStart()-timeOfLastChange) / period >= (numCurrentPeriodsElapsed+1)) {
				getOnPeriod().accept(b);
				numCurrentPeriodsElapsed++;
				addToTotalPeriods();
			}
		}
	}

	@Override
	public Runnable executePeriod(final Board b) {
		return new Runnable() {
			@Override
			public void run() {
				executeIfTime(b);
			}
		};
	}
	public long numCurrentPeriodsElapsed() {
		return numCurrentPeriodsElapsed;
	}
	@Override
	public CustomPeriodEvent makeCopy() {
		return new CustomPeriodEvent(this);
	}
}
