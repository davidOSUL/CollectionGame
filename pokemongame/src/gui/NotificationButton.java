package gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class NotificationButton extends GameSpace {
	private int numNotifications;
	private boolean hideOnEmpty;
	private Consumer<Presenter> onClick = x -> {};
	Presenter p;
	public NotificationButton(Image img) {
		this(img, new Point(0,0));
	}
	public NotificationButton(Image img, Point location) {
		super(img, location);
		hideOnEmpty = false;
		setNumNotifications(0);
	}
	public NotificationButton(Image img, Point location, Consumer<Presenter> onClick, Presenter p, boolean hideOnEmpty) {
		this(img, location);
		this.hideOnEmpty = hideOnEmpty;
		this.onClick = onClick;
		this.addMouseListener(new MouseAdapter() {
			 @Override
			 public void mouseClicked(MouseEvent e) {
			if (!hideOnEmpty || numNotifications > 0)
			   onClick.accept(p);
			 }

		});
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
		if (!hideOnEmpty || numNotifications > 0) 
			g.drawString("" + numNotifications, 0, 0);
	}
}
