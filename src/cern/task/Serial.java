package cern.task;

import gnu.io.*;
import java.io.*;

/*
 * The Serial class handles all of the serial communication for the program. Everything is
 * static, so first call connect() then you can use sendPack(). sendPack() will not throw a error
 * if you don't connect first, it just won't send anything. Please close when you are done :)
 */
public class Serial {

	private static SerialPort serialPort;
	private static OutputStream out;
	private final static byte[] pack = { 0x56, 0x5A, 0x00, 0x01, 0x0B, 0x01, (byte) 0xF2 };
	
	public static boolean connect ( String port ) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(port);
        
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
            return false;
        }
        else
        {
            CommPort commPort = portIdentifier.open("CERN",2000);
            
            if ( commPort instanceof SerialPort )
            {
                serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(57600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                out = serialPort.getOutputStream();

                return true;
            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
                return false;
            }
        }     
    }
	
	public static void sendPack() {
		if (out == null)
			return;
		
		try {
			out.write(pack, 0, pack.length);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Notifications.errorWrite();
		}
	}
	
	public static void close() {
		if (serialPort != null)
			serialPort.close();
	}
}
