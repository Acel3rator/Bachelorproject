package shallowThought;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.game.Observation;
import core.game.StateObservation;

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
     * Reads all lines from file and returns them as String-Array String[]
     */
    public String[] readAllLines(File file) {
    	Charset charset = Charset.forName("US-ASCII");
    	List<String> lines = new ArrayList<String>();
    	String line = null;
    	// read all lines:
    	try (BufferedReader reader = Files.newBufferedReader(file.toPath(), charset)) {
    	    while ((line = reader.readLine()) != null) {
    	    	lines.add(line);
    	    }
    	} catch (IOException x) {
    	    System.err.format("IOException: %s%n", x);
    	}
    	// turn list to array:
    	return lines.toArray(new String[lines.size()]);
    }

    /*
     * Gets parameters[][], including ranges and value types, from config file for cmaes
     * Currently from "./src/shallowThought/offline/config.txt"
     * Parameter ranges etc. have to be added MANUALLY
     * Returns parameters as a String[][], WITHOUT subAgent-name
     */
    public String[][] getParametersFromConfig(String subAgent) {
    	File config = new File("./src/shallowThought/offline/config.txt");
    	String[] allLines = readAllLines(config);
    	String[][] parameters = null;
		// search file for correct subAgent
    	for (String oneLine : allLines) {
    		String[] parameters_pre = oneLine.split(":", 0);  // splits subagentname:param1:param2:...
    		if (subAgent.equals(parameters_pre[0])) {  // right one found
    			parameters = new String[parameters_pre.length-1][4];
    	    	for (int i = 1; i < parameters_pre.length; i++) {
    	    		parameters[i-1] = parameters_pre[i].split(",", 0); // splits for param: paramName,min,max,type
    	    	}
    	    	return parameters;
    		}
    	}
    	// TODO raise error "no parameters specified"
    	return parameters;
    }
    
    /*
     * Gets number of parameters to be optimized from config file as dimension for cmaes
     */
    public int getDim(String subAgent) {
    	return getParametersFromConfig(subAgent).length;
    }
    
    /*
     * Looks for "didWin: " in specified records-file and expects integer 0 or 1
     * after that.
     * Returns true if player won a game specified in record
     */
    public boolean didWin(String recordPath) {
    	File record = new File(recordPath);
    	String[] allLines = readAllLines(record);
    	Pattern p = Pattern.compile("didWin: (.*)");
    	for (String line : allLines) {
    		Matcher m = p.matcher(line);
    		if (m.find()) {
    			if (Integer.parseInt(m.group(1)) == 1) return true;
    			else return false;
    		}
    	}
    	// TODO raise error, record file fucked
    	return false;
    }
    
    /*
     * Reads next exercise from exercise-file and returns the sorted components:
     * name of subAgent:game:level:RunsToOptimize:[parameters]
     * example: OLMCTSAgent:catapults:0:998/1000:MCTS_ITERATIONS=100&ROLLOUT_DEPTH=9:NUM_TURNS=1to100&REWARD_DISCOUNT=0.50to1.50
     * TODO : delete?
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
    	String[] exercise = line.split(":", 0);
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

    void writeToFileReplaceFirstLine(File file, String string) {
    	try {
        	if(!file.exists())
        	{
        		file.createNewFile();
        		writeToFileAppend(file, string);
        		return;
        	}
        	// delete first line:
        	deleteFirstLine(file);
        	// read in file:
        	Charset charset = Charset.forName("US-ASCII");
        	ArrayList<String> data = new ArrayList<String>(); 
        	try (BufferedReader reader = Files.newBufferedReader(file.toPath(), charset)) {
        		String line;
        	    while ((line = reader.readLine()) != null) data.add(line);
        	    reader.close();
        	} catch (IOException x) {
        	    System.err.format("IOException: %s%n", x);
        	}
        	// create an APPENDING writer
        	PrintWriter pWriter = new PrintWriter(file);
        	pWriter.close();
        	writer = new BufferedWriter(new FileWriter(file, false));
        	// write file
        	writeToFileAppend(file, string);
        	for (String s : data) {
        		writeToFileAppend(file, s);
    		}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    void writeLevelToFile(File file, StateObservation so) {
    	try {
        	if(SHOULD_LOG) {
        		// parameters:
        		boolean logEverything = true;
        		if(!file.exists())
        		{
        			file.createNewFile();
        		}
        		// create an APPENDING writer
        		writer = new BufferedWriter(new FileWriter(file, true));
        		if (logEverything) {
        			// write npcs:
        		writer.write("NPC:(");
            	if (so.getNPCPositions() != null) {
            	  ArrayList<Observation>[] list = so.getNPCPositions();
            	  for (int i=0; i<list.length; i++) {
            		  for (int j=0; j<list[i].size(); j++) {
            			  if (list[i] != null) {
            				  writer.write("(" + list[i].get(j).itype + ",");
            				  writer.write(list[i].get(j).position.toString()+")");
            			  }
            		  }
            	  }
            	}
            	writer.write("),");
            	// write immovables
            	writer.write("Immovable:(");
            	if (so.getImmovablePositions() != null) {
            	  ArrayList<Observation>[] list = so.getImmovablePositions();
            	  for (int i=0; i<list.length; i++) {
            		  for (int j=0; j<list[i].size(); j++) {
            			  if (list[i] != null) {
            				  writer.write("(" + list[i].get(j).itype + ",");
            				  writer.write(list[i].get(j).position.toString()+")");
            			  }
            		  }
            	  }
            	}
            	writer.write("),");
            	// write movables
            	writer.write("Movable:(");
            	if (so.getMovablePositions() != null) {
            	  ArrayList<Observation>[] list = so.getMovablePositions();
            	  for (int i=0; i<list.length; i++) {
            		  for (int j=0; j<list[i].size(); j++) {
            			  if (list[i] != null) {
            				  writer.write("(" + list[i].get(j).itype + ",");
            				  writer.write(list[i].get(j).position.toString()+")");
            			  }
            		  }
            	  }
            	}
            	writer.write("),");
            	// write resources
            	writer.write("Resources:(");
            	if (so.getResourcesPositions() != null) {
            	  ArrayList<Observation>[] list = so.getResourcesPositions();
            	  for (int i=0; i<list.length; i++) {
            		  for (int j=0; j<list[i].size(); j++) {
            			  if (list[i] != null) {
            				  writer.write("(" + list[i].get(j).itype + ",");
            				  writer.write(list[i].get(j).position.toString()+")");
            			  }
            		  }
            	  }
            	}
            	writer.write("),");
            	// write portals
            	writer.write("Portals:(");
            	if (so.getPortalsPositions() != null) {
            	  ArrayList<Observation>[] list = so.getPortalsPositions();
            	  for (int i=0; i<list.length; i++) {
            		  for (int j=0; j<list[i].size(); j++) {
            			  if (list[i] != null) {
            				  writer.write("(" + list[i].get(j).itype + ",");
            				  writer.write(list[i].get(j).position.toString()+")");
            			  }
            		  }
            	  }
            	}
            	writer.write(")\r\n");
        		} else {
        			// log only counts of thing (simple)
        			writer.write("NPC: ");
        		}
            	// close writer to write from buffer to file
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

	public void clear(File file) {
		try {
			PrintWriter pWriter;
			pWriter = new PrintWriter(file);
			pWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes best found parameters of session to "solutions"
	 * Assuming learning-file is organized in this way:
	 * - "parameters and stuff"
	 * - SCORE
	 * @param file file with learning session content
	 * @param result file to which best parameters should be written
	 */
	public void solve(File file, File result) {
		String[] bestParaAndScore = new String[] {"", "-1000"};
		// read in file:
    	Charset charset = Charset.forName("US-ASCII");
    	String[] parameterAndScore = new String[2];
    	try (BufferedReader reader = Files.newBufferedReader(file.toPath(), charset)) {
    		String line1;
    		String line2;
    	    while ((line1 = reader.readLine()) != null && (line2 = reader.readLine()) != null) {
    	    	parameterAndScore[0] = line1;
    			parameterAndScore[1] = line2;
    			System.out.println(line1+line2);
    			if (Double.parseDouble(parameterAndScore[1]) > Double.parseDouble(bestParaAndScore[1])) {
    				System.out.println(parameterAndScore[1]+bestParaAndScore[1]);

    				bestParaAndScore[0] = parameterAndScore[0];
    				bestParaAndScore[1] = parameterAndScore[1];
    			}
    	    }
    	    reader.close();
    	    writeToFileAppend(result, bestParaAndScore[0]);
    	    writeToFileAppend(result, bestParaAndScore[1]);
    	} catch (IOException x) {
    	    System.err.format("IOException: %s%n", x);
    	}
	}

}
