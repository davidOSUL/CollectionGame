package userIO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Used to save the game to the user's local file system
 * @author David O'Sullivan
 *
 */
public class GameSaver implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final File saveFile;
	private static final String DEFAULT_SAVE_PATH = System.getProperty("user.home") + "/CollectionGame/saves/saveinfo.txt";
	/**
	 * Creates a new GameSaver with the Default Save Path
	 */
	public GameSaver() {
		this(DEFAULT_SAVE_PATH);
	}
	/**
	 * Creates a new GameSaver with the provided save path
	 * @param pathToSave the path to use as the Save Path
	 */
	public GameSaver(final String pathToSave) {
		saveFile = new File(pathToSave);
		
	}
	/**
	 * Delete the file that contains the current save file
	 */
	public void deleteSave() {
		saveFile.delete();
	}
	/**
	 * Returns true if the Save File exists
	 * @return true if the Save File exists
	 * @throws IOException
	 */
	public boolean hasSave() throws IOException {
		return GameSaver.hasSave(saveFile);
	}
	private static boolean hasSave(final File f) throws IOException{
		if (!f.exists())
			return false;
		final BufferedReader br = new BufferedReader(new FileReader(f));
		if (br.readLine() == null) {
			br.close();
			return false;
		}
		br.close();
		return true;
	}
	/**
	 * If the save file does not presently exist creates a new Save File at the appropriate path
	 * @throws IOException
	 */
	public void createSaveFile() throws IOException {
		if (hasSave())
			return;
		final File parent = saveFile.getParentFile();
		if (!parent.exists() && !parent.mkdirs()) {
		    throw new IllegalStateException("Couldn't create dir: " + parent);
		}
	}
	/**
	 * Writes the provided objects to the save file
	 * @param objects the Serializable objects to write
	 * @throws IOException
	 */
	public void save(final Serializable...objects) throws IOException {
		final FileOutputStream fos = new FileOutputStream(saveFile);
		final ObjectOutputStream oos = new ObjectOutputStream(fos);
		for (final Object o: objects)
			oos.writeObject(o);
		oos.flush();
		oos.close();
	}
	/**
	 * Reads in the values stored in the save file
	 * @return an ObjectInputStream of the values stored in the Save File
	 * @throws IOException
	 */
	public ObjectInputStream readSave() throws IOException {
		if (!hasSave())
			return null;
		final FileInputStream fis = new FileInputStream(saveFile);
		final ObjectInputStream ois = new ObjectInputStream(fis);
		return ois;
	}
}
