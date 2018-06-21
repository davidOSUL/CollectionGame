package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import guiutils.GuiUtils;

public class GameView extends JFrame {
	private MainGamePanel mainGamePanel;
	protected static final int WIDTH = 843;
	protected static final int HEIGHT = 549;
	private static final Image background = new ImageIcon(MainGamePanel.class.getResource("/sprites/ui/background.png")).getImage();

	private Map<Integer, GameSpace> gameSpaces = new HashMap<Integer, GameSpace>();
	private Presenter p;
	public GameView(String name, Presenter p) {
		super(name);
		this.p= p;
		mainGamePanel = new MainGamePanel(p);
		setLayout(null);
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
	
	
	public static void main(String...args) {
		SwingUtilities.invokeLater(new Runnable() {
		    @Override
		    public void run() {
		        GameView gv = new GameView("Pokemon Game V. Alpha");
		        gv.setVisible(true);
		        
		    }
		});	
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
		add(jp);
		revalidate();
		repaint();
	}
	public void removeDisplay(JPanel jp) {
		remove(jp);
		revalidate();
		repaint();
	}
	public void attemptThingAdd(GameSpace gs) {
		mainGamePanel.thingAdd(gs);
	}


	
}
