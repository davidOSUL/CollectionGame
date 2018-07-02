package gui.mouseAdapters;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.BiConsumer;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Constructs a MouseListener to trigger a JPopupMenu
 * @author David O'Sullivan
 * @param <T> The Item that the elements in the window will be acting on
 */
public class SelectionWindowBuilder<T> {
	
	JPopupMenu menu = new JPopupMenu();
	private int allowableDistance;
	/**
	 * Create a new SelectionWindow with no title for the JPopupMenu
	 * @param allowableDistance the maximum distance to allow between pressing and releasing on the object
	 */
	public SelectionWindowBuilder(int allowableDistance) {
		this.allowableDistance = allowableDistance;
	}
	/**
	 * Create a new SelectionWindow
	 * @param allowableDistance the maximum distance to allow between pressing and releasing on the object
	 * @param title The title of the JPopupMenu that will be created
	 */
	public SelectionWindowBuilder(int allowableDistance, String title) {
		this(allowableDistance);
		menu.setLabel(title);
	}
	/**
	 * Adds a new Element to the Popupmenu, uses the passed in allowableDistance as the value for the maximum distance
	 * between pressing and releasing an object
	 * @param name the name of the element
	 * @param onClick what should happen when it is clicked
	 * @param actOn what it is acted on
	 * @return this
	 */
	public SelectionWindowBuilder<T> addOption(String name, BiConsumer<T, MouseEvent> onClick, T actOn) {
		MouseListener listener = new MouseClickWithThreshold<T>(allowableDistance, onClick, actOn);
		return addOption(name, listener);
	}
	/**
	 * Adds a new Element to the Popupmenu, uses the passed in allowableDistance as the value for the maximum distance
	 * between pressing and releasing an object
	 * @param name the name of the element
	 * @param listener what should happen when it is clicked
	 * @return this
	 */
	public SelectionWindowBuilder<T> addOption(String name, MouseListener listener) {
		JMenuItem item = new JMenuItem(name);
		item.addMouseListener(listener);
		menu.add(item);
		return this;
	}
	/**
	 * Adds a new disabled Element to the PopupMenu with no effect upon pressing
	 * @param name the name of the element
	 * @return this
	 */
	public SelectionWindowBuilder<T> addOption(String name) {
		JMenuItem item = new JMenuItem(name);
		item.setEnabled(false);
		menu.add(item);
		return this;
	}
	/**
	 * @return the listener constructed by this instance
	 */
	public MouseListener getListener() {
		return new PopupClickListener(allowableDistance, menu);
	}
	/**
	 * Simple extension of MouseClickWithThreshold. Currently Just auto sets CLickType to RIGHT and allowNonPrimary to false
	 * @author David O'Sullivan
	 */
	private class PopupClickListener extends MouseClickWithThreshold<JPopupMenu> {
		
		/**
		 * Creates a new PopupClickListener with the specified allowableDistance, and what should happen when the popup is triggered
		 * @param allowableDistance the maximum allowableDistance (pixels) from the location of mouse being pressed to location of mouse release to count as a press
		 * @param function the effect that the mouse should have when clicked. 
		 * @param actOn what will be passed into function when it is called
		 */
		private JPopupMenu popupMenu;
		public PopupClickListener(int allowableDistance, JPopupMenu popupMenu) {
			super(allowableDistance, (pop, e) -> {
				pop.show(e.getComponent(), e.getX(), e.getY());
			}, popupMenu, false, ClickType.RIGHT);
			this.popupMenu = popupMenu;
		}
	}
	
	
}
