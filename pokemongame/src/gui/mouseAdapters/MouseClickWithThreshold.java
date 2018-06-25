package gui.mouseAdapters;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;

import javax.swing.SwingUtilities;

/**
 * Will register on a mouse click when the mouse release is within an allowable distance of the mouse press
 * @author DOSullivan
 * 
 * @param <T> what type of object will be affected when the mouse is clicked
 */
public class MouseClickWithThreshold<T> extends MouseAdapter {
	private int allowableDistance;
	private Point currentPoint;
	private BiConsumer<T, MouseEvent> function;
	private T actOn;
	private boolean allowNonLeftClick = false;
	/**
	 * Creates a new MouseClickWithThreshold with the specified allowableDistance, and what should happen when the mouse is clicked. Only allows left clicks.
	 * @param allowableDistance the maximum allowableDistance (pixels) from the location of mouse being pressed to location of mouse release to count as a press
	 * @param function the effect that the mouse should have when clicked. 
	 * @param actOn what will be passed into function when it is called
	 */
	public MouseClickWithThreshold(int allowableDistance, BiConsumer<T, MouseEvent> function, T actOn) {
		this.allowableDistance = allowableDistance;
		this.function = function;
		this.actOn = actOn;
	}
	/**
	 * Creates a new MouseClickWithThreshold with the specified allowableDistance, and what should happen when the mouse is clicked. Can allows non-left clicks
	 * @param allowableDistance the maximum allowableDistance (pixels) from the location of mouse being pressed to location of mouse release to count as a press
	 * @param function the effect that the mouse should have when clicked. 
	 * @param actOn what will be passed into function when it is called
	 * @param allowNonLeftClick if set to true will allow all clicks to trigger event, not just left clicks
	 */
	public MouseClickWithThreshold(int allowableDistance, BiConsumer<T, MouseEvent> function, T actOn, boolean allowNonLeftClick) {
		this(allowableDistance, function, actOn);
		this.allowNonLeftClick = allowNonLeftClick;
	}
	@Override
	public void mousePressed(MouseEvent e) {
		currentPoint = e.getPoint();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if (currentPoint.distance(e.getPoint()) < allowableDistance && (allowNonLeftClick || SwingUtilities.isLeftMouseButton(e))) {
			function.accept(actOn, e);
		}
	}
	
}
