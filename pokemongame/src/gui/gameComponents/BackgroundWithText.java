package gui.gameComponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

import gui.guiutils.GuiUtils;

/**
 * A gamespace with both an image, as well as the potential for text that can be updated 
 * @author David O'Sullivan
 *
 */
public class BackgroundWithText extends GameSpace{

	
	private static final Font DEFAULT_FONT = new Font("TimesRoman", Font.BOLD, 20);
	private static final long serialVersionUID = 1L;
	private String[] texts;
	private Point[] points;
	private TextPreset preset = TextPreset.NO_PRESET;
	
	/**
	 * Creates a new BackgroundWithText with no texts, with the specified image, and at the specified location
	 * @param background the image to display in the background
	 * @param location where this BackgroundWithText should reside
	 */
	public BackgroundWithText(final Image background, final Point location) {
		super(background, location);
		setUp();
	}
	/**
	 * Creates a new BackgroundWithText with texts set to "",  displayed at the passed in locations
	 * (e.g. texts[0] is at points[0]). With default font.
	 * @param background the image for the background
	 * @param points the location of the texts
	 */
	public BackgroundWithText(final Image background, final Point[] points) {
		this(background, points, DEFAULT_FONT);
	}
	/**
	 * Creates a new BackgroundWithText with texts set to "",  displayed at the passed in locations
	 * (e.g. texts[0] is at points[0]
	 * @param background the image for the background
	 * @param points the location of the texts
	 * @param font the font of the text
	 */
	public BackgroundWithText(final Image background, final Point[] points, final Font font) {
		super(background);
		setUp(null, points, font);
	}
	/**
	 * Creates a new BackgroundWithText JLabel with texts set to "",  displayed at the passed in locations
	 * (e.g. texts[0] is at points[0]
	 * @param background the image for the background
	 * @param points the location of the texts
	 * @param font the font of the text
	 * @param preset how to arrange the text, default is NO_PRESET
	 */
	public BackgroundWithText(final Image background, final Point[] points, final Font font, final TextPreset preset) {
		super(background);
		setUp(null, points, font, preset);
	}
	/**
	 * Creates a new BackgroundWithText JLabel with the texts displayed the passed in locations
	 * (e.g. texts[0] is at points[0]
	 * @param background the image for the background
	 * @param texts the texts to display
	 * @param points the location of the texts
	 */
	public BackgroundWithText(final Image background, final String[] texts, final Point[] points, final Font font) {
		super(background);
		if (texts.length != points.length)
			throw new IllegalArgumentException("Texts and points must be the same length");
		setUp(texts, points, font);
	}
	/**
	 * Creates a new BackgroundWithText JLabel with the texts displayed the passed in locations
	 * (e.g. texts[0] is at points[0]
	 * @param background the image for the background
	 * @param texts the texts to display
	 * @param points the location of the texts
	 * @param preset how to arrange the text, default is NO_PRESET
	 */
	public BackgroundWithText(final Image background, final String[] texts, final Point[] points, final Font font, final TextPreset preset) {
		super(background);
		if (texts.length != points.length)
			throw new IllegalArgumentException("Texts and points must be the same length");
		setUp(texts, points, font, preset);
	}
	/**
	 * Sets up all instance variables
	 */
	private void setUp(final String[] texts, Point[] points, final Font font, final TextPreset preset) {
		setLayout(null);
		if (points == null)
			points = new Point[0];
		if (texts == null) {
			this.texts = new String[points.length];
			for (int i = 0; i < points.length; i++)
				this.texts[i] = "";
		}
		else {
			this.texts = texts;
		}
		this.points = points;
		setFont(font);
		this.preset = preset;
		revalidate();
		repaint();
		setVisible(true);
	}
	private void setUp(final String[] texts, final Point[] points, final Font font) {
		setUp(texts, points, font, TextPreset.NO_PRESET);
	}
	private void setUp() {
		setUp(null, null, null, TextPreset.NO_PRESET);
	}
	/**
	 * Update the text with the specified index
	 * @param index the index of the tex t
	 * @param newText the new text to display
	 */
	public void updateText(final int index, final String newText) {
		texts[index] = newText;
	}
	/**
	 * If this background with text currently doesn't have any text, allow it to have one piece of text.
	 * Either way, make the first element of the texts array the passed in string. 
	 * @param text
	 */
	protected void setFirstText(final String text) {
		if (texts.length == 0) { 
			texts = new String[1];
			points = new Point[1];
		}
		texts[0] = text;
		points[0] = new Point(0, 0);
		
	}
	/**
	 * Set the TextPreset for this BackgroundWithText
	 * @param preset the preset to set
	 */
	public void setPreset(final TextPreset preset) {
		this.preset = preset;
	}
	@Override
	public void setFont(final Font font) {
		final Font f = (font == null) ? DEFAULT_FONT : font;
		super.setFont(f);
	}
	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		g.setFont(getFont());
		g.setColor(Color.BLACK);
		for (int i = 0; i < texts.length; i++) {
			switch(preset) {
			case CENTER_ALL_TEXT:
				GuiUtils.drawCenteredString(g, texts[i], new Rectangle(0, 0, getWidth(), getHeight()), getFont(), Color.BLACK);
				break;
			case NO_PRESET:
				g.drawString(texts[i], points[i].x, points[i].y);
				break;
			
			}
		}
		//g.drawImage(background, 0,0,this);
		
	}
	/**
	 * how to arrange the text
	 * @author David O'Sullivan
	 *
	 */
	public enum TextPreset{
		/**
		 * standard arrangmenet wherever the points are 
		 */
		NO_PRESET, 
		/**
		 * ignore the points and place every piece of text in the center 
		 */
		CENTER_ALL_TEXT
	}
}
