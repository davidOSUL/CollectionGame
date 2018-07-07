package interfaces;

/**
 * Any object that has a path to an image should implement this
 * @author David O'Sullivan
 *
 */
public interface Imagable {
	/**
	 * @return the path to the image associated with this object
	 */
	public String getImage();
}
