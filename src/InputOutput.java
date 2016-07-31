import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
/**
 * Serves clients with operations that are required for IO.
 * 
 * @author Omar Abdel Bari
 * @Modified April 1 2013
 */
public class InputOutput {
  //---------------------------------------------------------
  //CONSTANTS
  //---------------------------------------------------------
  final public static char DELIMITER = ',';
  final public static String SAVE_FILE_NAME = "Save.txt";
  final public static String SCORE_FILE_NAME = "Scores.txt";
  
  //---------------------------------------------------------
  //METHODS
  //---------------------------------------------------------  
  /**
   * @param
   * @return String array with all the fields divided by the delimiter in the given string input.
   * @author Omar Abdel Bari
   * @Modified April 1 2013
   */
  public static ArrayList<String> returnFieldsInLine (String line) {
    ArrayList<String> fields = new ArrayList<String>();
    
    String field;
    boolean successfulAddition = false; //Marks whether addition of element to ArrayList successful
    int previousDelimiter = 0, nextDelimiter;

    //Find end of first field    
    if (line!=null) {
      nextDelimiter = line.indexOf(DELIMITER);
      field = line.substring(previousDelimiter, nextDelimiter);
    }
    else
      return fields;
    
    //Read all fields in line divided by delimiter and add one by one into ArrayList
    try {
      while (nextDelimiter != -1 && previousDelimiter != (line.length()-1) ) {
        
        if (previousDelimiter < (line.length()-1)) {
            nextDelimiter = line.indexOf(DELIMITER, previousDelimiter + 1);
            
            if (previousDelimiter == 0)
              field = line.substring(previousDelimiter, nextDelimiter);
            else if (nextDelimiter != -1)
          	  field = line.substring(previousDelimiter + 1, nextDelimiter);
            else if (previousDelimiter != (line.length()-1))
              field = line.substring(previousDelimiter + 1, line.length());       
          }
        
        successfulAddition = fields.add(field);        
        if (!successfulAddition)
            throw new Exception();         

        previousDelimiter = nextDelimiter; //Move old value of nextDelimiter before giving it new value

      } 
    } 
    catch (FileNotFoundException e) {
      System.out.println("Score.txt was not found in the directory");
    }      
    catch (Exception e) {
      System.out.println("Attempt to add field to ArrayList unsuccessful");
    }

    
    return fields;
  }
  
  /**
   * @param name
   * @return formatted String containing information for interface to display information regarding
   * player score, ranking and name.
   * @author Omar Abdel Bari
   * @Modified April 10, 2013
   */
  public static String getHighestPlayerStanding(String name) {
    
    //The case where the user remains anonymous
    if (name.equals("")) {
      return "NotFound" + DELIMITER + ' ' + DELIMITER + ' '; 
    }
    
    //When the user's name is defined
    else {
      //IO Resources
      try {
        FileReader fileStream = new FileReader(SCORE_FILE_NAME);
        BufferedReader bufferedReader = new BufferedReader(fileStream);

    
        //Finding Highest Standing
        ArrayList<String> fields; //fields of each line in file
        int playerHighestScore = -1; //Given sentinel value
        int playerBestRank = 0; //also a counter for the loop below, starts from highest ranking 1 and increases
        String line = bufferedReader.readLine();; //stores each line in the file, reads the first line in scores
      
        if (line != null) { //Ensure file not empty to prevent error.
          do {
            fields = new ArrayList<String>();
            fields = returnFieldsInLine(line);
      
            playerBestRank++;
        
            if (fields.get(0).equals(name))
              playerHighestScore = Integer.parseInt(fields.get(1));
          
            line = bufferedReader.readLine();
      
          } while ((line != null) && (playerHighestScore == -1) );
        }
      
        //Formatted String Generation
        String formattedOutput;

        if (playerHighestScore != -1)
          formattedOutput = name + DELIMITER + playerBestRank + DELIMITER + playerHighestScore;
        else
          formattedOutput = "NotFound" + DELIMITER + ' ' + DELIMITER + ' '; 
        
        fileStream.close();
        
        return formattedOutput;
      }
      catch (IOException e) {
        System.out.println("Error While Reading Lines from Scores.txt");
      }
    }
      //If it reaches this point then there was an error while reading
      return "Error";
  }
  
  /**
   * Updates Score.txt to have new player in the correct ranking. Highest score goes on the top.
   * @param player
   */
  public static void updateScores(BattleGrid player) {
	     String fileName = SCORE_FILE_NAME;
	     ArrayList<String> listOfScores = new ArrayList<String>(); //Also contains player names
	   
	     //First read old content of file and place in string ArrayList
	     try {
	       FileReader readerStream = new FileReader(fileName);
	       BufferedReader bufferedReader = new BufferedReader(readerStream);
	       
	       //Read First Line
	       String readLine = bufferedReader.readLine();
	       
	       while(readLine != null) {
	         listOfScores.add(readLine);
	         readLine = bufferedReader.readLine();
	       }
	       
	       readerStream.close();
	     }
	     catch (IOException e) {
	       //Situation where file was not able to be read, in which case we ignore assuming we create a new file.
	         System.out.println("Failed to create stream for reading scores. May be ok if its the first time we set scores to this file.");
	         
	     }
	     
	     //Determine location of new player (if same score then first in has higher ranking)
	     int playerScore = player.getPlayerScore();
	     int storedPlayerScore;
	     ArrayList<String> lineFields = new ArrayList<String>();
	     
	     //Run code only if there are scores previously in file and the name of the user is not null
	     if (!listOfScores.isEmpty() && (player.name != null)) {
	       for (int index = 0; index < listOfScores.size(); index++) {
	         //Retrieve String array of fields in line
	         lineFields = (returnFieldsInLine(listOfScores.get(index)));
	         
	         //Convert score from string to int (2nd element)
	         storedPlayerScore = Integer.parseInt(lineFields.get(1));
	         lineFields.clear(); //Clear out for next set
	         
	         //Compare with new score to be added and inserts, shifting old element right
	         if (storedPlayerScore < playerScore) {	           
	           listOfScores.add(index, player.name + DELIMITER + playerScore);	             
		       break; //Once we found the correct location we end the loop
	         }         
	       }
	     }
	     //When it's the first code to be entered
	     else
	       listOfScores.add(player.name + DELIMITER + playerScore);
	     
	     //Delete old content from file and add scores again with new one.
	     try {
	       FileWriter writerStream = new FileWriter(fileName);
	       PrintWriter fileWriter = new PrintWriter(writerStream);
	       
	       for (String index : listOfScores) {
	         fileWriter.println(index);
	       }
	       
	       writerStream.close(); //Resource Leaks are Bad! :(
	     }
	     catch (IOException e) {
	       System.out.println("Failed to create stream for writing scores.");
	     }     
	     
	   }  
}
