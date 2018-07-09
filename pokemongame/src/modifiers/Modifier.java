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
	private long timeStart = System.currentTimeMillis();
	public Modifier(final long lifeInMillis, final SerializablePredicate<T> shouldModify, final SerializableConsumer<T> modification) {
		this.lifeInMillis = lifeInMillis;
		this.shouldModify = shouldModify;
		this.modification = modification;
	}
	public void performModification(final T actOn) {
		timeStart = System.currentTimeMillis();
		modification.accept(actOn);
	}
	
	public boolean isDone() {
		return (System.currentTimeMillis() - timeStart) >= lifeInMillis && lifeInMillis != -1;
	}
}
