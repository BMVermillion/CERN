import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

public class Notifications {

	public static void finished() {
		JOptionPane.showMessageDialog(new JFrame(), new JLabel("<html><center>All Done!</center> Press ok to exit</html>", JLabel.CENTER), "Finished", JOptionPane.PLAIN_MESSAGE);
		Serial.close();
	}
	
	public static void errorOutputFile() {
		JOptionPane.showMessageDialog(new JFrame("Error"), "Output file can not be empty.");
	}
	
	public static void errorInputFile() {
		JOptionPane.showMessageDialog(new JFrame("Error"), "Input file does not exist.");
	}
	
	public static void errorPort() {
		JOptionPane.showMessageDialog(new JFrame("Error"), "Could not connect to port.");

	}
	
	public static void errorWrite() {
		JOptionPane.showMessageDialog(new JFrame("Error"), "Could not write to port.");
	}
}
