package game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TimeTest {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Board b = new Board();
		for (int j =0; j <3; j++) {
			for (int i =0; i < 100; i++) {
				b.update();
				System.out.println(b.getTotalGameTime());
				Thread.sleep(10);
			}

			System.out.println("sleeping");
			Thread.sleep(5000);
			File f = new File("saveinfo");
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(b);
			oos.flush();
			oos.close();
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			b = (Board) ois.readObject();
			ois.close();
			b.newSession(b.getTotalGameTime());
		}
		

	}

}
