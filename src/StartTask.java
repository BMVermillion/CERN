import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;


public class StartTask implements Runnable{

	private JFrame frame;
	private ArrayList<Pair> input_data;
	private ArrayList<String> output_data;
	private Task draw;
	private JLabel info;


	private String inputFile;
	private String outputFile;
	private boolean scroll = false;
	
	private KeyListener buttonBindings;
	private MouseListener cellClick;
	private static ActionListener buttonPress;
	
	private boolean key_pressed = false;
	private long press_time = 0;
	
	
	private int counter = 0;
	
	public StartTask(String in, String out, boolean boo) {
		inputFile = in;
		outputFile = out;
		scroll = boo;
		
		output_data = new ArrayList<String>();
	}
	
	@Override
	public void run() {

		makeListeners();
		setFrame();
		
		String[][] s = readInputFile();		
				
		setDrawPane(s);
		
		frame.pack();
		frame.setVisible(true);
				
		if (!scroll)
			autoScroll();
		
	}
	
	
	private void autoScroll() {
		

		//Timing variables
		long start_time = 0;				
		
		//Variables for use in the while loop
		long relative_time;
		ArrayList<String> output = new ArrayList<String>();
		String[] ps;
		int[] pi;
		int i;
				
		/*
		 * Note: When outputting system time, the time a new line is put 
		 * on screen is the default output. This changes when a user presses
		 * the space bar. The time when a user presses the space bar is recorded
		 * instead of the new line time.
		 */
		while ( draw.incData() ) {
			draw.repaint();			//Repaint screen
		
			if (start_time == 0) {
				start_time = System.currentTimeMillis();
				relative_time = 0;
			
			}
			else {
				relative_time = System.currentTimeMillis() - start_time;
			}
			
				try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//Record output after waiting 
			i = draw.getPostion()[0] - 1;
			ps = input_data.get(i).str;
			pi = input_data.get(i).in;
			output.add(
					ps[0] + ", " +
					ps[1] + ", " +
					ps[2] + ", " +
					ps[3] + ", " +
					pi[0] + ", " +
					((key_pressed) ? 1 : 0) + ", " +
					((key_pressed) ? press_time - start_time : relative_time) + ", " +
					pi[1]					
					);
		}
		
		
		//Records all output to a file
		FileIO.outputToFile(output);
		
		JOptionPane.showMessageDialog(frame, new JLabel("<html><center>All Done!</center> Press ok to exit</html>", JLabel.CENTER), "Finished", JOptionPane.PLAIN_MESSAGE);
		System.exit(0);
	}
	
	//Sets default options for the Window
	private void setFrame() {
		
		frame = new JFrame("Viglence Test");
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setAlwaysOnTop(true);
		frame.setUndecorated(true);
		frame.addMouseListener(cellClick);
		frame.addKeyListener(buttonBindings);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBackground(Color.BLACK);
	}
	
	private void makeListeners() {
		
		buttonBindings = new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {			
				if (arg0.getExtendedKeyCode() == KeyEvent.VK_ESCAPE)
					buttonPress.actionPerformed(new ActionEvent(buttonBindings, ActionEvent.ACTION_FIRST, "Clear"));
				
				if (arg0.getExtendedKeyCode() == KeyEvent.VK_SPACE) {
					buttonPress.actionPerformed(new ActionEvent(buttonBindings, ActionEvent.ACTION_FIRST, "Scroll"));
					press_time  = System.currentTimeMillis();
					key_pressed = true;
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		cellClick = new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				draw.setSelected(arg0.getX(), arg0.getY());
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		buttonPress = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				switch (arg0.getActionCommand()) {
				case "Scroll":
					//Set line number
					info.setText("Line " + draw.getPostion()[0] + " of " + draw.getPostion()[1] + " (" + String.format("%.1f",((float)draw.getPostion()[0]/(float)draw.getPostion()[1]*100.)) + "%)");
					
					//Increment data, if on the
					if (!draw.incData()) {
	
						JOptionPane.showMessageDialog(frame, new JLabel("<html><center>All Done!</center> Press ok to exit</html>", JLabel.CENTER), "Finished", JOptionPane.PLAIN_MESSAGE);
						System.exit(0);
					}
					
					counter++;
					break;
					
				case "Select":
					int current = input_data.get(counter).in[0];
					int [][] selected = draw.getSelected();
					
					System.out.println("Current data: " + input_data.get(counter).str[0] + ", "+ input_data.get(counter).str[1] + ", "+ input_data.get(counter).str[2] + ", "+ input_data.get(counter).str[3]);
					System.out.println("Selected data: " + selected[0][0] + ", " + selected[0][1] + " and " + selected[1][0] + ", " + selected[1][1]);
					//if (input_data.get(current));
					
					draw.sleepAndClear();
					
					break;
					
				case "Clear":
					draw.clearSelected();
					break;
					
				default:
					System.out.println("Something went wrong...");
				}
				
				output_data.add(arg0.getActionCommand() + ", ");
				
				draw.repaint();
				
			}
				
		};
	
	
	}
	
	private String[][] readInputFile() {
		//Read in from file
		input_data = FileIO.getInputFile(inputFile);
		if (input_data == null) {
			JOptionPane.showMessageDialog(new JFrame("Error"), "Input file does not exist.");
			return null;
		}
		return FileIO.getStrings(input_data);
	}
	
	//Makes the pane that will do the drawing
	private void setDrawPane(String[][] s) {
		draw = new Task(s, Toolkit.getDefaultToolkit().getScreenSize(), !scroll);
		//draw = new Task(s, new Dimension(gd.getDisplayMode().getHeight(), gd.getDisplayMode().getHeight()) );
		
		
		if (!scroll) {
			frame.setContentPane(draw);
			return;
		}
		
		
		info = new JLabel("Line " + draw.getPostion()[0] + " of " + draw.getPostion()[1] + " (" + (draw.getPostion()[0]/draw.getPostion()[1]*100) + "%)", JLabel.RIGHT);
		info.setForeground(Color.WHITE);
		info.setBackground(Color.BLACK);
		info.setOpaque(true);
		info.setEnabled(false);
		info.setPreferredSize(new Dimension(200,50));
		/*
		JButton button = new JButton("Scroll");
		button.addActionListener(buttonPress);
		button.setPreferredSize(new Dimension(100,50));
		button.setBackground(Color.BLACK);
		
		JLabel notice = new JLabel("Press to scroll down");
		notice.setBackground(Color.BLACK);
		notice.setForeground(Color.WHITE);
		notice.setPreferredSize(new Dimension(200,50));
		*/
		
		//Button to scroll the text 
		JButton scroll = new JButton("Scroll");
		scroll.addActionListener(buttonPress);
		scroll.setPreferredSize(new Dimension(100,50));
		scroll.setBackground(Color.BLACK);
		scroll.setForeground(Color.WHITE);
		
		//Button to select a ip match
		JButton select = new JButton("Select");
		select.addActionListener(buttonPress);
		select.setPreferredSize(new Dimension(100,50));
		select.setBackground(Color.BLACK);
		select.setForeground(Color.WHITE);
		
		//Clears currently selected boxes
		JButton clear = new JButton("Clear");
		clear.addActionListener(buttonPress);
		clear.setPreferredSize(new Dimension(100,50));
		clear.setBackground(Color.BLACK);
		clear.setForeground(Color.WHITE);
		
		Container footer = new Container();		
		footer.setLayout(new FlowLayout(FlowLayout.CENTER, 75, 0));
		footer.add(info);
		footer.add(scroll);
		footer.add(select);
		footer.add(clear);
		//footer.add(notice);
		
		Container C = new Container();
		C.setBackground(Color.BLACK);
		C.setLayout(new BoxLayout(C, BoxLayout.PAGE_AXIS));
		C.add(draw);
		C.add(footer);
		
		frame.setContentPane(C);
		

	}
	
	/*
	//Records time on key press
	@SuppressWarnings("serial")
	private static Action keyPress = new AbstractAction() {

		public void actionPerformed(ActionEvent e) {
			System.out.println("Test" + e.getActionCommand());
		}	
	};
	
	*/
	
	/*
	private static Dimension getScreenSize() {
		int width = 0;
		int height = 0;
		
		//size of the screen
		width = Toolkit.getDefaultToolkit().getScreenSize().width;
		height = Toolkit.getDefaultToolkit().getScreenSize().height;

		//height of the task bar
		Insets toolBar = Toolkit.getDefaultToolkit().getScreenInsets(frame.getGraphicsConfiguration());
		
		System.out.println("(" + width + "," + height + ")");
		System.out.println("Bottom = " + toolBar.bottom);
		System.out.println("Top = " + toolBar.top);
		System.out.println("Left = " + toolBar.left);
		System.out.println("Right = " + toolBar.right);
		if (toolBar.bottom != 0)
			height -= toolBar.bottom;
		if (toolBar.top != 0)
			height += toolBar.top;
		if (toolBar.left != 0)
			width += toolBar.left;
		if (toolBar.right != 0)
			width += toolBar.right;
		
		return new Dimension(width, height);

	}
	*/
	
	


}
