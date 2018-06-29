package gui.guiComponents;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;

//from  w  w  w.j av  a2  s.c o m
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;

public class Main {
  public static void main(String args[]) {
    JPanel container = new ScrollablePanel();
    container.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
   // container.setPreferredSize(new Dimension(50, 50));
    container.setSize(50, 50);
    for (int i = 0; i < 20; ++i) {
      JPanel p = new JPanel();
      p.setPreferredSize(new Dimension(50, 50));
      p.add(new JLabel("" + i));
      container.add(p);
    }

    JScrollPane scroll = new JScrollPane(container);
    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    JFrame f = new JFrame();
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.getContentPane().add(scroll);
    f.pack();
    f.setSize(250, 300);
    f.setVisible(true);
  }
}
class ScrollablePanel extends JPanel implements Scrollable {
  public Dimension getPreferredSize() {
    return getPreferredScrollableViewportSize();
  }

  public Dimension getPreferredScrollableViewportSize() {
    if (getParent() == null)
      return getSize();
    Dimension d = getParent().getSize();
    return d;
  }
  public int getScrollableBlockIncrement(Rectangle visibleRect,
      int orientation, int direction) {
    return 50;
  }
  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation,
      int direction) {
    return 10;
  }
  public boolean getScrollableTracksViewportHeight() {
    return false;
  }
  public boolean getScrollableTracksViewportWidth() {
    return getParent() != null ? getParent().getSize().width > getPreferredSize().width
        : true;
  }
}