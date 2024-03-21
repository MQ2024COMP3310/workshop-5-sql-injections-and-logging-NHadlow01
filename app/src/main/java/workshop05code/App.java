package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            System.out.println("Wordle created and connected.");
        } else {
            System.out.println("Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            System.out.println("Wordle structures in place.");
        } else {
            System.out.println("Not able to launch. Sorry!");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                try {
                    wordleDatabaseConnection.addValidWord(i, line);
                    logger.log(Level.INFO, "Valid word added: " + line); // Log valid words
                } catch (InvalidWordException e) { // Assuming InvalidWordException is thrown for invalid words
                    logger.log(Level.SEVERE, "Invalid word in data.txt: " + line); // Log invalid words at severe level
                }
                i++;
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Not able to load data.txt.", e);
        }
        

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            String guess;
            while (true) {
                System.out.print("Enter a 4 letter word for a guess or q to quit: ");
                guess = scanner.nextLine();
                if (guess.equals("q")) break;
        
                if (wordleDatabaseConnection.isValidWord(guess)) {
                    System.out.println("Success! It is in the the list.\n");
                } else {
                    System.out.println("Sorry. This word is NOT in the list.\n");
                    logger.log(Level.INFO, "Invalid guess: " + guess); // Log invalid guess
                }
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.SEVERE, "Scanner error", e);
        }
        

    }
}