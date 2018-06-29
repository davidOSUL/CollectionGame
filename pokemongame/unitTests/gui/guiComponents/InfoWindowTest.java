package gui.guiComponents;

import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;

import gui.displayComponents.InfoWindowBuilder;
import gui.guiutils.GuiUtils;
import gui.mvpFramework.GameView;

public class InfoWindowTest {
	public static void main(String...args) {
		GameView jf = new GameView("test");
		jf.setLayout(null);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(843, 549);
		JPanel iw2 =new InfoWindowBuilder()
				.setInfo("A wild 2appeared!")
				.addEnterButton()
				.setBackgroundImage(GuiUtils.getScaledImage(GuiUtils.readImage("/sprites/ui/background.png"), 200, 100))
				.createWindow();
		iw2.setLocation(new Point(400, 200));
		jf.getLayeredPane().add(iw2, jf.getLayeredPane().POPUP_LAYER);
		jf.setVisible(true);
		jf.revalidate();
		jf.repaint();
		
		

	}
}
