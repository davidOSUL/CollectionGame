package modifiers;

import java.io.Serializable;

import interfaces.SerializableConsumer;
import interfaces.SerializablePredicate;

/**
 * @author David O'Sullivan
 *
 * @param <T> the object that this modifies
 */
public class Modifier<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * How long should exist before going away. -1 to never go away.
	 */
	private final long lifeInMillis;
	private final SerializablePredicate<T> shouldModify;
	private final SerializableConsumer<T> modification;
	private final SerializableConsumer<T> reverseModification;
	private long timeStart = System.currentTimeMillis();
	public Modifier (final SerializableConsumer<T> modification,  final SerializableConsumer<T> reverseModification) {
		this(-1, x -> true, modification, reverseModification);
	}
	public Modifier (final long lifeInMillis, final SerializableConsumer<T> modification,  final SerializableConsumer<T> reverseModification) {
		this(lifeInMillis, x -> true, modification, reverseModification);
	}
	public Modifier (final SerializablePredicate<T> shouldModify, final SerializableConsumer<T> modification,  final SerializableConsumer<T> reverseModification) {
		this(-1, shouldModify, modification, reverseModification);
	}
	public Modifier (final long lifeInMillis, final SerializablePredicate<T> shouldModify, final SerializableConsumer<T> modification, final SerializableConsumer<T> reverseModification) {
		this.lifeInMillis = lifeInMillis;
		this.shouldModify = shouldModify;
		this.modification = modification;
		this.reverseModification = reverseModification;
	}
	public void startCount(final long startTime) {
		timeStart = startTime;
	}
	public boolean shouldModify(final T t) {
		return shouldModify.test(t);
	}
	public boolean performModificationIfShould(final T t) {
		if (shouldModify.test(t)) {
			modification.accept(t);
			return true;
		}
		return false;
	}
	
	public boolean isDone(final long currentTime) {
		return lifeInMillis != -1 && (currentTime - timeStart) >= lifeInMillis;
	}
	public void performReverseModification(final T actOn) {
		reverseModification.accept(actOn);
	}
	public String timeLeft(final long currentTime)  {
		return millisecondsToTime(lifeInMillis - (currentTime - timeStart));
	}
	private String millisecondsToTime(final long milliseconds) {
	    if (milliseconds < 0)
	    	return "0:00";
		final long minutes = (milliseconds / 1000) / 60;
	    final long seconds = (milliseconds / 1000) % 60;
	    final String secondsStr = Long.toString(seconds);
	    String secs;
	    if (secondsStr.length() >= 2) {
	        secs = secondsStr.substring(0, 2);
	    } else {
	        secs = "0" + secondsStr;
	    }

	    return minutes + ":" + secs;
	}
	
	
	
}
