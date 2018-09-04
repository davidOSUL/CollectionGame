package interfaces;

import java.io.Serializable;

/**
 * A SerializableTriConsumer. Takes in three elements and returns nothing.
 * @author David O'Sullivan
 *
 * @param <A> the type of the first argument
 * @param <B> the type of the second argument
 * @param <C> the type of the third argument
 */
@FunctionalInterface
public interface SerializableTriConsumer<A, B, C> extends Serializable{
	/**
	 * Performs operation on three arguments
	 * @param a the first argument
	 * @param b the second argument
	 * @param c the third argument
	 */
	public void accept(A a, B b, C c);
}
