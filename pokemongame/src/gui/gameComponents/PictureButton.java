package gui.gameComponents;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.BorderFactory;

import gui.guiutils.GUIConstants;
import gui.mouseAdapters.MouseClickWithThreshold;
import gui.mvpFramework.GameView;
import gui.mvpFramework.Presenter;

/**
 * Essentially A gamespace with a mouselistener
 * @author David O'Sullivan
 *
 */
public class PictureButton<T> extends GameSpace {

	private static final long serialVersionUID = 1L;
	private static final int CLICK_DIST_THRESH = GUIConstants.CLICK_DIST_THRESH;
	private boolean hasBorder = true;
	private Image clickImg;
	private Image hoverImg;
	private Image lockedImg;
	private String text;
	/**
	 * Creates a new Picture Button with the given image. Places the button at the location (0,0)
	 * @param img the image to set the notification button as
	 */
	public PictureButton(Image img) {
		this(img, new Point(0,0));
	}
	/**
	 *Creates a new Picture Button with the given image. Places the button at the specified location
	 * @param img the image to set the notification button as
	 * @param location the location of the button
	 */
	public PictureButton(Image img, Point location) {
		super(img, location);
	}
	
	/**
	 * Creates a new Picture Button with the given image. Places the button at the specified location. 
	 * It will have the effect onClick on the presenter of the passed in GameView
	 * @param img the image to set the notification button as
	 * @param location the location of the button
	 * @param onClick the effect on the presenter of gv 
	 * @param gv the GameView that houses this button
	 */
	public PictureButton(Image img, Point location, Consumer<T> onClick, T actOn) {
		this(img, location);
		BiConsumer<Consumer<T>, MouseEvent> input = (con, e) -> {
				con.accept(actOn);
		};
		this.addMouseListener(new MouseClickWithThreshold<Consumer<T>>(CLICK_DIST_THRESH, input, onClick));
	
	}
	/**
	 * Creates a new Picture Button with the given images. Places the button at the specified location. 
	 * It will have the effect onClick on the presenter of the passed in GameView
	 * @param img the image to set the notification button as
	 * @param location the location of the button
	 * @param onClick the effect on the presenter of gv 
	 * @param gv the GameView that houses this button
	 */
	public PictureButton(Image normalImg, Image clickImg, Image hoverImg, Image lockedImg, Point location, Consumer<T> onClick, T actOn) {
		this(normalImg, location);
		this.clickImg = clickImg;
		this.hoverImg = hoverImg;
		this.lockedImg = lockedImg;
		BiConsumer<Consumer<T>, MouseEvent> input = (con, e) -> {
				con.accept(actOn);
				if (isEnabled() && clickImg != null)
					setImage(this.clickImg);
		};
		this.addMouseListener(new MouseClickWithThreshold<Consumer<T>>(CLICK_DIST_THRESH, input, onClick));
		this.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseEntered(MouseEvent e) {
				if (isEnabled() && PictureButton.this.hoverImg != null)
					setImage(PictureButton.this.hoverImg);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				if (isEnabled())
					setImage(normalImg);
			}
		});
	
	}
	/**
	 * Creates a new Picture Button with the given image. Places the button at (0,0) 
	 * It will have the effect onClick on the presenter of the passed in GameView
	 * @param img the image to set the notification button as
	 * @param onClick the effect on the presenter of gv 
	 * @param gv the GameView that houses this button
	 */
	public PictureButton(Image img, Consumer<T> onClick, T actOn) {
		this(img, new Point(0,0), onClick, actOn);
	
	}
	public PictureButton(Image normalImg, Image clickImg, Image hoverImg, Image lockedImg, Consumer<T> onClick, T actOn) {
		this(normalImg, clickImg, hoverImg, lockedImg, new Point(0, 0), onClick, actOn);
	}
	public PictureButton(Image img, Point location, MouseListener ml) {
		this(img, location);
		this.addMouseListener(ml);
	}
	/**
	 * Disables the border and returns this instance
	 * @return this
	 */
	public PictureButton<T> disableBorder() {
		hasBorder = false;
		return this;
	}
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (lockedImg != null)
			setImage(lockedImg);
	}
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * Calls GameSpaces paint method and also creates a border
	 * @see gui.gameComponents.GameSpace#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (hasBorder)
			this.setBorder(BorderFactory.createLineBorder(Color.black));
		if (text != null)
			
			
	}


}
