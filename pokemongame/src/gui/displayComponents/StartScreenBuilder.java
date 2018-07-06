package gui.displayComponents;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
		frame.setVisible(false);
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
		JButton newGameButton = ButtonWithBackgroundBuilder.generateButton(BUTTON_IMG, "New Game");
		newGameButton.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
			    onNewGame.accept(actOn);
			  } 
		});
		JButton continueGameButton = ButtonWithBackgroundBuilder.generateButton(BUTTON_IMG, "Continue Game");
		continueGameButton.setEnabled(enabledContinue);
		continueGameButton.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
			    onContinue.accept(actOn);
			  } 
		});
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
