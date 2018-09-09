package effects;

import gameutils.GameUtils;
import interfaces.SerializableConsumer;
import interfaces.SerializableFunction;
import model.ModelInterface;

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
	private SerializableFunction<ModelInterface, Double> generatePeriod = x -> {return -1.0;};
	private volatile long numCurrentPeriodsElapsed = 0;
	private volatile long timeOfLastChange;
	private volatile double currentPeriodVal = -1;
	private static final double MIN_PERIOD = .01;
	/**
	 * Creates a new CustomPeriodEvent
	 * @param onPeriod what should happen every period
	 * @param generatePeriod the function that, given the state of the ModelInterface, determines this Event's current period
	 */
	public CustomPeriodEvent(final SerializableConsumer<ModelInterface> onPeriod, final SerializableFunction<ModelInterface, Double> generatePeriod) {
		super(onPeriod, Integer.MAX_VALUE);
		this.generatePeriod = generatePeriod;
	}
	/**
	 * Creates a new CustomperiodEvent
	 * @param onPlace what should happen when this event is placed
	 * @param onPeriod what should happen every period
	 * @param generatePeriod the function that, given the state of the ModelInterface, determines this Event's current period
	 */
	public CustomPeriodEvent(final SerializableConsumer<ModelInterface> onPlace, final SerializableConsumer<ModelInterface> onPeriod, final SerializableFunction<ModelInterface, Double> generatePeriod) {
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
	private synchronized void executeIfTime(final ModelInterface b) {
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
	 * @see effects.Event#executePeriod(model.ModelInterface)
	 */
	@Override
	public Runnable executePeriod(final ModelInterface model) {
		return new Runnable() {
			@Override
			public void run() {
				executeIfTime(model);
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
