package gui.displayComponents;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.border.Border;

import gui.gameComponents.GameSpace;
import gui.guiutils.GuiUtils;

/**
 * Creates the initial start screen where user can select continue or new game
 * @author David O'Sullivan
 *
 */
public class StartScreenBuilder {
	private static final Dimension WINDOW_SIZE = new Dimension(460, 335);
	private static final Border PANEL_BORDER = BorderFactory.createEmptyBorder(100, 50, 100, 50);
	private final static Image START_SCREEN_BACKGROUND = GuiUtils.changeOpacity(GuiUtils.readImage("/sprites/ui/pikabackground.jpg"), .5f);
	private final static String BUTTON_NAME= "button";
	public static <T> JFrame getFrame(final String title, final boolean enabledContinue, final Consumer<T> onNewGame, final Consumer<T> onContinue, final T actOn)  {
		final JFrame frame = new JFrame();
		frame.setVisible(false);
		frame.setSize(WINDOW_SIZE);
		frame.setLayout(new BorderLayout());
		frame.setTitle(title);
		frame.setLocationByPlatform(true);
		final GameSpace background = new GameSpace(GuiUtils.getScaledImage(START_SCREEN_BACKGROUND, WINDOW_SIZE), true);
		background.setLayout(new GridLayout(2, 1));
		background.setBorder(PANEL_BORDER);
		/*JPanel panel = new JPanel();
		panel.setSize(WINDOW_SIZE);
		panel.setLayout(new GridLayout(2, 1));
		panel.setBorder(PANEL_BORDER);*/
		final JButton newGameButton = ButtonWithBackgroundBuilder.generateJButton(BUTTON_NAME, "New Game");
		newGameButton.addActionListener(e -> onNewGame.accept(actOn));
		final JButton continueGameButton = ButtonWithBackgroundBuilder.generateJButton(BUTTON_NAME, "Continue Game");
		continueGameButton.setEnabled(enabledContinue);
		continueGameButton.addActionListener(e -> onContinue.accept(actOn));
		background.add(newGameButton);
		background.add(continueGameButton);
		background.revalidate();
		background.repaint();
		background.setVisible(true);
		frame.add(background, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.revalidate();
		frame.repaint();
		frame.pack();
		return frame;
	}
}
