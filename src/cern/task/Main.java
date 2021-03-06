package cern.task;

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

/*
 * Contains the main thread of the program and creates a options menu
 * for settings before the main task begins.
 */
public class Main{
	public static JFrame settings;
	public static boolean scrolling = false;
	
	private static JTextField outText;
	private static JTextField inText;
	private static JTextField sText;
	private static JTextField pText;
	private static JRadioButton serial;
	private static JRadioButton binary;
	private static JRadioButton bar;
	
	private static final int boxWidth = 150;
	
	public static void main(String[] args) {
		
		settings = new JFrame("Settings");
		settings.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = new Container();
		c.setLayout(new BoxLayout(c,BoxLayout.PAGE_AXIS));
		
		
		Container footer = new Container();
		footer.setLayout(new BoxLayout(footer,BoxLayout.LINE_AXIS));
		
		serial = new JRadioButton("Serial");
		serial.setSelected(true);
		
		binary = new JRadioButton("Binary");
		bar = new JRadioButton("Bar");
		bar.setSelected(true);
		
		JButton button = new JButton("Start");
		button.addActionListener(buttonPress);
		
		
		footer.add(serial);
		footer.add(binary);
		footer.add(bar);
		footer.add(Box.createHorizontalGlue());
		footer.add(button);

		
		c.add( buildRow(outText = new JTextField(), "Output File:", "Participant_00.txt") );
		c.add( buildRow(inText = new JTextField(), "Input File:", "HH_ABCD_TEXT.txt") );
		c.add( buildRow(sText = new JTextField(), "Scroll Delay (ms):", "3250") );
		c.add( buildRow(pText = new JTextField(), "Port:", "COM1") );
		c.add(footer);
		
		settings.setContentPane(c);
		settings.pack();
		settings.setVisible(true);	
		
	}
	
	private static Container buildRow(JTextField text, String label, String box) {
		Container t = new Container();
		t.setLayout(new BoxLayout(t,BoxLayout.LINE_AXIS));
		JLabel lab = new JLabel(label);
		
		text.setPreferredSize(new Dimension(boxWidth,0));
		text.setMaximumSize(new Dimension(250,25));
		text.setText(box);
		
		t.add(lab);
		t.add(Box.createHorizontalGlue());
		t.add(text);
		return t;
	}
	
	
	private static ActionListener buttonPress = new ActionListener() {

		
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			
			if(serial.isSelected()) {
				try {
					if ( !Serial.connect(pText.getText()) ) {
						Notifications.errorPort();
						return;
					}
			
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Notifications.errorPort();
					return;
				}
			}
			
			
			
			
			if (outText.getText().equals("")) {
				Notifications.errorOutputFile();
				return;
			}
			
			if (FileIO.testInputFile(inText.getText())) {
				Notifications.errorInputFile();
				return;
			}
			
			
			
			StartTask task = new StartTask(
					inText.getText(), 
					outText.getText(),
					Integer.valueOf(sText.getText()),
					6,
					binary.isSelected(),
					bar.isSelected());
			
			settings.dispose();
			
			Thread t = new Thread(task);
			t.start();
			
		}
			
	};
	


}
