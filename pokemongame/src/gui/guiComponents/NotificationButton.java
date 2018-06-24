package gui.guiComponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import gui.guiutils.GuiUtils;
import gui.mouseAdapters.MouseClickWithThreshold;
import gui.mvpFramework.GameView;
import gui.mvpFramework.Presenter;

public class NotificationButton extends GameSpace {

	private static final long serialVersionUID = 1L;
	private int numNotifications;
	private boolean hideOnEmpty;
	private Consumer<Presenter> onClick = x -> {};
	GameView gv;
	private static final int CLICK_DIST_THRESH = 20;
	private static final Font DEFAULT_FONT = new Font("TimesRoman", Font.BOLD, 30);
	public NotificationButton(Image img) {
		this(img, new Point(0,0));
	}
	public NotificationButton(Image img, Point location) {
		super(img, location);
		hideOnEmpty = false;
		setNumNotifications(0);
	}
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
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!hideOnEmpty || numNotifications > 0) {
			GuiUtils.drawCenteredString(g, Integer.toString(numNotifications), new Rectangle(0,0,getWidth(),getHeight()), DEFAULT_FONT, Color.black);
		}
	}
}
