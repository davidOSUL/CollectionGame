package gui.mouseAdapters;

import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;

public class DoubleClickWithThreshold<T> extends MouseClickWithThreshold<T> {

	public DoubleClickWithThreshold(int allowableDistance, BiConsumer<T, MouseEvent> function, T actOn) {
		super(allowableDistance, function, actOn);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getClickCount() == 2)
			super.mouseReleased(e);
	}

}
