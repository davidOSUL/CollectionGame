package gui;

import java.util.List;
import java.util.function.Consumer;
import java.awt.CardLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import guiutils.GuiUtils;
import thingFramework.Item;
import thingFramework.Thing;

public class InfoWindow extends JPanel {
	private Thing t;
	private String pictureCaption;
	private String title;
	private String info;
	private List<JButton> buttons = new ArrayList<JButton>();
	private static final int DEFAULT_WIDTH = 200;
	private static final int DEFAULT_HEIGHT = 100;
	private int width = DEFAULT_WIDTH;
	private int height = DEFAULT_HEIGHT;
	private boolean isDone = false;
	private boolean isEntered = false;
	public InfoWindow() {this.setLayout(new CardLayout());}
	public InfoWindow(int width, int height) {
		this();
		this.width = width;
		this.height = height;
		this.setBounds(0,0, width, height);
	}
	public InfoWindow setCaption(String s) {
		this.pictureCaption = s;
		return this;
	}
	public InfoWindow setTitle(String s) {
		this.title = s;
		return this;
	}
	public InfoWindow setItem(Thing t) {
		this.t = t;
		return this;
	}
	public InfoWindow setInfo(String info) {
		this.info = info;
		return this;
	}
	public InfoWindow addButton(String name, Consumer<Presenter> con, Presenter p, boolean setDone, boolean setEntered) {
		JButton jb = new JButton(name);
		jb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				con.accept(p);
				isDone = setDone;
				isEntered = setEntered;
			 }

		});
		buttons.add(jb);
		return this;
	}
	public InfoWindow Create() {
		this.setTitle(title);
		JPanel infoPan = new JPanel();
		infoPan.add(new JLabel(info));
		JPanel itemPan = new JPanel();
		try {
			JLabel jl = new JLabel(new ImageIcon(GuiUtils.readImage(t.getImage())));
			jl.setText(pictureCaption);
			itemPan.add(jl);
		} catch (IOException e) {
			e.printStackTrace();
		}
		JPanel buttonPan = new JPanel();
		for (JButton jb: buttons) {
			buttonPan.add(jb);
		}
		add(infoPan);
		add(itemPan);
		add(buttonPan);
		setVisible(true);
		return this;
		
	}
	public InfoWindow addCancelButton(String text) {
		JButton jb = new JButton(text);
		jb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				isDone = true;
			 }

		});
		buttons.add(jb);
		return this;
	}
	public InfoWindow addEnterButton(String text) {
		JButton jb = new JButton(text);
		jb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				isDone = true;
				isEntered = true;
			 }

		});
		buttons.add(jb);
		return this;
	}
	public InfoWindow addEnterButton() {
		return addEnterButton("Enter");
	}
	public InfoWindow addCancelButton() {
		return addCancelButton("Cancel");
	}
	public boolean isEntered() {
		return isEntered;
	}
	public boolean isDone() {
		return isDone;
	}
}
