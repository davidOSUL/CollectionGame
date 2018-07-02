package gui.guiutils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Set of Useful functions for GUI purposes.
 * @author DOSullivan
 */
public final class GuiUtils {
	private static String pokeCash = "<html><img src=\"" + GuiUtils.class.getResource("/sprites/ui/dollar.png") + "\">";
	private GuiUtils() {}
	/**
	 * Checks if two Rectangles overlap with each other
	 * @param rec1 the first rectangle
	 * @param rec2 the second rectangle
	 * @return true if they overlap
	 */
	public boolean recsOverlap(Rectangle rec1, Rectangle rec2) {
		Point l1 = new Point((int)rec1.getX(), (int)rec1.getY());
		Point r1 = new Point((int)rec1.getMaxX(), (int)rec1.getMaxY());
		Point l2 = new Point((int)rec2.getX(), (int)rec2.getY());
		Point r2 = new Point((int)rec2.getMaxX(), (int)rec2.getMaxY());
		
		if (l1.x > r2.x || l2.x > r1.x)
	        return false;
	 
	    // If one rectangle is above other
	    if (l1.y < r2.y || l2.y < r1.y)
	        return false;
	 
	    return true;
	}
	public static BufferedImage overlayText(Image image, String text, Point p, Font f) {
		BufferedImage input = newBufferedImage(image);
		Graphics2D g2d = input.createGraphics();
		g2d.setFont(f);
		g2d.setColor(Color.BLACK);
		g2d.drawString(text, p.x, p.y);
		return input;
	}
	public static BufferedImage overlayImage(Image image, Image overlay, Point p) {
		BufferedImage input =  newBufferedImage(image);
		Graphics2D g2d = input.createGraphics();
		g2d.drawImage(overlay, p.x, p.y, null);
		return input;
		
	}
	public static String getToolTipDollar() {
		//return pokeCash;
		return "$";
	}
	/**
	 * Creates a NEW instance of a buffered image, copying over from an image
	 * @param image the image to copy from
	 * @return the new buffered image
	 */
	public static BufferedImage newBufferedImage(Image image) {
		BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bi.createGraphics();
		g2.drawImage(image, 0, 0, bi.getWidth(), bi.getHeight(), null);
		return bi;
	}
	/**
	 * Returns a new BufferedImage which is the passed in image with all of white space around it gotten rid of
	 * @param image the buffered image to trim
	 * @return the trimmed image
	 */
	public static BufferedImage trimImage(BufferedImage image) {
	    int width = image.getWidth();
	    int height = image.getHeight();
	    int top = height;
	    int bottom = 0;
	    int left = width;
	    int right = 0;
	    for (int x = 0; x < width; x++) {
	        for (int y = 0; y < height; y++) {
	            if (image.getRGB(x, y) != 0){
	                top    = Math.min(top, y);
	                bottom = Math.max(bottom, y);
	                left   = Math.min(left, x);
	                right  = Math.max(right, x);
	            }
	        }
	    }
	    return image.getSubimage(left, top, right-left, bottom-top);
	 }
	
	/**
	 * Returns a new BufferedImage which is the passed in Image converted into a BufferedImage
	 *
	 * @param img The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}
	/**
	 * Returns a new image with the width/height of the srcImage to the passed in width/height, scaling down the image 
	 * @param srcImg the original image
	 * @param w the new width
	 * @param h the new height
	 * @return the new image
	 */
	public static BufferedImage getScaledImage(Image srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}
	/**
	 * Returns a new image which is the passed in image with the opacity changed to the given value
	 * @param i the image to change
	 * @param opacity the opacity given as a decimal (0 is transparent, 1 is the origianl image. E.g. .5 would be 50% transparent)
	 * @return the new image
	 */
	public static BufferedImage changeOpacity(Image i, float opacity) {
		BufferedImage bi = new BufferedImage(i.getWidth(null), i.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bi.createGraphics();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
	    g2.drawImage(i, 0, 0, bi.getWidth(), bi.getHeight(), null);
	    return bi;

	}
	/**
	 * Reads in an image from the given path
	 * @param input the path to the image
	 * @return the Image
	 */
	public static Image readImage(String input) {
		try {
			return ImageIO.read(GuiUtils.class.getResourceAsStream(input));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static BufferedImage readTrimAndScaleImage(String input, int w, int h) {
		return getScaledImage(readAndTrimImage(input),w ,h);
	}
	/**
	 * Reads an image from the given path and then trims it and returns that new image
	 * @param input the path to the image
	 * @return the trimmed image
	 */
	public static BufferedImage readAndTrimImage(String input) {
		return trimImage(toBufferedImage(readImage(input)));
	}
	/**
	 * Subtracts coordinate-wise p1-p2 (that is (p1.x-p2.x, p1.y-p2.y))
	 * @param p1 the first point
	 * @param p2 the second point
	 * @return the new point which is p1-p2
	 */
	public static Point subtractPoints(Point p1, Point p2) {
		return new Point(p1.x-p2.x, p1.y-p2.y);
	}
	/**
	 * Returns a new image which is the given image but with all transparent pixels filled in with the provided color
	 * @param image the original image
	 * @param color the background color
	 * @return the new filled-in image
	 */
	public static BufferedImage FillIn(Image image, Color color) {
		BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bi.createGraphics();
		g2.setPaint(color);
		g2.fillRect(0, 0, bi.getWidth(), bi.getHeight());
		g2.drawImage(image, 0, 0, bi.getWidth(), bi.getHeight(), null);
		return bi;
	}
	/**
	 * Draw a String centered in the middle of a Rectangle.
	 *
	 * @param g The Graphics instance.
	 * @param text The String to draw.
	 * @param rect The Rectangle to center the text in.
	 * @param c The color of the font (will be set back to default color after)
	 */
	public static void drawCenteredString(Graphics g, String text, Rectangle rect, Font font, Color c) {
	    // Get the FontMetrics
	    FontMetrics metrics = g.getFontMetrics(font);
	    // Determine the X coordinate for the text
	    int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
	    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
	    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
	    // Set the font
	    g.setFont(font);
	    //Set the color
	    Color old = g.getColor();
	    g.setColor(c);
	    // Draw the String
	    g.drawString(text, x,y);
	    g.setColor(old);
	}
	/**
	 * Returns a new image which is the given imaged rotated 90 degrees clockwise
	 * @param curr the original image
	 * @return the rotated image
	 */
	public static BufferedImage rotateImage90ClockwiseAndTrim(Image curr) {
		BufferedImage img = toBufferedImage(curr);
		int         width  = img.getWidth();
	    int         height = img.getHeight();
	    BufferedImage   newImage = new BufferedImage( height, width, img.getType() );
	 
	    for( int i=0 ; i < width ; i++ )
	        for( int j=0 ; j < height ; j++ )
	            newImage.setRGB( height-1-j, i, img.getRGB(i,j) );
	 
	    return newImage;
	}
}
