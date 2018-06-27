package game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PopularityTest {
	public static void main(String ...strings ) {
		PopularityTest.myPop();
	}
	public static void myPop() {
		File f = new File("popularityTest");
		Scanner c = null;
		try {
			 c = new Scanner(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int totalPop = 0;
		while (c.hasNextLine()) {
			String x = c.nextLine();
			int popVal =0;
			if (x.startsWith("Popularity")) {
				popVal = Integer.parseInt(x.substring("Popularity: ".length()));
			}
			totalPop += popVal;
		}
		System.out.println(totalPop);
	}

}
