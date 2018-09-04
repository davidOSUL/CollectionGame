package interfaces;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * A Serializable form of java.util.consumer
 * @author David O'Sullivan
 *
 * @param <T> the first argument's type
 */
@FunctionalInterface
public interface SerializableConsumer<T> extends Consumer<T>, Serializable {}