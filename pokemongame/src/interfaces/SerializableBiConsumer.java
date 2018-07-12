package interfaces;

import java.io.Serializable;
import java.util.function.BiConsumer;
@FunctionalInterface
public interface SerializableBiConsumer<T, R> extends BiConsumer<T, R>, Serializable {}
