package shallowThought;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import core.game.Event;
import core.game.Observation;
import core.game.StateObservation;
import ontology.Types.ACTIONS;
import tools.Vector2d;

/**
 * Creates a custom version of a state-observation from the gvgai-framework,
 * used by shallowThought. Saves all the features and meta-features of
 * a state.
 */

public class CustomState {
	// Basic features directly extracted from state-observation
	private Vector2d avatarPos;
	private Vector2d avatarOri;
	private int avatarType;
	private double avatarSpeed;
	private HashMap<Integer, Integer> avatarRes;
	private ArrayList<ArrayList<CustomObservation>> npcPos;
	private ArrayList<ArrayList<CustomObservation>> immovPos;
	private ArrayList<ArrayList<CustomObservation>> movPos;
	private ArrayList<ArrayList<CustomObservation>> resPos;
	private ArrayList<ArrayList<CustomObservation>> portalPos;
	private double worldDimWidth;
	private double worldDimHeight;
	private int blockSize;
	private ACTIONS avatarLastAction;
	private TreeSet<CustomEvent> eventHistory;
	private ArrayList<ArrayList<CustomObservation>> spritesByAvatar;
	private double gameScore;
	private int gameTick;
	
	// Meta-features
	private int numNPCs;
	private int numImmov;
	private int numMov;
	private int numRes;
	private int numPortal;
	
	
	// constructors
	public CustomState(StateObservation so) {
		avatarPos = so.getAvatarPosition();
		avatarOri = so.getAvatarOrientation();
		avatarType = so.getAvatarType();
		avatarSpeed = so.getAvatarSpeed();
		avatarRes = so.getAvatarResources();
		npcPos = obsToCusObs(so.getNPCPositions());
		immovPos = obsToCusObs(so.getImmovablePositions());
		movPos = obsToCusObs(so.getMovablePositions());
		resPos = obsToCusObs(so.getResourcesPositions());
		portalPos = obsToCusObs(so.getPortalsPositions());
		worldDimWidth = so.getWorldDimension().getWidth();
		worldDimHeight = so.getWorldDimension().getHeight();
		blockSize = so.getBlockSize();
		avatarLastAction = so.getAvatarLastAction();
		eventHistory = eventsToCusEvents(so.getEventsHistory()); //mmh...
		spritesByAvatar = obsToCusObs(so.getFromAvatarSpritesPositions());
		gameScore = so.getGameScore();
		gameTick = so.getGameTick();
		generateMetaFeatures();
	}
	

	public CustomState(String line) {
		readFromFile(line);
	}

	/**
	 * Use this method to generate any meta-features
	 * Not in constructor due to time-reasons (not always needed)
	 */
	public void generateMetaFeatures() {
		numNPCs = 0;
		if (npcPos != null) {
		for (ArrayList<CustomObservation> obs : npcPos) { numNPCs += obs.size(); }}
		numImmov = 0;
		if (immovPos != null) {
		for (ArrayList<CustomObservation> obs : immovPos) { numImmov += obs.size(); }}
		numMov = 0;
		if (movPos != null) {
		for (ArrayList<CustomObservation> obs : movPos) { numMov += obs.size(); }}
		numRes = 0;
		if (resPos != null) {
		for (ArrayList<CustomObservation> obs : resPos) { numRes += obs.size(); }}
		numPortal = 0;
		if (portalPos != null) {
		for (ArrayList<CustomObservation> obs : portalPos) { numPortal += obs.size(); }}
	
	}
	
    /**
     * Writes a custom state to a file in csv-format.
     * Check readme for format information
     * AvatarPosX, AvatarPosY, AvatarOriX, AvatarOriY, AvatarType, AvatarSpeed,
     * AvatarResources, NPCs, Immovables, Movables, Resources, Portals, GridSizeX, GridSizeY,
     * WorldDimInPixX, WorldDimInPixY, BlockSize, AvatarLastAction, EventsSoFar,
     * SpritesByAvatar, GameScore, GameTick
     * @param file File to write the level to.
     * @return Nothing I guess
     */
    public void writeToFile(File file, boolean append) {
        BufferedWriter writer;
    	try {
        	boolean logEverything = true;
        	// parameters:
        	if(!file.exists()){file.createNewFile();}
        	writer = new BufferedWriter(new FileWriter(file, append));
        	// Write avatar information
        	writer.write(avatarPos.toString());
        	writer.write(',');
        	writer.write(avatarOri.toString());
        	writer.write(',');
        	writer.write(String.valueOf(avatarType));
        	writer.write(',');
        	writer.write(String.valueOf(avatarSpeed));
        	writer.write(',');
        	if (avatarRes != null) {
        		Iterator it = avatarRes.entrySet().iterator();
        	    while (it.hasNext()) {
        	        Map.Entry pair = (Map.Entry)it.next();
        	        writer.write(pair.getKey() + " : " + pair.getValue());
        	        it.remove(); // avoids a ConcurrentModificationException
        	        if (it.hasNext()) writer.write("|");
        	    }
        	} else {
        		//writer.write("None");
        	}
        	writer.write(',');
        	// Write other state-observations
        	ArrayList<ArrayList<ArrayList<CustomObservation>>> observations = new ArrayList<ArrayList<ArrayList<CustomObservation>>>();
            observations.add(npcPos);
        	observations.add(immovPos);
        	observations.add(movPos);
        	observations.add(resPos);
        	observations.add(portalPos);
        	observations.add(spritesByAvatar);
        	for (ArrayList<ArrayList<CustomObservation>> list : observations) {
            	if (list != null) {
            		for (ArrayList<CustomObservation> innerList : list) {
	                    if (! (list.indexOf(innerList) == 0))writer.write("#"); //Delimiting different types of sprites of object
	                    for (int j=0; j<innerList.size(); j++) {
	                     	if (! (j==0)) {writer.write("|");}
	                        writer.write(innerList.get(j).toString());                   
	                    }
            		}
                } else {
                	//writer.write("None");
               	}
                writer.write(",");
            }
        	// Write general levelinformation (size, etc.)
        	writer.write(String.valueOf(worldDimWidth));
        	writer.write(',');
            writer.write(String.valueOf(worldDimHeight));
        	writer.write(',');
            writer.write(String.valueOf(blockSize));
        	writer.write(',');
            writer.write(avatarLastAction.toString());
        	writer.write(',');
        	if (eventHistory != null) {
        		Iterator it = eventHistory.iterator();
        	    while (it.hasNext()) {
        	        writer.write(it.next().toString());
        	        it.remove(); // avoids a ConcurrentModificationException
        	        if (it.hasNext()) writer.write("|");  // only write | if there is something coming afterwards
        	    }
        	} else {
        		//writer.write("None");
        	}
        	writer.write(',');
            writer.write(String.valueOf(gameScore));
            writer.write(',');
            writer.write(String.valueOf(gameTick));
            writer.write("\r\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ATTENTION! IF YOU CHANGE THE ORDER/AMOUNT OF INFORMATION, PLEASE ALSO CHANGE
     * THE features.txt, SINCE THERE THE FEATURE NAMES ARE SAVED IN CORRECT ORDER
     * 
     * Writes a custom state to a file in csv-format.
     * AvatarPosX, AvatarPosY, AvatarOriX, AvatarOriY, AvatarType, AvatarSpeed,
     * AvatarResources, NPCs, Immovables, Movables, Resources, Portals, GridSizeX, GridSizeY,
     * WorldDimInPixX, WorldDimInPixY, BlockSize, AvatarLastAction, EventsSoFar,
     * SpritesByAvatar, GameScore, GameTick
     * @param file File to write the level to.
     * @return Nothing I guess
     */
    public void readFromFile(String s) {
    	String[] features = s.split(",");
    	avatarPos = stringToVector(features[0]);
        avatarOri = stringToVector(features[1]);
        avatarType = Integer.parseInt(features[2]);
        avatarSpeed = Double.parseDouble(features[3]);
        avatarRes = new HashMap<Integer, Integer>();
        String[] tmp = features[4].split("\\|");
        for (String sT : tmp) {
        	if (sT.equals("") || sT.equals("None")) break;  // nothing to add
        	String[] split = sT.split(" : ");
        	avatarRes.put(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        }
        // lists...
        for (int i = 5; i < 11; i++) {
        
	        tmp = features[i].split("#");
	        switch(i) {
	        case 5:
	        	npcPos = new ArrayList<ArrayList<CustomObservation>>();
	        	break;
	        case 6:
	        	immovPos = new ArrayList<ArrayList<CustomObservation>>();
	            break;
	        case 7:
	        	movPos = new ArrayList<ArrayList<CustomObservation>>();
	            break;
	        case 8:
	        	resPos = new ArrayList<ArrayList<CustomObservation>>();
	            break;
	        case 9:
	            portalPos = new ArrayList<ArrayList<CustomObservation>>();
	            break;
	        case 10:
	        	spritesByAvatar = new ArrayList<ArrayList<CustomObservation>>();    	
	        	break;
	        }
	        for (String sT : tmp) {
	        	ArrayList<CustomObservation> tmpList = new ArrayList<CustomObservation>();
	        	String[] split = sT.split("\\|");
	        	for (String obser : split) {
	        		//System.out.println(obser);
		        	if (obser.equals("") || obser.equals("None")) continue;  // nothing to add
	        		tmpList.add(new CustomObservation(obser));
	        	}
	        	switch(i) {
		        case 5:
		        	npcPos.add(tmpList);
		        	break;
		        case 6:
		        	immovPos.add(tmpList);
		            break;
		        case 7:
		        	movPos.add(tmpList);
		            break;
		        case 8:
		        	resPos.add(tmpList);
		            break;
		        case 9:
		            portalPos.add(tmpList);
		            break;
		        case 10:
		        	spritesByAvatar.add(tmpList);    	
		        	break;
		        }
	        }
        }
        

        // other stuff
        worldDimWidth = Double.valueOf(features[11]);
        worldDimHeight = Double.valueOf(features[12]);
        blockSize = Integer.valueOf(features[13]);
        avatarLastAction = ACTIONS.fromString(features[14]);
        eventHistory = stringToTreeSetEventHistory(features[15]);
        gameScore = Double.valueOf(features[16]);
        gameTick = Integer.valueOf(features[17]);
    }


	private TreeSet<CustomEvent> stringToTreeSetEventHistory(String string) {
		TreeSet<CustomEvent> result = new TreeSet<CustomEvent>();
		System.out.println(string);
		String[] split = string.split("\\|");
		for (String s : split) {
			if (s.equals("") || s.equals("None")) break;
			System.out.println(s);;
			result.add(new CustomEvent(s));
		}
		return result;
	}


	private wVector2d stringToVector(String s) {
		String[] splitted = s.split(" : ");
		return new Vector2d(Double.parseDouble(splitted[0]),Double.parseDouble(splitted[1])); 
	}
	
	/**
	 * Converts an array of arrayLists of Observations (as is the format in the game) to an
	 * ArrayList of ArrayLists of CustomObservations 
	 * @param obs format as in so.getNPCPositions()
	 * @return customObservations
	 */
	private ArrayList<ArrayList<CustomObservation>> obsToCusObs(ArrayList<Observation>[] obs) {
		if (obs == null) {
			return null;
		}
		
		ArrayList<ArrayList<CustomObservation>> result = new ArrayList<ArrayList<CustomObservation>>();
		for (ArrayList<Observation> list : obs) {
			ArrayList<CustomObservation> tmp = new ArrayList<CustomObservation>();
			for (Observation o : list) {
				tmp.add(new CustomObservation(o));
			}
			result.add(tmp);
		}
		return result;
	}
	
	/**
	 * Converts a TreeSet of Events (from framework) to a TreeSet of CustomEvents
	 * @param eventsHistory
	 * @return customEventsHistory
	 */
	private TreeSet<CustomEvent> eventsToCusEvents(TreeSet<Event> eventsHistory) {
		TreeSet<CustomEvent> result = new TreeSet<CustomEvent>();
		if (eventsHistory != null) {
    		Iterator it = eventsHistory.iterator();
    	    while (it.hasNext()) {
    	        result.add(new CustomEvent((Event)it.next()));
    	    }
		}
		return result;
	}
	
    // Getters
    
	public Vector2d getAvatarPos() {
		return avatarPos;
	}

	public Vector2d getAvatarOri() {
		return avatarOri;
	}

	public int getAvatarType() {
		return avatarType;
	}

	public double getAvatarSpeed() {
		return avatarSpeed;
	}

	public HashMap<Integer, Integer> getAvatarRes() {
		return avatarRes;
	}

	public ArrayList<ArrayList<CustomObservation>> getNpcPos() {
		return npcPos;
	}

	public ArrayList<ArrayList<CustomObservation>> getImmovPos() {
		return immovPos;
	}

	public ArrayList<ArrayList<CustomObservation>> getMovPos() {
		return movPos;
	}

	public ArrayList<ArrayList<CustomObservation>> getResPos() {
		return resPos;
	}

	public ArrayList<ArrayList<CustomObservation>> getPortalPos() {
		return portalPos;
	}

	public double getWorldDimWidth() {
		return worldDimWidth;
	}

	public double getWorldDimHeight() {
		return worldDimHeight;
	}

	public int getBlockSize() {
		return blockSize;
	}

	public ACTIONS getAvatarLastAction() {
		return avatarLastAction;
	}

	public TreeSet<CustomEvent> getEventHistory() {
		return eventHistory;
	}

	public ArrayList<ArrayList<CustomObservation>> getSpritesByAvatar() {
		return spritesByAvatar;
	}

	public double getGameScore() {
		return gameScore;
	}

	public int getGameTick() {
		return gameTick;
	}

	public int getNumNPCs() {
		return numNPCs;
	}

	public int getNumImmov() {
		return numImmov;
	}

	public int getNumMov() {
		return numMov;
	}

	public int getNumRes() {
		return numRes;
	}

	public int getNumPortal() {
		return numPortal;
	}
}
