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

/*
 * This is where the task starts after the options menu closes. It reads in data, initializes other classes
 * handles the main loop of the program, records data, then outputs data.
 */
public class StartTask implements Runnable{
	//Main window
	private JFrame frame;
	//Draw frame
	private Task draw;
	
	//Data structs for input and output
	private ArrayList<Pair> input_data;
	private ArrayList<String> output_data;
	
	//Important variables
	private String inputFile;
	private String outputFile;
	private int scrollSpeed;
	private int rowNum;
	
	//Listeners
	private KeyListener buttonBindings;
	private MouseListener cellClick;
	
	//Variables for timing and such
	private int key_pressed = 0;
	private long press_time = 0;
	private boolean binary;
	private boolean bar;
	private int counter = 0;
	
	public StartTask(String in, String out, int speed, int rows, boolean isBinary, boolean bar) {
		inputFile = in;
		outputFile = out;
		scrollSpeed = speed;
		rowNum = rows;
		binary = isBinary;
		this.bar = bar;
		
		output_data = new ArrayList<String>();
	}
	
	@Override
	//Thread that runs the task
	public void run() {

		/*init*/
		makeListeners();
		setFrame();
		
		String[][] s = readInputFile();		
		
		setDrawPane(s);
		////////
		
		
		frame.pack();
		frame.setVisible(true);
		
		UserFeedback.setTask(draw);
		UserFeedback.setBarStart(scrollSpeed);
		
		Thread t = new Thread(new UserFeedback());
		t.start();
		
		Notifications.ready();
		
		
		if (binary) {
			
			autoScrollBinary();
		}
		else
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
				
		
		
		Serial.sendPack();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		for (Pair data : input_data) {
			
			draw.incData(data.str);
			draw.repaint();			
		
			UserFeedback.startBarTime();
			
			if (first) {
				
					start_time = System.nanoTime()/10000000;
					Serial.sendPack();
					
					relative_time = 0;
					first = false;
					output.add("Participant Number: " + outputFile);
					output.add("Timing interval, " + scrollSpeed);
			}
			else 
					relative_time = System.nanoTime()/10000000 - start_time;
			
			
			try {
				Thread.sleep(scrollSpeed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
				
			
			//Record output
			ps = data.str;
			pi = data.in;
			output.add(
					ps[0] + ", " +
					ps[1] + ", " +
					ps[2] + ", " +
					ps[3] + ", " +
					pi[0] + ", " +
					((key_pressed == 1) ? 1 : 0) + ", " +
					relative_time + ", " +
					((key_pressed == 1) ? press_time - start_time - relative_time: 0) + ", " +
					pi[1]					
					);
			
			
			
			
			if (pi[0] == 1) {
				match = check(counter);
				if (key_pressed != 1)
					UserFeedback.setMissLoc(match[2]*2, 2, match[2]*2, match[0]-match[1]+2);
			}

			key_pressed = 0;
			counter++;
			
			
		}
		
		
		//Records all output to a file
		FileIO.outputToFile(output, outputFile);
		
		Notifications.finished();
		System.exit(0);
	}
	
	private void autoScrollBinary() {
		

		boolean first = true;
		int[] match = null;
		
		long start_time = 0;				
		
		//Variables for use in the while loop
		long relative_time = 0;
		ArrayList<String> output = new ArrayList<String>();
		String[] ps;
		int[] pi;
		
		Serial.sendPack();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for (Pair data : input_data) {
			
			UserFeedback.startBarTime();
			
			draw.incData(data.str);
			draw.repaint();			//Repaint screen
		
			if (first) {
					start_time = System.nanoTime()/10000000;
					Serial.sendPack();
					
					relative_time = 0;
					first = false;
					output.add("Participant Number: " + outputFile);
					output.add("Timing interval, " + scrollSpeed);
			}
			else 
					relative_time = System.nanoTime()/10000000 - start_time;
			
			try {
				Thread.sleep(scrollSpeed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
				
			
			if (key_pressed == 0)
				UserFeedback.setFalse();
			
			ps = data.str;
			pi = data.in;
			output.add(
					ps[0] + ", " +
					ps[1] + ", " +
					ps[2] + ", " +
					ps[3] + ", " +
					pi[0] + ", " +
					key_pressed + ", " +
					relative_time + ", " +
					((key_pressed == 1) ? press_time - start_time - relative_time: 0) + ", " + 
					pi[1]					
					);
			
			
			
			
			if (pi[0] == 1) {
				match = check(counter);
				if (key_pressed != 1)
					UserFeedback.setMissLoc(match[2]*2, 2, match[2]*2, match[0]-match[1]+2);
			}

			key_pressed = 0;
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
				
				if (key_pressed != 0)
					return;
				
				Serial.sendPack();
				press_time = System.nanoTime()/1000000;
				
				if (!binary && arg0.getExtendedKeyCode() == KeyEvent.VK_SPACE) {
					key_pressed = 1;
						
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
				else if (binary) {
					if (arg0.getExtendedKeyCode() == KeyEvent.VK_UP) {
						key_pressed = 1;
						
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
					else if (arg0.getExtendedKeyCode() == KeyEvent.VK_DOWN) {
						key_pressed = 2;
						
						int[] hit = check(counter);
						if (hit != null) {
							UserFeedback.setMissLoc(hit[2]*2, 1, hit[2]*2, hit[0]-hit[1]+1);
							draw.repaint();
						}
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
		draw.setBinary(bar);
		
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
		for (int i=0; i<cnt; i++) 
			current_data[i] = input_data.get(pos-i);

		
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
		
		return null;
	}
}
