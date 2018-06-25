package gui.guiComponents;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import gui.guiutils.GuiUtils;
import gui.mouseAdapters.MouseClickWithThreshold;
import gui.mvpFramework.Presenter;
import thingFramework.Thing;

/**
 * A Pop-Up Window that interfaces with a provided Presenter. All Features are added incrementally, only adding what is needed.
 * Must call Create() to finalize creation of InfoWindow
 * @author DOSullivan
 */
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
	/**
	 * Create a new InfoWindow with Default Width and Height
	 */
	public InfoWindow() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	/**
	 * Create a new InfoWindow with specified width/height
	 * @param width the width of the info window
	 * @param height the height of the info window
	 */
	public InfoWindow(int width, int height) {
		this.setBounds(0,0, width, height);
	}
	/**
	 * Sets the caption (the text below the picture)
	 * @param s the caption
	 * @return the new InfoWindow
	 */
	public InfoWindow setCaption(String s) {
		this.pictureCaption = s;
		return this;
	}
	/**
	 * Sets the Item associated with this info window, this is used to set the picture on the info window
	 * @param t the Thing to get a picture from
	 * @return the new info window
	 */
	public InfoWindow setItem(Thing t) {
		this.t = t;
		return this;
	}
	/**
	 * Sets the information text, this is placed above the picture
	 * @param info the text to add
	 * @return the new info window
	 */
	public InfoWindow setInfo(String info) {
		this.info = info;
		return this;
	}
	/**
	 * Sets the presenter associated with this object, this must be called for button functionanlity
	 * @param p the presenter
	 * @return the New Info Window
	 */
	public InfoWindow setPresenter(Presenter p) {
		this.p = p;
		return this;
	}
	/**
	 * Sets the background image for this InfoWindow
	 * @param i the background image
	 * @return the new Info Window
	 */
	public InfoWindow setBackgroundImage(Image i) {
		this.backgroundImage = GuiUtils.changeOpacity(GuiUtils.getScaledImage(i, getWidth(), getHeight()), .5f);
		return this;
	}
	/**
	 * Adds a new button to this Info Window. It should be noted that the order of calls will be CleanUp(), con.accept(p) Entered(), Finish()
	 * @param name the text to display on the butotn
	 * @param con the affect that the button should have on the currently set Presenter
	 * @param setFinish have the button call the Presenters Finish() method when clicked
	 * @param setEntered have the button call the Presenters Entered method when clicked
	 * @param cleanUp have the button call the Presenters CleanUp() method when clicked
	 * @return the new Info Window
	 */
	public InfoWindow addButton(String name, Consumer<Presenter> con, boolean setFinish, boolean setEntered, boolean cleanUp) {
		JButton jb = new JButton(name);
		BiConsumer<Presenter, MouseEvent> input = (p, e) -> {
			if (cleanUp)
				p.CleanUp();
			con.accept(p);
			if (setEntered)
				p.Entered();
			if (setFinish)
				p.Finish();
			
			
		};
		jb.addMouseListener(new MouseClickWithThreshold<Presenter>(CLICK_DIST_THRESH, input, p));
		buttons.add(jb);
		return this;
	}
	/**
	 * takes all the info that has been added so far and finalizes it
	 * @return the created info window
	 */
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
	/**
	 * Add a new button with the specified name that calls the presenters CleanUp() method and then calls its Canceled() method
	 * @param text the text on the button
	 * @return the new info window
	 */
	public InfoWindow addCancelButton(String text) {
		JButton jb = new JButton(text);
		BiConsumer<Presenter, MouseEvent> input = (p, e) -> {
			p.CleanUp();
			p.Canceled();	
			
			
		};
		jb.addMouseListener(new MouseClickWithThreshold<Presenter>(CLICK_DIST_THRESH, input, p));
		buttons.add(jb);
		return this;
	}
	/**
	 * Add a new button with the specified name that calls the presenters CleanUp() method and then calls its Entered() method
	 * @param text the text on the button
	 * @return the new info window
	 */
	public InfoWindow addEnterButton(String text) {
		JButton jb = new JButton(text);
		BiConsumer<Presenter, MouseEvent> input = (p, e) -> {
			p.CleanUp(); 
			p.Entered();
			
			
		};
		jb.addMouseListener(new MouseClickWithThreshold<Presenter>(CLICK_DIST_THRESH, input, p));
		buttons.add(jb);
		return this;
	}
	/**
	 * Add a new button with a name of "Enter" that calls the presenters CleanUp() method and then calls its Entered() method
	 * @param text the text on the button
	 * @return the new info window
	 */
	public InfoWindow addEnterButton() {
		return addEnterButton("Enter");
	}
	/**
	 * Add a new button with a name of "Cancel" that calls the presenters CleanUp() method and then calls its Canceled() method
	 * @param text the text on the button
	 * @return the new info window
	 */
	public InfoWindow addCancelButton() {
		return addCancelButton("Cancel");
	}
	
}
