package gui.mvpFramework.view;

/**
 * Interface used to allow ViewInterface to set up all the key bindings
 * @author David O'Sullivan
 *
 */
public interface ViewKeyBinder {
	/**
	 * Sets all required key bindings for this ViewInterface
	 * @param vi the view interface that is watching for key bindings
	 */
	void setKeyBindings(ViewInterface vi);
}
