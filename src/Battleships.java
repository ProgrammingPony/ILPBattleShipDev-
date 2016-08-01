import javax.swing.*;
/**
 * Creates and maintains the game frame (screen).
 * 
 * @author Albraa Al Nabulsi
 * @Modified March 29 2013
 */

public class Battleships {

	public static void main(String[] args) {
		new Battleships();	//Creates a new instance of Battleships (line 9)
	}
	
	/**
	 * @param
	 * @return Default constructor, creates an instance of the JFrame object, then sets its title, add all 
	 * 		   game elements to the frame, set the frame size, makes it visible and centralizes on screen, 
	 * 		   and finally ensures proper termination when frame is closed.
	 * @author Albraa Al Nabulsi
	 * @Modified March 29, 2013
	 */
	public Battleships() {
		JFrame frame = new JFrame();	//Creates a new frame.
		frame.setTitle("Battleships Game");	//Sets the title of the frame.
		frame.add(new Board());	//Creates a new instance of the Board class and displays it on the frame.
		frame.setSize(1216, 839);	//Sets the size of the frame, the dimension includes the edges of the frame.
		frame.setVisible(true);	//Displays the frame.
		frame.setLocationRelativeTo(null);	//Centers the frame.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	//Completely close the frame.
	}
}