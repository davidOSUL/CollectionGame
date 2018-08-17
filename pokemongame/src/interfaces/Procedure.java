package interfaces;

import java.io.Serializable;

/**
 * A Procedure takes in no inputs, does something, and returns nothing
 * @author David O'Sullivan
 *
 */
@FunctionalInterface
public interface Procedure extends Serializable {
public void invoke();
}
