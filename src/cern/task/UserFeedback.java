package cern.task;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;


public class UserFeedback implements Runnable {

	private static int hitTime = 0;
	private static int missTime = 0;
	private static int falseTime = 0;
	
	private static final int wait = 25;
	private static final int time = 250;
	
	private static int[][] hitLoc;
	private static int[][] missLoc;
	
	private static Task t;
	public void run() {
		
		while(true) {
		
			try {
				Thread.sleep(wait);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			if (hitTime > 0)
				hitTime -= wait;
		
			if (missTime > 0)
				missTime -= wait;
		
			if (falseTime > 0)
				falseTime -= wait;
		
			t.repaint();
		}
	}
	
	public static void setHitLoc(int x1, int x2, int y1, int y2) {
		hitLoc = new int[2][2];
		hitLoc[0][0] = x1;
		hitLoc[0][1] = x2;
		hitLoc[1][0] = y1;
		hitLoc[1][1] = y2;
		
		hitTime = time;
		
	}
	
	public static void drawHit(Graphics g) {
		if (hitTime <= 0)
			return;
		
		for (int[] i : hitLoc)
			t.drawBox(g, Color.GREEN, i[0], i[1]);
		
	}
	
	public static void setMissLoc(int x1, int x2, int y1, int y2) {
		missLoc = new int[2][2];
		missLoc[0][0] = x1;
		missLoc[0][1] = x2;
		missLoc[1][0] = y1;
		missLoc[1][1] = y2;
		
		missTime = time;
		
	}
	
	public static void drawMiss(Graphics g) {
		if (missTime <= 0)
			return;
		
		for (int[] i : missLoc)
			t.drawBox(g, Color.RED, i[0], i[1]);
		
	}
	
	public static void setFalse() {
		falseTime = time;
		
	}
	
	public static void drawFalse(Graphics g) {
		if (falseTime <= 0)
			return;
		
		
		t.drawBox(g, Color.RED, 0, 0);
		t.drawBox(g, Color.RED, 2, 0);
		
	}
	
	public static void setTask (Task task) {
		t = task;
	}
}
