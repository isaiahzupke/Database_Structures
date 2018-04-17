package zupkeim;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.sound.sampled.LineUnavailableException;

/**
 * The Guitar class generates guitar sounds based on user input.
 * 
 * In order to use this class correctly, one must create a Guitar
 * object, add all of the desired notes to the object, and then
 * call the play() method.  The play() method will generate a
 * list of samples for all of the notes to be played (by calling
 * an internal method (jaffeSmith())) and then send them to the
 * audio output stream.
 * @author t a y l o r@msoe.edu
 * @version 2018.03.26_2.3
 */
public class Guitar {
    /** 
     * Default sample rate in Hz 
     */
    private static final int DEFAULT_SAMPLE_RATE = 8000;
    
    /** 
     * Maximum sample rate in Hz
     */
    private static final int MAX_SAMPLE_RATE = 48000;
    
    /** 
     * Default decay rate
     */
    private static final float DEFAULT_DECAY_RATE = 0.99f;
    
    /**
     * Queue of notes 
     */
    private Queue<Note> notes = new LinkedList<>();
    
    /**
     *  Sample rate in samples per second 
     */
    private int sampleRate;
    
    /** 
     * Decay rate 
     */
    private float decayRate;
        
    /**
     * Constructs a new Guitar object with the default sample rate
     * and decay rate.
     */
    public Guitar() {
        this.decayRate = DEFAULT_DECAY_RATE;
        this.sampleRate = DEFAULT_SAMPLE_RATE;
    }
    
    /**
     * Constructs a new Guitar object with the specified parameters.
     * If an invalid sampleRate or decayRate is specified, the
     * default value will be used and an error message is sent to
     * System.err.
     * @param sampleRate sample rate (between 8000 Hz and 48000 Hz)
     * @param decayRate decay rate (between 0.0f and 1.0f)
     */
    public Guitar(int sampleRate, float decayRate) {
        if(sampleRate < DEFAULT_SAMPLE_RATE || sampleRate > MAX_SAMPLE_RATE){
            System.err.println("Invalid Sample Rate. Will use default sample rate ("
                    + DEFAULT_SAMPLE_RATE + ") instead");
            this.sampleRate = DEFAULT_SAMPLE_RATE;
        } else {
            this.sampleRate = sampleRate;
        }

        if(decayRate < 0.0f || decayRate > 1.0f){
            System.err.println("Invalid Decay Rate. Will use default decay rate ("
                    + DEFAULT_DECAY_RATE + ") instead");
            this.decayRate = DEFAULT_DECAY_RATE;
        } else {
            this.decayRate = decayRate;
        }
    }
        
    /**
     * Adds the specified note to this Guitar.
     * @param note Note to be added.
     */
    public void addNote(Note note) {
        if(!notes.offer(note)){
            System.err.println("Note could not be added to queue due to space restrictions.");
        }
    }
    
    /**
     * Generates the audio samples for the notes listed in the
     * current Guitar object by calling the jaffeSmith algorithm and
     * sends the samples to the speakers.
     * @throws LineUnavailableException If audio line is unavailable.
     * @throws IOException If any other input/output problem is encountered.
     */
    public void play() throws LineUnavailableException, IOException {
        SimpleAudio simpleAudio = new SimpleAudio(sampleRate);
        simpleAudio.play(jaffeSmith());
    }

    /**
     * Uses the Jaffe-Smith algorithm to generate the audio samples.
     * <br />Implementation note:<br />
     * Use Jaffe-Smith algorithm described on the assignment page
     * to generate a sequence of samples for each note in the list
     * of notes.
     * 
     * @return List of samples comprising the pluck sound(s).
     */
    private List<Float> jaffeSmith() {
        //Initialization Phase
        List<Float> samples = new ArrayList<>();
        final int MILLI_OFFSET = 1000;
        Random random = new Random();
        Queue<Float> periodSamples = new LinkedList<>();

        for(int amtNotes = 0; amtNotes < notes.size(); amtNotes++){
            Note note = notes.poll();
            int samplesPerPeriod = (int)(sampleRate / note.getFrequency());
            float numberOfSamples = sampleRate * (note.getDuration() / MILLI_OFFSET);
            periodSamples = new LinkedList<>();
            for(int amtPeriodSamples = 0; amtPeriodSamples < samplesPerPeriod; amtPeriodSamples++){
                float randomNumber = random.nextFloat();
                if(Math.random()>.5){
                    randomNumber *= -1.0f;
                }
                periodSamples.offer(randomNumber);
            }

            //Looping Phase
            float previousSample = 0.0f;
            float currentSample;
            float newSample;

            for(int amtSamples = 0; amtSamples < numberOfSamples; amtSamples++){
                currentSample = periodSamples.poll();
                newSample = ((previousSample + currentSample)/2) * decayRate;
                periodSamples.offer(newSample);
                samples.add(newSample);
                previousSample = newSample;
            }
        }
        return samples;
    }

    /**
     * Returns an array containing all the notes in this Guitar.
     * OPTIONAL
     * @return An array of Notes in the Guitar object.
     */
    public Note[] getNotes() {
        throw new UnsupportedOperationException("Optional method not implemented");
    }
    
    /**
     * Creates a new file and writes to that file.
     * OPTIONAL
     * @param file File to write to.
     * @throws IOException If any other input/output problem is encountered.
     */
    public void write(File file) throws IOException {
        throw new UnsupportedOperationException("Optional method not implemented");
    }
}
