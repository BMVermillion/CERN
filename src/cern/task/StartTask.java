package cern.task;

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


	private String inputFile;
	private String outputFile;
	private int scrollSpeed;
	private int rowNum;
	
	private KeyListener buttonBindings;
	private MouseListener cellClick;
	
	private boolean key_pressed = false;
	private long press_time = 0;
	
	
	private int counter = 0;
	
	public StartTask(String in, String out, int speed, int rows) {
		inputFile = in;
		outputFile = out;
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
		
		autoScroll();
		
	}
	
	private void autoScroll() {
		

		boolean first = true;
		int[] match = null;
		//Timing variables
		long start_time = 0;				
		
		//Variables for use in the while loop
		long relative_time = 0;
		ArrayList<String> output = new ArrayList<String>();
		String[] ps;
		int[] pi;
				
		/*
		 * Note: When outputting system time, the time a new line is put 
		 * on screen is the default output. This changes when a user presses
		 * the space bar. The time when a user presses the space bar is recorded
		 * instead of the new line time.
		 */
		
		try {
			Thread.sleep(scrollSpeed);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (Pair data : input_data) {
			
			
			//System.out.println("Current data: " + data.str[0]);
			
			draw.incData(data.str);
			draw.repaint();			//Repaint screen
		
			if (first) {
					start_time = System.currentTimeMillis();
					relative_time = 0;
					first = false;
					output.add("System start time, " + start_time);
					//continue;
			}
			else 
					relative_time = System.currentTimeMillis() - start_time;
			
			try {
				Thread.sleep(scrollSpeed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
				
			
			//Record output after waiting 
			//i = draw.getPostion()[0] - 2;
			ps = data.str;
			pi = data.in;
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
			
			
			
			
			if (pi[0] == 1) {
				match = check(counter);
				if (!key_pressed)
					UserFeedback.setMissLoc(match[2]*2, 2, match[2]*2, match[0]-match[1]+2);
			}

			key_pressed = false;
			counter++;
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
		//frame.setAlwaysOnTop(true);
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
				
				Serial.sendPack();
				if (arg0.getExtendedKeyCode() == KeyEvent.VK_SPACE) {
					key_pressed = true;
					press_time = System.currentTimeMillis();
						
					int[] hit = check(counter);
					if (hit != null) {
						UserFeedback.setHitLoc(hit[2]*2, 1, hit[2]*2, hit[0]-hit[1]+1);
						draw.repaint();
						}
					else {
						UserFeedback.setFalse();
						draw.repaint();
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
		
		frame.setContentPane(draw);
		
	}
	
	private int[] check(int pos) {
		
		Pair[] current_data;
		
		
		int cnt;
		if (pos < rowNum)
			cnt = pos;
		else
			cnt = rowNum;
		
		current_data = new Pair[cnt];
		//System.out.println("Pos: " + pos);
		for (int i=0; i<cnt; i++) {
			current_data[i] = input_data.get(pos-i);
			System.out.println(current_data[i].str[0]);
		}
		System.out.println("Done");
		
		for (int i=1; i<cnt; i++) {
			
			if (current_data[0].str[0].equals(current_data[i].str[0]) && current_data[0].str[1].equals(current_data[i].str[1])) {
				int[] r = new int[3];
				r[0] = pos;
				r[1] = pos-i;
				r[2] = 0;
				return r;
			}
			else if (current_data[0].str[2].equals(current_data[i].str[2]) && current_data[0].str[3].equals(current_data[i].str[3])) {
				int[] r = new int[3];
				r[0] = pos;
				r[1] = pos-i;
				r[2] = 1;
				return r;
			}
				
		}
		
		//System.out.println("Returning null");
		return null;
	}
}
