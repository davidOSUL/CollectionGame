package interfaces;

import java.io.Serializable;
import java.util.function.BiConsumer;

/**
 * @author David O'Sullivan
 * Serializable BiConsumer
 * @param <T> the first argument
 * @param <R> the second argument
 * 
 */
@FunctionalInterface
public interface SerializableBiConsumer<T, R> extends BiConsumer<T, R>, Serializable {}
