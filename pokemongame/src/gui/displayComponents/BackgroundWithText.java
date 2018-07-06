package gui.displayComponents;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gui.guiutils.GuiUtils;

public class BackgroundWithText extends JPanel{

	
	private static final Font DEFAULT_FONT = new Font("TimesRoman", Font.BOLD, 11);
	private static final long serialVersionUID = 1L;
	private Image background;
	private String[] texts;
	private Point[] points;
	private Font font;
	/**
	 * Creates a new BackgroundWithText JLabel with texts set to "",  displayed at the passed in locations
	 * (e.g. texts[0] is at points[0]). With default font.
	 * @param i the image for the background
	 * @param points the location of the texts
	 */
	public BackgroundWithText(Image background, Point[] points) {
		this(background, points, null);
	}
	/**
	 * Creates a new BackgroundWithText JLabel with texts set to "",  displayed at the passed in locations
	 * (e.g. texts[0] is at points[0]
	 * @param i the image for the background
	 * @param points the location of the texts
	 * @param font the font of the text
	 */
	public BackgroundWithText(Image background, Point[] points, Font font) {
		setLayout(null);
		this.background = background;
		this.texts = new String[points.length];
		for (int i = 0; i < points.length; i++)
			texts[i] = "";
		this.points = points;
		this.font = font;
		if (font == null)
			this.font = DEFAULT_FONT;
		revalidate();
		repaint();
		setVisible(true);
	}
	/**
	 * Creates a new BackgroundWithText JLabel with the texts displayed the passed in locations
	 * (e.g. texts[0] is at points[0]
	 * @param i the image for the background
	 * @param texts the texts to display
	 * @param points the location of the texts
	 */
	public BackgroundWithText(Image background, String[] texts, Point[] points, Font font) {
		if (texts.length != points.length)
			throw new IllegalArgumentException("Texts and points must be the same length");
		setLayout(null);
		this.background = background;
		this.texts = texts;
		this.points = points;
		this.font = font;
		revalidate();
		repaint();
		setVisible(true);
	}
	/**
	 * Update the text with the specified index
	 * @param index the index of the tex t
	 * @param newText the new text to display
	 */
	public void updateText(int index, String newText) {
		texts[index] = newText;
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setFont(font);
		for (int i = 0; i < texts.length; i++) {
			g.drawString(texts[i], points[i].x, points[i].y);
		}
		//g.drawImage(background, 0,0,this);
		
	}
	
}
