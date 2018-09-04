package interfaces;

import java.io.Serializable;
import java.util.function.BiFunction;

/**
 * @author David O'Sullivan
 * Serializable BiFunction
 * @param <T> the first argument's type
 * @param <U> the second argument's type
 * @param <R> the return value's type
 */
public interface SerializableBiFunction<T, U, R> extends BiFunction<T, U, R>, Serializable {

}
