package modifiers;

import java.io.Serializable;

import gameutils.GameUtils;
import interfaces.SerializableConsumer;
import interfaces.SerializablePredicate;
import thingFramework.Thing;

/**
 * @author David O'Sullivan
 *
 * @param <Thing> the object that this modifies
 */
public final class Modifier implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * How long should exist before going away. -1 to never go away.
	 */
	private final long lifeInMillis;
	private final SerializablePredicate<Thing> shouldModify;
	private final SerializableConsumer<Thing> modification;
	private final SerializableConsumer<Thing> reverseModification;
	private long timeStart = System.currentTimeMillis();
	public Modifier (final SerializableConsumer<Thing> modification,  final SerializableConsumer<Thing> reverseModification) {
		this(-1, x -> true, modification, reverseModification);
	}
	public Modifier (final long lifeInMillis, final SerializableConsumer<Thing> modification,  final SerializableConsumer<Thing> reverseModification) {
		this(lifeInMillis, x -> true, modification, reverseModification);
	}
	public Modifier (final SerializablePredicate<Thing> shouldModify, final SerializableConsumer<Thing> modification,  final SerializableConsumer<Thing> reverseModification) {
		this(-1, shouldModify, modification, reverseModification);
	}
	public Modifier (final long lifeInMillis, final SerializablePredicate<Thing> shouldModify, final SerializableConsumer<Thing> modification, final SerializableConsumer<Thing> reverseModification) {
		this.lifeInMillis = lifeInMillis;
		this.shouldModify = shouldModify;
		this.modification = modification;
		this.reverseModification = reverseModification;
	}
	private Modifier(final Modifier m) {
		this(m.lifeInMillis, m.shouldModify, m.modification, m.reverseModification);
	}
	public void startCount(final long startTime) {
		timeStart = startTime;
	}
	public boolean shouldModify(final Thing t) {
		return shouldModify.test(t);
	}
	public boolean performModificationIfShould(final Thing t) {
		if (shouldModify.test(t)) {
			modification.accept(t);
			return true;
		}
		return false;
	}
	
	public boolean isDone(final long currentTime) {
		return lifeInMillis > 0  && (currentTime - timeStart) >= lifeInMillis;
	}
	public void performReverseModification(final Thing actOn) {
		reverseModification.accept(actOn);
	}
	public String timeLeft(final long currentTime)  {
		return GameUtils.millisecondsToTime(lifeInMillis - (currentTime - timeStart));
	}
	public boolean hasTimeLimit() {
		return lifeInMillis > 0;
	}
	public Modifier makeCopy() {
		return new Modifier(this);
	}
	public long getLifetimeInMillis() {
		return lifeInMillis;
	}

	
}
