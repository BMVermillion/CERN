import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;


public class Main{
	public static JFrame settings;
	public static boolean scrolling = false;
	
	private static JTextField outText;
	private static JTextField inText;
	private static JTextField sText;
	private static JTextField rText;
	private static JRadioButton radio;
	
	private static final int boxWidth = 150;
	
	public static void main(String[] args) {
		
		settings = new JFrame("Settings");
		Container c = new Container();
		c.setLayout(new BoxLayout(c,BoxLayout.PAGE_AXIS));
		
		Container o = new Container();
		o.setLayout(new BoxLayout(o,BoxLayout.LINE_AXIS));
		JLabel out = new JLabel("Output file:");
		
		outText = new JTextField();
		outText.setPreferredSize(new Dimension(boxWidth,0));
		outText.setMaximumSize(new Dimension(250,25));
		
		o.add(out);
		o.add(Box.createHorizontalGlue());
		o.add(outText);
		
		Container i = new Container();
		i.setLayout(new BoxLayout(i,BoxLayout.LINE_AXIS));
		JLabel in = new JLabel("Input file:");
		
		inText = new JTextField();
		inText.setPreferredSize(new Dimension(boxWidth,0));
		inText.setMaximumSize(new Dimension(250,25));
		inText.setText("HH_ABCD_TEXT.txt");
		
		i.add(in);
		i.add(Box.createHorizontalGlue());
		i.add(inText);
		
		Container s = new Container();
		s.setLayout(new BoxLayout(s,BoxLayout.LINE_AXIS));
		JLabel speed = new JLabel("Autoscroll speed (ms):");
		
		sText = new JTextField();
		sText.setPreferredSize(new Dimension(boxWidth,0));
		sText.setMaximumSize(new Dimension(250,25));
		sText.setText("2000");
		
		s.add(speed);
		s.add(Box.createHorizontalGlue());
		s.add(sText);
		
		Container r = new Container();
		r.setLayout(new BoxLayout(r,BoxLayout.LINE_AXIS));
		JLabel row = new JLabel("Number of rows:");
		
		rText = new JTextField();
		rText.setPreferredSize(new Dimension(boxWidth,0));
		rText.setMaximumSize(new Dimension(250,25));
		rText.setText("6");
		
		r.add(row);
		r.add(Box.createHorizontalGlue());
		r.add(rText);
		
		Container footer = new Container();
		footer.setLayout(new BoxLayout(footer,BoxLayout.LINE_AXIS));
		
		radio = new JRadioButton("Scrolling");
		
		JButton button = new JButton("OK");
		button.addActionListener(buttonPress);
		
		footer.add(radio);
		footer.add(Box.createHorizontalGlue());
		footer.add(button);
		
		c.add(o);
		c.add(i);
		c.add(s);
		c.add(r);
		c.add(footer);
		
		settings.setContentPane(c);
		settings.pack();
		settings.setVisible(true);	
	}
	
	
	private static ActionListener buttonPress = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			StartTask task = new StartTask(
					inText.getText(), 
					outText.getText(),
					Integer.valueOf(sText.getText()),
					Integer.valueOf(rText.getText()),
					radio.isSelected());
			
			settings.dispose();
			
			Thread t = new Thread(task);
			t.start();
			
		}
			
	};
	


}
