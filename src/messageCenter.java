import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;


import javax.swing.JComponent;



public class messageCenter extends JComponent 
{
	
	public Graphics startGameMessage(Graphics graphics)
	{
		Graphics2D g2 = (Graphics2D) graphics;
		
		g2.setFont(new Font("serif", Font.BOLD, 20));
		
		g2.setColor(Color.BLACK);
		
		g2.drawString("Start Game: Sink Enemy Ships !!" , 300, 100);
		
		return g2;
		
	}
		
	public Graphics PlayerOneShipPlace(Graphics graphics)
	{
		Graphics2D g2 = (Graphics2D) graphics;
		
		g2.setFont(new Font("serif", Font.BOLD, 18));
		
		g2.setColor(Color.BLACK);
		
		g2.drawString("Player 1 plesase continue to place all your ships on game board" , 300, 100);
		
		return g2;
		
	}
	
	public Graphics PlayerOneShipPlaceComplete(Graphics graphics)
	{
		Graphics2D g2 = (Graphics2D) graphics;
		
		g2.setFont(new Font("serif", Font.BOLD, 18));
		
		g2.setColor(Color.BLACK);
		
		g2.drawString("Player 1 has placed all ships" , 300, 100);
		
		return g2;
		
	}
	public Graphics PlayerTwoShipPlace(Graphics graphics)
	{
		Graphics2D g2 = (Graphics2D) graphics;
		
		g2.setFont(new Font("serif", Font.BOLD, 18));
		
		g2.setColor(Color.BLACK);
		
		g2.drawString("Player 2 please continue to place all your ships on gameboard" , 300, 100);
		
		return g2;
		
	}
	
	public Graphics PlayerTwoShipPlaceComplete(Graphics graphics)
	{
		Graphics2D g2 = (Graphics2D) graphics;
		
		g2.setFont(new Font("serif", Font.BOLD, 18));
		
		g2.setColor(Color.BLACK);
		
		g2.drawString("Player 2 has placed all ships" , 300, 100);
		
		return g2;
		
	}
	
	public Graphics PlayerOneOutOfBounds(Graphics graphics)
	{
			
		Graphics2D g2 = (Graphics2D) graphics;
		
		g2.setFont(new Font("serif", Font.BOLD, 18));
		
		g2.setColor(Color.BLACK);
		
		g2.drawString("Player 1 cannot place ship here; out of bounds!" , 300, 100);
		
		//g2.dispose();
		return g2;
	}
	
	public Graphics PlayerTwoOutOfBounds(Graphics graphics)
	{
			
		Graphics2D g2 = (Graphics2D) graphics;
		
		g2.setFont(new Font("serif", Font.BOLD, 18));
		
		g2.setColor(Color.BLACK);
		
		g2.drawString("Player 2 cannot place ship here; out of bounds!" , 300, 100);
		
		//g2.dispose();
		return g2;
	}
	
	
	public Graphics PlayerOneHitMessage(Graphics graphics)
	{
			
		Graphics2D g2 = (Graphics2D) graphics;
		
		g2.setFont(new Font("serif", Font.BOLD, 18));
		
		g2.setColor(Color.BLACK);
		
		g2.drawString("Player 1 has hit the opponents ship" , 300, 100);
		
		//g2.dispose();
		return g2;
	}
	
	public Graphics PlayerOneMissMessage(Graphics graphics)
	{
		
		Graphics2D g2 = (Graphics2D) graphics;
		
		g2.setFont(new Font("serif", Font.BOLD, 18));
		
		g2.setColor(Color.BLACK);
			
		g2.drawString("Player 1 has missed the opponents ship" , 300, 100);
		
		//g2.dispose();
		
		return g2;
	}
	
	public Graphics PlayerTwoHitMessage(Graphics graphics)
	{
		
					
		Graphics2D g2 = (Graphics2D) graphics;
		
		g2.setFont(new Font("serif", Font.BOLD, 18));
		
		g2.setColor(Color.BLACK);
		
		g2.drawString("Player 2 has hit the opponents ship" , 300, 100);
		
		//g2.dispose();
		return g2;
	}
	
	public Graphics PlayerTwoMissMessage(Graphics graphics)
	{
			
					
		Graphics2D g2 = (Graphics2D) graphics;
		
		g2.setFont(new Font("serif", Font.BOLD, 18));
		
		g2.setColor(Color.BLACK);
		
		g2.drawString("Player 2 has missed the opponents ship" , 300, 100);
		
		//g2.dispose();
		return g2;
	}
	
	public Graphics ComputerHitMessage(Graphics graphics)
	{
		
					
		Graphics2D g2 = (Graphics2D) graphics;
		
		g2.setFont(new Font("serif", Font.BOLD, 18));
		
		g2.setColor(Color.BLACK);
		
		g2.drawString("Computer has hit your ship" , 300, 100);
		
		//g2.dispose();
		return g2;
		
	}
	
	public Graphics ComputerMissMessage(Graphics graphics)
	{
								
		Graphics2D g2 = (Graphics2D) graphics;
		
		g2.setFont(new Font("serif", Font.BOLD, 18));
		
		g2.setColor(Color.BLACK);
		
		g2.drawString("Computer has missed your ship" , 300, 100);
		
		//g2.dispose();
		return g2;
	}
}
