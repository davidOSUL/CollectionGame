package gui.mvpFramework;

import java.awt.Image;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import gui.gameComponents.GameSpace;
import gui.gameComponents.Grid.GridSpace;
import gui.guiutils.GuiUtils;
import gui.mvpFramework.Presenter.AddType;

/**
 * The JFrame which houses the game
 * @author DOSullivan
 */
public class GameView extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MainGamePanel mainGamePanel;
	protected static final int WIDTH = 843;
	protected static final int HEIGHT = 549;
	private static final Image background = GuiUtils.readImage("/sprites/ui/background.png");
	private Presenter p;
	/**
	 * Creates a new GameView with the specified title
	 * @param name the title of the GameView
	 */
	public GameView(String name) {
		super(name);
		mainGamePanel = new MainGamePanel(this);
		setLayout(null);
		setResizable(false);
		setLocationByPlatform(true);
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel backgroundLabel = new JLabel(new ImageIcon(GuiUtils.getScaledImage(background, WIDTH, HEIGHT)));
		backgroundLabel.setSize(WIDTH, HEIGHT);
		//backgroundLabel.setOpaque(true);
		add(mainGamePanel);
		add(backgroundLabel);
		
		
		revalidate();
		repaint();
		
	}
	/**
	 * Sets the presenter that coordinates with this GameView
	 * @param p the presenter to coordinate with
	 */
	public void setPresenter(Presenter p) {
		this.p = p;
	}
	/**
	 * @return the presenter associated with this GameView
	 */
	public Presenter getPresenter() {
		return p;
	}

	/**
	 * @param width the width of the graphical entity
	 * @param height the height of the graphical entity
	 *@return the upper left hand corner that centers the graphical entity
	 */
	private Point getCenterPoint(int width, int height) {
		Point myCenter = new Point(width/2, height/2);
		Point viewCenter = new Point(WIDTH/2, HEIGHT/2);
		return new Point(viewCenter.x-myCenter.x, viewCenter.y-myCenter.y);
	}
	
	/**
	 * Adds to the POPUP_LAYER of this GameView's LayeredPane() the provided JPanel. Displays in the center of the JFrame.
	 * @param jp the JPanel to center and display as a pop up
	 */
	public void displayPanelCentered(JPanel jp) {
		jp.setLocation(getCenterPoint(jp.getWidth(), jp.getHeight()));
		getLayeredPane().add(jp, JLayeredPane.POPUP_LAYER);
		updateDisplay();
	}
	/**
	 * Removes the provided JPanel from the POPUP_LAYER
	 * @param jp the JPanel to remove
	 */
	public void removeDisplay(JPanel jp) {
		getLayeredPane().remove(jp);
		updateDisplay();		
	}
	/**
	 * Starts the process of attempting to add the newly created provided GameSpace to the maingamePanel.
	 * Converts the GameSpace to a gridSpace on the default grid and then adds. 
	 * @param gs the GameSpace to add
	 * @param type the context of the add (e.g. pokemon from queue, moving an existing GameSpace, etc.)
	 */
	public void attemptNewGridSpaceAdd(GameSpace gs, AddType type) {
		mainGamePanel.gridSpaceAdd(mainGamePanel.generateGridSpaceWithDefaultGrid(gs), type);
	}
	/**
	 * Starts the process of attempting to add the existing GridSpace to the maingamePanel
	 * @param gs the GameSpace to add
	 * @param type the context of the add (e.g. pokemon from queue, moving an existing GameSpace, etc.)
	 */
	public void attemptExistingGridSpaceAdd(GridSpace gs, AddType type) {
		mainGamePanel.gridSpaceAdd(gs, type);
	}
	/**
	 * Sets the value of the notification button
	 * @param num the number to set the notification button to
	 */
	public  void setWildPokemonCount(int num) {
		mainGamePanel.updateNotifications(num);
	}
	public void setBoardAttributes(int gold, int popularity) {
		mainGamePanel.updateDisplayedAttributes(gold, popularity);
	}
	/**
	 * Revalidates and repaints the display
	 */
	public void updateDisplay() {
		revalidate();
		repaint();
	}
	
	

	
}
