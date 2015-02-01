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
	private int scrollSpeed;
	private int rowNum;
	
	private KeyListener buttonBindings;
	private MouseListener cellClick;
	private static ActionListener buttonPress;
	
	private boolean key_pressed = false;
	private long press_time = 0;
	
	
	private int counter = 0;
	
	public StartTask(String in, String out, int speed, int rows, boolean boo) {
		inputFile = in;
		outputFile = out;
		scroll = boo;
		scrollSpeed = speed;
		rowNum = rows;
		
		System.out.println(rows);
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
		
		UserFeedback.setTask(draw);
		Thread t = new Thread(new UserFeedback());
		t.start();
		
		if (!scroll)
			autoScroll();
		
	}
	
	private void autoScroll() {
		

		
		boolean match = false;
		int[] match_val = null;
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
				Thread.sleep(scrollSpeed);
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
			
			
			if (match && !key_pressed) {
				UserFeedback.setMissLoc(match_val[2]*2, 2, match_val[2]*2, match_val[0]-match_val[1]+2);
				match = false;
			}
			
			if (pi[0] == 1) {
				match = true;
				match_val = check(1);
			}

			key_pressed = false;
		}
		
		
		//Records all output to a file
		FileIO.outputToFile(output, outputFile);
		
		Notifications.finished();
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
				if (scroll) {
					if (arg0.getExtendedKeyCode() == KeyEvent.VK_ESCAPE)
						buttonPress.actionPerformed(new ActionEvent(buttonBindings, ActionEvent.ACTION_FIRST, "Clear"));
				
					if (arg0.getExtendedKeyCode() == KeyEvent.VK_SPACE) {
						buttonPress.actionPerformed(new ActionEvent(buttonBindings, ActionEvent.ACTION_FIRST, "Scroll"));
						press_time  = System.currentTimeMillis();
						key_pressed = true;
					}
				}
				else {
					if (arg0.getExtendedKeyCode() == KeyEvent.VK_SPACE) {
						key_pressed = true;
						press_time = System.currentTimeMillis();
						
						int[] hit = check(2);
						if (hit != null) {
							System.out.println(hit[0] + " " + hit[1] + " " + hit[2]);
							UserFeedback.setHitLoc(hit[2]*2, 1, hit[2]*2, hit[0]-hit[1]+1);
							draw.repaint();
						}
						else {
							UserFeedback.setFalse();
							draw.repaint();
						}
							/////draw.setColor(Color.RED);
						
						/////(new Thread(draw)).start();
					}
						
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
		
		if (!scroll)
			return;
		
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
	
						Notifications.finished();
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
			Notifications.errorInputFile();
			return null;
		}
		return FileIO.getStrings(input_data);
	}
	
	//Makes the pane that will do the drawing
	private void setDrawPane(String[][] s) {
		draw = new Task(s, Toolkit.getDefaultToolkit().getScreenSize(), rowNum);
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
	
	private int[] check(int offset) {
		int pos = draw.getPostion()[0]-offset;
		
		Pair current;
		if (pos < 0)
			return null;
		else
			current = input_data.get(pos);
		
		int cnt;
		if (pos < rowNum)
			cnt = pos;
		else
			cnt = rowNum;
		
		for (int i=1; i<=cnt; i++) {
			Pair back = input_data.get(pos-i);
			System.out.print("Compairing " + current.str[0] + " p" + current.str[1] + " with " + back.str[0] + " p" + back.str[1] );
			System.out.println("Compairing " + current.str[2] + " p" + current.str[3] + " with " + back.str[2] + " p" + back.str[3] );

			if (current.str[0].equals(back.str[0]) && current.str[1].equals(back.str[1])) {
				int[] r = new int[3];
				r[0] = pos;
				r[1] = pos-i;
				r[2] = 0;
				return r;
			}
			else if (current.str[2].equals(back.str[2]) && current.str[3].equals(back.str[3])) {
				int[] r = new int[3];
				r[0] = pos;
				r[1] = pos-i;
				r[2] = 1;
				return r;
			}
				
		}
		
		System.out.println("Returning null");
		return null;
	}
}
