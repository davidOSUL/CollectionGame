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

public final class GuiUtils {
	private GuiUtils() {}
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
	 * Converts a given Image into a BufferedImage
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
	public static Image getScaledImage(Image srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}
	public static Image changeOpacity(Image i, float opacity) {
		BufferedImage bi = new BufferedImage(i.getWidth(null), i.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bi.createGraphics();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
	    g2.drawImage(i, 0, 0, bi.getWidth(), bi.getHeight(), null);
	    return bi;

	}
	public static Image readImage(String input) {
		try {
			return ImageIO.read(GuiUtils.class.getResourceAsStream(input));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static BufferedImage readAndTrimImage(String input) {
		return trimImage(toBufferedImage(readImage(input)));
	}
	public static Point subtractPoints(Point p1, Point p2) {
		return new Point(p1.x-p2.x, p1.y-p2.y);
	}
	public static Image FillIn(Image image, Color color) {
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
	public static Image rotateImage90ClockwiseAndTrim(Image curr) {
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
