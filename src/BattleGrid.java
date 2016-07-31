import java.util.Map;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.IndexOutOfBoundsException;

/**
 * Battle grid units containing additional information for each player
 * on what is occupied by each square as well as its current state.
 * 
 * @author Omar Abdel Bari
 * Last Modified: March 28 2013
 */
public class BattleGrid {
  //---------------------------------------------------------
  //ATTRIBUTES
  //---------------------------------------------------------
  private Map<ShipLabel, Integer> shipCounter; //Stores remaining unhit squares for each ship
  private GridFields[][] grid = new GridFields [BattleGrid.GRID_SIZE][BattleGrid.GRID_SIZE]; //Constants appear under PUBLIC below
  private int score = 0;
  private static PlayerTurn userTurn = PlayerTurn.PLAYER1; //Player1 is always human  
  private Map<ShipLabel, Integer> sourceRow; //Stores source rows for each ship
  private Map<ShipLabel, Integer> sourceColumn; //Stores source columns for each ship
  private Map<ShipLabel, ShipRotation> shipRotation;
  public String name;
  private ArrayList<int[]> hitTiles = new ArrayList<int[]>(); 
  
	//Coordinates of the ships.
	public int carrierX, battleshipX, cruiserX, destroyer1X, destroyer2X, submarine1X, submarine2X;
	public int carrierY, battleshipY, cruiserY, destroyer1Y, destroyer2Y, submarine1Y, submarine2Y;

	public boolean ROTCarrier = false, ROTBattleship = false, ROTCruiser = false, ROTDestroyer1 = false, ROTDestroyer2 = false, ROTSubmarine1 = false, ROTSubmarine2 = false;

  
  //---------------------------------------------------------
  //OBJECTS
  //---------------------------------------------------------
  /**
   * Data structure containing fields for each square in grid.
   * 
   * @author Omar Abdel Bari
   * Last Modified: March 28 2013
   */
  public class GridFields {
    //Fields  
    BattleGrid.ShipLabel occupiedShip;
    BattleGrid.SquareState shipState;
      
    //Constructors
    public GridFields () {
      this.occupiedShip = BattleGrid.ShipLabel.NO_SHIP;
      this.shipState = BattleGrid.SquareState.UNHIT;
    }  
    
  }
  //---------------------------------------------------------
  //ENUMERATED TYPES
  //---------------------------------------------------------
  public static enum ShipLabel { 
    NO_SHIP, //Default value for when no ship occupies a square
    AIRCRAFT_CARRIER,
    SUBMARINE1, SUBMARINE2,
    BATTLESHIP,
    CRUISER,
    DESTROYER1, DESTROYER2
    };
  
  //Status of the square  
  public static enum SquareState {
    UNHIT, //Default value
    HIT
  }
  
  //Rotation of the ship
  public static enum ShipRotation {
    VERTICAL,
    HORIZONTAL,
    UNDEFINED //Used for debugging
  }
  
  public static enum PlayerTurn {
    PLAYER1,
    PLAYER2
  }
  //---------------------------------------------------------
  //CONSTANTS
  //---------------------------------------------------------
  final static int SUBMARINE_SIZE = 1;
  final static int DESTROYER_SIZE = 2;
  final static int CRUISER_SIZE = 3;
  final static int BATTLESHIP_SIZE = 4;
  final static int AIRCRAFT_CARRIER_SIZE = 5;
  
  final static int GRID_SIZE = 10;
  /**
   * Default constructor setting up a BattleShip grid for a player assuming
   * no ships have been placed.
   * 
   * @author Omar Abdel Bari
   * Last Modified March 29 2013
   */
  BattleGrid () {
	//Initialize grid
	for (int i = 0; i <= (GRID_SIZE-1); i++) {
	  for (int j = 0; j <= (GRID_SIZE-1); j++) {
	    grid[i][j] = new GridFields();
	  }
	}
	
	//Initialize source row and column maps
	this.sourceRow = new HashMap<ShipLabel, Integer>();
	this.sourceColumn = new HashMap<ShipLabel,Integer>();
	this.shipRotation = new HashMap<ShipLabel,ShipRotation>();
	    
	//Initialize ship counters
    this.shipCounter = new HashMap<ShipLabel, Integer>();
    this.shipCounter.put(ShipLabel.AIRCRAFT_CARRIER, BattleGrid.AIRCRAFT_CARRIER_SIZE);
    this.shipCounter.put(ShipLabel.SUBMARINE1, SUBMARINE_SIZE);
    this.shipCounter.put(ShipLabel.SUBMARINE2, SUBMARINE_SIZE);
    this.shipCounter.put(ShipLabel.BATTLESHIP, BATTLESHIP_SIZE);
    this.shipCounter.put(ShipLabel.CRUISER, CRUISER_SIZE);
    this.shipCounter.put(ShipLabel.DESTROYER1, DESTROYER_SIZE);
    this.shipCounter.put(ShipLabel.DESTROYER2, DESTROYER_SIZE);
  }
  
  //---------------------------------------------------------  
  //ACCESSORS
  //---------------------------------------------------------
  /**
   * @author Omar Abdel Bari
   * @return coordinates (row, column) of tiles which have been marked as hit
   * @Modified April 6 2013
   */
  public ArrayList<int[]> getHitTiles() {
    return hitTiles;
  }
  
  /**
   * @param column
   * @param row
   * @return If the row and column given is within the bounds, ship occupying that tile is return. 
   *         If the row and column define a tile outside the boundaries, NO_SHIP returned.
   * @author Omar Abdel Bari
   * @Modified April 4 2013
   */
  public ShipLabel getShipOccupyingSquare(int row, int column) {
  ShipLabel ship;
    if (checkBounds(row, column)) {
      ship = this.grid[row][column].occupiedShip; } 
    else 
      ship = ShipLabel.NO_SHIP;
      
    return ship;
  }
  
  /**
   * Returns the current state of a square on the grid.
   * @param row
   * @param column
   * @return The state of the square that was identified
   * @author Omar Abdel Bari
   * @Modified March 29 2013
   */
  public SquareState getTileState (int row, int column)
        throws Exception {
        
    //Ensures that the row and column are not out of bounds
    boolean withinBounds = checkBounds(row, column);
    
    if (!withinBounds)
      throw new Exception("The entered row and columns are out of bounds.");
    else {
      BattleGrid.SquareState squareState;
      squareState = grid[row][column].shipState;
      return squareState; 
    }  
  }
  
  /**
   * Returns the player's score.
   * @return the player's score
   * @author Omar Abdel Bari
   * @Modified: March 30 2013
   */
  public int getPlayerScore() {
    return this.score;
  }
  
  /**
   * @param ship
   * @return integer containing row identification of ship source coordinate
   * @author Omar Abdel Bari
   */
  public int getShipSourceRow(ShipLabel ship) {
    return sourceRow.get(ship);
  }
  
  /**
   * @param ship
   * @return integer containing column identification of ship source coordinate
   * @author Omar Abdel Bari
   */
  public int getShipSourceColumn(ShipLabel ship) {
    return sourceColumn.get(ship);
  }
  
  /**
   * @return PlayerTurn enumerated constant indicating player1 = human or player2 = computer
   * @author Omar Abdel Bari
   * @Modified April 4 2013
   */
  public PlayerTurn getPlayerTurn() {
    return BattleGrid.userTurn;
  }
  
  public boolean getShipRotation(ShipLabel ship) {
    ShipRotation rotation = shipRotation.get(ship);
    
    if (rotation == ShipRotation.HORIZONTAL)
      return false;
    else
      return true;
  }
  
  //---------------------------------------------------------
  //MUTATORS
  //---------------------------------------------------------   
  /**
   * Alternates Player Turn.
   * @author Omar Abdel Bari
   * @Modified April 1 2013
   */
  public void changePlayerTurn() {
    if (BattleGrid.userTurn == PlayerTurn.PLAYER1)
      BattleGrid.userTurn = PlayerTurn.PLAYER2;
    else
      BattleGrid.userTurn = PlayerTurn.PLAYER1;      
  }
  
  /**
   * Decreases remaining non hit squares for ship by 1.
   * @param shipID
   * @author Omar Abdel Bari
   * Last Modified: March 29 2013
   */
  private void decreaseRemainingShipSquares (BattleGrid.ShipLabel shipID) {
    //If we are directed to lower the remaining squares for any ship
    if (shipID != BattleGrid.ShipLabel.NO_SHIP) {
      int oldValue = this.shipCounter.get(shipID);
      
      //If the oldValue stored is higher than 0 reduce by 1
      if (oldValue > 0) {
        this.shipCounter.remove(shipID);
        this.shipCounter.put(shipID, oldValue-1);
      }
      //If the old value stored is 0 do nothing     
    }
    //If we are directed to lower the remaining squares for NO_SHIP, do nothing     
  }
  
   /**
    * Marks a Square as being hit.
    * 
    * @param row
    * @param column
    * 
    * @return True if operation is successful.
    *         False if either the row or column exceeds the grid size or the square
    *         has already been hit in a previous turn.
    * 
    * @author Omar Abdel Bari
    * Last Modified April 6 2013
    */
    public boolean markSquareAsHit (int row, int column, BattleGrid opponent) {
     
      //row and column check done automatically by getSquareState
      try {
        BattleGrid.SquareState squareState = getTileState(row, column);
        
        /*If this point is reached that means that currentSquareState
          was successfully retrieved. The new state is then determined
          and the boolean is returned.*/
        if (squareState == BattleGrid.SquareState.UNHIT) {
          grid[row][column].shipState = BattleGrid.SquareState.HIT;
          
          ShipLabel shipOccupyingTile = grid[row][column].occupiedShip;
          
          //Add Tile to hit ship array
          int [] coordinate = new int[2];
          coordinate[0] = row;
          coordinate[1] = column;
          
          hitTiles.add(coordinate);
          
          //Operations for when the newly hit square is a ship occupied piece
          if (shipOccupyingTile != ShipLabel.NO_SHIP) {
            decreaseRemainingShipSquares(shipOccupyingTile);            
            BattleGrid.adjustScore(opponent, this);
            
          }
          
          return true;
        }
        else
          //This means that the square was already hit
          return false;
        }      
      catch (Exception e)
      {
        //This means that the coordinates used is out of bounds and nothing needs to be done here, handles by Board.
      }
      
      return false;     
    }
    
    /**
     * Adjusts the player score as required every time a hit is landed on a ship.
     * 
     * @param playerScored player who made the hit.
     * @param playerHit player who received the hit.
     * @param row
     * @param column
     * 
     * @author Omar Abdel Bari
     * Last Modified April 6 2013
     */
    public static void adjustScore (BattleGrid playerScored, BattleGrid playerHit) {
      playerScored.score++;
      playerHit.score--;
    }
    
    /**
     * @param ship
     * @param startRow
     * @param startColumn
     * @param orientation
     * @return
     */
    public boolean addShip (ShipLabel ship, int startRow, int startColumn, ShipRotation orientation) {
      if (ship == ShipLabel.NO_SHIP)
        return false;        
      else {          
        //Initialize every square it lies on to have that shiplabel active, depending on rotation
        if (orientation == ShipRotation.HORIZONTAL) {
          //First Ensure that tiles are not already occupied
          for (int j = 0; j < shipCounter.get(ship); j++ ) {
            if (this.grid[startRow][startColumn + j].occupiedShip != ShipLabel.NO_SHIP)
              return false;
          }

          
          //Place Ship if not occupied
          for (int j = 0; j < shipCounter.get(ship); j++ ) {
            this.grid[startRow][startColumn + j].occupiedShip = ship;
          }
          
        }       
              
        else if (orientation == ShipRotation.VERTICAL) {
          //First Ensure that tiles are not already occupied
          for (int i = 0; i < shipCounter.get(ship); i++) {
            if (this.grid[startRow + i][startColumn].occupiedShip != ShipLabel.NO_SHIP)
              return false;
          }
          
          //Place Ship if not occupied
          for (int i = 0; i < shipCounter.get(ship); i++) {
        	this.grid[startRow + i][startColumn].occupiedShip = ship;
          }     
        }   
        //This means orientation is UNDEFINED, this is used for debugging purposes
        else {
          System.out.println("The Orientation is Undefined while call made to addShip");
          return false;
        }
        
        //Must have reached this point to be successful
        loadShipSourceCoordinate(ship, startColumn, startRow, orientation); //Stores head of ship
        return true;  
              
      }
      
    }
    
   /**
    * Places ship rotation and head coordinate data into its field maps.
    * 
    * @param ship
    * @param column
    * @param row
    * @param orientation
    */
    private void loadShipSourceCoordinate(ShipLabel ship, int column, int row, ShipRotation orientation) {
      sourceRow.put(ship, row);
      sourceColumn.put(ship, column);
      shipRotation.put(ship, orientation);
    } 
   
   //---------------------------------------------------------
   //ADDITIONAL METHODS
   //--------------------------------------------------------- 
   /**
    * Checks the input row and column to see if it falls within the bounds.
    * @param row
    * @param column
    * @return true if the row and column correctly match the grid dimensions.
    *         false if otherwise.
    * @author Omar Abdel Bari
    * @Modified March 29 2013
    */
   public static boolean checkBounds (int row, int column) {
     if ((row > (GRID_SIZE - 1) || row < 0) || (column > (GRID_SIZE-1) || column < 0))
       return false;
     else
       return true;
   }
   
   /**
    * Saves current game data into directory.
    * @author Omar Abdel Bari
    * @Modified April 7 2013
    */
   public static void exportData (PrintWriter fileWriter, BattleGrid human, BattleGrid computer) {
     //Print userTurn, humanScore, computerScore, humanName
     fileWriter.println(userTurn.toString() + InputOutput.DELIMITER + human.getPlayerScore() + InputOutput.DELIMITER + computer.getPlayerScore());

     //Print shipLabel, state of tile column by column, row by row (each line has one element of grid)
     //First human, then computer
     ShipLabel ship;
     SquareState state;
        
     for (int i = 0; i < GRID_SIZE; i++) {
       for (int j = 0; j < GRID_SIZE; j++) {	  
         ship = human.grid[i][j].occupiedShip;
         state = human.grid[i][j].shipState;
		    
         fileWriter.println(ship.toString() + InputOutput.DELIMITER + state.toString());		  
       }
     }
	 	
     for (int i = 0; i < GRID_SIZE; i++) {
       for (int j = 0; j < GRID_SIZE; j++) {	  
         ship = computer.grid[i][j].occupiedShip;
         state = computer.grid[i][j].shipState;
			    
         fileWriter.println(ship.toString() + InputOutput.DELIMITER + state.toString());		  
       }
     }
     
     //Added 07/28/2016
     fileWriter.println(Integer.toString(human.carrierX) + InputOutput.DELIMITER + human.battleshipX + InputOutput.DELIMITER + human.cruiserX + InputOutput.DELIMITER + human.destroyer1X + InputOutput.DELIMITER + human.destroyer2X + InputOutput.DELIMITER + human.submarine1X + InputOutput.DELIMITER + human.submarine2X);
	 fileWriter.println(Integer.toString(human.carrierY) + InputOutput.DELIMITER + human.battleshipY + InputOutput.DELIMITER + human.cruiserY + InputOutput.DELIMITER + human.destroyer1Y + InputOutput.DELIMITER + human.destroyer2Y + InputOutput.DELIMITER + human.submarine1Y + InputOutput.DELIMITER + human.submarine2Y);
     
	 fileWriter.println(Integer.toString(computer.carrierX) + InputOutput.DELIMITER + computer.battleshipX + InputOutput.DELIMITER + computer.cruiserX + InputOutput.DELIMITER + computer.destroyer1X + InputOutput.DELIMITER + computer.destroyer2X + InputOutput.DELIMITER + computer.submarine1X + InputOutput.DELIMITER + computer.submarine2X);
	 fileWriter.println(Integer.toString(computer.carrierY) + InputOutput.DELIMITER + computer.battleshipY + InputOutput.DELIMITER + computer.cruiserY + InputOutput.DELIMITER + computer.destroyer1Y + InputOutput.DELIMITER + computer.destroyer2Y + InputOutput.DELIMITER + computer.submarine1Y + InputOutput.DELIMITER + computer.submarine2Y);
	  
     //Print ship Counters, First human then computer
     String shipCounterString = "";
     shipCounterString += human.shipCounter.get(ShipLabel.AIRCRAFT_CARRIER).toString() + InputOutput.DELIMITER;
     shipCounterString += human.shipCounter.get(ShipLabel.BATTLESHIP).toString() + InputOutput.DELIMITER;
     shipCounterString += human.shipCounter.get(ShipLabel.CRUISER).toString() + InputOutput.DELIMITER;
     shipCounterString += human.shipCounter.get(ShipLabel.DESTROYER1).toString() + InputOutput.DELIMITER;
     shipCounterString += human.shipCounter.get(ShipLabel.DESTROYER2).toString() + InputOutput.DELIMITER;
     shipCounterString += human.shipCounter.get(ShipLabel.SUBMARINE1).toString() + InputOutput.DELIMITER;
     shipCounterString += human.shipCounter.get(ShipLabel.SUBMARINE2).toString();
     fileWriter.println(shipCounterString);
     
     shipCounterString = "";
     shipCounterString += computer.shipCounter.get(ShipLabel.AIRCRAFT_CARRIER).toString() + InputOutput.DELIMITER;
     shipCounterString += computer.shipCounter.get(ShipLabel.BATTLESHIP).toString() + InputOutput.DELIMITER;
     shipCounterString += computer.shipCounter.get(ShipLabel.CRUISER).toString() + InputOutput.DELIMITER;
     shipCounterString += computer.shipCounter.get(ShipLabel.DESTROYER1).toString() + InputOutput.DELIMITER;
     shipCounterString += computer.shipCounter.get(ShipLabel.DESTROYER2).toString() + InputOutput.DELIMITER;
     shipCounterString += computer.shipCounter.get(ShipLabel.SUBMARINE1).toString() + InputOutput.DELIMITER;
     shipCounterString += computer.shipCounter.get(ShipLabel.SUBMARINE2).toString();
     fileWriter.println(shipCounterString);
   
     //Print sourceRow for first line, sourceColumn for second, rotation for third ~ human then computer
     //Not using keySet and for( : ) because we are unsure of the order of keys used in iteration
     String rowString = "";
     String columnString = "";
     String rotationString = "";
     
     rowString += human.sourceRow.get(ShipLabel.AIRCRAFT_CARRIER).toString() + InputOutput.DELIMITER;
     rowString += human.sourceRow.get(ShipLabel.BATTLESHIP).toString() + InputOutput.DELIMITER;
     rowString += human.sourceRow.get(ShipLabel.CRUISER).toString() + InputOutput.DELIMITER;
     rowString += human.sourceRow.get(ShipLabel.DESTROYER1).toString() + InputOutput.DELIMITER;
     rowString += human.sourceRow.get(ShipLabel.DESTROYER2).toString() + InputOutput.DELIMITER;
     rowString += human.sourceRow.get(ShipLabel.SUBMARINE1).toString() + InputOutput.DELIMITER;
     rowString += human.sourceRow.get(ShipLabel.SUBMARINE2).toString();
     fileWriter.println(rowString);
     
     columnString += human.sourceColumn.get(ShipLabel.AIRCRAFT_CARRIER).toString() + InputOutput.DELIMITER;
     columnString += human.sourceColumn.get(ShipLabel.BATTLESHIP).toString() + InputOutput.DELIMITER;
     columnString += human.sourceColumn.get(ShipLabel.CRUISER).toString() + InputOutput.DELIMITER;
     columnString += human.sourceColumn.get(ShipLabel.DESTROYER1).toString() + InputOutput.DELIMITER;
     columnString += human.sourceColumn.get(ShipLabel.DESTROYER2).toString() + InputOutput.DELIMITER;
     columnString += human.sourceColumn.get(ShipLabel.SUBMARINE1).toString() + InputOutput.DELIMITER;
     columnString += human.sourceColumn.get(ShipLabel.SUBMARINE2).toString();
     fileWriter.println(columnString);
     
     rotationString += human.shipRotation.get(ShipLabel.AIRCRAFT_CARRIER).toString() + InputOutput.DELIMITER;
     rotationString += human.shipRotation.get(ShipLabel.BATTLESHIP).toString() + InputOutput.DELIMITER;
     rotationString += human.shipRotation.get(ShipLabel.CRUISER).toString() + InputOutput.DELIMITER;
     rotationString += human.shipRotation.get(ShipLabel.DESTROYER1).toString() + InputOutput.DELIMITER;
     rotationString += human.shipRotation.get(ShipLabel.DESTROYER2).toString() + InputOutput.DELIMITER;
     rotationString += human.shipRotation.get(ShipLabel.SUBMARINE1).toString() + InputOutput.DELIMITER;
     rotationString += human.shipRotation.get(ShipLabel.SUBMARINE2).toString();
     fileWriter.println(rotationString);
     
     rowString = "";
     columnString = "";
     rotationString = "";
     
     rowString += computer.sourceRow.get(ShipLabel.AIRCRAFT_CARRIER).toString() + InputOutput.DELIMITER;
     rowString += computer.sourceRow.get(ShipLabel.BATTLESHIP).toString() + InputOutput.DELIMITER;
     rowString += computer.sourceRow.get(ShipLabel.CRUISER).toString() + InputOutput.DELIMITER;
     rowString += computer.sourceRow.get(ShipLabel.DESTROYER1).toString() + InputOutput.DELIMITER;
     rowString += computer.sourceRow.get(ShipLabel.DESTROYER2).toString() + InputOutput.DELIMITER;
     rowString += computer.sourceRow.get(ShipLabel.SUBMARINE1).toString() + InputOutput.DELIMITER;
     rowString += computer.sourceRow.get(ShipLabel.SUBMARINE2).toString();
     fileWriter.println(rowString);
     
     columnString += computer.sourceColumn.get(ShipLabel.AIRCRAFT_CARRIER).toString() + InputOutput.DELIMITER;
     columnString += computer.sourceColumn.get(ShipLabel.BATTLESHIP).toString() + InputOutput.DELIMITER;
     columnString += computer.sourceColumn.get(ShipLabel.CRUISER).toString() + InputOutput.DELIMITER;
     columnString += computer.sourceColumn.get(ShipLabel.DESTROYER1).toString() + InputOutput.DELIMITER;
     columnString += computer.sourceColumn.get(ShipLabel.DESTROYER2).toString() + InputOutput.DELIMITER;
     columnString += computer.sourceColumn.get(ShipLabel.SUBMARINE1).toString() + InputOutput.DELIMITER;
     columnString += computer.sourceColumn.get(ShipLabel.SUBMARINE2).toString();
     fileWriter.println(columnString);
     
     rotationString += computer.shipRotation.get(ShipLabel.AIRCRAFT_CARRIER).toString() + InputOutput.DELIMITER;
     rotationString += computer.shipRotation.get(ShipLabel.BATTLESHIP).toString() + InputOutput.DELIMITER;
     rotationString += computer.shipRotation.get(ShipLabel.CRUISER).toString() + InputOutput.DELIMITER;
     rotationString += computer.shipRotation.get(ShipLabel.DESTROYER1).toString() + InputOutput.DELIMITER;
     rotationString += computer.shipRotation.get(ShipLabel.DESTROYER2).toString() + InputOutput.DELIMITER;
     rotationString += computer.shipRotation.get(ShipLabel.SUBMARINE1).toString() + InputOutput.DELIMITER;
     rotationString += computer.shipRotation.get(ShipLabel.SUBMARINE2).toString();
     fileWriter.println(rotationString);

	 //Save the tiles that were hit, human then computer
	 String coordinateString = "";
	 for (int[] coordinate : human.hitTiles) {
	   coordinateString = coordinateString  + coordinate[0] + InputOutput.DELIMITER + coordinate[1] + InputOutput.DELIMITER;
	 }
	  
	 fileWriter.println(coordinateString);
	  
	 coordinateString = "";
	 for (int[] coordinate : computer.hitTiles) {
	   coordinateString = coordinateString  + coordinate[0] + InputOutput.DELIMITER + coordinate[1] + InputOutput.DELIMITER;
	 }
	  
	 fileWriter.println(coordinateString);
   
   }
     /**
      * Imports Saved Game Data from File relevant to BattleGrid. Another call required for AI Module
      * 
      * @param bufferedReader
      * @param human
      * @param computer
      * @author Omar Abdel Bari
      * @Modified April 7 2013
      */
     public static void importData(BufferedReader bufferedReader, BattleGrid human, BattleGrid computer) {
    
       try {
         ArrayList<String> lineFields = new ArrayList<String>();
     
         //Read Player Turn, scores and human name from line1
         lineFields = InputOutput.returnFieldsInLine( bufferedReader.readLine() ); //Read First Line
         
         human.userTurn = PlayerTurn.valueOf(lineFields.get(0));
         human.score = ( Integer.parseInt(lineFields.get(1)) );
         computer.score = ( Integer.parseInt(lineFields.get(2)) );
         
         //Read Human Grid Fields
         for (int i = 0; i < GRID_SIZE; i++) {
           for (int j = 0; j < GRID_SIZE; j++) {
             lineFields = InputOutput.returnFieldsInLine( bufferedReader.readLine() ); //Read Line by Line and separate fields
             
             human.grid[i][j].occupiedShip = ShipLabel.valueOf( lineFields.get(0) );
             human.grid[i][j].shipState = SquareState.valueOf( lineFields.get(1) );
           }
         }
         
         //Read Computer Grid Fields
         for (int i = 0; i < GRID_SIZE; i++) {
             for (int j = 0; j < GRID_SIZE; j++) {
               lineFields = InputOutput.returnFieldsInLine( bufferedReader.readLine() ); //Read Line by Line and separate fields
               
               computer.grid[i][j].occupiedShip = ShipLabel.valueOf( lineFields.get(0) );
               computer.grid[i][j].shipState = SquareState.valueOf( lineFields.get(1) );
             }
          }
         
         //Added 07/28/2016
         lineFields = InputOutput.returnFieldsInLine( bufferedReader.readLine() ); //Read First Line
         human.carrierX = Integer.parseInt(lineFields.get(0));
         human.battleshipX = Integer.parseInt(lineFields.get(1));
         human.cruiserX = Integer.parseInt(lineFields.get(2));
         human.destroyer1X = Integer.parseInt(lineFields.get(3));
         human.destroyer2X = Integer.parseInt(lineFields.get(4));
         human.submarine1X = Integer.parseInt(lineFields.get(5));
         human.submarine2X = Integer.parseInt(lineFields.get(6));
         
         lineFields = InputOutput.returnFieldsInLine( bufferedReader.readLine() ); //Read First Line
         human.carrierY = Integer.parseInt(lineFields.get(0));
         human.battleshipY = Integer.parseInt(lineFields.get(1));
         human.cruiserY = Integer.parseInt(lineFields.get(2));
         human.destroyer1Y = Integer.parseInt(lineFields.get(3));
         human.destroyer2Y = Integer.parseInt(lineFields.get(4));
         human.submarine1Y = Integer.parseInt(lineFields.get(5));
         human.submarine2Y = Integer.parseInt(lineFields.get(6));
         
         lineFields = InputOutput.returnFieldsInLine( bufferedReader.readLine() ); //Read First Line
         computer.carrierX = Integer.parseInt(lineFields.get(0));
         computer.battleshipX = Integer.parseInt(lineFields.get(1));
         computer.cruiserX = Integer.parseInt(lineFields.get(2));
         computer.destroyer1X = Integer.parseInt(lineFields.get(3));
         computer.destroyer2X = Integer.parseInt(lineFields.get(4));
         computer.submarine1X = Integer.parseInt(lineFields.get(5));
         computer.submarine2X = Integer.parseInt(lineFields.get(6));
         
         lineFields = InputOutput.returnFieldsInLine( bufferedReader.readLine() ); //Read First Line
         computer.carrierY = Integer.parseInt(lineFields.get(0));
         computer.battleshipY = Integer.parseInt(lineFields.get(1));
         computer.cruiserY = Integer.parseInt(lineFields.get(2));
         computer.destroyer1Y = Integer.parseInt(lineFields.get(3));
         computer.destroyer2Y = Integer.parseInt(lineFields.get(4));
         computer.submarine1Y = Integer.parseInt(lineFields.get(5));
         computer.submarine2Y = Integer.parseInt(lineFields.get(6));
          
          //Read ship counters, human then computer
          lineFields = InputOutput.returnFieldsInLine( bufferedReader.readLine() ); //Read First Line
          
          human.shipCounter = new HashMap<BattleGrid.ShipLabel, Integer>();
          human.shipCounter.put(ShipLabel.AIRCRAFT_CARRIER, Integer.valueOf(lineFields.get(0)));
          human.shipCounter.put(ShipLabel.BATTLESHIP, Integer.valueOf(lineFields.get(1)));
          human.shipCounter.put(ShipLabel.CRUISER, Integer.valueOf(lineFields.get(2)));
          human.shipCounter.put(ShipLabel.DESTROYER1, Integer.valueOf(lineFields.get(3)));
          human.shipCounter.put(ShipLabel.DESTROYER2, Integer.valueOf(lineFields.get(4)));
          human.shipCounter.put(ShipLabel.SUBMARINE1, Integer.valueOf(lineFields.get(5)));
          human.shipCounter.put(ShipLabel.SUBMARINE2, Integer.valueOf(lineFields.get(6)));
          
          lineFields = InputOutput.returnFieldsInLine( bufferedReader.readLine() ); //Read First Line
          
          computer.shipCounter = new HashMap<BattleGrid.ShipLabel, Integer>();
          computer.shipCounter.put(ShipLabel.AIRCRAFT_CARRIER, Integer.valueOf(lineFields.get(0)));
          computer.shipCounter.put(ShipLabel.BATTLESHIP, Integer.valueOf(lineFields.get(1)));
          computer.shipCounter.put(ShipLabel.CRUISER, Integer.valueOf(lineFields.get(2)));
          computer.shipCounter.put(ShipLabel.DESTROYER1, Integer.valueOf(lineFields.get(3)));
          computer.shipCounter.put(ShipLabel.DESTROYER2, Integer.valueOf(lineFields.get(4)));
          computer.shipCounter.put(ShipLabel.SUBMARINE1, Integer.valueOf(lineFields.get(5)));
          computer.shipCounter.put(ShipLabel.SUBMARINE2, Integer.valueOf(lineFields.get(6)));
          
          //Read sourceRow, sourceColumn, shipRotation ~ human then computer
          lineFields = InputOutput.returnFieldsInLine( bufferedReader.readLine() ); //Read First Line
          
          human.sourceRow = new HashMap<BattleGrid.ShipLabel, Integer>();       
          human.sourceRow.put(ShipLabel.AIRCRAFT_CARRIER, Integer.parseInt(lineFields.get(0))) ;
          human.sourceRow.put(ShipLabel.BATTLESHIP, Integer.parseInt(lineFields.get(1)));
          human.sourceRow.put(ShipLabel.CRUISER, Integer.parseInt(lineFields.get(2)));
          human.sourceRow.put(ShipLabel.DESTROYER1, Integer.parseInt(lineFields.get(3))) ;
          human.sourceRow.put(ShipLabel.DESTROYER2, Integer.parseInt(lineFields.get(4))) ;
          human.sourceRow.put(ShipLabel.SUBMARINE1, Integer.parseInt(lineFields.get(5))) ;
          human.sourceRow.put(ShipLabel.SUBMARINE2, Integer.parseInt(lineFields.get(6)));
          
          lineFields = InputOutput.returnFieldsInLine( bufferedReader.readLine() ); //Read First Line
          
          human.sourceColumn = new HashMap<BattleGrid.ShipLabel, Integer>();   
          human.sourceColumn.put(ShipLabel.AIRCRAFT_CARRIER, Integer.parseInt(lineFields.get(0))) ;
          human.sourceColumn.put(ShipLabel.BATTLESHIP, Integer.parseInt(lineFields.get(1)));
          human.sourceColumn.put(ShipLabel.CRUISER, Integer.parseInt(lineFields.get(2)));
          human.sourceColumn.put(ShipLabel.DESTROYER1, Integer.parseInt(lineFields.get(3))) ;
          human.sourceColumn.put(ShipLabel.DESTROYER2, Integer.parseInt(lineFields.get(4))) ;
          human.sourceColumn.put(ShipLabel.SUBMARINE1, Integer.parseInt(lineFields.get(5))) ;
          human.sourceColumn.put(ShipLabel.SUBMARINE2, Integer.parseInt(lineFields.get(6)));
          
          lineFields = InputOutput.returnFieldsInLine( bufferedReader.readLine() ); //Read First Line
          
          human.shipRotation = new HashMap<BattleGrid.ShipLabel, BattleGrid.ShipRotation>();   
          human.shipRotation.put(ShipLabel.AIRCRAFT_CARRIER, ShipRotation.valueOf(lineFields.get(0)));
          human.shipRotation.put(ShipLabel.BATTLESHIP, ShipRotation.valueOf(lineFields.get(1)));
          human.shipRotation.put(ShipLabel.CRUISER, ShipRotation.valueOf(lineFields.get(2)));
          human.shipRotation.put(ShipLabel.DESTROYER1, ShipRotation.valueOf(lineFields.get(3)));
          human.shipRotation.put(ShipLabel.DESTROYER2, ShipRotation.valueOf(lineFields.get(4)));
          human.shipRotation.put(ShipLabel.SUBMARINE1, ShipRotation.valueOf(lineFields.get(5)));
          human.shipRotation.put(ShipLabel.SUBMARINE2, ShipRotation.valueOf(lineFields.get(6)));
          
          lineFields = InputOutput.returnFieldsInLine( bufferedReader.readLine() ); //Read First Line
          
          computer.sourceRow = new HashMap<BattleGrid.ShipLabel, Integer>();       
          computer.sourceRow.put(ShipLabel.AIRCRAFT_CARRIER, Integer.parseInt(lineFields.get(0))) ;
          computer.sourceRow.put(ShipLabel.BATTLESHIP, Integer.parseInt(lineFields.get(1)));
          computer.sourceRow.put(ShipLabel.CRUISER, Integer.parseInt(lineFields.get(2)));
          computer.sourceRow.put(ShipLabel.DESTROYER1, Integer.parseInt(lineFields.get(3))) ;
          computer.sourceRow.put(ShipLabel.DESTROYER2, Integer.parseInt(lineFields.get(4))) ;
          computer.sourceRow.put(ShipLabel.SUBMARINE1, Integer.parseInt(lineFields.get(5))) ;
          computer.sourceRow.put(ShipLabel.SUBMARINE2, Integer.parseInt(lineFields.get(6)));
          
          lineFields = InputOutput.returnFieldsInLine( bufferedReader.readLine() ); //Read First Line
          
          computer.sourceColumn = new HashMap<BattleGrid.ShipLabel, Integer>();   
          computer.sourceColumn.put(ShipLabel.AIRCRAFT_CARRIER, Integer.parseInt(lineFields.get(0))) ;
          computer.sourceColumn.put(ShipLabel.BATTLESHIP, Integer.parseInt(lineFields.get(1)));
          computer.sourceColumn.put(ShipLabel.CRUISER, Integer.parseInt(lineFields.get(2)));
          computer.sourceColumn.put(ShipLabel.DESTROYER1, Integer.parseInt(lineFields.get(3))) ;
          computer.sourceColumn.put(ShipLabel.DESTROYER2, Integer.parseInt(lineFields.get(4))) ;
          computer.sourceColumn.put(ShipLabel.SUBMARINE1, Integer.parseInt(lineFields.get(5))) ;
          computer.sourceColumn.put(ShipLabel.SUBMARINE2, Integer.parseInt(lineFields.get(6)));
          
          lineFields = InputOutput.returnFieldsInLine( bufferedReader.readLine() ); //Read First Line
          
          computer.shipRotation = new HashMap<BattleGrid.ShipLabel, BattleGrid.ShipRotation>();   
          computer.shipRotation.put(ShipLabel.AIRCRAFT_CARRIER, ShipRotation.valueOf(lineFields.get(0)));
          computer.shipRotation.put(ShipLabel.BATTLESHIP, ShipRotation.valueOf(lineFields.get(1)));
          computer.shipRotation.put(ShipLabel.CRUISER, ShipRotation.valueOf(lineFields.get(2)));
          computer.shipRotation.put(ShipLabel.DESTROYER1, ShipRotation.valueOf(lineFields.get(3)));
          computer.shipRotation.put(ShipLabel.DESTROYER2, ShipRotation.valueOf(lineFields.get(4)));
          computer.shipRotation.put(ShipLabel.SUBMARINE1, ShipRotation.valueOf(lineFields.get(5)));
          computer.shipRotation.put(ShipLabel.SUBMARINE2, ShipRotation.valueOf(lineFields.get(6)));
          
          //Load hit tiles ~ Human then computer
          lineFields = InputOutput.returnFieldsInLine( bufferedReader.readLine() ); //Read First Line
          int counter = 0;
          int [] coordinates;
          try {
            while(lineFields.get(counter) != null) {
              coordinates = new int[2];
              coordinates[0] = Integer.parseInt(lineFields.get(counter));
              coordinates[1] = Integer.parseInt(lineFields.get(counter+1));
              human.hitTiles.add(coordinates);
            
              counter+=2; //Read next 2
            }
          }
          catch (IndexOutOfBoundsException e) {
            //Means its empty so we ignore.
          }
          
          lineFields = InputOutput.returnFieldsInLine( bufferedReader.readLine() ); //Read First Line
          counter = 0;
          
          try {
            while(lineFields.get(counter) != null) {
              coordinates = new int[2];
              coordinates[0] = Integer.parseInt(lineFields.get(counter));
              coordinates[1] = Integer.parseInt(lineFields.get(counter+1));
              computer.hitTiles.add(coordinates);
              
              counter+=2;
            }
          }
          catch (IndexOutOfBoundsException e) {
            //Do nothing, means empty
          }
          
       }
       
       catch (IOException e) {
         System.out.println("Error Occurred While Reading Saved File ~ BattleGrid");
       }
    }
    
    /**
     * Determines and returns whether not the termination condition for the game has been made.
     * 
     * @return True if all ships have been destroyed on the player's grid.
     *         False if at least one ship has an unhit square.
     * @author Omar Abdel Bari
     * @Modified April 6 2013
     */
    public boolean checkVictoryCondition() {
      boolean allShipsDestroyed = true;
      
      //Checks the amount of ship squares not hit remaining for each ship in shipCounter
      for (ShipLabel ship : shipCounter.keySet()) {      
        //There isnt a NO_SHIP key in the counter map so we skip
        if (ship == ShipLabel.NO_SHIP)
          continue;
        else {
          //If any ship has more than one unhit square, victory condition has not been made
          if (shipCounter.get(ship) != 0)
            return false;
        }    
      }
      
      //If it reached this point all the ships have had their squares hit, default value true is returned.
      return allShipsDestroyed;
    }
    
    public boolean isEmpty() {      
      //Check all the tiles to see if any shipLabels are stored
      for (int i = 0; i < GRID_SIZE; i++) {
        for (int j = 0; j < GRID_SIZE; j++) {
          if (grid[i][j].occupiedShip != ShipLabel.NO_SHIP)
            return false;
        }
      }
      
      //Must mean no ship occupies any square to reach this point
      return true;
    }
}
