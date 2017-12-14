package thingFramework;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import game.Board;

public class TestDeserialize {
	public static void main(String...args) throws ClassNotFoundException, IOException {
		/*File f = new File("saveinfo");
		FileInputStream fis = new FileInputStream(f);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Board b = (Board) ois.readObject();
		ois.close();
		System.out.println(b.checkForPokemon.getTimeCreated());*/
	}
}
