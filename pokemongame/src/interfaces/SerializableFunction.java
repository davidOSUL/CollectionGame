package interfaces;

import java.io.Serializable;
import java.util.function.Function;

/**
 * A serializable form of java.util.Function
 * @author David O'Sullivan
 *
 * @param <T>
 * @param <R>
 */
@FunctionalInterface
public interface SerializableFunction<T,R> extends Function<T,R>, Serializable {}
