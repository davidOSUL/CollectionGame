package gui.mvpFramework;

import java.awt.Image;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import gui.guiComponents.GameSpace;
import gui.guiutils.GuiUtils;
import gui.mvpFramework.Presenter.AddType;

public class GameView extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MainGamePanel mainGamePanel;
	protected static final int WIDTH = 843;
	protected static final int HEIGHT = 549;
	private static final Image background = GuiUtils.readImage("/sprites/ui/background.png");
	private Map<Integer, GameSpace> gameSpaces = new HashMap<Integer, GameSpace>();
	private Presenter p;
	public GameView(String name) {
		super(name);
		mainGamePanel = new MainGamePanel(this);
		setLayout(null);
		setLocationByPlatform(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		JLabel backgroundLabel = new JLabel(new ImageIcon(GuiUtils.getScaledImage(background, WIDTH, HEIGHT)));
		backgroundLabel.setSize(WIDTH, HEIGHT);
		//backgroundLabel.setOpaque(true);
		add(mainGamePanel);
		add(backgroundLabel);
		
		
		revalidate();
		repaint();
		
	}
	public void setPresenter(Presenter p) {
		this.p = p;
	}
	public Presenter getPresenter() {
		return p;
	}

	/**
	 * @return the upper left hand corner that centers the object 
	 */
	private Point getCenterPoint(int width, int height) {
		Point myCenter = new Point(width/2, height/2);
		Point viewCenter = new Point(WIDTH/2, HEIGHT/2);
		return new Point(viewCenter.x-myCenter.x, viewCenter.y-myCenter.y);
	}
	
	public void displayPanelCentered(JPanel jp) {
		jp.setLocation(getCenterPoint(jp.getWidth(), jp.getHeight()));
		getLayeredPane().add(jp, JLayeredPane.POPUP_LAYER);
		updateDisplay();
	}
	public void removeDisplay(JPanel jp) {
		getLayeredPane().remove(jp);
		updateDisplay();		
	}
	public void attemptThingAdd(GameSpace gs, AddType type) {
		mainGamePanel.gameSpaceAdd(gs, type);
	}
	public  void setWildPokemonCount(int num) {
		mainGamePanel.updateNotifications(num);
	}
	public void updateDisplay() {
		revalidate();
		repaint();
	}
	

	
}
