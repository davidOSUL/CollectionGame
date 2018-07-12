package interfaces;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * A serializable form of java.util.consumer
 * @author David O'Sullivan
 *
 * @param <T>
 */
@FunctionalInterface
public interface SerializableConsumer<T> extends Consumer<T>, Serializable {}