package gui.mvpFramework.view.defaultimplementation;

import java.awt.Image;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import gui.gameComponents.GameSpace;
import gui.gameComponents.grid.GridSpace;
import gui.gameComponents.grid.GridSpace.GridSpaceData;
import gui.guiutils.GuiUtils;
import gui.mvpFramework.presenter.AddType;
import gui.mvpFramework.presenter.Presenter;
import gui.mvpFramework.view.ViewInterface;

/**
 * Main Implementation of ViewInterface.
 * @author David O'Sullivan
 */
public class GameView implements ViewInterface {
	private final MainGamePanel mainGamePanel;
	/**
	 * The width of the JFrame
	 */
	protected static final int WIDTH = 843;
	/**
	 * The height of the JFrame
	 */
	protected static final int HEIGHT = 549;
	private static final Image background = GuiUtils.readImage("/sprites/ui/background.png");
	private Presenter p;
	private final JFrame frame;
	/**
	 * Creates a new GameView with the specified title
	 * @param name the title of the GameView
	 */
	public GameView(final String name) {
		frame = new JFrame(name);
		mainGamePanel = new MainGamePanel(this);
		frame.setLayout(null);
		frame.setResizable(false);
		frame.setLocationByPlatform(true);
		frame.setSize(WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final JLabel backgroundLabel = new JLabel(new ImageIcon(GuiUtils.getScaledImage(background, WIDTH, HEIGHT)));
		backgroundLabel.setSize(WIDTH, HEIGHT);
		//backgroundLabel.setOpaque(true);
		frame.add(mainGamePanel);
		frame.add(backgroundLabel);
		
		
		frame.revalidate();
		frame.repaint();
		
	}
	/** 
	 * @see gui.mvpFramework.view.ViewInterface#setPresenter(gui.mvpFramework.presenter.Presenter)
	 */
	@Override
	public void setPresenter(final Presenter p) {
		this.p = p;
	}
	/** 
	 * @see gui.mvpFramework.view.ViewInterface#getPresenter()
	 */
	@Override
	public Presenter getPresenter() {
		return p;
	}

	/**
	 * @param width the width of the graphical entity
	 * @param height the height of the graphical entity
	 *@return the upper left hand corner that centers the graphical entity
	 */
	private Point getCenterPoint(final int width, final int height) {
		final Point myCenter = new Point(width/2, height/2);
		final Point viewCenter = new Point(WIDTH/2, HEIGHT/2);
		return new Point(viewCenter.x-myCenter.x, viewCenter.y-myCenter.y);
	}
	/** 
	 * @see gui.mvpFramework.view.ViewInterface#cancelGridSpaceAdd()
	 */
	@Override
	public void cancelGridSpaceAdd() {
		mainGamePanel.cancelGridSpaceAdd();
	}
	/** 
	 * @see gui.mvpFramework.view.ViewInterface#displayComponentCentered(javax.swing.JComponent)
	 */
	@Override
	public void displayComponentCentered(final JComponent jp) {
		jp.setLocation(getCenterPoint(jp.getWidth(), jp.getHeight()));
		frame.getLayeredPane().add(jp, JLayeredPane.POPUP_LAYER);
		updateDisplay();
	}
	/** 
	 * @see gui.mvpFramework.view.ViewInterface#removeDisplay(javax.swing.JComponent)
	 */
	@Override
	public void removeDisplay(final JComponent jp) {
		frame.getLayeredPane().remove(jp);
		updateDisplay();		
	}
	/** 
	 * @see gui.mvpFramework.view.ViewInterface#attemptNewGridSpaceAdd(gui.gameComponents.GameSpace, gui.mvpFramework.presenter.AddType)
	 */
	@Override
	public void attemptNewGridSpaceAdd(final GameSpace gameSpace, final AddType type) {
		mainGamePanel.gridSpaceAdd(mainGamePanel.generateGridSpaceWithDefaultGrid(gameSpace), type);
	}
	/** 
	 * @see gui.mvpFramework.view.ViewInterface#attemptExistingGridSpaceAdd(gui.gameComponents.grid.GridSpace, gui.mvpFramework.presenter.AddType)
	 */
	@Override
	public void attemptExistingGridSpaceAdd(final GridSpace gridSpace, final AddType type) {
		mainGamePanel.gridSpaceAdd(gridSpace, type);
	}
	/** 
	 * @see gui.mvpFramework.view.ViewInterface#setWildCreatureCount(int)
	 */
	@Override
	public  void setWildCreatureCount(final int num) {
		mainGamePanel.updateNotifications(num);
	}
	/** 
	 * @see gui.mvpFramework.view.ViewInterface#setModelAttributes(int, int)
	 */
	@Override
	public void setModelAttributes(final int gold, final int popularity) {
		mainGamePanel.updateDisplayedAttributes(gold, popularity);
	}
	/** 
	 * @see gui.mvpFramework.view.ViewInterface#updateDisplay()
	 */
	@Override
	public void updateDisplay() {
		frame.revalidate();
		frame.repaint();
	}
	/** 
	 * @see gui.mvpFramework.view.ViewInterface#setEnabledForButtons(boolean)
	 */
	@Override
	public void setEnabledForButtons(final boolean enabled) {
		mainGamePanel.setEnabledForButtons(enabled);
	}
	/** 
	 * @see gui.mvpFramework.view.ViewInterface#addNewGridSpaceFromSave(gui.gameComponents.GameSpace, gui.gameComponents.grid.GridSpace.GridSpaceData)
	 */
	@Override
	public GridSpace addNewGridSpaceFromSave(final GameSpace gameSpace, final GridSpaceData data) {
		return mainGamePanel.addSavedGridSpaceToGrid(gameSpace, data);
	}
	/** 
	 * @see gui.mvpFramework.view.ViewInterface#getFrame()
	 */
	@Override
	public JFrame getFrame() {
		return frame;
	}
	

	
}
