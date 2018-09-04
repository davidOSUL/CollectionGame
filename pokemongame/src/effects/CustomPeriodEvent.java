package effects;

import game.Board;
import gameutils.GameUtils;
import interfaces.SerializableConsumer;
import interfaces.SerializableFunction;

/**
 * Event whose period can change over time
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
	/**
	 * Creates a new CustomPeriodEvent
	 * @param onPeriod what should happen every period
	 * @param generatePeriod the function that, given the state of the board, determines this Event's current period
	 */
	public CustomPeriodEvent(final SerializableConsumer<Board> onPeriod, final SerializableFunction<Board, Double> generatePeriod) {
		super(onPeriod, Integer.MAX_VALUE);
		this.generatePeriod = generatePeriod;
	}
	/**
	 * Creates a new CustomperiodEvent
	 * @param onPlace what should happen when this event is placed
	 * @param onPeriod what should happen every period
	 * @param generatePeriod the function that, given the state of the board, determines this Event's current period
	 */
	public CustomPeriodEvent(final SerializableConsumer<Board> onPlace, final SerializableConsumer<Board> onPeriod, final SerializableFunction<Board, Double> generatePeriod) {
		super(onPlace, onPeriod, Integer.MAX_VALUE);
		this.generatePeriod = generatePeriod;
	}
	/**
	 * Copy constructor
	 */
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

	/** 
	 * @see effects.Event#executePeriod(game.Board)
	 */
	@Override
	public Runnable executePeriod(final Board b) {
		return new Runnable() {
			@Override
			public void run() {
				executeIfTime(b);
			}
		};
	}
	/** 
	 * @see effects.Event#makeCopy()
	 */
	@Override
	public CustomPeriodEvent makeCopy() {
		return new CustomPeriodEvent(this);
	}
}
