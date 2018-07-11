package modifiers;

import java.io.Serializable;

import gameutils.GameUtils;
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
	public Modifier(final Modifier m) {
		this(m.lifeInMillis, m.shouldModify, m.modification, m.reverseModification);
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
		return lifeInMillis > 0  && (currentTime - timeStart) >= lifeInMillis;
	}
	public void performReverseModification(final T actOn) {
		reverseModification.accept(actOn);
	}
	public String timeLeft(final long currentTime)  {
		return GameUtils.millisecondsToTime(lifeInMillis - (currentTime - timeStart));
	}
	public boolean hasTimeLimit() {
		return lifeInMillis > 0;
	}

	

	
}
