package modifiers;

import java.io.Serializable;

import gameutils.GameUtils;
import interfaces.SerializableConsumer;
import interfaces.SerializablePredicate;
import thingFramework.Thing;

/**
 * @author David O'Sullivan
 *
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
	/**
	 * Creates a new Modifier
	 * @param modification what should happen to the thing when the modifier is applied
	 * @param reverseModification what should happen to the thing when the modifier is removed
	 */
	public Modifier (final SerializableConsumer<Thing> modification,  final SerializableConsumer<Thing> reverseModification) {
		this(-1, x -> true, modification, reverseModification);
	}
	/**
	 * Creates a new Modifier with a lifetime
	 * @param lifeInMillis how long the modifier should ask in milliseconds
	 * @param modification what should happen to the thing when the modifier is applied
	 * @param reverseModification what should happen to the thing when the modifier is removed
	 */
	public Modifier (final long lifeInMillis, final SerializableConsumer<Thing> modification,  final SerializableConsumer<Thing> reverseModification) {
		this(lifeInMillis, x -> true, modification, reverseModification);
	}
	/**
	 * Creates a new Modifier with a predicate that checks if it should modify the provided thing
	 * @param shouldModify checks if the provided thing should be modified
	 * @param modification what should happen to the thing when the modifier is applied
	 * @param reverseModification what should happen to the thing when the modifier is removed
	 */
	public Modifier (final SerializablePredicate<Thing> shouldModify, final SerializableConsumer<Thing> modification,  final SerializableConsumer<Thing> reverseModification) {
		this(-1, shouldModify, modification, reverseModification);
	}
	/**
	 * Creates a new Modifier with a lifetime and a predicate that checks if it should modify the provided thing
	 * @param lifeInMillis how long the modifier should ask in milliseconds
	 * @param shouldModify checks if the provided thing should be modified
	 * @param modification what should happen to the thing when the modifier is applied
	 * @param reverseModification what should happen to the thing when the modifier is removed
	 */
	public Modifier (final long lifeInMillis, final SerializablePredicate<Thing> shouldModify, final SerializableConsumer<Thing> modification, final SerializableConsumer<Thing> reverseModification) {
		this.lifeInMillis = lifeInMillis;
		this.shouldModify = shouldModify;
		this.modification = modification;
		this.reverseModification = reverseModification;
	}
	private Modifier(final Modifier m) {
		this(m.lifeInMillis, m.shouldModify, m.modification, m.reverseModification);
	}
	/**
	 * Sets the start time for this modifier, this is used to detemine when the modifier is done if it has a lifetime
	 * @param startTime the start time in milliseconds
	 */
	public void startCount(final long startTime) {
		timeStart = startTime;
	}
	/**
	 * Returns true if this modifier should modify the provided thing
	 * @param t the thing to check
	 * @return true if should modify, false otherwise
	 */
	public boolean shouldModify(final Thing t) {
		return shouldModify.test(t);
	}
	/**
	 * Performs the modification on the provided thing if shouldModify(thing)
	 * @param t the thing to modify
	 * @return true if the modification occured, false otherwise
	 */
	public boolean performModificationIfShould(final Thing t) {
		if (shouldModify.test(t)) {
			modification.accept(t);
			return true;
		}
		return false;
	}
	
	/**
	 * Returns true if the time since the start Time of the modifier has elapsed its lifetime
	 * @param currentTime the currenttime
	 * @return true if the modifier is completed (currentTime - startTime >= lifeTime), false if lifeTime is < 0 or 
	 * it is not yet completed
	 */
	public boolean isDone(final long currentTime) {
		return lifeInMillis > 0  && (currentTime - timeStart) >= lifeInMillis;
	}
	/**
	 * Performs the reverse modification on the provided thing. This should undo the affects the modification.
	 * @param actOn the thing to perform the reverse modification on
	 */
	public void performReverseModification(final Thing actOn) {
		reverseModification.accept(actOn);
	}
	/**
	 * Get the time Left on this modifier as a formatted String
	 * @param currentTime the current time
	 * @return {@link gameutils.GameUtils#millisecondsToTime(long)} with (lifeInMillis - (currentTime - timeStart)) as the input;
	 */
	public String timeLeft(final long currentTime)  {
		return GameUtils.millisecondsToTime(lifeInMillis - (currentTime - timeStart));
	}
	/**
	 * Return true if modifier has a time limit (lifeTime > 0), false otherwise
	 * @return true if modifier has a time limit, false otherwise
	 */
	public boolean hasTimeLimit() {
		return lifeInMillis > 0;
	}
	/**
	 * Make a copy of this Modifier
	 * @return the new modifier
	 */
	public Modifier makeCopy() {
		return new Modifier(this);
	}
	/**
	 * Get the lifetime of this modifier in milliseconds
	 * @return the lifetime of this modifier in milliseconds
	 */
	public long getLifetimeInMillis() {
		return lifeInMillis;
	}


	
}
