package gui.displayComponents;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import gui.gameComponents.GameSpace;
import gui.gameComponents.PictureButton;
import gui.guiutils.GuiUtils;

public class StartScreenBuilder {
	private static final Dimension WINDOW_SIZE = new Dimension(500, 500);
	private static final Border PANEL_BORDER = BorderFactory.createEmptyBorder(100, 50, 100, 50);
	private final static Image START_SCREEN_BACKGROUND = GuiUtils.readImage("/sprites/ui/pikabackground.jpg");
	private final static Image BUTTON_IMG = GuiUtils.readImage("/sprites/ui/blue_button03.png");
	public static <T> JFrame getFrame(String title, boolean enabledContinue, Consumer<T> onNewGame, Consumer<T> onContinue, T actOn)  {
		JFrame frame = new JFrame();
		frame.setSize(WINDOW_SIZE);
		frame.setLayout(new BorderLayout());
		frame.setTitle(title);
		frame.setLocationByPlatform(true);
		GameSpace background = new GameSpace(GuiUtils.getScaledImage(START_SCREEN_BACKGROUND, WINDOW_SIZE), true);
		background.setLayout(new GridLayout(2, 1));
		background.setBorder(PANEL_BORDER);
		/*JPanel panel = new JPanel();
		panel.setSize(WINDOW_SIZE);
		panel.setLayout(new GridLayout(2, 1));
		panel.setBorder(PANEL_BORDER);*/
		JButton newGameButton = new JButton("NewGame", new ImageIcon(BUTTON_IMG));
		JButton continueGameButton = new JButton("Continue Game", new ImageIcon(BUTTON_IMG));
		newGameButton.setVerticalTextPosition(JButton.CENTER);
		newGameButton.setHorizontalTextPosition(JButton.CENTER);
		newGameButton.setBorderPainted(false);
		//newGameButton.setMargin(new Insets(0,0,0,0));
		//PictureButton<T> newGameButton = new PictureButton<T>(BUTTON_IMG, onNewGame, actOn).setResize(true).disableBorder();
		//PictureButton<T> continueGameButton =  new PictureButton<T>(BUTTON_IMG, onContinue, actOn).setResize(true).disableBorder();
		//newGameButton.setText("New Game");
		//continueGameButton.setText("Continue Game");
		continueGameButton.setEnabled(enabledContinue);
		background.add(newGameButton);
		background.add(continueGameButton);
		frame.add(background, BorderLayout.CENTER);
		frame.revalidate();
		frame.repaint();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return frame;
	}
}
