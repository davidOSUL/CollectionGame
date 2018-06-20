package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GameView extends JFrame implements MouseListener{
	JPanel mainGamePanel;
	private static final int WIDTH = 843;
	private static final int HEIGHT = 549;
	private static final Image background = new ImageIcon(MainGamePanel.class.getResource("/sprites/ui/background.png")).getImage();

	Map<Integer, GameSpace> gameSpaces = new HashMap<Integer, GameSpace>();
	public GameView(String name) {
		super(name);
		addMouseListener(this);
		mainGamePanel = new MainGamePanel();
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		JLabel backgroundLabel = new JLabel(new ImageIcon(getScaledImage(background, WIDTH, HEIGHT)));
		backgroundLabel.setSize(WIDTH, HEIGHT);
		//backgroundLabel.setOpaque(true);
		add(mainGamePanel);
		add(backgroundLabel);
		
		
		revalidate();
		repaint();
		
	}
	
	private Image getScaledImage(Image srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}
	public static void main(String...args) {
		SwingUtilities.invokeLater(new Runnable() {
		    @Override
		    public void run() {
		        GameView gv = new GameView("test");
		        gv.setVisible(true);
		        
		    }
		});
		
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		 

		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	
}
