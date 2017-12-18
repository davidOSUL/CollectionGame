package gui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameView {
	Presenter presenter;
	JPanel mainGamePanel;
	private static final int DEFAULT_NUM_SPOTS = 16;
	private static final int SPOTS_PER_ROW = 4;
	private static final int SPOTS_PER_COLUMN = 4;
	private static final int DEFAULT_WIDTH = GameSpace.DEFAULT_WIDTH;
	private static final int DEFAULT_HEIGHT = GameSpace.DEFAULT_HEIGHT;
	Map<Integer, GameSpace> gameSpaces = new HashMap<Integer, GameSpace>();
	public GameView(Presenter presenter) {
		mainGamePanel = defaultLoad();
		mainGamePanel.validate();
	}
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	private JPanel defaultLoad() {
		JPanel panel = new JPanel();
	
		int startX =0;
		int startY = 0;	
		for (int i = 0; i < DEFAULT_NUM_SPOTS; i++) {
			int xLocation = startX + (i % SPOTS_PER_ROW)*DEFAULT_WIDTH;
			int yLocation = startY + (i % SPOTS_PER_COLUMN)*DEFAULT_HEIGHT;
			GameSpace gs = new GameSpace(xLocation, yLocation);
			gameSpaces.put(i, gs);
			panel.add(gs);
		}
		panel.validate();
		return panel;
	}
	public static void main(String...args) {
		JFrame frame = new JFrame("test");
		frame.add(new GameView(new Presenter()).mainGamePanel);
		frame.setSize(400, 400);
		frame.setVisible(true);
		frame.repaint();
	}
}
