package gui.mouseAdapters;

import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;

/**
 * Triggers on a double click within an allowable distance
 * @author DOSullivan
 *
 * @param <T>
 */
public class DoubleClickWithThreshold<T> extends MouseClickWithThreshold<T> {

	/**Creates a new DoubleClickWithThreshold with the specified allowableDistance, and what should happen when the mouse is clicked. Only allows left clicks.
	 * @param allowableDistance the maximum allowableDistance (pixels) from the location of mouse being pressed to location of mouse release to count as a press
	 * @param function the effect that the mouse should have when double clicked. 
	 * @param actOn what will be passed into function when it is called
	 */
	public DoubleClickWithThreshold(int allowableDistance, BiConsumer<T, MouseEvent> function, T actOn) {
		super(allowableDistance, function, actOn);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getClickCount() == 2)
			super.mouseReleased(e);
	}

}
