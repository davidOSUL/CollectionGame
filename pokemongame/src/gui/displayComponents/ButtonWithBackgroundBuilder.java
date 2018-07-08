package gui.displayComponents;

import java.awt.Image;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JButton;

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
	public static JButton generateButton(final Image img, final String text) {
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
	 * Creates new JButton using the default path. Assumes there exists "normal, click, hover, and lock" versions
	 * @param buttonname the name of the button
	 * @param text the text to go on top of the button
	 * @return the button
	 */
	public static JButton generateJButton(final String buttonname, final String text) {
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
}
