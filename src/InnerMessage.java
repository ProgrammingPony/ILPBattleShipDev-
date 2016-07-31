import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;

public class InnerMessage extends JInternalFrame {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			
			
			public void run() {
				
				try 
				{
					
					JLabel label = new JLabel("This is it", JLabel.TOP);
					label.setAlignmentX(0);
					label.setAlignmentY(0);
					InnerMessage innerFrame = new InnerMessage();
					innerFrame.setVisible(true);
					innerFrame.add(label);
					
					
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public InnerMessage() {
		setBounds(50, 500, 600, 60);
		
	}
	
	
}
