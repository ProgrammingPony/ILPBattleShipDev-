import java.util.Random;
import java.util.LinkedList;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * All the support for Artificial Intelligence of the computer.
 * @author Omar Abdel Bari
 * @Modified April 4 2013
 */
public class ArtificialIntelligence {
  //---------------------------------------------------------
  //ATTRIBUTES
  //---------------------------------------------------------
  private LinkedList<Target> nextMoveStack = new LinkedList<Target>();
  BattleGrid computerGrid = new BattleGrid();

  //---------------------------------------------------------
  //CONSTRUCTORS
  //---------------------------------------------------------
  /**
   * @Modified April 4 2013
   */
    public ArtificialIntelligence() {
      //removed setComputerShips() for the situation in which the user wants to load an old game, now requires manual call
      this.addRandomHitTarget(); //Add first target location
    }
  //---------------------------------------------------------
  //OBJECTS
  //---------------------------------------------------------
  public class Target {
    int row = -1;
    int column = -1;
    Direction direction;    
    
    //Constructors
    public Target () {    
    }
    
    public Target (int column, int row, ArtificialIntelligence.Direction direction) {
      this.column = column;
      this.row = row;
      this.direction = direction;
    }
    
  }
  
  //---------------------------------------------------------
  //ENUMERATED TYPES
  //---------------------------------------------------------
  enum Direction {
    RIGHT,
    UP,
    LEFT,
    DOWN,
    SOURCE, //If it was the first successful hit
  }
  
  //---------------------------------------------------------
  //ACCESSORS
  //---------------------------------------------------------
  /**
   * @param 
   * @return column of the Target object.
   * @author Omar Abdel Bari
   * @Modified April 1 2013
   */
  public int getTargetColumn(Target target) {
    return target.column;
  }
  
  /**
   * @param 
   * @return row of the Target object.
   * @author Omar Abdel Bari
   * @Modified April 1 2013
   */
  public int getTargetRow(Target target) {
    return target.row;
  }
  
  /**
   * @param target
   * @return Direction constant indicating movement from source hit.
   * @author Omar Abdel Bari
   * @Modified April 1 2013
   */
  public static Direction getTargetDirection(Target target) {
    return target.direction;
  }
  
  //---------------------------------------------------------
  //MUTATORS
  //---------------------------------------------------------
  
  /**
   * Determines the next suitable choice for selecting a target and adds this to the nextMoveStack
   * @param prevTarget
   * @author Omar Abdel Bari
   * @Modified April 4 2013
   */
  public Target getNextMove(BattleGrid human) {
    Target target = nextMoveStack.pop();
    
    int targetColumn = target.column;
    int targetRow = target.row;
    
    //To catch any error with trying to access a row or column outside the grid
    try {
      //If this is the first hit in a chain then add the surrounding tiles to the stack
	  if ( (human.getShipOccupyingSquare(targetRow, targetColumn) != BattleGrid.ShipLabel.NO_SHIP) && (target.direction == Direction.SOURCE) && (human.getTileState(targetRow, targetColumn) == BattleGrid.SquareState.UNHIT))
	    addSurroundingTiles(target);
	  //If we are already in a chain of hits then it adds the next tile in the same direction away from the source of the chain.
	  else if ((human.getShipOccupyingSquare(targetRow, targetColumn) != BattleGrid.ShipLabel.NO_SHIP) && (target.direction != Direction.SOURCE)  && (human.getTileState(targetRow, targetColumn) == BattleGrid.SquareState.UNHIT))
	    addNextTargetInChain(target);
	} catch (Exception e) {
		System.out.println("(row, column) access out of Grid range. ~ getNextMove()");
	}     
    //If no ship was hit then do nothing

    //If stack is empty then add a random hit target
    if (nextMoveStack.isEmpty()) 
      addRandomHitTarget();  
    
    return target;
  }
  
  
  /**
   * Returns a random coordinate.
   * 
   * @return Target object for a random hit location on player grid.
   * @author Omar Abdel Bari
   * @Modified April 2 2013
   */
  private void addRandomHitTarget () {
    Target randomTarget;
    
    int randomRow =  getRandomNumber(BattleGrid.GRID_SIZE); //Column   
    int randomColumn = getRandomNumber(BattleGrid.GRID_SIZE); //Row
    
    randomTarget = new Target(randomColumn, randomRow, Direction.SOURCE);
    
    nextMoveStack.add(randomTarget);
  } 
  
  /**
   * @param 
   * @return Stack of next moves, from right counter-clockwise.
   * @author Omar Abdel Bari
   * @Modified April 6 2013
   */
  private void addSurroundingTiles(Target prevTarget) {
    Target target = new Target();
    //When playing the game (since it is a stack) these plays are done in opposite order in which they are added
    //Add right adjacent square first (if applicable) 	
    target.column = prevTarget.column + 1;
    target.row = prevTarget.row; 
    target.direction = Direction.RIGHT;
       
    if (BattleGrid.checkBounds(target.row, target.column))
      nextMoveStack.push(target);

    //Add top adjacent square second
    target = new Target();
    
    target.column = prevTarget.column;
    target.row = prevTarget.row-1; 
    target.direction = Direction.UP;
        
    if (BattleGrid.checkBounds(target.row, target.column))
        nextMoveStack.push(target);;
    
    //Add left adjacent square
    target = new Target();
    
    target.column = prevTarget.column-1;
    target.row = prevTarget.row; 
    target.direction = Direction.LEFT; 
        
    if (BattleGrid.checkBounds(target.row, target.column))
        nextMoveStack.push(target);;
    
    //Add bottom adjacent square
    target = new Target();
    
    target.column = prevTarget.column;
    target.row = prevTarget.row+1; 
    target.direction = Direction.DOWN;
    
    if (BattleGrid.checkBounds(target.row, target.column))
        nextMoveStack.push(target);;
  }
  
  /**
   * @param column
   * @param row
   * @param direction
   * @return integer array length 2 with coordinates (column, row)
   * @author Omar Abdel Bari
   * @Modified April 1 2013
   */
  private void addNextTargetInChain (Target previousTarget) {
    //Retrieve information about previous target
    int previousRow = this.getTargetRow(previousTarget);
    int previousColumn = this.getTargetColumn(previousTarget);
    Direction previousDirection = ArtificialIntelligence.getTargetDirection(previousTarget);

    //Filters out the two inapplicable direction cases
    if (previousDirection == Direction.SOURCE)
      return; 
    
    //Create New Target Fields  (defaulted to prevent error with compiler)
    int nextRow = previousRow;
    int nextColumn = previousColumn;
    //Direction for next is identical for previous Target so not included
    
    //Will only be true if one of the 4 directions is active
    boolean terminate = false;

    switch(previousDirection) {
    case UP:
      nextRow = previousRow - 1;
      nextColumn = previousColumn;
      terminate = true;
      break;
    case DOWN:
      nextRow = previousRow + 1;
      nextColumn = previousColumn;
      terminate = true;    
      break;
    case LEFT:
      nextRow = previousRow;
      nextColumn = previousColumn-1;
      terminate = true;
      break;      
    case RIGHT:
      nextRow = previousRow;
      nextColumn = previousColumn+1;
      terminate = true;
      break;
	default:
	  //Do nothing
	  break;
    }
    Target newTarget;
    
    //Inserts a new Target only if it is in a defined non-statutory direction and that the new square is not outside the c
    if (terminate && BattleGrid.checkBounds(nextRow, nextColumn)) {
      newTarget = new Target(nextColumn, nextRow, previousDirection); 
      nextMoveStack.push(newTarget);
    }
  }
  
  //---------------------------------------------------------
  //OTHER METHODS
  //---------------------------------------------------------
  /**
   * @return A random integer within the range of indices for grid coordinates
   * @author Omar Abdel Bari
   * @Modified March 31 2013
   */
  private int getRandomNumber(int max) {
    Random randomGeneration = new Random();
    
    int randomNumber = (int) ( max*randomGeneration.nextDouble());

    //Ensures that in the unlikely case that the random number is not one over.
    if (randomNumber >= max)
      randomNumber = max - 1;
    
    return randomNumber;
  }
  
  public void exportData (PrintWriter fileWriter) {   
    Target target;
    boolean terminate = false;
    
    //First pop
    target = nextMoveStack.pop();  
    try {
      while (!terminate) {
        
        fileWriter.println(Integer.toString(target.column) + InputOutput.DELIMITER + target.row + InputOutput.DELIMITER + target.direction.toString());
        if (!nextMoveStack.isEmpty())
          target = nextMoveStack.pop();
        else
          terminate = true;
      }
      
    }
    catch (Exception e) {
      System.out.println("Error while attempting to export data ~ AI");
      e.printStackTrace();
      fileWriter.close();
    }
  }
  
  /**
   * Imports AI Sequence of Targets from a file.
   * 
   * @param bufferedReader
   * @author Omar Abdel Bari
   * @Modified April 2 2013
   */
  public void importData (BufferedReader bufferedReader) {
    try {
      //
      Target target;
      int column, row;
      Direction direction;
      ArrayList<String> fields = new ArrayList<String>();
      String line = bufferedReader.readLine(); //Read First Line
      
      while (line != null) {
        fields = InputOutput.returnFieldsInLine(line);
        
        //Add to stack
        column = Integer.parseInt( fields.get(0) );
        row = Integer.parseInt( fields.get(1) );
        direction = Direction.valueOf( fields.get(2) );
        target = new Target(column, row, direction);        
        
        nextMoveStack.push(target);
        
        line = bufferedReader.readLine();
      }
    }
    catch (IOException e) {
      System.out.println("Error while trying to import data ~ ArtificalIntelligence");
    }
  }
  
  /**
   * Setup all the ships for computer on the grid.
   * 
   * @author Omar Abdel Bari
   * @Modified April 2 2013
   */
  public void setComputerShips() {
    
    boolean terminate = false; //Indicates when a ship has been placed.
    
    //Load Aircraft Carrier

    int row = -1, column = -1;
    BattleGrid.ShipRotation orientation = BattleGrid.ShipRotation.HORIZONTAL;
    
    while (!terminate) {
      orientation = getRandomShipRotation();
      ;    
      if (orientation == BattleGrid.ShipRotation.HORIZONTAL) {
        row = getRandomNumber(BattleGrid.GRID_SIZE);
        column = getRandomNumber(BattleGrid.GRID_SIZE - BattleGrid.AIRCRAFT_CARRIER_SIZE);
      }
      else {
        row = getRandomNumber(BattleGrid.GRID_SIZE - BattleGrid.AIRCRAFT_CARRIER_SIZE);
        column = getRandomNumber(BattleGrid.GRID_SIZE);
      }   
    
      terminate = computerGrid.addShip(BattleGrid.ShipLabel.AIRCRAFT_CARRIER, row, column, orientation);
    }

    //Load Battleship
    terminate = false;
    
    while (!terminate) {
      orientation = getRandomShipRotation();
       
      if (orientation == BattleGrid.ShipRotation.HORIZONTAL) {
          row = getRandomNumber(BattleGrid.GRID_SIZE);
          column = getRandomNumber(BattleGrid.GRID_SIZE - BattleGrid.BATTLESHIP_SIZE);
      }
      else {
        row = getRandomNumber(BattleGrid.GRID_SIZE - BattleGrid.BATTLESHIP_SIZE);
        column = getRandomNumber(BattleGrid.GRID_SIZE);
      }
    
        terminate = computerGrid.addShip(BattleGrid.ShipLabel.BATTLESHIP, row, column, orientation);
    }

    //Load Cruiser
    terminate = false;
    
    while (!terminate) {
      orientation = getRandomShipRotation();
      if (orientation == BattleGrid.ShipRotation.HORIZONTAL) {
          row = getRandomNumber(BattleGrid.GRID_SIZE);
          column = getRandomNumber(BattleGrid.GRID_SIZE - BattleGrid.CRUISER_SIZE);
      }
      else {
        row = getRandomNumber(BattleGrid.GRID_SIZE - BattleGrid.CRUISER_SIZE);
        column = getRandomNumber(BattleGrid.GRID_SIZE);
      }
      
      terminate = computerGrid.addShip(BattleGrid.ShipLabel.CRUISER, row, column, orientation);
    }

    //Load Destroyer1
    terminate = false;
    
    while (!terminate) {
    orientation = getRandomShipRotation();
      if (orientation == BattleGrid.ShipRotation.HORIZONTAL) {
        row = getRandomNumber(BattleGrid.GRID_SIZE);
        column = getRandomNumber(BattleGrid.GRID_SIZE - BattleGrid.DESTROYER_SIZE);
      }
      else {
        row = getRandomNumber(BattleGrid.GRID_SIZE - BattleGrid.DESTROYER_SIZE);
        column = getRandomNumber(BattleGrid.GRID_SIZE);
      }
      terminate = computerGrid.addShip(BattleGrid.ShipLabel.DESTROYER1, row, column, orientation);
    }
    
    //Load Destroyer2
    terminate = false;
    
    while (!terminate) {
      orientation = getRandomShipRotation();
      if (orientation == BattleGrid.ShipRotation.HORIZONTAL) {
        row = getRandomNumber(BattleGrid.GRID_SIZE);
        column = getRandomNumber(BattleGrid.GRID_SIZE - BattleGrid.DESTROYER_SIZE);
      }
      else {
        row = getRandomNumber(BattleGrid.GRID_SIZE - BattleGrid.DESTROYER_SIZE);
        column = getRandomNumber(BattleGrid.GRID_SIZE);
      }
        terminate = computerGrid.addShip(BattleGrid.ShipLabel.DESTROYER2, row, column, orientation);
    }
    
    //Load Submarine1
    terminate = false;
    
    while (!terminate) {
      orientation = getRandomShipRotation();
      if (orientation == BattleGrid.ShipRotation.HORIZONTAL) {
        row = getRandomNumber(BattleGrid.GRID_SIZE);
        column = getRandomNumber(BattleGrid.GRID_SIZE - BattleGrid.SUBMARINE_SIZE);
      }
      else {
        row = getRandomNumber(BattleGrid.GRID_SIZE - BattleGrid.SUBMARINE_SIZE);
        column = getRandomNumber(BattleGrid.GRID_SIZE);
      }
        terminate = computerGrid.addShip(BattleGrid.ShipLabel.SUBMARINE1, row, column, orientation);
    }
    
    //Load Submarine2
    terminate = false;
    
    while (!terminate) {
      orientation = getRandomShipRotation();
      if (orientation == BattleGrid.ShipRotation.HORIZONTAL) {
        row = getRandomNumber(BattleGrid.GRID_SIZE);
        column = getRandomNumber(BattleGrid.GRID_SIZE - BattleGrid.SUBMARINE_SIZE);
      }
      else {
        row = getRandomNumber(BattleGrid.GRID_SIZE - BattleGrid.SUBMARINE_SIZE);
        column = getRandomNumber(BattleGrid.GRID_SIZE);
      }
      terminate = computerGrid.addShip(BattleGrid.ShipLabel.SUBMARINE2, row, column, orientation);
    }    
  }
  
  private BattleGrid.ShipRotation getRandomShipRotation() {
    BattleGrid.ShipRotation randomRotation;    
    int randomInt = (int)(getRandomNumber(2));
    
    //The very rare case that truncation gives 2!
    if (randomInt == 2)
      randomInt = 1;
    
    //Convert randomInt to its appropriate rotation  
    if (randomInt == 0)
      randomRotation = BattleGrid.ShipRotation.HORIZONTAL;
    else
      randomRotation = BattleGrid.ShipRotation.VERTICAL;
    
    return randomRotation;    
  }
}
