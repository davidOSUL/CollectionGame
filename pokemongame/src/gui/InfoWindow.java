package gui;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import guiutils.GuiUtils;
import thingFramework.Item;
import thingFramework.Thing;

public class InfoWindow extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Thing t;
	private String pictureCaption;
	private String info;
	private List<JButton> buttons = new ArrayList<JButton>();
	private static final int DEFAULT_WIDTH = 300;
	private static final int DEFAULT_HEIGHT = 200;
	private static final int CLICK_DIST_THRESH = 20;
	private boolean isDone = false;
	private boolean isEntered = false;
	private Image backgroundImage = null;
	private Presenter p;
	public InfoWindow() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	public InfoWindow(int width, int height) {
		this.setBounds(0,0, width, height);
	}
	public InfoWindow setCaption(String s) {
		this.pictureCaption = s;
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
	public InfoWindow setPresenter(Presenter p) {
		this.p = p;
		return this;
	}
	public InfoWindow setBackgroundImage(Image i) {
		this.backgroundImage = GuiUtils.changeOpacity(GuiUtils.getScaledImage(i, getWidth(), getHeight()), .5f);
		return this;
	}
	public InfoWindow addButton(String name, Consumer<Presenter> con, boolean setDone, boolean setEntered) {
		JButton jb = new JButton(name);
		BiConsumer<Presenter, MouseEvent> input = (p, e) -> {
			con.accept(p);
			if (setEntered)
				p.Entered();
			if (setDone)
				p.Finished();
		};
		jb.addMouseListener(new MouseClickWithThreshold<Presenter>(CLICK_DIST_THRESH, input, p));
		buttons.add(jb);
		return this;
	}
	public InfoWindow Create() {

		JLayeredPane result = new JLayeredPane();
		result.setPreferredSize(new Dimension(getWidth(), getHeight()));

		JPanel foreground = new JPanel();
		foreground.setBounds(0, 0, getWidth(), getHeight());
		foreground.setOpaque(false);
		
		foreground.setLayout(new BoxLayout(foreground, BoxLayout.Y_AXIS));
		foreground.setPreferredSize(new Dimension(getWidth(), getHeight()));
		
		JPanel infoPan = new JPanel();
		infoPan.add(new JLabel(info));
		infoPan.setOpaque(false);
		
		JPanel itemPan = new JPanel();
		JLabel jl = new JLabel("");
		if (t!=null)
			jl = new JLabel(new ImageIcon(GuiUtils.readImage(t.getImage())));
		jl.setText(pictureCaption);
		itemPan.add(jl);
		itemPan.setOpaque(false);
		
		JPanel buttonPan = new JPanel();
		for (JButton jb: buttons) {
			buttonPan.add(jb);
		}
		buttonPan.setOpaque(false);
		
		foreground.add(infoPan);
		foreground.add(Box.createVerticalGlue());
		foreground.add(itemPan);
		foreground.add(Box.createVerticalGlue());
		foreground.add(buttonPan);
		foreground.revalidate();
		foreground.repaint();
		if (backgroundImage != null) {
			JLabel backgroundLabel = new JLabel(new ImageIcon(backgroundImage));
			backgroundLabel.setBounds(0,0, getWidth(), getHeight());
			result.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);
		}
		
		result.add(foreground, JLayeredPane.PALETTE_LAYER);
		result.revalidate();
		result.repaint();
		result.setVisible(true);
		add(result);
		repaint();
		revalidate();
		setVisible(true);

		return this;
		
	}
	public InfoWindow addCancelButton(String text) {
		JButton jb = new JButton(text);
		BiConsumer<Presenter, MouseEvent> input = (p, e) -> {
			p.Canceled();			 
			p.Finished();
		};
		jb.addMouseListener(new MouseClickWithThreshold<Presenter>(CLICK_DIST_THRESH, input, p));
		buttons.add(jb);
		return this;
	}
	public InfoWindow addEnterButton(String text) {
		JButton jb = new JButton(text);
		BiConsumer<Presenter, MouseEvent> input = (p, e) -> {
			 p.Entered();
			 p.Finished();
		};
		jb.addMouseListener(new MouseClickWithThreshold<Presenter>(CLICK_DIST_THRESH, input, p));
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
