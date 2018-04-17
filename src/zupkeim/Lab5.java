package zupkeim;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Driver program for Data Structures lab assignment
 * @author t a y l o r@msoe.edu
 * @version 2018.03.26
 */
public class Lab5 {

    private static Logger exceptionLogger;


    /**
     * Program that reads in notes from a text file and plays a song
     * using the Guitar class to generate the sounds which are then
     * played by a SimpleAudio object.
     * @param args Ignored
     */
    public static void main(String[] args) {
        Guitar guitar = new Guitar();
        Scanner in = new Scanner(System.in);
        System.out.print("Please enter the filename you want to read in: ");
        String filename = in.nextLine();
        System.out.println("Do you want to use the default settings? Please type " +
                "'y' for yes or 'n' for no.");
        String userInput = "";
        userInput = in.nextLine();
        if(userInput.equalsIgnoreCase("y")){
            guitar = new Guitar();
        } else if(userInput.equalsIgnoreCase("n")){
            int userSampleRate = -1;
            float userDecayRate = -1f;

            System.out.print("Please enter your desired sample rate in hz (integer between 8000-48000)");
            if(in.hasNextInt()){
                userSampleRate = in.nextInt();
            } else {
                do {
                    in.nextLine();
                    System.err.print("\nPlease enter a valid integer between 8000 and 48000: ");
                    if(in.hasNextInt()){
                        userSampleRate = in.nextInt();
                    }
                } while(userSampleRate == -1);
            }

            System.out.println("Please enter a floating-point value for decay rate (between 0.0 and 1.0)");
            if(in.hasNextFloat()){
                userDecayRate = in.nextFloat();
            } else {
                do{
                    in.nextLine();
                    System.err.println("Please enter a valid float between 0.0 and 1.0");
                    if(in.hasNextFloat()){
                        userDecayRate = in.nextFloat();
                    }
                } while(userDecayRate==-1);
            }
            guitar = new Guitar(userSampleRate, userDecayRate);
        } else {
            while(!userInput.equalsIgnoreCase("y") || !userInput.equalsIgnoreCase("n")){
                System.err.println("Please enter valid input");
                System.out.println("Do you want to use the default settings? Please type " +
                        "'y' for yes or 'n' for no.");
                userInput = in.nextLine();
            }
        }

        int lineCount = 0;
        try (Scanner inFile = new Scanner(new File(Paths.get(filename).toString()))) { //
            while (inFile.hasNextLine()) {
                lineCount++;
                Note note = parseNote(inFile.nextLine());
                if(note != null){
                    guitar.addNote(note);
                } else {
                    System.err.println("Skipped line " + lineCount + "; was blank or malformed");
                }

            }
            guitar.play();
        } catch(FileNotFoundException notFound){
            System.err.println("File Not Found. Error message: " + notFound.getMessage());
        } catch(IOException ioException){
            System.err.println("Error message: " + ioException.getMessage());
        } catch(LineUnavailableException lineUnavailable){
            System.err.println("Error message: " + lineUnavailable.getMessage());
        }


    }
    
    /**
     * Returns a new Note initialized to the value represented by the specified String
     * @param line Description of a note with scientific pitch followed by duration in milliseconds.
     * @return Note represented by the String passed in.  Returns null if it is unable to parse
     * the note data correctly.
     */
    private static Note parseNote(String line) {
        Note returnNote = null;
        if(!line.equals("")) {
            String[] splitString = line.split(" ");
            String spn = splitString[0]; //Scientific Pitch Notation
            float duration = Float.parseFloat(splitString[1]);
            returnNote = new Note(spn, duration);
        }
        return returnNote;
    }
}
