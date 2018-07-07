package gui.displayComponents;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.JButton;

/**
 * Used to create a Button with a the specified image as background and with text over top
 * @author David O'Sullivan
 *
 */
public class ButtonWithBackgroundBuilder {
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
}
