package gui.mouseAdapters;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;

public class MouseClickWithThreshold<T> extends MouseAdapter {
	private int allowableDistance;
	private Point currentPoint;
	private BiConsumer<T, MouseEvent> function;
	private T actOn;
	public MouseClickWithThreshold(int allowableDistance, BiConsumer<T, MouseEvent> function, T actOn) {
		this.allowableDistance = allowableDistance;
		this.function = function;
		this.actOn = actOn;
	}
	@Override
	public void mousePressed(MouseEvent e) {
		currentPoint = e.getPoint();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if (currentPoint.distance(e.getPoint()) < allowableDistance) {
			function.accept(actOn, e);
		}
	}
	
}
