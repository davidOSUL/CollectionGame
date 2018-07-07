package gui.displayComponents;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import gui.gameComponents.PictureButton;
import gui.guiutils.GuiUtils;

/**
 * Used to create a Button with a the specified image as background and with text over top
 * @author David O'Sullivan
 *
 */
public class ButtonWithBackgroundBuilder {
	private static final String BUTTON_PATH = "/sprites/ui/buttons/";
	private ButtonWithBackgroundBuilder() {}
	/**
	 * Creates a new button
	 * @param img the background image
	 * @param text the text on the button
	 * @return the button
	 */
	public static JButton generateButton(Image img, String text) {
		StretchIcon si = new StretchIcon(img);
		JButton button = new JButton(text, si);
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
	 * Creates new button using the default path. Assumes there exists "normal, click, hover, and lock" versions
	 * @param buttonname the name of the button
	 * @param text the text to go on top of the button
	 * @return the button
	 */
	public static <T> PictureButton<T> generateButton(String buttonname, String text, Consumer<T> onClick, T actOn) {
		Image i_normal = GuiUtils.readImage(BUTTON_PATH + buttonname + "_normal.png");
		Image i_hover = GuiUtils.readImage(BUTTON_PATH + buttonname + "_hover.png");
		Image i_lock = GuiUtils.readImage(BUTTON_PATH + buttonname + "_lock.png");
		Image i_click = GuiUtils.readImage(BUTTON_PATH + buttonname + "_click.png");
		return new PictureButton<T>(i_normal, i_click, i_hover, i_lock, onClick, actOn);
		/*JButton button = new JButton(text, i_normal);
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
		return button;*/
	}
}
