package interfaces;

import java.io.Serializable;

@FunctionalInterface
public interface SerializableTriConsumer<A, B, C> extends Serializable{
	public void accept(A a, B b, C c);
}
