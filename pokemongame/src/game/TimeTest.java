package game;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import thingFramework.Attribute;

public class TimeTest {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

		Attribute at = Attribute.generateAttribute("gph", "5");
		Set<Attribute> test = new HashSet<Attribute>();
		Map<String, Attribute> test2 = new HashMap<String, Attribute>();
		test.add(at);
		test2.put(at.getName(), at);
		for (Attribute att: test) {
			System.out.println(att.getValue());
		}
		test = Collections.unmodifiableSet(test);
		test2.get(at.getName()).setValue(new Integer(7));
		for (Attribute att: test) {
			System.out.println(att.getValue());
		}
		//		Board b = new Board();
//		for (int j =0; j <3; j++) {
//			for (int i =0; i < 100; i++) {
//				b.update();
//				System.out.println(b.getTotalGameTime());
//				Thread.sleep(10);
//			}
//
//			System.out.println("sleeping");
//			Thread.sleep(5000);
//			File f = new File("saveinfo");
//			FileOutputStream fos = new FileOutputStream(f);
//			ObjectOutputStream oos = new ObjectOutputStream(fos);
//			oos.writeObject(b);
//			oos.flush();
//			oos.close();
//			FileInputStream fis = new FileInputStream(f);
//			ObjectInputStream ois = new ObjectInputStream(fis);
//			b = (Board) ois.readObject();
//			ois.close();
//			b.newSession(b.getTotalGameTime());
//		}
//		

	}

}
