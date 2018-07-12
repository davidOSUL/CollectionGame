package gui.mouseAdapters;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.BiConsumer;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import interfaces.Procedure;

/**
 * Constructs a MouseListener to trigger a JPopupMenu
 * @author David O'Sullivan
 * @param <T> The Item that the elements in the window will be acting on
 */
public class SelectionWindowBuilder<T> {
	
	private final JPopupMenu menu = new JPopupMenu();
	private Procedure onMenuVisible = () -> {};
	private Procedure onMenuClose = () -> {};
	private int allowableDistance;
	/**
	 * Create a new SelectionWindow with no title for the JPopupMenu
	 * @param allowableDistance the maximum distance to allow between pressing and releasing on the object
	 */
	public SelectionWindowBuilder(final int allowableDistance) {
		this.allowableDistance = allowableDistance;
	}
	/**
	 * Create a new SelectionWindow
	 * @param allowableDistance the maximum distance to allow between pressing and releasing on the object
	 * @param title The title of the JPopupMenu that will be created
	 */
	public SelectionWindowBuilder(final int allowableDistance, final String title) {
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
	public SelectionWindowBuilder<T> addOption(final String name, final BiConsumer<T, MouseEvent> onClick, final T actOn) {
		final MouseListener listener = new MouseClickWithThreshold<T>(allowableDistance, onClick, actOn);
		return addOption(name, listener);
	}
	/**
	 * Adds a new Element to the Popupmenu, uses the passed in allowableDistance as the value for the maximum distance
	 * between pressing and releasing an object
	 * @param name the name of the element
	 * @param listener what should happen when it is clicked
	 * @return this
	 */
	public SelectionWindowBuilder<T> addOption(final String name, final MouseListener listener) {
		final JMenuItem item = new JMenuItem(name);
		item.addMouseListener(listener);
		menu.add(item);
		return this;
	}
	/**
	 * Adds a new disabled Element to the PopupMenu with no effect upon pressing
	 * @param name the name of the element
	 * @return this
	 */
	public SelectionWindowBuilder<T> addOption(final String name) {
		final JMenuItem item = new JMenuItem(name);
		item.setEnabled(false);
		menu.add(item);
		return this;
	}
	public SelectionWindowBuilder<T> addDoOnMenuVisible(final Procedure onMenuVisible) {
		this.onMenuVisible = onMenuVisible;
		return this;
	}
	public SelectionWindowBuilder<T> addDoOnMenuClose(final Procedure onMenuClose) {
		this.onMenuClose = onMenuClose;
		return this;
	}
	/**
	 * @return the listener constructed by this instance
	 */
	public MouseListener getListener() {
		return new PopupClickListener(allowableDistance, menu, onMenuVisible, onMenuClose);
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
		private final JPopupMenu popupMenu;
		public PopupClickListener(final int allowableDistance, final JPopupMenu popupMenu, final Procedure onMenuVisible, final Procedure onMenuClose) {
			super(allowableDistance, (pop, e) -> {
				pop.show(e.getComponent(), e.getX(), e.getY());
			}, popupMenu, false, ClickType.RIGHT);
			popupMenu.addPopupMenuListener(new PopupMenuListener() {

				@Override
				public void popupMenuCanceled(final PopupMenuEvent arg0) {
					onMenuClose.invoke();
				}

				@Override
				public void popupMenuWillBecomeInvisible(final PopupMenuEvent arg0) {
					onMenuClose.invoke();
				}

				@Override
				public void popupMenuWillBecomeVisible(final PopupMenuEvent arg0) {
					onMenuVisible.invoke();
				}
				
			});
			this.popupMenu = popupMenu;
		}
	}
	
	
}
