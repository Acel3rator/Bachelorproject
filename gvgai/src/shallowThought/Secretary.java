package shallowThought;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Scanner;

/*
 * Secretary for Agent: does all the reading and writing
 */
public class Secretary {

    // Writer for the actions file.
    private BufferedWriter writer;

    // Set this variable to FALSE to avoid logging the actions to a file.
    private static final boolean SHOULD_LOG = true;
    private static final boolean LEARNING = true;
    
	
    /*
     * Reads next exercise from exercise-file and returns the sorted components:
     * name of subAgent:game:level:RunsToOptimize:[parameters]
     * Counts down runs to optimize one.
     */
    public String[] readExercise(File file) {
    	Charset charset = Charset.forName("US-ASCII");
    	String line = null;
    	try (BufferedReader reader = Files.newBufferedReader(file.toPath(), charset)) {
    	    line = reader.readLine();
    	} catch (IOException x) {
    	    System.err.format("IOException: %s%n", x);
    	}
    	if (line == null) {return null;}
    	// Use regex to split exercise in its components
    	String[] exercise = line.split(":", 5);
    	// TODO: if this is the last repetition, delete entry otherwise count runs to go one down
    	return exercise;
    }
    
    /*
     * Writes to a file, appending!
     */
    void writeToFileAppend(File file, String string) {
    	try {
        	if(true){//SHOULD_LOG) {
        		if(!file.exists())
        		{
        			file.createNewFile();
        		}
        		// create an APPENDING writer
        		writer = new BufferedWriter(new FileWriter(file, true));
        		// write string
        		writer.write(string+"\r\n");
        		writer.close();
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
  

    void writeToFileReplaceLastLine(File file, String string) {
    	try {
        	if(SHOULD_LOG) {
        		if(!file.exists())
        		{
        			file.createNewFile();
        		}
        		// create an APPENDING writer
        		writer = new BufferedWriter(new FileWriter(file, true));
        		// delete last line:
        		RandomAccessFile f = new RandomAccessFile(file.getPath(), "rw");
        		long length = f.length() - 1;
        		f.seek(length);
      		    byte b = f.readByte();
        		do {                     
        		  length -= 1;
        		  f.seek(length);
        		  b = f.readByte();
        		} while(b != 10 && length>0);
        		f.setLength(length+1);
        		f.close();
        		// write string
        		writer.write(string+"\r\n");
        		writer.close();
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    /*
     * Deletes the first line of a file
     */
    public void deleteFirstLine(File file) {
    	Scanner fileScanner;
		try {
			fileScanner = new Scanner(file);
			fileScanner.nextLine();
			FileWriter fileStream = new FileWriter(file.getPath());
			BufferedWriter out = new BufferedWriter(fileStream);
			while(fileScanner.hasNextLine()) {
			    String next = fileScanner.nextLine();
			    if(next.equals("\n")) out.newLine();
			    else out.write(next);
			    out.newLine();
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
