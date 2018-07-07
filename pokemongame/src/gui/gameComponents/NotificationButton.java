package gui.gameComponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import gui.guiutils.GUIConstants;
import gui.guiutils.GuiUtils;
import gui.mouseAdapters.MouseClickWithThreshold;
import gui.mvpFramework.GameView;
import gui.mvpFramework.Presenter;

/**
 * A button that keeps track of number of notifications, displays number of notifications, and can disappear when the number of notifications is <=0
 * @author David O'Sullivan
 */
public class NotificationButton extends PictureButton {

	private static final long serialVersionUID = 1L;
	private int numNotifications;
	private boolean hideOnEmpty;
	private Consumer<Presenter> onClick = x -> {};
	private GameView gv;
	private static final int CLICK_DIST_THRESH = GUIConstants.CLICK_DIST_THRESH;
	private static final Font DEFAULT_FONT = new Font("TimesRoman", Font.BOLD, 30);
	/**
	 * Creates a new Notification Button with the given image at location (0,0)
	 * @param img the image to set the notification button as
	 */
	public NotificationButton(Image img) {
		this(img, new Point(0,0));
	}
	/**
	 *Creates a new Notification Button with the given image at the specified location
	 * @param img the image to set the notification button as
	 * @param location the location of the button
	 */
	public NotificationButton(Image img, Point location) {
		super(img, location);
		hideOnEmpty = false;
		setNumNotifications(0);
	}
	
	/**
	 * Creates a new Notification Button with the given image at the specified location. 
	 * It will have the effect onClick on the presenter of the passed in GameView, and will only be visible when 
	 * (numNotifications >0 || !hideOnEmpty)
	 * @param img the image to set the notification button as
	 * @param location the location of the button
	 * @param onClick the effect on the presenter of gv 
	 * @param gv the GameView that houses this button
	 * @param hideOnEmpty true if button should be invisible and deactivated when numNotifications is <= 0
	 */
	public NotificationButton(Image img, Point location, Consumer<Presenter> onClick, GameView gv, boolean hideOnEmpty) {
		this(img, location);
		this.hideOnEmpty = hideOnEmpty;
		this.onClick = onClick;
		BiConsumer<Consumer<Presenter>, MouseEvent> input = (con, e) -> {
			if (!hideOnEmpty || numNotifications > 0)
				con.accept(gv.getPresenter());
		};
		this.addMouseListener(new MouseClickWithThreshold<Consumer<Presenter>>(CLICK_DIST_THRESH, input, onClick));
	
	}
	/**
	 * Change the number of Notifications associated with this button. if (hideOnEmpty && num <= 0) then the button will become invisble and disabled
	 * @param num the number of notifications
	 */
	public void setNumNotifications(int num) {
		this.numNotifications = num;
		if (hideOnEmpty && numNotifications <= 0) {
			setEnabled(false);
			setVisible(false);
		}
			
		else if (!isEnabled() && numNotifications > 0) {
			setEnabled(true);
			setVisible(true);
		}
			
	}
	/**
	 * if (!hideOnEmpty || numNotifications > 0) Adds on the number of notifications in NotificationButton.DEFAULT_FONT, centered on the button
	 * @see gui.gameComponents.GameSpace#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!hideOnEmpty || numNotifications > 0) {
			GuiUtils.drawCenteredString(g, Integer.toString(numNotifications), new Rectangle(0,0,getWidth(),getHeight()), DEFAULT_FONT, Color.black);
		}
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
