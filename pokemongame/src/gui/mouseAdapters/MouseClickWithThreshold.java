package gui.mouseAdapters;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;

import javax.swing.SwingUtilities;

import interfaces.Procedure;

/**
 * Will register on a mouse click when the mouse release is within an allowable distance of the mouse press
 * @author David O'Sullivan
 * 
 * @param <T> what type of object will be affected when the mouse is clicked
 */
public class MouseClickWithThreshold<T> extends MouseAdapter {
	private int allowableDistance;
	private Point currentPoint;
	private BiConsumer<T, MouseEvent> function;
	private T actOn;
	private boolean allowNonPrimaryClick = false;
	private ClickType type;
	private Procedure onMouseRelease = () -> {};
	private Procedure onMousePressed = () -> {};
	/**
	 * Creates a new MouseClickWithThreshold with the specified allowableDistance, and what should happen when the mouse is clicked. Doesn't allow non primary clicks. By default, the primary click is Left click
	 * @param allowableDistance the maximum allowableDistance (pixels) from the location of mouse being pressed to location of mouse release to count as a press
	 * @param function the effect that the mouse should have when clicked. 
	 * @param actOn what will be passed into function when it is called
	 */
	public MouseClickWithThreshold(final int allowableDistance, final BiConsumer<T, MouseEvent> function, final T actOn) {
		this.allowableDistance = allowableDistance;
		this.function = function;
		this.actOn = actOn;
		this.type = ClickType.LEFT;
	}
	/**
	 * Creates a new MouseClickWithThreshold with the specified allowableDistance, and what should happen when the mouse is clicked.
	 * @param allowableDistance the maximum allowableDistance (pixels) from the location of mouse being pressed to location of mouse release to count as a press
	 * @param function the effect that the mouse should have when clicked. 
	 * @param actOn what will be passed into function when it is called
	 * @param allowNonPrimaryClick if set to true will allow all clicks to trigger event, not just the primary click
	 */
	public MouseClickWithThreshold(final int allowableDistance, final BiConsumer<T, MouseEvent> function, final T actOn, final boolean allowNonPrimaryClick) {
		this(allowableDistance, function, actOn);
		this.allowNonPrimaryClick = allowNonPrimaryClick;
	}
	/**
	 * Creates a new MouseClickWithThreshold with the specified allowableDistance, and what should happen when the mouse is clicked. 
	 * @param allowableDistance the maximum allowableDistance (pixels) from the location of mouse being pressed to location of mouse release to count as a press
	 * @param function the effect that the mouse should have when clicked. 
	 * @param actOn what will be passed into function when it is called
	 * @param allowNonPrimaryClick if set to true will allow all clicks to trigger event, not just the primary click
	 * @param type Which mouse button to consider the primary click
	 */
	public MouseClickWithThreshold(final int allowableDistance, final BiConsumer<T, MouseEvent> function, final T actOn, final boolean allowNonPrimaryClick, final ClickType type) {
		this(allowableDistance, function, actOn, allowNonPrimaryClick);
		this.type = type;
	}
	/**
	 * Add an additional action to occur when the mouse is released
	 * @param onMouseRelease what should happen when the mouse is released in addition to the click action if applicable
	 * @return this
	 */
	public MouseClickWithThreshold<T> doOnRelease(final Procedure onMouseRelease) {
		this.onMouseRelease = onMouseRelease;
		return this;
	}
	/**
	 * Add an additional action to occur when the mouse is pressed
	 * @param onMouseRelease what should happen when the mouse is pressed in addition to the click action if applicable
	 * @return this
	 */
	public MouseClickWithThreshold<T> doOnPress(final Procedure onMousePressed) {
		this.onMousePressed = onMousePressed;
		return this;
	}
	@Override
	public void mousePressed(final MouseEvent e) {
		currentPoint = e.getPoint();
		onMousePressed.invoke();
	}
	@Override
	public void mouseReleased(final MouseEvent e) {
		if (currentPoint.distance(e.getPoint()) < allowableDistance && (allowNonPrimaryClick || isPrimaryClick(e))) {
			function.accept(actOn, e);
		}
		onMouseRelease.invoke();
	}
	public enum ClickType{
		LEFT, RIGHT
	}
	public boolean isPrimaryClick(final MouseEvent e) {
		switch(type) {
		case LEFT:
			return SwingUtilities.isLeftMouseButton(e);
		case RIGHT:
			return SwingUtilities.isRightMouseButton(e);
		default:
			return SwingUtilities.isLeftMouseButton(e);
		}
	}
	
}
