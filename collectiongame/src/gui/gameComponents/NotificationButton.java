package gui.gameComponents;

import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.util.function.Consumer;

import gui.mvpFramework.view.ViewInterface;

/**
 * A button that keeps track of number of notifications, displays number of notifications, and can disappear when the number of notifications is <=0
 * @author David O'Sullivan
 */
public class NotificationButton extends PictureButton<ViewInterface> {

	private static final long serialVersionUID = 1L;
	private int numNotifications;
	private final boolean hideOnEmpty;
	private static final Font DEFAULT_FONT = new Font("TimesRoman", Font.BOLD, 30);
	
	/**
	 * Creates a new Notification Button with the given image at the specified location. 
	 * It will have the effect onClick on the presenter of the passed in ViewInterface, and will only be visible when 
	 * (numNotifications >0 || !hideOnEmpty)
	 * @param img the image to set the notification button as
	 * @param location the location of the button
	 * @param onClick the effect on the presenter of gv 
	 * @param vi the ViewInterface that houses this button
	 * @param hideOnEmpty true if button should be invisible and deactivated when numNotifications is <= 0
	 */
	public NotificationButton(final Image img, final Point location, final Consumer<ViewInterface> onClick, final ViewInterface vi, final boolean hideOnEmpty) {
		super(img, location, onClick, vi);
		setFont(DEFAULT_FONT);
		this.hideOnEmpty = hideOnEmpty;
		setNumNotifications(0);
	
	}
	/**
	 * Change the number of Notifications associated with this button. if (hideOnEmpty && num <= 0) then the button will become invisble and disabled
	 * @param num the number of notifications
	 */
	public void setNumNotifications(final int num) {
		this.numNotifications = num;
		if (hideOnEmpty && numNotifications <= 0) {
			setEnabled(false);
			setVisible(false);
		}
			
		else if (!isVisible() && numNotifications > 0) {
			setEnabled(true);
			setVisible(true);
		}
		
		if (!hideOnEmpty || numNotifications > 0)
			setText(Integer.toString(numNotifications));
		else
			setText("");
			
	}
	/**
	 * Disables the border and returns this instance
	 * @return this
	 */
	@Override
	public NotificationButton disableBorder() {
		super.disableBorder();
		return this;
	}
}
