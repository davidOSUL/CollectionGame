package interfaces;

import java.io.Serializable;
import java.util.function.Predicate;

/**
 * @author David O'Sullivan
 *
 * @param <T>
 */
public interface  SerializablePredicate<T> extends Predicate<T>, Serializable {

}
