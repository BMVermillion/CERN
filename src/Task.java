import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class Task extends JPanel{

	private String[][] Data;			//Data imported from file
	private String[][] data_to_draw;	//Current data being drawn to screen
	private int[][] selected;			//2x2 array with the points of the selected box
	private int data_position = 0;		//Current row of data
	
	//Table formating variables
	private int width;
	private int height;
	private int row_height;
	private int column_width;
	private int column_num = 4;
	private int row_num = 10;
	
	//Font variables
	private Font font;
	private int font_size=5;
	private Color color;
	
	
	public Task(String[][] data, Dimension d, int rows) {
		//Set string data
		this.Data = data;
		row_num = rows;
		
		this.setPreferredSize(new Dimension((int)d.getHeight()-50, (int)d.getWidth()));
		super.setBackground(Color.BLACK);

		//Array to hold the data to draw to screen
		data_to_draw = new String[row_num+1][column_num];
		
		//Holds the locations of currently selected cells
		selected = new int[][] {{0,0},{0,0}};
		
		//Variables to help draw the table
		width = (int) d.getWidth();
		height = (int) d.getHeight()-50;
		
		System.out.println("(" + width + "," + height + ")");
		row_height = (height / (row_num+1));
		column_width = width / column_num;
		
		color = Color.YELLOW;
		
		//Adjusts the font to fit in the boxes
		setMaxFont();
	}
	
	
	//Expands the font size until it is greater than the column width
	//uses a string that is longer than the longest possible string (an ip address)
	private void setMaxFont() {		
		font = new Font(Font.SERIF, Font.BOLD, font_size);
		String s = "123456789123456789";
		
		while (this.getFontMetrics(font).stringWidth(s) < column_width-20 && this.getFontMetrics(font).getHeight() < row_height) {
			font_size += 2;
			font = new Font(Font.SERIF, Font.BOLD, font_size);
		}
		
	}
	
	
	//Shifts all of the data down a row from bottom to top, then
	// inserts new data at top
	public boolean incData() {
		
		if (data_position == Data.length)
			return false;
		
		for (int i=row_num-1; i>=0; i--) 
			data_to_draw[i+1] = data_to_draw[i];
			
		data_to_draw[0] = Data[data_position++];
		
		return true;
	}
	
	//Draws data to the screen
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
	
		
		g2.setFont(font);					//Sets font for text
		g2.setColor(Color.LIGHT_GRAY);		//Default line and text color
		g2.setStroke(new BasicStroke(5));	//Line width
		
		drawLines(g2);
		drawText(g2);
		drawSelected(g2);
		
		UserFeedback.drawFalse(g2);
		UserFeedback.drawHit(g2);
		UserFeedback.drawMiss(g2);
		
	}


	private void drawSelected(Graphics2D g2) {
		g2.setColor(color);
		
		for (int[] point : selected) {
			if (point[1] != 0)
				g2.drawRect(point[0]*column_width*2, point[1]*row_height, column_width*2, row_height);
		}
		
		g2.setColor(Color.WHITE);
	}
	

	private void drawLines(Graphics2D g2) {
		
		//Horizontal Lines
		for (int i=1; i<=row_num+1; i++) {
			int pos = i*row_height;
			g2.drawLine(0, pos, width, pos);
		}
		
		//Vertical Lines
		for (int i=1; i<column_num; i++) {
			int pos = i*column_width;
			g2.drawLine(pos, 0, pos, row_height*(row_num+1));
		}
	}

	//Draws the text onto the screen
	private void drawText(Graphics2D g2) {
		FontMetrics fm = g2.getFontMetrics();
		
		//Draws the top row with column 
		String[] title = {"Source Addr.", "Source Port", "Dest. Addr.", "Dest. Port" };
		for (int i=0; i<column_num; i++) 
			g2.drawString(title[i], column_width*i + (column_width - fm.stringWidth(title[i]))/2, row_height - (row_height - fm.getHeight())/2 - 5);
	
		//Fills in the boxes below the data
		for(int j=1; j<=row_num; j++) {
			for (int i=0; i<column_num; i++)  {

				if (data_to_draw[j][i] == null)
					continue;
				g2.drawString(data_to_draw[j][i],													//String
						column_width*i + (column_width - fm.stringWidth(data_to_draw[j][i]))/2,		//Column
						(row_height)*(j+1) - (row_height - fm.getHeight())/2 - 5);								//Row
	
			}
		}
	}
	
	public void drawBox(Graphics g, Color c, int x, int y) {
		g.setColor(c);
		g.drawRect(x*column_width, y*row_height, column_width*2, row_height);
	}
	
	public int[] getPostion() {
		return new int[] {data_position, Data.length};
	}
	
	public void clearSelected() {
		selected = new int[][] {{0,0},{0,0}};
	}
	
	public void setSelected(int x, int y) {
		
		for (int i=0; i<=row_num; i++) {
			if (i*row_height <= y && (i+1)*row_height > y) {
				for (int j=0; j<column_num/2; j++) {
					if (j*column_width*2 <= x && (j+1)*column_width*2 > x){
						x = j;
						y = i;
					}
				}	
			}
		}
		
		System.out.println(x + " " + y);
		
			
		
		if (selected[0][0] == x && selected[0][1] == y)
			selected[0] = new int[] {0,0};
		else if (selected[1][0] == x && selected[1][1] == y)
			selected[1] = new int[] {0,0};
		else {
			if (selected[0][0] != x || selected[1][0] != x)
				clearSelected();
			
			selected[1][0] = selected[0][0];
			selected[1][1] = selected[0][1];
		
			selected[0][0] = x;
			selected[0][1] = y;
		}
		
		this.repaint();
		
	}
	
	public int[][] getSelected() {
		return selected;
	}
	
	public void sleepAndClear() {
		color = Color.MAGENTA;
		paintComponent(this.getGraphics());
		
		try {
			Thread.sleep(750);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		color = Color.YELLOW;
		clearSelected();
				
	}


	
	public void setColor(Color color) {
		this.color = color;
	}

	
	
	
	
	
}
