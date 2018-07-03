package gui.displayComponents;

import java.awt.Image;
import java.awt.Insets;

import javax.swing.JButton;

public class ButtonWithBackgroundBuilder {
	private ButtonWithBackgroundBuilder() {}
	public static JButton generateButton(Image img, String text) {
		JButton button = new JButton(text, new StretchIcon(img));
		button.setPressedIcon(new StretchIcon(img));
		button.setDisabledIcon(new StretchIcon(img));
		button.setVerticalTextPosition(JButton.CENTER);
		button.setHorizontalTextPosition(JButton.CENTER);
		button.setBorderPainted(false);
		button.setMargin(new Insets(0,0,0,0));
		return button;
	}
}
