package userIO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Used to save the game to the users local file system
 * @author David O'Sullivan
 *
 */
public class GameSaver implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private File saveFile;
	private static final String DEFAULT_SAVE_PATH = System.getProperty("user.home") + "/CollectionGame/saves/saveinfo.txt";
	public GameSaver() {
		this(DEFAULT_SAVE_PATH);
	}
	public GameSaver(String pathToSave) {
		saveFile = new File(pathToSave);
		
	}
	public void deleteSave() {
		saveFile.delete();
	}
	public boolean hasSave() throws IOException {
		return GameSaver.hasSave(saveFile);
	}
	public static boolean hasSave(File f) throws IOException{
		if (!f.exists())
			return false;
		BufferedReader br = new BufferedReader(new FileReader(f));
		if (br.readLine() == null) {
			br.close();
			return false;
		}
		br.close();
		return true;
	}
	public void createSaveFile() throws IOException {
		if (hasSave())
			return;
		File parent = saveFile.getParentFile();
		if (!parent.exists() && !parent.mkdirs()) {
		    throw new IllegalStateException("Couldn't create dir: " + parent);
		}
	}
	public void save(Serializable...objects) throws IOException {
		FileOutputStream fos = new FileOutputStream(saveFile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		for (Object o: objects)
			oos.writeObject(o);
		oos.flush();
		oos.close();
	}
	public ObjectInputStream readSave() throws IOException {
		if (!hasSave())
			return null;
		FileInputStream fis = new FileInputStream(saveFile);
		ObjectInputStream ois = new ObjectInputStream(fis);
		return ois;
	}
}
