import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	private static JRadioButton radio;
	
	public static void main(String[] args) {
		
		settings = new JFrame("Settings");
		Container c = new Container();
		c.setLayout(new BoxLayout(c,BoxLayout.PAGE_AXIS));
		
		Container o = new Container();
		o.setLayout(new BoxLayout(o,BoxLayout.LINE_AXIS));
		JLabel out = new JLabel("Output file:");
		outText = new JTextField(25);
		
		o.add(out);
		o.add(outText);
		
		Container i = new Container();
		i.setLayout(new BoxLayout(i,BoxLayout.LINE_AXIS));
		JLabel in = new JLabel("Input file:");
		inText = new JTextField(25);
		inText.setText("HH_ABCD_TEXT.txt");
		
		i.add(in);
		i.add(inText);
		
		radio = new JRadioButton("Scrolling");
		
		JButton button = new JButton("OK");
		button.addActionListener(buttonPress);
		
		c.add(o);
		c.add(i);
		c.add(radio);
		c.add(button);
		
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
					radio.isSelected());
			
			settings.dispose();
			
			Thread t = new Thread(task);
			t.start();
			
		}
			
	};
	


}
