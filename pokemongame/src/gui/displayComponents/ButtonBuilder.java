package gui.displayComponents;

import java.awt.Image;
import java.awt.Insets;
import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.JButton;

import gui.gameComponents.PictureButton;
import gui.guiutils.GuiUtils;

/**
 * Used to create a Button with a the specified image as background and with text over top
 * @author David O'Sullivan
 *
 */
public class ButtonBuilder {
	private static final String BUTTON_PATH = "/sprites/ui/buttons/";
	private ButtonBuilder() {}
	/**
	 * Creates a new button, with a strech icon using the given image, and with all icons (locked, hover, etc. ) set to that icon
	 * @param img the background image
	 * @param text the text on the button
	 * @return the button
	 */
	public static JButton generateStretchIconJButton(final Image img, final String text) {
		final StretchIcon si = new StretchIcon(img);
		final JButton button = new JButton(text, si);
		button.setBorder(null);
		button.setPressedIcon(si);
		button.setContentAreaFilled(false);
		button.setRolloverIcon(si);
		button.setDisabledIcon(si);
		button.setVerticalTextPosition(JButton.CENTER);
		button.setHorizontalTextPosition(JButton.CENTER);
		button.setBorderPainted(false);
		button.setMargin(new Insets(0,0,0,0));
		button.setOpaque(false);
		return button;
	}
	/**
	 * Creates new JButton using the default path. Assumes there exists "normal, click, hover, and lock" versions, and creates a strech icon out of those images
	 * @param buttonname the name of the button
	 * @param text the text to go on top of the button
	 * @return the button
	 */
	public static JButton generateStretchIconJButton(final String buttonname, final String text) {
		final Icon i_normal = new StretchIcon(GuiUtils.readImage(BUTTON_PATH + buttonname + "_normal.png"));
		final Icon i_hover = new StretchIcon(GuiUtils.readImage(BUTTON_PATH + buttonname + "_hover.png"));
		final Icon i_lock = new StretchIcon(GuiUtils.readImage(BUTTON_PATH + buttonname + "_lock.png"));
		final Icon i_click = new StretchIcon(GuiUtils.readImage(BUTTON_PATH + buttonname + "_click.png"));
		/*final PictureButton pb = new PictureButton<T>();
		pb.switchToIcons(i_normal, i_click, i_hover, i_lock);
		pb.setText(text);
		return pb;*/
		final JButton button = new JButton(text, i_normal);
		button.setBorder(null);
		button.setPressedIcon(i_click);
		button.setContentAreaFilled(false);
		button.setRolloverIcon(i_hover);
		button.setDisabledIcon(i_lock);
		button.setVerticalTextPosition(JButton.CENTER);
		button.setHorizontalTextPosition(JButton.CENTER);
		button.setBorderPainted(false);
		button.setMargin(new Insets(0,0,0,0));
		button.setOpaque(false);
		return button;
	}
	/**
	 * Create a new PictureButton with no border using the default path. Assumes there exists "normal, click, hover, and lock" versions
	 * @param <T> the type the buttons consumer (onClick) acts on
	 * @param buttonname the name of the button
	 * @param text the text to go on the button
	 * @param onClick what happens when the button is pressed
	 * @param actOn what object the consumer acts on
	 * @return the created button
	 */
	public static <T> PictureButton<T> generatePictureButton(final String buttonname, final String text, final Consumer<T> onClick, final T actOn) {
		final Image i_normal = GuiUtils.readImage(BUTTON_PATH + buttonname + "_normal.png");
		final Image i_hover = GuiUtils.readImage(BUTTON_PATH + buttonname + "_hover.png");
		final Image i_lock = GuiUtils.readImage(BUTTON_PATH + buttonname + "_lock.png");
		final Image i_click = GuiUtils.readImage(BUTTON_PATH + buttonname + "_click.png");
		final PictureButton<T> pb = new PictureButton<T>(i_normal, i_click, i_hover, i_lock, onClick, actOn).disableBorder();
		return pb;
	}
	/**
	 * Create a new PictureButton with no border using the default path. Assumes there exists "normal, click, hover, and lock" versions
	 * @param <T> the type the buttons consumer (onClick) acts on
	 * @param buttonname the name of the button
	 * @param text the text to go on the button
	 * @param onClick what happens when the button is pressed
	 * @param actOn what object the consumer acts on
	 * @param newWidth resize to this width
	 * @param newHeight resize to this height
	 * @return the created button
	 */
	public static <T> PictureButton<T> generatePictureButton(final String buttonname, final String text, final Consumer<T> onClick, final T actOn, final int newWidth, final int newHeight) {
		final Image i_normal = GuiUtils.readAndScaleImage(BUTTON_PATH + buttonname + "_normal.png", newWidth, newHeight);
		final Image i_hover = GuiUtils.readAndScaleImage(BUTTON_PATH + buttonname + "_hover.png", newWidth, newHeight);
		final Image i_lock = GuiUtils.readAndScaleImage(BUTTON_PATH + buttonname + "_lock.png", newWidth, newHeight);
		final Image i_click = GuiUtils.readAndScaleImage(BUTTON_PATH + buttonname + "_click.png", newWidth, newHeight);
		final PictureButton<T> pb = new PictureButton<T>(i_normal, i_click, i_hover, i_lock, onClick, actOn).disableBorder();
		return pb;
	}
	/**
	 * Create a new PictureButton with no border using the default path. Assumes there exists "normal, click, hover, and lock" versions. This button will have no text. 
	 * @param <T> the type the buttons consumer (onClick) acts on
	 * @param buttonname the name of the button
	 * @param onClick what happens when the button is pressed
	 * @param actOn what object the consumer acts on
	 * @return the created button
	 */
	public static <T> PictureButton<T> generatePictureButton(final String buttonname, final Consumer<T> onClick, final T actOn) {
		return generatePictureButton(buttonname, "", onClick, actOn);
	}
	/**
	 * Create a new PictureButton with no border using the default path. Assumes there exists "normal, click, hover, and lock" versions. This button will have no text
	 * @param <T> the type the buttons consumer (onClick) acts on
	 * @param buttonname the name of the button
	 * @param text the text to go on the button
	 * @param onClick what happens when the button is pressed
	 * @param actOn what object the consumer acts on
	 * @param newWidth resize to this width
	 * @param newHeight resize to this height
	 * @return the created button
	 */
	public static <T> PictureButton<T> generatePictureButton(final String buttonname, final Consumer<T> onClick, final T actOn, final int newWidth, final int newHeight) {
		final Image i_normal = GuiUtils.readAndScaleImage(BUTTON_PATH + buttonname + "_normal.png", newWidth, newHeight);
		final Image i_hover = GuiUtils.readAndScaleImage(BUTTON_PATH + buttonname + "_hover.png", newWidth, newHeight);
		final Image i_lock = GuiUtils.readAndScaleImage(BUTTON_PATH + buttonname + "_lock.png", newWidth, newHeight);
		final Image i_click = GuiUtils.readAndScaleImage(BUTTON_PATH + buttonname + "_click.png", newWidth, newHeight);
		final PictureButton<T> pb = new PictureButton<T>(i_normal, i_click, i_hover, i_lock, onClick, actOn).disableBorder();
		return pb;
	}
}
