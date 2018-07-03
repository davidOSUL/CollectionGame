package userIO;

import java.io.File;

public class GameSaver {
	private File saveFile;
	public GameSaver(String pathToSave) {
		saveFile = new File(pathToSave);
		File parent = saveFile.getParentFile();
		if (!parent.exists() && !parent.mkdirs()) {
		    throw new IllegalStateException("Couldn't create dir: " + parent);
		}
	}
}
