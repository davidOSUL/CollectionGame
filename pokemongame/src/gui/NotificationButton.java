package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NotificationButton extends GameSpace {
	private int numNotifications;
	private boolean hideOnEmpty;
	private Consumer<Presenter> onClick = x -> {};
	GameView gv;
	private static final int CLICK_DIST_THRESH = 20;
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
		g.setFont(new Font("TimesRoman", Font.PLAIN, 16));
		g.setColor(Color.black);
		if (!hideOnEmpty || numNotifications > 0) 
			g.drawString("" + numNotifications, 0, 0);
	}
}
