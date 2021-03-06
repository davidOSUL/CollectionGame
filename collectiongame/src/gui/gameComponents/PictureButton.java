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

/**
 * Essentially A gamespace with a mouselistener
 * @author David O'Sullivan
 * @param <T> the type of object that this PictureButton should act upon when pressed
 *
 */
public class PictureButton<T> extends BackgroundWithText {

	private static final long serialVersionUID = 1L;
	private static final int CLICK_DIST_THRESH = GUIConstants.CLICK_DIST_THRESH;
	private boolean hasBorder = true;
	private Image clickImg;
	private Image hoverImg;
	private Image lockedImg;
	private Image normalImg;

	/**
	 * Creates a new picture button a location 0,0 with no image
	 */
	public PictureButton() {
		this(null);
	}
	/**
	 * Creates a new Picture Button with the given image. Places the button at the location (0,0)
	 * @param img the image to set the notification button as
	 */
	public PictureButton(final Image img) {
		this(img, new Point(0,0));
	}
	/**
	 *Creates a new Picture Button with the given image. Places the button at the specified location
	 * @param img the image to set the notification button as
	 * @param location the location of the button
	 */
	public PictureButton(final Image img, final Point location) {
		super(img, location);
		setPreset(TextPreset.CENTER_ALL_TEXT);
		this.normalImg = img;
	}

	/**
	 * Creates a new Picture Button with the given image. Places the button at the specified location. 
	 * It will have the effect onClick on the presenter of the passed in ViewInterface
	 * @param img the image to set the notification button as
	 * @param location the location of the button
	 * @param onClick the effect on the T to act on
	 * @param actOn the object to act on
	 */
	public PictureButton(final Image img, final Point location, final Consumer<T> onClick, final T actOn) {
		this(img, location);
		final BiConsumer<Consumer<T>, MouseEvent> input = (con, e) -> {
			con.accept(actOn);
		};
		this.addMouseListener(new MouseClickWithThreshold<Consumer<T>>(CLICK_DIST_THRESH, input, onClick));

	}
	/**
	 * Creates a new Picture Button with the given images. Places the button at the specified location. 
	 * It will have the effect onClick using the actOn as the input to the consumer
	 * @param normalImg the normal button
	 * @param clickImg the button when clicked
	 * @param hoverImg the button when moused over
	 * @param lockedImg the button when disabled
	 * @param location the location of the button
	 * @param onClick the effect of clicking this button
	 * @param actOn what the consumer acts on
	 */
	public PictureButton(final Image normalImg, final Image clickImg, final Image hoverImg, final Image lockedImg, final Point location, final Consumer<T> onClick, final T actOn) {
		this(normalImg, location);
		this.clickImg = clickImg;
		this.hoverImg = hoverImg;
		this.lockedImg = lockedImg;
		final BiConsumer<Consumer<T>, MouseEvent> input = (con, e) -> {
			if (isEnabled())
				con.accept(actOn);
		};
		final MouseClickWithThreshold<Consumer<T>> clickListener = new MouseClickWithThreshold<Consumer<T>>(CLICK_DIST_THRESH, input, onClick);
		clickListener.doOnPress(() -> {
			if (isEnabled() && clickImg != null)
				setImage(this.clickImg);
		});
		clickListener.doOnRelease(() -> 
		{
			if (isEnabled())
				setImage(this.normalImg);
		});
		this.addMouseListener(clickListener);
		this.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseEntered(final MouseEvent e) {
				if (isEnabled() && PictureButton.this.hoverImg != null)
					setImage(PictureButton.this.hoverImg);
			}
			@Override
			public void mouseExited(final MouseEvent e) {
				if (isEnabled())
					setImage(normalImg);
			}
		});

	}
	/** 
	 * @see javax.swing.JComponent#addNotify()
	 */
	@Override 
	public void addNotify() {
		super.addNotify();
		setImage(normalImg);
	}
	/**
	 * Creates a new Picture Button with the given image. Places the button at (0,0) 
	 * It will have the effect onClick on the presenter of the passed in ViewInterface
	 * @param img the image to set the notification button as
	 * @param onClick the effect on the presenter of the T to act on
	 * @param actOn the object to act on
	 */
	public PictureButton(final Image img, final Consumer<T> onClick, final T actOn) {
		this(img, new Point(0,0), onClick, actOn);

	}
	/**
	 * Creates a new Picture Button with the given images. Places the button at the (0,0)
	 * It will have the effect onClick using the actOn as the input to the consumer
	 * @param normalImg the normal button
	 * @param clickImg the button when clicked
	 * @param hoverImg the button when moused over
	 * @param lockedImg the button when disabled
	 * @param location the location of the button
	 * @param onClick the effect of clicking this button
	 * @param actOn what the consumer acts on
	 */
	public PictureButton(final Image normalImg, final Image clickImg, final Image hoverImg, final Image lockedImg, final Consumer<T> onClick, final T actOn) {
		this(normalImg, clickImg, hoverImg, lockedImg, new Point(0, 0), onClick, actOn);
	}
	/**
	 * Creates a new picture button with the given image and with the given mouse listener
	 * @param img the image for the button
	 * @param location the location to place the button
	 * @param ml the mouse listener
	 */
	public PictureButton(final Image img, final Point location, final MouseListener ml) {
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

	/**
	 * Will enabled/disable this button from receiving input and also update the image to it's locked image 
	 * if it has one
	 * @param enabled enables this picture button
	 * @see javax.swing.JComponent#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		if (!enabled && lockedImg != null)
			setImage(lockedImg);
		if (enabled)
			setImage(normalImg);
	}
	/**
	 * Set the text of this picture button
	 * @param text the text to display atop this button
	 */
	public void setText(final String text) {
		setFirstText(text);
	}

	/**
	 * Calls GameSpaces paint method and also creates a border
	 * @see gui.gameComponents.GameSpace#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		if (hasBorder)
			this.setBorder(BorderFactory.createLineBorder(Color.black));

	}


}
