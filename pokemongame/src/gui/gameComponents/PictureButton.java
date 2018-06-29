package gui.gameComponents;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.BorderFactory;

import gui.guiutils.GUIConstants;
import gui.mouseAdapters.MouseClickWithThreshold;
import gui.mvpFramework.GameView;
import gui.mvpFramework.Presenter;

public class PictureButton extends GameSpace {

	private static final long serialVersionUID = 1L;
	private GameView gv;
	private static final int CLICK_DIST_THRESH = GUIConstants.CLICK_DIST_THRESH;
	/**
	 * Creates a new Picture Button with the given image at location (0,0)
	 * @param img the image to set the notification button as
	 */
	public PictureButton(Image img) {
		this(img, new Point(0,0));
	}
	/**
	 *Creates a new Picture Button with the given image at the specified location
	 * @param img the image to set the notification button as
	 * @param location the location of the button
	 */
	public PictureButton(Image img, Point location) {
		super(img, location);
	}
	
	/**
	 * Creates a new Picture Button with the given image at the specified location. 
	 * It will have the effect onClick on the presenter of the passed in GameView
	 * @param img the image to set the notification button as
	 * @param location the location of the button
	 * @param onClick the effect on the presenter of gv 
	 * @param gv the GameView that houses this button
	 */
	public PictureButton(Image img, Point location, Consumer<Presenter> onClick, GameView gv) {
		this(img, location);
		BiConsumer<Consumer<Presenter>, MouseEvent> input = (con, e) -> {
				con.accept(gv.getPresenter());
		};
		this.addMouseListener(new MouseClickWithThreshold<Consumer<Presenter>>(CLICK_DIST_THRESH, input, onClick));
	
	}
	public PictureButton(Image img, Point location, MouseListener ml) {
		this(img, location);
		this.addMouseListener(ml);
	}
	/**
	 * Calls GameSpaces paint method and also creates a border
	 * @see gui.gameComponents.GameSpace#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setBorder(BorderFactory.createLineBorder(Color.black));
	}


}
