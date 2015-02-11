CERN (Cyber Error Related Negativity)

Setup:
1) Install Java (https://www.java.com)
2) Install RTXT if using serial
	-Instuctions are in the lib folder with the necesary files. The library files are for a 64-bit computer. If you are running on a 32-bit computer, got to the RTXT webpage and follow te instalation isntructions.
3) Put the input file in the same folder as CERN.jar, then execute

RTXT: 		rtxt.qbang.org
RTXT-64bit: 	http://mfizz.com/oss/rxtx-for-java

Note: Serial libraries are only provided for Windows. Go to the RTXT site for library file on other systems.

Use:
To start the program double click the CERN.jar file.
This will bring up a small outions menu
-Output File: The name of the file you want the program to save results to
-Input File: The name of the file that contains all the information the program will display
-Scroll Delay(ms): How fast the contents on the screen will scroll, in milliseconds.
-Port: The COM port of the serial connection
-Rows: how many rows of information to display (6 default, may not work properly with more)
-Serial: Toggle to turn serial on or off
-Binary: Toggle to turn binary selection on and off;
-Bar: Toggle to turn the timing bar on and off;
-Start: starts the program

Running the program:
The program consists of 7 rows and 4 columns. The first row is the label for each column which are Source Addr, Port, Dest Addr, Port. The following rows contin the information loaded from the input file, that will scroll down after each scroll delay. The objective is to find matches of addresses and ports on either the left two columns (Source) or the right two columns (Destination). There is no matching between the source and destination. When there is a match on the screen, the user must press space bar, which will cause the match to flash green. When you miss a match it will flash red and if you press the space bar when there is no match the top bar will flsh red. The Binary option adds a few extra options to the program. Instead of the space bar you have to press up if there is a match and down if none. Not choosing a option will automatically make it wrong. The binary portion of the program also has a timing bar at the bottom of the screen.
