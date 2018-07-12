package interfaces;

/**
 * A Procedure takes in no inputs, does something, and returns nothing
 * @author David O'Sullivan
 *
 */
@FunctionalInterface
public interface Procedure {
public void invoke();
}
