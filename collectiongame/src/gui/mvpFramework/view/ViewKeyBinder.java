package gui.mvpFramework.view;

/**
 * Interface used to allow ViewInterface to set up all the key bindings
 * @author David O'Sullivan
 *
 */
public interface ViewKeyBinder {
	/**
	 * Sets all required key bindings for this view
	 */
	void setKeyBindings(ViewInterface vi);
}
