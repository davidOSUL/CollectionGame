package interfaces;

import java.io.Serializable;
import java.util.function.Predicate;

/**
 * Serializable version of predicate
 * @author David O'Sullivan
 *
 * @param <T> the argument's type
 */
@FunctionalInterface
public interface  SerializablePredicate<T> extends Predicate<T>, Serializable {

}
