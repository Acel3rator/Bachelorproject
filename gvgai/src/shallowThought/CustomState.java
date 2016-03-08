package shallowThought;

import java.io.BufferedWriter;
import java.io.File;
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
	private ArrayList<Observation>[] npcPos;
	private ArrayList<Observation>[] immovPos;
	private ArrayList<Observation>[] movPos;
	private ArrayList<Observation>[] resPos;
	private ArrayList<Observation>[] portalPos;
	private double worldDimWidth;
	private double worldDimHeight;
	private int blockSize;
	private ACTIONS avatarLastAction;
	private TreeSet<Event> eventHistory; //mmh...
	private ArrayList<Observation>[] spritesByAvatar;
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
		npcPos = so.getNPCPositions();
		immovPos = so.getImmovablePositions();
		movPos = so.getMovablePositions();
		resPos = so.getResourcesPositions();
		portalPos = so.getPortalsPositions();
		worldDimWidth = so.getWorldDimension().getWidth();
		worldDimHeight = so.getWorldDimension().getHeight();
		blockSize = so.getBlockSize();
		avatarLastAction = so.getAvatarLastAction();
		eventHistory = so.getEventsHistory(); //mmh...
		spritesByAvatar = so.getFromAvatarSpritesPositions();
		gameScore = so.getGameScore();
		gameTick = so.getGameTick();    
	}
	
	/**
	 * Use this method to generate any meta-features
	 * Not in constructor due to time-reasons (not always needed)
	 */
	public void generateMetaFeatures() {
		numNPCs = 0;
		for (ArrayList<Observation> obs : npcPos) { numNPCs += obs.size(); }
		numImmov = 0;
		for (ArrayList<Observation> obs : immovPos) { numImmov += obs.size(); }
		numMov = 0;
		for (ArrayList<Observation> obs : movPos) { numMov += obs.size(); }
		numRes = 0;
		for (ArrayList<Observation> obs : resPos) { numRes += obs.size(); }
		numPortal = 0;
		for (ArrayList<Observation> obs : portalPos) { numPortal += obs.size(); }
	
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
    public void writeToFile(File file) {
        BufferedWriter writer;
    	try {
        	boolean logEverything = true;
        	// parameters:
        	if(!file.exists()){file.createNewFile();}
        	writer = new BufferedWriter(new FileWriter(file, false));
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
        	        writer.write("|");
        	    }
        	} else {
        		writer.write("None");
        	}
        	writer.write(',');
        	// Write other state-observations
        	ArrayList<ArrayList<Observation>[]> observations = new ArrayList<ArrayList<Observation>[]>();
            observations.add(npcPos);
        	observations.add(immovPos);
        	observations.add(movPos);
        	observations.add(resPos);
        	observations.add(portalPos);
        	observations.add(spritesByAvatar);
        	for (ArrayList<Observation>[] list : observations) {
                if (list != null) {
                    for (ArrayList<Observation> innerList : list) {
                        writer.write("#"); //Delimiting different types of sprites of object
                        for (int j=0; j<innerList.size(); j++) {
                        	if (! (j==0)) {writer.write("|");}
                        	writer.write(innerList.get(j).position.toString());                   
                        }
                    }
                } else {
            		writer.write("None");
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
        	writer.write("Events so far yet to be realized...");
        	//writer.write(so.getEventsHistory());
        	writer.write(',');
            writer.write(String.valueOf(gameScore));
            writer.write(',');
            writer.write(String.valueOf(gameTick));
        	writer.write(',');
            writer.write("\r\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
