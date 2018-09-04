package displayComponents;


import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
 
public class CardLayoutDemo implements ItemListener {
    JPanel cards; //a panel that uses CardLayout
    final static String BUTTONPANEL = "Card with JButtons";
    final static String TEXTPANEL = "Card with JTextField";
     
    public JPanel addComponentToPane() {
        //Put the JComboBox in a JPanel to get a nicer look.
        final JPanel comboBoxPane = new JPanel(); //use FlowLayout
        final String comboBoxItems[] = { BUTTONPANEL, TEXTPANEL };
        final JComboBox cb = new JComboBox(comboBoxItems);
        cb.setEditable(false);
        cb.addItemListener(this);
        comboBoxPane.add(cb);
         
        //Create the "cards".
        final JPanel card1 = new JPanel();
        card1.add(new JButton("Button 1"));
        card1.add(new JButton("Button 2"));
        card1.add(new JButton("Button 3"));
        card1.setBounds(0,0,600,600);
        final JPanel card2 = new JPanel();
        card2.add(new JTextField("TextField", 20));
        card2.setBounds(0,0,600,600);
        //Create the panel that contains the "cards".
        cards = new JPanel(new CardLayout());
        cards.setBounds(0, 0, 600, 600);
        cards.add(card1, BUTTONPANEL);
        cards.add(card2, TEXTPANEL);
        return cards;
        
    }
     
   @Override
	public void itemStateChanged(final ItemEvent evt) {
        final CardLayout cl = (CardLayout)(cards.getLayout());
        cl.show(cards, (String)evt.getItem());
    }
     
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        final JFrame frame = new JFrame("CardLayoutDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setBounds(0, 0, 600, 600);
        frame.setPreferredSize(new Dimension(600, 600));
        frame.setResizable(false);
        //Create and set up the content pane.
        final CardLayoutDemo demo = new CardLayoutDemo();
        frame.getLayeredPane().add(demo.addComponentToPane(), JLayeredPane.POPUP_LAYER);
        frame.revalidate();
        frame.repaint();
         
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
     
    public static void main(final String[] args) {
        /* Use an appropriate Look and Feel */
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (final UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (final IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (final InstantiationException ex) {
            ex.printStackTrace();
        } catch (final ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
         
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
}
