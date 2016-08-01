import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 * Handles all graphics and user action (GUI).
 * 
 * @author Albraa Al Nabulsi
 * @Modified April 2, 2013
 * Images created by Albraa Al Nabulsi
 */

public class Board extends JPanel implements ActionListener, MouseListener, KeyListener, MouseMotionListener {

	private Timer timer;	//Refresh rate frequency.
	private int imageSwitch = 0;	//Switch that controls which menu is up, 0 is start, 1 is game, 2 is rules.
	private int shipSwitch = 7;		//Switch that controls which ship, from carrier at 7 to last sub at 1.
	private int lightSwitch = 0;	//Switch that controls which light is set, 0 is off, 1 is green, 2 is yellow, 3 is red.
	private int x, storedX, flashX;			//x coordinate determined by mouse, and stored coordinate indicating the latest box in the array (A1, B3, etc)
	private int y, storedY, flashY;			//Same as above but for y.
	private int clickCount = 0;		//Counts the number of clicks.
	
	//Communication with Games Module
	private GameMode currentMode = null;
	private Player playerPlacingShips = Player.Player1;
	private boolean allShipsPlaced = false;
	
	private BattleGrid player1Board = new BattleGrid();
	private BattleGrid player2Board = new BattleGrid();
	
	//Communication with Artificial Intelligence Module
    ArtificialIntelligence computer = new ArtificialIntelligence();;
	
	//Switches that indicate whether to draw the ships or not.
	private boolean drawCarrier, drawBattleship, drawCruiser, drawDestroyer1, drawDestroyer2, drawSubmarine1, drawSubmarine2;

	//Switch that works with clickCount to receive and store current storedX and storedY.
	private boolean Switch = true;
	
	//Player name, and a switch that makes sure that the player is asked only once.
	private boolean namePrompt = false;

	//Load game prompt, and a switch that makes sure that the player is asked only once, and one that makes sure the AI generates.
	private int loadGame = 0;
	private boolean loadPrompt = true;
	
	/**
	 * @param
	 * @return Default constructor, creates an instance of the Timer object and starts it, adds MouseListener, MouseMotionListener, and KeyListener.
	 * @author Albraa Al Nabulsi
	 * @Modified March 29, 2013
	 */
	public Board() {
		timer = new Timer(25, this);	//Refresh frequency is once per 25 milliseconds
		timer.start();					//Start the timer
		addMouseListener (this);		//Allows MouseListener to 'listen' to the mouse.
		addMouseMotionListener (this);	//Allows MouseMotionListener to 'listen' to the mouse.
		setFocusable(true);				//KeyListener requires this for some reason when in frames...
		addKeyListener (this);			//Allows KeyListener to 'listen' to the keyboard.
	}

	/**
	 * @param
	 * @return Repaints or refreshes when action is performed, tied to timer.
	 * @author Albraa Al Nabulsi
	 * @Modified March 29, 2013
	 */
	public void actionPerformed(ActionEvent e) {
		boolean successfullyHitTile = false; //Only true when a move is made on an unhit tile on the correct player turn
		boolean victoryConditionMet = false; //Used to determine if all of one player's ships are exhausted
		
		
		repaint();	//refresh
		if (shipSwitch == 0) {
			if (imageSwitch == 1) {
				if (player1Board.getPlayerTurn() == BattleGrid.PlayerTurn.PLAYER2) {
					while (!successfullyHitTile) {
						ArtificialIntelligence.Target target = computer.getNextMove(player1Board);
						int row = computer.getTargetRow(target);
						int column = computer.getTargetColumn(target);
						successfullyHitTile = player1Board.markSquareAsHit(row, column, computer.computerGrid);
						
						if (successfullyHitTile) {
							//player1TilesHit = player1Board.getHitTiles();
							
						    victoryConditionMet = player1Board.checkVictoryCondition();
						    //If this happened while its the computers turn then the computer must have won!
						    if (victoryConditionMet) {					    
						      JOptionPane.showMessageDialog(null, "The Computer has won! \n Click 'Ok' to return to the main menu.");
							  imageSwitch = 0;
							  resetGame();
						    }
						    
							player1Board.changePlayerTurn();	
							
						}
					}
				}
			}
		}
	}

	/**
	 * @param
	 * @return Initializes image variables and loads the images into the variables, controls all 
	 * 		   graphics displayed on the frame, also prompts player for name.
	 * @author Albraa Al Nabulsi
	 * @Modified April 2, 2013
	 */
	public void paint(Graphics graphics) {
		
		//Initializes menu images.
		BufferedImage startMenu = null;
		BufferedImage gameModeMenu = null;
		BufferedImage onePlayerBoard = null;
		BufferedImage twoPlayerBoard = null;
		BufferedImage rulesMenu = null;
		BufferedImage scoresMenu = null;
		
		//Initializes rectangles images.
		BufferedImage greenRectangle = null;	//Player
		BufferedImage yellowRectangle = null;	//Enemy
		
		//Initializes ship images.
		BufferedImage P1carrier = null;
		BufferedImage P1battleship = null;
		BufferedImage P1cruiser = null;
		BufferedImage P1destroyer1 = null;
		BufferedImage P1destroyer2 = null;
		BufferedImage P1submarine1 = null;
		BufferedImage P1submarine2 = null;
		
		BufferedImage P2carrier = null;
		BufferedImage P2battleship = null;
		BufferedImage P2cruiser = null;
		BufferedImage P2destroyer1 = null;
		BufferedImage P2destroyer2 = null;
		BufferedImage P2submarine1 = null;
		BufferedImage P2submarine2 = null;
		
		//Initializes rotated ship images.
		BufferedImage P1ROTcarrier = null;
		BufferedImage P1ROTbattleship = null;
		BufferedImage P1ROTcruiser = null;
		BufferedImage P1ROTdestroyer1 = null;
		BufferedImage P1ROTdestroyer2 = null;
		BufferedImage P1ROTsubmarine1 = null;
		BufferedImage P1ROTsubmarine2 = null;
		
		BufferedImage P2ROTcarrier = null;
		BufferedImage P2ROTbattleship = null;
		BufferedImage P2ROTcruiser = null;
		BufferedImage P2ROTdestroyer1 = null;
		BufferedImage P2ROTdestroyer2 = null;
		BufferedImage P2ROTsubmarine1 = null;
		BufferedImage P2ROTsubmarine2 = null;
		
		//Will try to open and assign images in project folder as specified, if failed then variable(s) stays null.
		try {
			startMenu = ImageIO.read(new File("Start Menu.jpg"));
			gameModeMenu = ImageIO.read(new File("mode-selection.jpg"));
			onePlayerBoard = ImageIO.read(new File("1-Player-Board.jpg"));
			twoPlayerBoard = ImageIO.read(new File("2-Player-Board.jpg"));
			rulesMenu = ImageIO.read(new File("Game Rules.jpg"));
			scoresMenu = ImageIO.read(new File("Scores Menu.jpg"));
			
			greenRectangle = ImageIO.read(new File("greenRectangle.png"));
			yellowRectangle = ImageIO.read(new File("yellowRectangle.png"));
			
			P1carrier = ImageIO.read(new File("Aircraft Carrier.png"));
			P1battleship = ImageIO.read(new File("Battleship.png"));
			P1cruiser = ImageIO.read(new File("Cruiser.png"));
			P1destroyer1 = ImageIO.read(new File("Destroyer1.png"));
			P1destroyer2 = ImageIO.read(new File("Destroyer2.png"));
			P1submarine1 = ImageIO.read(new File("Submarine1.png"));
			P1submarine2 = ImageIO.read(new File("Submarine2.png"));
			
			P2carrier = ImageIO.read(new File("Aircraft Carrier.png"));
			P2battleship = ImageIO.read(new File("Battleship.png"));
			P2cruiser = ImageIO.read(new File("Cruiser.png"));
			P2destroyer1 = ImageIO.read(new File("Destroyer1.png"));
			P2destroyer2 = ImageIO.read(new File("Destroyer2.png"));
			P2submarine1 = ImageIO.read(new File("Submarine1.png"));
			P2submarine2 = ImageIO.read(new File("Submarine2.png"));
			
			P1ROTcarrier = ImageIO.read(new File("ROT Aircraft Carrier.png"));
			P1ROTbattleship = ImageIO.read(new File("ROT Battleship.png"));
			P1ROTcruiser = ImageIO.read(new File("ROT Cruiser.png"));
			P1ROTdestroyer1 = ImageIO.read(new File("ROT Destroyer1.png"));
			P1ROTdestroyer2 = ImageIO.read(new File("ROT Destroyer2.png"));
			P1ROTsubmarine1 = ImageIO.read(new File("ROT Submarine1.png"));
			P1ROTsubmarine2 = ImageIO.read(new File("ROT Submarine2.png"));
			
			P2ROTcarrier = ImageIO.read(new File("ROT Aircraft Carrier.png"));
			P2ROTbattleship = ImageIO.read(new File("ROT Battleship.png"));
			P2ROTcruiser = ImageIO.read(new File("ROT Cruiser.png"));
			P2ROTdestroyer1 = ImageIO.read(new File("ROT Destroyer1.png"));
			P2ROTdestroyer2 = ImageIO.read(new File("ROT Destroyer2.png"));
			P2ROTsubmarine1 = ImageIO.read(new File("ROT Submarine1.png"));
			P2ROTsubmarine2 = ImageIO.read(new File("ROT Submarine2.png"));
		} 
		catch (IOException e) {
		    System.out.print("Unable to load one or more images.");
		    e.printStackTrace();
		}
		
		//Draw start menu if imageSwitch is equal to 0.
		if (imageSwitch == 0) {
		  String name;
			graphics.drawImage(startMenu, 0, 0, this);		
			if (namePrompt == true) {	//Asks for player name.
				namePrompt = false;
				name = JOptionPane.showInputDialog (null, "What is your name? (maximum of 9 characters)", "ANONYMOUS");
				
				//If the user clicks "cancel" then the name will be left null, the following is to prevent an error
				if (name == null)
					 name = "";
				
				//Ensure that we only take the name if its not the default value or blank spaces, and that we only take the first 9 character
				if ( ! (name.trim().equals("") || name.equals("ANONYMOUS") ) ) {
				  if (name.length() > 9)
					  player1Board.name = name.substring(0, 9);
					  
				  else
				  	  player1Board.name = name;
				}
				else
				  player1Board.name = "";
			}
			if (loadPrompt == true) {	//Asks for player name.
				loadPrompt = false;
				loadGame = JOptionPane.showConfirmDialog(null, "Load previous game?", "input", JOptionPane.YES_NO_OPTION);
				if (loadGame == 0) {
					Load();
					
					switch(currentMode) {
					case OnePlayer:
						imageSwitch = 1;
						break;
					case TwoPlayer:
						imageSwitch = 5;
						break;
					}
					
					
				}
				else {
					computer.setComputerShips();
					namePrompt = true;
				}
			}
		}

		//Draw game menu if imageSwitch is equal to 1. Also draws game related elements.
		else if (imageSwitch == 1)
		{
			graphics.drawImage(onePlayerBoard, 0, 0, this);	//Draws the game menu.
			Font font = new Font("Arial", Font.PLAIN, 24);
			graphics.setFont (font);
			graphics.drawString( Integer.toString(player1Board.getPlayerScore() ) , 155, 669) ;
			
			//Draw player 1 pieces 
			if (drawCarrier == true) {	//If the switch for carrier is true...
				if (player1Board.ROTCarrier == false)	//If the rotation switch for carrier is false...
					graphics.drawImage(P1carrier, player1Board.carrierX, player1Board.carrierY, this);	//Draw carrier
				else
					graphics.drawImage(P1ROTcarrier, player1Board.carrierX, player1Board.carrierY, this);	//Draw rotated carrier
			}
			
			if (drawBattleship == true) {	//Same as before but for the Battleship.
				if (player1Board.ROTBattleship == false)
					graphics.drawImage(P1battleship, player1Board.battleshipX, player1Board.battleshipY, this);
				else
					graphics.drawImage(P1ROTbattleship, player1Board.battleshipX, player1Board.battleshipY, this);
			}
			
			if (drawCruiser == true) {	//Same as before but for the Cruiser.
				if (player1Board.ROTCruiser == false)
					graphics.drawImage(P1cruiser, player1Board.cruiserX, player1Board.cruiserY, this);
				else
					graphics.drawImage(P1ROTcruiser, player1Board.cruiserX, player1Board.cruiserY, this);
			}
			
			if (drawDestroyer1 == true) {	//Same as before but for the first Destroyer.
				if (player1Board.ROTDestroyer1 == false)
					graphics.drawImage(P1destroyer1, player1Board.destroyer1X, player1Board.destroyer1Y, this);
				else
					graphics.drawImage(P1ROTdestroyer1, player1Board.destroyer1X, player1Board.destroyer1Y, this);
			}
			
			if (drawDestroyer2 == true) {	//Same as before but for the second Destroyer.
				if (player1Board.ROTDestroyer2 == false)
					graphics.drawImage(P1destroyer2, player1Board.destroyer2X, player1Board.destroyer2Y, this);
				else
					graphics.drawImage(P1ROTdestroyer2, player1Board.destroyer2X, player1Board.destroyer2Y, this);
			}
			
			if (drawSubmarine1 == true) {	//Same as before but for the first Submarine.
				if (player1Board.ROTSubmarine1 == false)
					graphics.drawImage(P1submarine1, player1Board.submarine1X, player1Board.submarine1Y, this);
				else
					graphics.drawImage(P1ROTsubmarine1, player1Board.submarine1X, player1Board.submarine1Y, this);
			}
			
			if (drawSubmarine2 == true) {	//Same as before but for the second Submarine.
				if (player1Board.ROTSubmarine2 == false)
					graphics.drawImage(P1submarine2, player1Board.submarine2X, player1Board.submarine2Y, this);
				else
					graphics.drawImage(P1ROTsubmarine2, player1Board.submarine2X, player1Board.submarine2Y, this);
			}
				
		    //Draw playerTiles which have been hit	
		    BattleGrid.ShipLabel ship;
		        
		    for (int[] item : player1Board.getHitTiles()) {
		      ship = player1Board.getShipOccupyingSquare(item[0], item[1]);
		      if (ship != BattleGrid.ShipLabel.NO_SHIP)
		        graphics.setColor(Color.orange);
		      else
		        graphics.setColor(Color.red);
		      graphics.fillRect(item[1]*50+73, item[0]*50+131, 45, 45);
		    }
			  
		    //Draw computerTiles which have been hit
		    for (int[] item : computer.computerGrid.getHitTiles()) {
		      ship = computer.computerGrid.getShipOccupyingSquare(item[0], item[1]);	
		      if (ship != BattleGrid.ShipLabel.NO_SHIP)
			    graphics.setColor(Color.orange);
			  else
			    graphics.setColor(Color.red);
			  graphics.fillRect(item[1]*50+633, item[0]*50+131, 45, 45);	  
	        }
		    
		    //Draw color tiles which haven't been hit	    
	    	if (lightSwitch == 1)
				graphics.drawImage(greenRectangle, flashX, flashY, this);
			else if (lightSwitch == 2)
				graphics.drawImage(yellowRectangle, flashX, flashY, this);
			
		}

		//Draw rules menu if imageSwitch is equal to 2.
		else if (imageSwitch == 2)
			graphics.drawImage(rulesMenu, 0, 0, this);
		
		//Draw High Score menu
		else if (imageSwitch == 3) {
			graphics.drawImage(scoresMenu, 0, 0, this);
			Font font = new Font("Arial", Font.PLAIN, 34);
			graphics.setFont (font);
			
			String playerInfo = InputOutput.getHighestPlayerStanding(player1Board.name);
			
			ArrayList<String>lineFields = new ArrayList<String>();
			lineFields = InputOutput.returnFieldsInLine( playerInfo );
			
			graphics.drawString( lineFields.get(0) , 707, 249 ) ;
			graphics.drawString( lineFields.get(1) , 707, 391 ) ;
			graphics.drawString( lineFields.get(2) , 707, 542 ) ;
		}
		
		//Draw Game Mode Selection Menu if imageSwitch is 4
		else if (imageSwitch == 4) {
			graphics.drawImage(gameModeMenu, 0, 0, this);
		}
		
		else if (imageSwitch == 5) {
			graphics.drawImage(twoPlayerBoard, 0, 0, this);	//Draws the game board page.
			
			//Draw pieces only when ships have not been placed
			if ( !allShipsPlaced ) {
				
				//Draw player 1 pieces only if player 1 is selecting ships
				if ( playerPlacingShips == Player.Player1 ) {
					if (drawCarrier == true) {	//If the switch for carrier is true...
						if (player1Board.ROTCarrier == false)	//If the rotation switch for carrier is false...
							graphics.drawImage(P1carrier, player1Board.carrierX, player1Board.carrierY, this);	//Draw carrier
						else
							graphics.drawImage(P1ROTcarrier, player1Board.carrierX, player1Board.carrierY, this);	//Draw rotated carrier
					}
					
					if (drawBattleship == true) {	//Same as before but for the Battleship.
						if (player1Board.ROTBattleship == false)
							graphics.drawImage(P1battleship, player1Board.battleshipX, player1Board.battleshipY, this);
						else
							graphics.drawImage(P1ROTbattleship, player1Board.battleshipX, player1Board.battleshipY, this);
					}
					
					if (drawCruiser == true) {	//Same as before but for the Cruiser.
						if (player1Board.ROTCruiser == false)
							graphics.drawImage(P1cruiser, player1Board.cruiserX, player1Board.cruiserY, this);
						else
							graphics.drawImage(P1ROTcruiser, player1Board.cruiserX, player1Board.cruiserY, this);
					}
					
					if (drawDestroyer1 == true) {	//Same as before but for the first Destroyer.
						if (player1Board.ROTDestroyer1 == false)
							graphics.drawImage(P1destroyer1, player1Board.destroyer1X, player1Board.destroyer1Y, this);
						else
							graphics.drawImage(P1ROTdestroyer1, player1Board.destroyer1X, player1Board.destroyer1Y, this);
					}
					
					if (drawDestroyer2 == true) {	//Same as before but for the second Destroyer.
						if (player1Board.ROTDestroyer2 == false)
							graphics.drawImage(P1destroyer2, player1Board.destroyer2X, player1Board.destroyer2Y, this);
						else
							graphics.drawImage(P1ROTdestroyer2, player1Board.destroyer2X, player1Board.destroyer2Y, this);
					}
					
					if (drawSubmarine1 == true) {	//Same as before but for the first Submarine.
						if (player1Board.ROTSubmarine1 == false)
							graphics.drawImage(P1submarine1, player1Board.submarine1X, player1Board.submarine1Y, this);
						else
							graphics.drawImage(P1ROTsubmarine1, player1Board.submarine1X, player1Board.submarine1Y, this);
					}
					
					if (drawSubmarine2 == true) {	//Same as before but for the second Submarine.
						if (player1Board.ROTSubmarine2 == false)
							graphics.drawImage(P1submarine2, player1Board.submarine2X, player1Board.submarine2Y, this);
						else
							graphics.drawImage(P1ROTsubmarine2, player1Board.submarine2X, player1Board.submarine2Y, this);
					}
				}
				
				//Show player 2 pieces only during ship placement phase
				else if ( playerPlacingShips == Player.Player2 ) {
					//Draw Player 2 Pieces
					if (drawCarrier == true) {	//If the switch for carrier is true...
						if (player2Board.ROTCarrier == false)	//If the rotation switch for carrier is false...
							graphics.drawImage(P2carrier, player2Board.carrierX, player2Board.carrierY, this);	//Draw carrier
						else
							graphics.drawImage(P2ROTcarrier, player2Board.carrierX, player2Board.carrierY, this);	//Draw rotated carrier
					}
					
					if (drawBattleship == true) {	//Same as before but for the Battleship.
						if (player2Board.ROTBattleship == false)
							graphics.drawImage(P2battleship, player2Board.battleshipX, player2Board.battleshipY, this);
						else
							graphics.drawImage(P2ROTbattleship, player2Board.battleshipX, player2Board.battleshipY, this);
					}
					
					if (drawCruiser == true) {	//Same as before but for the Cruiser.
						if (player2Board.ROTCruiser == false)
							graphics.drawImage(P2cruiser, player2Board.cruiserX, player2Board.cruiserY, this);
						else
							graphics.drawImage(P2ROTcruiser, player2Board.cruiserX, player2Board.cruiserY, this);
					}
					
					if (drawDestroyer1 == true) {	//Same as before but for the first Destroyer.
						if (player2Board.ROTDestroyer1 == false)
							graphics.drawImage(P2destroyer1, player2Board.destroyer1X, player2Board.destroyer1Y, this);
						else
							graphics.drawImage(P2ROTdestroyer1, player2Board.destroyer1X, player2Board.destroyer1Y, this);
					}
					
					if (drawDestroyer2 == true) {	//Same as before but for the second Destroyer.
						if (player2Board.ROTDestroyer2 == false)
							graphics.drawImage(P2destroyer2, player2Board.destroyer2X, player2Board.destroyer2Y, this);
						else
							graphics.drawImage(P2ROTdestroyer2, player2Board.destroyer2X, player2Board.destroyer2Y, this);
					}
					
					if (drawSubmarine1 == true) {	//Same as before but for the first Submarine.
						if (player2Board.ROTSubmarine1 == false)
							graphics.drawImage(P2submarine1, player2Board.submarine1X, player2Board.submarine1Y, this);
						else
							graphics.drawImage(P2ROTsubmarine1, player2Board.submarine1X, player2Board.submarine1Y, this);
					}
					
					if (drawSubmarine2 == true) {	//Same as before but for the second Submarine.
						if (player2Board.ROTSubmarine2 == false)
							graphics.drawImage(P2submarine2, player2Board.submarine2X, player2Board.submarine2Y, this);
						else
							graphics.drawImage(P2ROTsubmarine2, player2Board.submarine2X, player2Board.submarine2Y, this);
					}
				}
			}			
			
		    //Draw playerTiles which have been hit	
		    BattleGrid.ShipLabel ship;
		        
		    for (int[] item : player1Board.getHitTiles()) {
		      ship = player1Board.getShipOccupyingSquare(item[0], item[1]);
		      if (ship != BattleGrid.ShipLabel.NO_SHIP)
		        graphics.setColor(Color.orange);
		      else
		        graphics.setColor(Color.red);
		      graphics.fillRect(item[1]*50+73, item[0]*50+131, 45, 45);
		    }
			
		    for (int[] item : player2Board.getHitTiles()) {
			      ship = player2Board.getShipOccupyingSquare(item[0], item[1]);
			      if (ship != BattleGrid.ShipLabel.NO_SHIP)
			        graphics.setColor(Color.orange);
			      else
			        graphics.setColor(Color.red);
			      graphics.fillRect(item[1]*50+633, item[0]*50+131, 45, 45);
		    }
		    
		    
		   //Draw color tiles which haven't been hit
		    if ( playerPlacingShips == Player.Player1 || allShipsPlaced ) {
		    	if (lightSwitch == 1)
					graphics.drawImage(greenRectangle, flashX, flashY, this);
				else if (lightSwitch == 2)
					graphics.drawImage(yellowRectangle, flashX, flashY, this);
		    } else {
		    	if (lightSwitch == 1)
					graphics.drawImage(yellowRectangle, flashX, flashY, this);
				else if (lightSwitch == 2)
					graphics.drawImage(greenRectangle, flashX, flashY, this);
		    }
		}
		//Devin display all the ships in the end
		else if (imageSwitch == 6) {
				if (player1Board.ROTCarrier == false)	//If the rotation switch for carrier is false...
					graphics.drawImage(P1carrier, player1Board.carrierX, player1Board.carrierY, this);	//Draw carrier
				else
					graphics.drawImage(P1ROTcarrier, player1Board.carrierX, player1Board.carrierY, this);	//Draw rotated carrier
			
			
				if (player1Board.ROTBattleship == false)
					graphics.drawImage(P1battleship, player1Board.battleshipX, player1Board.battleshipY, this);
				else
					graphics.drawImage(P1ROTbattleship, player1Board.battleshipX, player1Board.battleshipY, this);
			
			
				if (player1Board.ROTCruiser == false)
					graphics.drawImage(P1cruiser, player1Board.cruiserX, player1Board.cruiserY, this);
				else
					graphics.drawImage(P1ROTcruiser, player1Board.cruiserX, player1Board.cruiserY, this);
			
			
				if (player1Board.ROTDestroyer1 == false)
					graphics.drawImage(P1destroyer1, player1Board.destroyer1X, player1Board.destroyer1Y, this);
				else
					graphics.drawImage(P1ROTdestroyer1, player1Board.destroyer1X, player1Board.destroyer1Y, this);
			
			
				if (player1Board.ROTDestroyer2 == false)
					graphics.drawImage(P1destroyer2, player1Board.destroyer2X, player1Board.destroyer2Y, this);
				else
					graphics.drawImage(P1ROTdestroyer2, player1Board.destroyer2X, player1Board.destroyer2Y, this);
			
			
				if (player1Board.ROTSubmarine1 == false)
					graphics.drawImage(P1submarine1, player1Board.submarine1X, player1Board.submarine1Y, this);
				else
					graphics.drawImage(P1ROTsubmarine1, player1Board.submarine1X, player1Board.submarine1Y, this);
			
			
				if (player1Board.ROTSubmarine2 == false)
					graphics.drawImage(P1submarine2, player1Board.submarine2X, player1Board.submarine2Y, this);
				else
					graphics.drawImage(P1ROTsubmarine2, player1Board.submarine2X, player1Board.submarine2Y, this);
					
		
				if (player2Board.ROTCarrier == false)	//If the rotation switch for carrier is false...
					graphics.drawImage(P2carrier, player2Board.carrierX, player2Board.carrierY, this);	//Draw carrier
				else
					graphics.drawImage(P2ROTcarrier, player2Board.carrierX, player2Board.carrierY, this);	//Draw rotated carrier
			
			
				if (player2Board.ROTBattleship == false)
					graphics.drawImage(P2battleship, player2Board.battleshipX, player2Board.battleshipY, this);
				else
					graphics.drawImage(P2ROTbattleship, player2Board.battleshipX, player2Board.battleshipY, this);
			
			
				if (player2Board.ROTCruiser == false)
					graphics.drawImage(P2cruiser, player2Board.cruiserX, player2Board.cruiserY, this);
				else
					graphics.drawImage(P2ROTcruiser, player2Board.cruiserX, player2Board.cruiserY, this);
			
			
				if (player2Board.ROTDestroyer1 == false)
					graphics.drawImage(P2destroyer1, player2Board.destroyer1X, player2Board.destroyer1Y, this);
				else
					graphics.drawImage(P2ROTdestroyer1, player2Board.destroyer1X, player2Board.destroyer1Y, this);
			
			
				if (player2Board.ROTDestroyer2 == false)
					graphics.drawImage(P2destroyer2, player2Board.destroyer2X, player2Board.destroyer2Y, this);
				else
					graphics.drawImage(P2ROTdestroyer2, player2Board.destroyer2X, player2Board.destroyer2Y, this);
			
			
				if (player2Board.ROTSubmarine1 == false)
					graphics.drawImage(P2submarine1, player2Board.submarine1X, player2Board.submarine1Y, this);
				else
					graphics.drawImage(P2ROTsubmarine1, player2Board.submarine1X, player2Board.submarine1Y, this);
			
			
				if (player2Board.ROTSubmarine2 == false)
					graphics.drawImage(P2submarine2, player2Board.submarine2X, player2Board.submarine2Y, this);
				else
					graphics.drawImage(P2ROTsubmarine2, player2Board.submarine2X, player2Board.submarine2Y, this);
			
		}
	}
	
	
	/**
	 * @param
	 * @return Triggered when mouse is pressed and released. Empty but required by MouseListener.
	 * @author Albraa Al Nabulsi
	 * @Modified March 29, 2013
	 */
	public void mouseClicked (MouseEvent e) {
		
	}

	/**
	 * @param
	 * @return Triggered when mouse enters the frame. Empty but required by MouseListener.
	 * @author Albraa Al Nabulsi
	 * @Modified March 29, 2013
	 */
	public void mouseEntered(MouseEvent e) {

	}

	/**
	 * @param
	 * @return Triggered when mouse exits the frame. Empty but required by MouseListener.
	 * @author Albraa Al Nabulsi
	 * @Modified March 29, 2013
	 */
	public void mouseExited(MouseEvent e) {
		
	}

	/**
	 * @param
	 * @return Triggered when mouse is pressed, controls how the program behaves when the user presses 
	 * 		   the mouse.
	 * @author Albraa Al Nabulsi
	 * @Modified April 1, 2013
	 */
	public void mousePressed(MouseEvent e) {
		x = e.getX ();	//tracker -> assigns x coordinate of the frame when mouse is pressed.
		y = e.getY ();	//tracker -> assigns y coordinate of the frame when mouse is pressed.
		clickCount++;	//Increases the counter with every click.
		
		int startX, startY;
		boolean successfullyAddedShip = false;
		boolean successfullyHitTile = false; //Only true if an unhit square of the correct grid is landed
		boolean victoryConditionMet = false;
		
		//Communication with Games Module (BattleGrid)
		BattleGrid.ShipLabel ship = BattleGrid.ShipLabel.NO_SHIP; //Defaulted
		BattleGrid.ShipRotation orientation = BattleGrid.ShipRotation.UNDEFINED; //Defaulted
		
		if (imageSwitch == 0) {	//If the start menu is displayed...
			clickCount = 0;		//Reset the counter
			if (x >= 43 && x <= (43 + 240) && y >= 280 && y <= (280 + 67)) {	//If the "The Game" button is clicked...
				imageSwitch = 4;	//Switch to game mode selection menu.			
				
				//If grids not initialized by this point we do so
				if (currentMode == GameMode.OnePlayer &&  computer.computerGrid.isEmpty()) {
				  computer.setComputerShips();
				}
				if (player1Board.isEmpty()) {
				  shipSwitch = 7;
				}
			}
			else if (x >= 43 && x <= (43 + 240) && y >= 414 && y <= (414 + 67))	//If the "The Rules" button is clicked...
				imageSwitch = 2;	//Switch to rule menu.
			else if (x >= 43 && x <= (43 + 240) && y >= 548 && y <= (548 + 67))	//If the "The Scores" button is clicked...
				imageSwitch = 3;	//Switch to score menu.
		}
		
		else if (imageSwitch == 1) {	//When the 1 Player game screen is present
			
			if (x >= 968 && x <= (968 + 161) && y >= 693 && y <= (693 + 51)) {	//If the 'quit game' is clicked...
			    if (!computer.computerGrid.getHitTiles().isEmpty() && (currentMode == GameMode.OnePlayer) ) {
			        Save(GameMode.OnePlayer);
			        currentMode = GameMode.None;
			        playerPlacingShips = Player.Player1;
			    }
    		    
			    //Clear old data from registers
			    this.resetGame();
			    
				imageSwitch = 0; //Switch to start menu.
			}	
			else {
				//Prevents out-of-player-bounds clicks.
				if ( x<=600  ) {		//If clicked is on player side...
					//Makes sure player-board edges (not included in stored coordinates) do not interfere with 'boxes'.  
					if (Switch == true)	//If the main switch is true...
						player1Board(x, y);	//Generate storedX and storedY at appropriate coordinate, if no coordinate is found set the switch to false
					if (Switch == false) {	//If the main switch is false
						clickCount = clickCount - 1;	//set click counter back by one, we don't want the last click (which didn't generate coordinates) to count!
						player1Board(x, y);	//Try generating again, if no coordinates are generated this segment of the code will repeat.
						Switch = true;
					}
					else if (shipSwitch == 7 && clickCount > 0) {	//First ship, first click					
						//If statements prevent out of bounds deployment of ships.
						if (player1Board.ROTCarrier == false) {
							orientation = BattleGrid.ShipRotation.HORIZONTAL;							
							if (x > 323) {								
								player1Board.carrierX = 323;
							}
							else {
								player1Board.carrierX = storedX;
							}
							player1Board.carrierY = storedY;
						}
						else if (player1Board.ROTCarrier == true) {
							orientation = BattleGrid.ShipRotation.VERTICAL;
							if (y > 381) {
								player1Board.carrierY = 381;
							}
							else {
								player1Board.carrierY = storedY;
							}
							player1Board.carrierX = storedX;
						}
						
						ship = BattleGrid.ShipLabel.AIRCRAFT_CARRIER;
						startX = calculatePlayer1ColumnIndex(player1Board.carrierX);
						startY = calculatePlayer1RowIndex(player1Board.carrierY);						
									
						successfullyAddedShip = player1Board.addShip(ship, startY, startX, orientation);

						if (successfullyAddedShip) {					
							shipSwitch = 6;
							drawCarrier = true;
						}
						else
							clickCount--;
							
					}
					else if (shipSwitch == 6 && clickCount > 1) {	//Next ship, next click...				    
						if (player1Board.ROTBattleship == false) {
							orientation = BattleGrid.ShipRotation.HORIZONTAL;
							if (x > 373) {
								player1Board.battleshipX = 373;
							}
							else {
								player1Board.battleshipX = storedX;
							}
							player1Board.battleshipY = storedY;
						}
						else if (player1Board.ROTBattleship == true) {
							orientation = BattleGrid.ShipRotation.VERTICAL;
							if (y > 431) {
								player1Board.battleshipY = 431;
							}
							else {
								player1Board.battleshipY = storedY;
							}
							player1Board.battleshipX = storedX;
						}
						
						ship = BattleGrid.ShipLabel.BATTLESHIP;
						startX = calculatePlayer1ColumnIndex(player1Board.battleshipX);
						startY = calculatePlayer1RowIndex(player1Board.battleshipY);
						
						successfullyAddedShip = player1Board.addShip(ship, startY, startX, orientation);
	
						if (successfullyAddedShip) {
						    shipSwitch = 5;
						    drawBattleship = true;
						}
						else
							clickCount--;
							
					}
					else if (shipSwitch == 5 && clickCount > 2) {	//And so on until number of unplaced ships is zero (line 327).
						if (player1Board.ROTCruiser == false) {
							orientation = BattleGrid.ShipRotation.HORIZONTAL;
							if (x > 423) {
								player1Board.cruiserX = 423;
							}
							else {
								player1Board.cruiserX = storedX;
							}
							player1Board.cruiserY = storedY;
						}
						else if (player1Board.ROTCruiser == true) {
							orientation = BattleGrid.ShipRotation.VERTICAL;
							if (y > 481) {
								player1Board.cruiserY = 481;
							}
							else {
								player1Board.cruiserY = storedY;
							}
							player1Board.cruiserX = storedX;
						}
						
						ship = BattleGrid.ShipLabel.CRUISER;
						startX = calculatePlayer1ColumnIndex(player1Board.cruiserX);
						startY = calculatePlayer1RowIndex(player1Board.cruiserY);
						
						successfullyAddedShip = player1Board.addShip(ship, startY, startX, orientation);

						if (successfullyAddedShip) {
							shipSwitch = 4;
							drawCruiser = true;
						}
						else
							clickCount--;
							
					}
					else if (shipSwitch == 4 && clickCount > 3) {
						if (player1Board.ROTDestroyer1 == false) {
							orientation = BattleGrid.ShipRotation.HORIZONTAL;
							if (x > 473) {
								player1Board.destroyer1X = 473;
							}
							else {
								player1Board.destroyer1X = storedX;
							}
							player1Board.destroyer1Y = storedY;
						}
						else if (player1Board.ROTDestroyer1 == true) {
							orientation = BattleGrid.ShipRotation.VERTICAL;
							if (y > 531) {
								player1Board.destroyer1Y = 531;
							}
							else {
								player1Board.destroyer1Y = storedY;
							}
							player1Board.destroyer1X = storedX;
						}
						
						ship = BattleGrid.ShipLabel.DESTROYER1;
						startX = calculatePlayer1ColumnIndex(player1Board.destroyer1X);
						startY = calculatePlayer1RowIndex(player1Board.destroyer1Y);
						
						successfullyAddedShip = player1Board.addShip(ship, startY, startX, orientation);

						if (successfullyAddedShip) {
							shipSwitch = 3;
							drawDestroyer1 = true;
						}
						else
							clickCount--;
							
					}
					else if (shipSwitch == 3 && clickCount > 4) {
						if (player1Board.ROTDestroyer2 == false) {
							orientation = BattleGrid.ShipRotation.HORIZONTAL;
							if (x > 473) {
								player1Board.destroyer2X = 473;
							}
							else {
								player1Board.destroyer2X = storedX;
							}
							player1Board.destroyer2Y = storedY;
						}
						else if (player1Board.ROTDestroyer2 == true) {
							orientation = BattleGrid.ShipRotation.VERTICAL;
							if (y > 531) {
								player1Board.destroyer2Y = 531;
							}
							else {
								player1Board.destroyer2Y = storedY;
							}
							player1Board.destroyer2X = storedX;
						}
						
						ship = BattleGrid.ShipLabel.DESTROYER2;
						startX = calculatePlayer1ColumnIndex(player1Board.destroyer2X);
						startY = calculatePlayer1RowIndex(player1Board.destroyer2Y);
						
						successfullyAddedShip = player1Board.addShip(ship, startY, startX, orientation);

						if (successfullyAddedShip) {
							shipSwitch = 2;
							drawDestroyer2 = true;
						}
						else
							clickCount--;
							
					}
					else if (shipSwitch == 2 && clickCount > 5) {
						player1Board.submarine1X = storedX;
						player1Board.submarine1Y = storedY;
						
						orientation = BattleGrid.ShipRotation.HORIZONTAL; //In this case it doesn't matter, size 1
						ship = BattleGrid.ShipLabel.SUBMARINE1;
						startX = calculatePlayer1ColumnIndex(player1Board.submarine1X);
						startY = calculatePlayer1RowIndex(player1Board.submarine1Y);
						
						successfullyAddedShip = player1Board.addShip(ship, startY, startX, orientation);
						
						if (successfullyAddedShip) {
							shipSwitch = 1;
							drawSubmarine1 = true;
						}
						else
							clickCount--;
							
					}
					else if (shipSwitch == 1 && clickCount > 6) {				
						player1Board.submarine2X = storedX;
						player1Board.submarine2Y = storedY;
						
						orientation = BattleGrid.ShipRotation.HORIZONTAL; //In this case it doesn't matter, size 1
						ship = BattleGrid.ShipLabel.SUBMARINE2;						
						startX = calculatePlayer1ColumnIndex(player1Board.submarine2X);
						startY = calculatePlayer1RowIndex(player1Board.submarine2Y);
						successfullyAddedShip = player1Board.addShip(ship, startY, startX, orientation);
						
						if (successfullyAddedShip) {
							shipSwitch = 0;
							drawSubmarine2 = true;
							
						}
						else
							clickCount--;
					}
				}
				if (shipSwitch == 0) {
					if (x >= 633 && x <= (633 + 495) && y >= 131 && y <= (131 + 495) && player1Board.getPlayerTurn() == BattleGrid.PlayerTurn.PLAYER1) {
						int row = calculatePlayer2RowIndex(y);
						int column = calculatePlayer2ColumnIndex(x);
						successfullyHitTile = computer.computerGrid.markSquareAsHit(row, column, player1Board);
						
						if (successfullyHitTile) {
						    victoryConditionMet = computer.computerGrid.checkVictoryCondition();
							player1Board.changePlayerTurn();
							
						    //If condition while the computer's ship was hit last, then this must mean that the Player has won.
						    if (victoryConditionMet) {	
						    
						      //Only updates scores for player if their name is defined.	
						      if (player1Board.name == null || player1Board.name.equals(""))
						    	  player1Board.name = "Player 1";
						      
					          InputOutput.updateScores(player1Board);
							  JOptionPane.showMessageDialog(null, "You have won! \n Your Score has been recorded! Click 'Ok' to return to the main menu.");

							  imageSwitch = 0;
							  resetGame();							  
						    }
					
						}
					}
				}
			}
		}
		
		else if (imageSwitch == 2) {		//If the rule menu is displayed...
			if (x >= 446 && x <= (446 + 307) && y >= 630 && y <= (630 + 140))	//If the 'back button is clicked...
				imageSwitch = 0;	//Switch to start menu
		}
		
		else if (imageSwitch == 3) {		//If the scores menu is displayed...
			if (x >= 446 && x <= (446 + 307) && y >= 630 && y <= (630 + 140))	//If the 'back button is clicked...
				imageSwitch = 0;	//Switch to start menu
		}
		
		else if (imageSwitch == 4) { // When the game mode selection screen appears
			clickCount = 0;		//Reset the counter
			if (x >= 43 && x <= (43 + 240) && y >= 280 && y <= (280 + 67)) {	//If the "The Game" button is clicked...
				imageSwitch = 1;	//Switch to 1 player game mode menu.			
				currentMode = GameMode.OnePlayer;
				
				//If grids not initialized by this point we do so
				if (computer.computerGrid.isEmpty()) {
				  computer.setComputerShips();
				}
				if (player1Board.isEmpty()) {
				  shipSwitch = 7;
				}
			}
			else if (x >= 43 && x <= (43 + 240) && y >= 414 && y <= (414 + 67))	{//If the "The Rules" button is clicked...
				imageSwitch = 5;	//Switch to 2 player game menu.
				currentMode = GameMode.TwoPlayer;
				
				//If grids not initialized by this point we do so
				if (player1Board.isEmpty()) {
					  shipSwitch = 7;
					}
				if (player2Board.isEmpty()) {
				  shipSwitch = 7;
				}
			}
			else if (x >= 43 && x <= (43 + 240) && y >= 548 && y <= (548 + 67))	//If the "The Scores" button is clicked...
				imageSwitch = 0;	//Switch to main menu.
		}
		
		else if (imageSwitch == 5) { // When the 2 player game screen is present
			
			//Clicks Save and Quit Button
			if (x >= 968 && x <= (968 + 161) && y >= 693 && y <= (693 + 51)) {
			    if ( !player2Board.getHitTiles().isEmpty() ) {
			        Save(GameMode.TwoPlayer); //Save game
			    	currentMode = GameMode.None;
			    	playerPlacingShips = Player.Player1;
			    }
    		    
			    //Clear old data from registers
			    this.resetGame();
			    
				imageSwitch = 0; //Switch to start menu.
			}
			
			else {
				
				if (shipSwitch == 0) {
					//check if victory conditions met					
					try {
						switch (player1Board.getPlayerTurn()) {
						case PLAYER1:
							
							victoryConditionMet = player2Board.checkVictoryCondition();
							
							if (victoryConditionMet) {
								if (player1Board.name == null)
									player1Board.name = "Player 1";
								imageSwitch = 6;
								InputOutput.updateScores(player1Board);								
								
								JOptionPane.showMessageDialog(null, player1Board.name +" has won! \n Your Score has been recorded! Click 'Ok' to return to the main menu.");
								
								imageSwitch = 0;
								resetGame();
							}
							
							break;
							
						case PLAYER2:
							
							victoryConditionMet = player1Board.checkVictoryCondition();
							
							if (victoryConditionMet) {
								if (player2Board.name == null)
									player2Board.name = "Player 2";
								imageSwitch = 6;
								InputOutput.updateScores(player2Board);								
								
								JOptionPane.showMessageDialog(null, player2Board.name +" has won! \n Your Score has been recorded! Click 'Ok' to return to the main menu.");
								
								imageSwitch = 0;
								resetGame();
							}
							
							break;
							
						default: throw new Exception("Only Player 1 or Player 2 should be stored in get player turn in 2 player mode.");
						}
						
					} catch (Exception err) {
						System.out.println("Was not able to check if player is victorious.");
						err.printStackTrace();
					}
					
					//Check for tile strike if its player 1's turn
					if (player1Board.getPlayerTurn() == BattleGrid.PlayerTurn.PLAYER1) {
						if (x >= 633 && x <= (633 + 495) && y >= 131 && y <= (131 + 495)) {
							int row = calculatePlayer2RowIndex(y);
							int column = calculatePlayer2ColumnIndex(x);
							successfullyHitTile = player2Board.markSquareAsHit(row, column, player2Board);
							
							if (successfullyHitTile)
								player1Board.changePlayerTurn();
						}						
						
					//Check for tile strike if its player 2's turn
					} else if (player1Board.getPlayerTurn() == BattleGrid.PlayerTurn.PLAYER2) {
						if (x >= 73 && x <= (73 + 495) && y >= 131 && y <= (131 + 495)) {
							int row = calculatePlayer1RowIndex(y);
							int column = calculatePlayer1ColumnIndex(x);
							successfullyHitTile = player1Board.markSquareAsHit(row, column, player1Board);
							
							if (successfullyHitTile)
								player1Board.changePlayerTurn();
						}
						
					}					
				}
				
				//Prevents out-of-player-bounds clicks.
				if (( x <= 600 && ( playerPlacingShips==Player.Player1 || allShipsPlaced ) ) || ( x>=624 && x<=1132 && playerPlacingShips == Player.Player2 )) {		//If clicked is on player side...
					//Makes sure player-board edges (not included in stored coordinates) do not interfere with 'boxes'.  
					if (Switch) {	//If the main switch is true...
						if (playerPlacingShips == Player.Player1 || allShipsPlaced)
							player1Board(x, y);	//Generate storedX and storedY at appropriate coordinate, if no coordinate is found set the switch to false
						else
							player2Board(x, y);
					}
						
					if (!Switch) {	//If the main switch is false
						clickCount = clickCount - 1;	//set click counter back by one, we don't want the last click (which didn't generate coordinates) to count!
						player1Board(x, y);	//Try generating again, if no coordinates are generated this segment of the code will repeat.
						Switch = true;
					}
					else if (shipSwitch == 7 && clickCount > 0) {	//First ship, first click					
						//If statements prevent out of bounds deployment of ships. 
						ship = BattleGrid.ShipLabel.AIRCRAFT_CARRIER;
						
						if (playerPlacingShips == Player.Player1) {
							if (player1Board.ROTCarrier == false) {
								orientation = BattleGrid.ShipRotation.HORIZONTAL;							
								if (x > 323) {								
									player1Board.carrierX = 323;
								}
								else {
									player1Board.carrierX = storedX;
								}
								player1Board.carrierY = storedY;
							}
							else if (player1Board.ROTCarrier == true) {
								orientation = BattleGrid.ShipRotation.VERTICAL;
								if (y > 381) {
									player1Board.carrierY = 381;
								}
								else {
									player1Board.carrierY = storedY;
								}
								player1Board.carrierX = storedX;
							}
							
							startX = calculatePlayer1ColumnIndex(player1Board.carrierX);
							startY = calculatePlayer1RowIndex(player1Board.carrierY);
							
							successfullyAddedShip = player1Board.addShip(ship, startY, startX, orientation);
						} else {
							if (player2Board.ROTCarrier == false) {
								orientation = BattleGrid.ShipRotation.HORIZONTAL;							
								if (x > 887) {								
									player2Board.carrierX = 887;
								}
								else {
									player2Board.carrierX = storedX;
								}
								player2Board.carrierY = storedY;
							}
							else if (player2Board.ROTCarrier == true) {
								orientation = BattleGrid.ShipRotation.VERTICAL;
								if (y > 381) {
									player2Board.carrierY = 381;
								}
								else {
									player2Board.carrierY = storedY;
								}
								player2Board.carrierX = storedX;
							}
							
							startX = calculatePlayer2ColumnIndex(player2Board.carrierX);
							startY = calculatePlayer2RowIndex(player2Board.carrierY);
							
							successfullyAddedShip = player2Board.addShip(ship, startY, startX, orientation);
						}
						
						if (successfullyAddedShip) {					
							shipSwitch = 6;
							drawCarrier = true;
						}
						else
							clickCount--;
							
					}
					else if (shipSwitch == 6 && clickCount > 1) {	//Next ship, next click...
						ship = BattleGrid.ShipLabel.BATTLESHIP;

						if (playerPlacingShips == Player.Player1) {
							if (player1Board.ROTBattleship == false) {
								orientation = BattleGrid.ShipRotation.HORIZONTAL;
								if (x > 373) {
									player1Board.battleshipX = 373;
								}
								else {
									player1Board.battleshipX = storedX;
								}
								player1Board.battleshipY = storedY;
							}
							else if (player1Board.ROTBattleship == true) {
								orientation = BattleGrid.ShipRotation.VERTICAL;
								if (y > 431) {
									player1Board.battleshipY = 431;
								}
								else {
									player1Board.battleshipY = storedY;
								}
								player1Board.battleshipX = storedX;
							}
							
							startX = calculatePlayer1ColumnIndex(player1Board.battleshipX);
							startY = calculatePlayer1RowIndex(player1Board.battleshipY);
						
							successfullyAddedShip = player1Board.addShip(ship, startY, startX, orientation);
							
						} else {
							if (player2Board.ROTBattleship == false) {
								orientation = BattleGrid.ShipRotation.HORIZONTAL;
								if (x > 934) {
									player2Board.battleshipX = 934;
								}
								else {
									player2Board.battleshipX = storedX;
								}
								player2Board.battleshipY = storedY;
							}
							else if (player2Board.ROTBattleship == true) {
								orientation = BattleGrid.ShipRotation.VERTICAL;
								if (y > 431) {
									player2Board.battleshipY = 431;
								}
								else {
									player2Board.battleshipY = storedY;
								}
								player2Board.battleshipX = storedX;
							}
							
							startX = calculatePlayer2ColumnIndex(player2Board.battleshipX);
							startY = calculatePlayer2RowIndex(player2Board.battleshipY);
							
							successfullyAddedShip = player2Board.addShip(ship, startY, startX, orientation);
						}
						
						if (successfullyAddedShip) {
						    shipSwitch = 5;
						    drawBattleship = true;
						}
						else
							clickCount--;
							
					}
					else if (shipSwitch == 5 && clickCount > 2) {	//And so on until number of unplaced ships is zero (line 327)..
						ship = BattleGrid.ShipLabel.CRUISER;

						if ( playerPlacingShips == Player.Player1 ) {
							if (player1Board.ROTCruiser == false) {
								orientation = BattleGrid.ShipRotation.HORIZONTAL;
								if (x > 423) {
									player1Board.cruiserX = 423;
								}
								else {
									player1Board.cruiserX = storedX;
								}
								player1Board.cruiserY = storedY;
							}
							else if (player1Board.ROTCruiser == true) {
								orientation = BattleGrid.ShipRotation.VERTICAL;
								if (y > 481) {
									player1Board.cruiserY = 481;
								}
								else {
									player1Board.cruiserY = storedY;
								}
								player1Board.cruiserX = storedX;
							}
							
							startX = calculatePlayer1ColumnIndex(player1Board.cruiserX);
							startY = calculatePlayer1RowIndex(player1Board.cruiserY);
						
							successfullyAddedShip = player1Board.addShip(ship, startY, startX, orientation);
								
						} else {
							//TODO
							if (player2Board.ROTCruiser == false) {
								orientation = BattleGrid.ShipRotation.HORIZONTAL;
								if (x > 988) {
									player2Board.cruiserX = 988;
								}
								else {
									player2Board.cruiserX = storedX;
								}
								player2Board.cruiserY = storedY;
							}
							else if (player2Board.ROTCruiser == true) {
								orientation = BattleGrid.ShipRotation.VERTICAL;
								if (y > 481) {
									player2Board.cruiserY = 481;
								}
								else {
									player2Board.cruiserY = storedY;
								}
								player2Board.cruiserX = storedX;
							}
							
							startX = calculatePlayer2ColumnIndex(player2Board.cruiserX);
							startY = calculatePlayer2RowIndex(player2Board.cruiserY);
							
							successfullyAddedShip = player2Board.addShip(ship, startY, startX, orientation);
						}
						
						if (successfullyAddedShip) {
							shipSwitch = 4;
							drawCruiser = true;
						}
						else
							clickCount--;
							
					}
					else if (shipSwitch == 4 && clickCount > 3) {
						
						ship = BattleGrid.ShipLabel.DESTROYER1;

						if ( playerPlacingShips == Player.Player1 ) {
							if (player1Board.ROTDestroyer1 == false) {
								orientation = BattleGrid.ShipRotation.HORIZONTAL;
								if (x > 473) {
									player1Board.destroyer1X = 473;
								}
								else {
									player1Board.destroyer1X = storedX;
								}
								player1Board.destroyer1Y = storedY;
							}
							else if (player1Board.ROTDestroyer1 == true) {
								orientation = BattleGrid.ShipRotation.VERTICAL;
								if (y > 531) {
									player1Board.destroyer1Y = 531;
								}
								else {
									player1Board.destroyer1Y = storedY;
								}
								player1Board.destroyer1X = storedX;
							}
							
							startX = calculatePlayer1ColumnIndex(player1Board.destroyer1X);
							startY = calculatePlayer1RowIndex(player1Board.destroyer1Y);
						
							successfullyAddedShip = player1Board.addShip(ship, startY, startX, orientation);

						} else {
							if (player2Board.ROTDestroyer1 == false) {
								orientation = BattleGrid.ShipRotation.HORIZONTAL;
								if (x > 1040) {
									player2Board.destroyer1X = 1040;
								}
								else {
									player2Board.destroyer1X = storedX;
								}
								player2Board.destroyer1Y = storedY;
							}
							else if (player2Board.ROTDestroyer1 == true) {
								orientation = BattleGrid.ShipRotation.VERTICAL;
								if (y > 531) {
									player2Board.destroyer1Y = 531;
								}
								else {
									player2Board.destroyer1Y = storedY;
								}
								player2Board.destroyer1X = storedX;
							}
							
							startX = calculatePlayer2ColumnIndex(player2Board.destroyer1X);
							startY = calculatePlayer2RowIndex(player2Board.destroyer1Y);
							
							successfullyAddedShip = player2Board.addShip(ship, startY, startX, orientation);
						}
						
						if (successfullyAddedShip) {
							shipSwitch = 3;
							drawDestroyer1 = true;
						}
						else
							clickCount--;
							
					}
					else if (shipSwitch == 3 && clickCount > 4) {
						ship = BattleGrid.ShipLabel.DESTROYER2;
						
						if ( playerPlacingShips == Player.Player1 ) {
							if (player1Board.ROTDestroyer2 == false) {
								orientation = BattleGrid.ShipRotation.HORIZONTAL;
								if (x > 473) {
									player1Board.destroyer2X = 473;
								}
								else {
									player1Board.destroyer2X = storedX;
								}
								player1Board.destroyer2Y = storedY;
							}
							else if (player1Board.ROTDestroyer2 == true) {
								orientation = BattleGrid.ShipRotation.VERTICAL;
								if (y > 531) {
									player1Board.destroyer2Y = 531;
								}
								else {
									player1Board.destroyer2Y = storedY;
								}
								player1Board.destroyer2X = storedX;
							}
							
							startX = calculatePlayer1ColumnIndex(player1Board.destroyer2X);
							startY = calculatePlayer1RowIndex(player1Board.destroyer2Y);
						
							successfullyAddedShip = player1Board.addShip(ship, startY, startX, orientation);
						} else {
							if (player2Board.ROTDestroyer2 == false) {
								orientation = BattleGrid.ShipRotation.HORIZONTAL;
								if (x > 1040) {
									player2Board.destroyer2X = 1040;
								}
								else {
									player2Board.destroyer2X = storedX;
								}
								player2Board.destroyer2Y = storedY;
							}
							else if (player2Board.ROTDestroyer2 == true) {
								orientation = BattleGrid.ShipRotation.VERTICAL;
								if (y > 531) {
									player2Board.destroyer2Y = 531;
								}
								else {
									player2Board.destroyer2Y = storedY;
								}
								player2Board.destroyer2X = storedX;
							}
							
							startX = calculatePlayer2ColumnIndex(player2Board.destroyer2X);
							startY = calculatePlayer2RowIndex(player2Board.destroyer2Y);
							
							successfullyAddedShip = player2Board.addShip(ship, startY, startX, orientation);

						}
						

						if (successfullyAddedShip) {
							shipSwitch = 2;
							drawDestroyer2 = true;
						}
						else
							clickCount--;
							
					}
					else if (shipSwitch == 2 && clickCount > 5) {
						
						
						orientation = BattleGrid.ShipRotation.HORIZONTAL; //In this case it doesn't matter, size 1
						ship = BattleGrid.ShipLabel.SUBMARINE1;
						
						switch (playerPlacingShips) {
							case Player1:
								player1Board.submarine1X = storedX;
								player1Board.submarine1Y = storedY;
								
								startX = calculatePlayer1ColumnIndex(player1Board.submarine1X);
								startY = calculatePlayer1RowIndex(player1Board.submarine1Y);
								
								successfullyAddedShip = player1Board.addShip(ship, startY, startX, orientation);
								
								break;
								
							case Player2:
								player2Board.submarine1X = storedX;
								player2Board.submarine1Y = storedY;
								
								startX = calculatePlayer2ColumnIndex(player2Board.submarine1X);
								startY = calculatePlayer2RowIndex(player2Board.submarine1Y);
								
								successfullyAddedShip = player2Board.addShip(ship, startY, startX, orientation);
								
								break;
						}			
						
						if (successfullyAddedShip) {
							shipSwitch = 1;
							drawSubmarine1 = true;
						}
						else
							clickCount--;
							
					}
					else if (shipSwitch == 1 && clickCount > 6) {				
						
						
						orientation = BattleGrid.ShipRotation.HORIZONTAL; //In this case it doesn't matter, size 1
						ship = BattleGrid.ShipLabel.SUBMARINE2;						
						
						switch (playerPlacingShips) {
							case Player1:
								player1Board.submarine2X = storedX;
								player1Board.submarine2Y = storedY;
								
								startX = calculatePlayer1ColumnIndex(player1Board.submarine2X);
								startY = calculatePlayer1RowIndex(player1Board.submarine2Y);
								
								successfullyAddedShip = player1Board.addShip(ship, startY, startX, orientation);
								
								break;
								
							case Player2:
								player2Board.submarine2X = storedX;
								player2Board.submarine2Y = storedY;
								
								startX = calculatePlayer2ColumnIndex(player2Board.submarine2X);
								startY = calculatePlayer2RowIndex(player2Board.submarine2Y);
								
								successfullyAddedShip = player2Board.addShip(ship, startY, startX, orientation);
								
								break;
						}
						
						if (successfullyAddedShip) {
							drawSubmarine2 = true;
							
							if (playerPlacingShips == Player.Player1) {
								shipSwitch = 7;
								clickCount = 0;
								playerPlacingShips = Player.Player2;
								
								drawBattleship = false;
								drawCarrier = false;
								drawCruiser = false;
								drawDestroyer1 = false;
								drawDestroyer2 = false;
								drawSubmarine1 = false;
								drawSubmarine2 = false;								
							} else if (playerPlacingShips == Player.Player2) {
								allShipsPlaced = true;
								shipSwitch = 0;
								clickCount = 7;
							}
							
						}
						else
							clickCount--;
					}
				}
				
				
			}
		}
	}

	/**
	 * @param
	 * @return Triggered when mouse is released. Empty but required by MouseListener.
	 * @author Albraa Al Nabulsi
	 * @Modified March 29, 2013
	 */
	public void mouseReleased(MouseEvent e) {
		
	}
	
	/**
	 * @param
	 * @return Triggered when key is pressed. If the R key is pressed on the keyboard before deploying a 
	 * 		   ship, the ship's orientation is rotated.
	 * @author Albraa Al Nabulsi
	 * @Modified March 30, 2013
	 */
	public void keyPressed(KeyEvent e) {
		char pressed = e.getKeyChar();
		
		//Set rotation of ship placement phase
		
		if (pressed == 'r' | pressed == 'R') {
			if (playerPlacingShips == Player.Player1) {
				if (clickCount == 0)
					player1Board.ROTCarrier = !player1Board.ROTCarrier;
				else if (clickCount == 1)
					player1Board.ROTBattleship = !player1Board.ROTBattleship;
				else if (clickCount == 2)
					player1Board.ROTCruiser = !player1Board.ROTCruiser;
				else if (clickCount == 3)
					player1Board.ROTDestroyer1 = !player1Board.ROTDestroyer1;
				else if (clickCount == 4)
					player1Board.ROTDestroyer2 = !player1Board.ROTDestroyer2;
				else if (clickCount == 5)
					player1Board.ROTSubmarine1 = !player1Board.ROTSubmarine1;
				else if (clickCount == 6)
					player1Board.ROTSubmarine2 = !player1Board.ROTSubmarine2;
			} else {
				if (clickCount == 0)
					player2Board.ROTCarrier = !player2Board.ROTCarrier;
				else if (clickCount == 1)
					player2Board.ROTBattleship = !player2Board.ROTBattleship;
				else if (clickCount == 2)
					player2Board.ROTCruiser = !player2Board.ROTCruiser;
				else if (clickCount == 3)
					player2Board.ROTDestroyer1 = !player2Board.ROTDestroyer1;
				else if (clickCount == 4)
					player2Board.ROTDestroyer2 = !player2Board.ROTDestroyer2;
				else if (clickCount == 5)
					player2Board.ROTSubmarine1 = !player2Board.ROTSubmarine1;
				else if (clickCount == 6)
					player2Board.ROTSubmarine2 = !player2Board.ROTSubmarine2;
			}
		}
	}

	/**
	 * @param
	 * @return Triggered when key is released. Empty but required by KeyListener.
	 * @author Albraa Al Nabulsi
	 * @Modified March 30, 2013
	 */
	public void keyReleased(KeyEvent e) {
		
	}

	/**
	 * @param
	 * @return Triggered when typing. Empty but required by KeyListener.
	 * @author Albraa Al Nabulsi
	 * @Modified March 30, 2013
	 */
	public void keyTyped(KeyEvent e) {
		
	}
	
	/**
	 * @param
	 * @return Triggered when mouse is clicked and dragged. Empty but required by MouseMotionListener.
	 * @author Albraa Al Nabulsi
	 * @Modified March 31, 2013
	 */
	public void mouseDragged(MouseEvent e) {
		
	}

	/**
	 * @param
	 * @return Triggered wherever mouse is pointing.
	 * @author Albraa Al Nabulsi
	 * @Modified March 31, 2013
	 */
	public void mouseMoved(MouseEvent e) {
		x = e.getX();	//track x coordinates
		y = e.getY();	//track y coordinates
		
		if (x < 600)
			try {
				playerFlash(x, y);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		else
			try {
				enemyFlash(x, y);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

	}
	
	/**
	 * @param
	 * @return Function that generates fixed x and y coordinates at the top left of every 'box' in the 
	 * 		   board 'array' depending on were the pointer is, used by mousePressed.
	 * @author Albraa Al Nabulsi
	 * @Modified March 31, 2013
	 */
	public void player1Board(int x, int y) {
		if (x >= 73 && x <= (73 + 495) && y >= 131 && y <= (131 + 495)) {
			boolean terminate = false;
			for (int i = 73; i < (73 + 50*10); i+=50) {
				for (int j = 131; j < (131 + 50*10); j+=50) {
					if (x >= i && x <= (i+45) && y >= j && y <= (j+45)) {
						storedX = i;
						storedY = j;
						terminate = true;
					}
					if (terminate)
						break;
				}
				if (terminate)
					break;
			} 
			if (!terminate) {
				Switch = false;
			}
		}
		else {
			Switch = false;
		}
	}
	
	/**
	 * @author Omar Abdel Bari
	 * @Modified July 24, 2016
	 */
	public void player2Board(int x, int y) {
		if (x >= 633 && x <= (633+ 495) && y >= 131 && y <= (131 + 495)) {
			boolean terminate = false;
			for (int i = 633; i < (633 + 50*10); i+=50) {
				for (int j = 131; j < (131 + 50*10); j+=50) {
					if (x >= i && x <= (i+45) && y >= j && y <= (j+45)) {
						storedX = i;
						storedY = j;
						terminate = true;
					}
					if (terminate)
						break;
				}
				if (terminate)
					break;
			} 
			if (!terminate) {
				Switch = false;
			}
		}
		else {
			Switch = false;
		}
	}

	/**
	 * @param
	 * @return Mutator - changes grid indices to pixels.
	 * @author Albraa Al Nabulsi
	 * @Modified April 2, 2013
	 */
	 /*
	public void enemyBoard(int row, int column, int shipNumber) {
		int x = 633 + column*50;
		int y = 131 + row*50;
		
		if (shipNumber == 7) {
			AIcarrierX = x;
			AIcarrierY = y;
		}
		else if (shipNumber == 6) {
			AIbattleshipX = x;
			AIbattleshipY = y;
		}
		else if (shipNumber == 5) {
			AIcruiserX = x;
			AIcruiserY = y;
		}
		else if (shipNumber == 4) {
			AIdestroyer1X = x;
			AIdestroyer1Y = y;
		}
		else if (shipNumber == 3) {
			AIdestroyer2X = x;
			AIdestroyer2Y = y;
		}
		else if (shipNumber == 2) {
			AIsubmarine1X = x;
			AIsubmarine1Y = y;
		}
		else if (shipNumber == 1) {
			AIsubmarine2X = x;
			AIsubmarine2Y = y;
		}
		
	}*/
	
	/**
	 * @param
	 * @return Function that generates fixed x and y coordinates at the top left of every 'box' in the 
	 * 		   player board 'array' depending on were the pointer is, controls light Switches.
	 * @author Albraa Al Nabulsi
	 * @throws Exception 
	 * @Modified March 31, 2013
	 */
	public void playerFlash(int x, int y) throws Exception {
		if (x >= 73 && x <= (73 + 495) && y >= 131 && y <= (131 + 495)) {
			boolean terminate = false;
			for (int i = 73; i < (73 + 50*10); i+=50) {
				for (int j = 131; j < (131 + 50*10); j+=50) {
					if (x >= i && x <= (i+45) && y >= j && y <= (j+45)) {
						if (player1Board.getTileState(calculatePlayer1RowIndex(y), calculatePlayer1ColumnIndex(x)) == BattleGrid.SquareState.HIT)
							lightSwitch = 3;
						else
							lightSwitch = 1;
						flashX = i;
						flashY = j;
						terminate = true;
					}
					if (terminate) //Exit loop if found the location
						break;
				}
				if (terminate) //Exit loop if square is located
					break;
			} 
			if (!terminate) {
				lightSwitch = 0;
				flashX = 0;
				flashY = 0;
			}
		}
		else {
			lightSwitch = 0;
			flashX = 0;
			flashY = 0;
		}
	}
	
	/**
	 * @param
	 * @return Function that generates fixed x and y coordinates at the top left of every 'box' in the 
	 * 		   enemy board 'array' depending on were the pointer is, controls light Switches.
	 * @author Albraa Al Nabulsi
	 * @throws Exception 
	 * @Modified March 31, 2013
	 */
	public void enemyFlash(int x, int y) throws Exception {
		if (x >= 633 && x <= (633 + 495) && y >= 131 && y <= (131 + 495)) {
			boolean terminate = false;
			for (int i = 633; i < (633 + 50*10); i+=50) {
				for (int j = 131; j < (131 + 50*10); j+=50) {
					if (x >= i && x <= (i+45) && y >= j && y <= (j+45)) {
						if (computer.computerGrid.getTileState(calculatePlayer2RowIndex(y), calculatePlayer2ColumnIndex(x)) == BattleGrid.SquareState.HIT)
							lightSwitch = 3;
						else
							lightSwitch = 2;
						flashX = i;
						flashY = j;
						terminate = true;
					}
					if (terminate) //Exit loop if found the location
						break;
				}
				if (terminate) //Exit loop if square is located
					break;
			} 
			if (!terminate) {
				lightSwitch = 0;
				flashX = 0;
				flashY = 0;
			}
		}
		else {
			lightSwitch = 0;
			flashX = 0;
			flashY = 0;
		}
	}
	
	/**
	 * @param
	 * @return Saves coordinates of ships to a file.
	 * @author Albraa Al Nabulsi
	 * @Modified April 2, 2013
	 */
	public void Save(GameMode mode) {
		//Save the following in the first few lines first
		//carrierX, battleshipX, cruiserX, destroyer1X, destroyer2X, submarine1X, submarine2X
		//carrierY, battleshipY, cruiserY, destroyer1Y, destroyer2Y, submarine1Y, submarine2Y
		//Export data from other used modules  
		
		BattleGrid computerGrid = computer.computerGrid;
      
        try{
          FileWriter fileStream = new FileWriter(InputOutput.SAVE_FILE_NAME);
          PrintWriter fileWriter = new PrintWriter(fileStream);
    	  //Lines 1 to 2
          fileWriter.println(mode.toString());    	  
  
    	  //Print Data from BattleGrid
    	  if (currentMode == GameMode.OnePlayer)
    		  BattleGrid.exportData(fileWriter, player1Board, computerGrid);
    	  else if (currentMode == GameMode.TwoPlayer)
    		  BattleGrid.exportData(fileWriter, player1Board, player2Board);
  	      
  	      //Export data for AI
  	      if (currentMode == GameMode.OnePlayer)
  	    	  computer.exportData(fileWriter);
  	      
  	      fileStream.close();
	    }

	    catch( IOException ioe )
	    {
	      System.out.println("Error while attempting to save data ~ Board");
	    }   
	    
	}
	
	/**
	 * @param
	 * @return Loads coordinates of ships from a file.
	 * @author Albraa Al Nabulsi
	 * @Modified April 2, 2013
	 */
	public void Load() {
		try {		
		  FileReader fileStream = new FileReader(InputOutput.SAVE_FILE_NAME);
		  BufferedReader fileReader = new BufferedReader(fileStream);

		  resetGame();   
	 
	      ArrayList<String> lineFields = new ArrayList<String>(); //Meant for holding delimited tokens from each line      
	      
	      //Read Line1 : Game Mode
	      currentMode = GameMode.valueOf(fileReader.readLine().trim());
	      
	      //Instantiate BattleGrid objects  	      
	      player1Board = new BattleGrid();
	      
	      if (currentMode == GameMode.OnePlayer) {
		      computer = new ArtificialIntelligence();
		      computer.computerGrid = new BattleGrid();
	      } else if (currentMode == GameMode.TwoPlayer) {
	    	  player2Board = new BattleGrid();
	      }
	      
	      BattleGrid computerGrid = computer.computerGrid;
	      
	    //Reads lines and splits into tokens
	      if (currentMode == GameMode.OnePlayer){
	    	  BattleGrid.importData(fileReader, player1Board, computer.computerGrid); 
	    	  computer.importData(fileReader);//Read AI details: ONLY applies to AI
	    	  
	    	  //Set all ships as viewable only if reading file was successful.
			  drawCarrier = true;
			  drawBattleship = true;
			  drawCruiser = true;
			  drawDestroyer1 = true;
			  drawDestroyer2 = true;
			  drawSubmarine1 = true;
			  drawSubmarine2 = true;  
	      }
	      else if (currentMode == GameMode.TwoPlayer) {
	    	  BattleGrid.importData(fileReader, player1Board, player2Board);
	      }
        	  
          //Applies to both game modes               
          
          //Finalization
          clickCount = 7;
          shipSwitch = 0;
          
          fileStream.close();        
	    }
	    catch( FileNotFoundException fnfe )
	    {
	    	JOptionPane.showMessageDialog(null, "No save records were found. The program will proceed to starting a new game.");
			computer.setComputerShips();
			namePrompt = true;
	    }
	    catch( IOException ioe )
	    {
	    	JOptionPane.showMessageDialog(null, "Save records may be corrupt or have been tampered with. The program will proceed to starting a new game.");
			computer.setComputerShips();
			namePrompt = true;
			throw new RuntimeException("Was an IOException");
	    }
	}
	
	//Methods for converting pixels to grid indices
	public int calculatePlayer1RowIndex (int mouseY) {
		//Range for Y coord 131 : 50 : 626 but width is 45
		int divisor = (mouseY-131) /50;
		int remainder = (mouseY-131) % 50;
		
		if (remainder <= 45)
		  return divisor;
		else //Otherwise it means it is not in a square but in the padding
		  return -1;
	}
	
	public int calculatePlayer1ColumnIndex (int mouseX) {
		//Range for X coord 73 : 50 : 568 but width is 45
		int divisor = (mouseX-73) /50;
		int remainder = (mouseX-73) % 50;
		
		if (remainder <= 45)
		  return divisor;
		else //Otherwise it means it is not in a square but in the padding
		  return -1;
	}
	
	public int calculatePlayer2RowIndex(int mouseY) {
	  //Range for y coord 131 : 50 : 626 but width is 45
		int divisor = (mouseY-131) /50;
		int remainder = (mouseY-131) % 50;
		
		if (remainder <= 45 && divisor >= 0)
		  return divisor;
		else //Otherwise it means it is not in a square but in the padding
		  return -1;  
	}
	
	public int calculatePlayer2ColumnIndex(int mouseX) { 
	  //Range for x coord 633 : 50 : 1128 but width is 45
		int divisor = (mouseX-633) /50;
		int remainder = (mouseX-633) % 50;
		
		if (remainder <= 45)
		  return divisor;
		else //Otherwise it means it is not in a square but in the padding
		  return -1;	  
	}
	
	/**
	 * Reset all the componenets required to start a new game.
	 * @author Omar Abdel Bari
	 * @Modified April 7 2013
	 */
	public void resetGame(){
	    player1Board = new BattleGrid();
	    computer = new ArtificialIntelligence();
		
		//Switches that indicate whether to draw the ships or not.
		drawCarrier = false;
		drawBattleship = false;
		drawCruiser = false;
		drawDestroyer1 = false;
		drawDestroyer2 = false;
		drawSubmarine1 = false;
		drawSubmarine2 = false;
		
		//Switches that indicates whether to draw the rotated image or not.
		player1Board.ROTCarrier = false;
		player1Board.ROTBattleship = false;
		player1Board.ROTCruiser = false;
		player1Board.ROTDestroyer1 = false;
		player1Board.ROTDestroyer2 = false;
		player1Board.ROTSubmarine1 = false;
		player1Board.ROTSubmarine2 = false;
		
		player2Board.ROTCarrier = false;
		player2Board.ROTBattleship = false;
		player2Board.ROTCruiser = false;
		player2Board.ROTDestroyer1 = false;
		player2Board.ROTDestroyer2 = false;
		player2Board.ROTSubmarine1 = false;
		player2Board.ROTSubmarine2 = false;
		
		//set booleans to default value
		allShipsPlaced = false;
		currentMode = GameMode.None;
		playerPlacingShips = Player.Player1;
		clickCount = 0;
		shipSwitch = 7;
		
		//Receive name again
		namePrompt = true;
	}
	

}