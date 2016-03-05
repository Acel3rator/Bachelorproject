package shallowThought;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import core.game.Event;
import core.game.Observation;
import core.game.StateObservation;
import ontology.Types.ACTIONS;
import tools.Vector2d;

/**
 * This class takes care of a whole game run, saving for each tick
 * the custom state (as a CustomState-Object) and providing a function
 * to interpret each attribute as a f: gametick x value.
 * Either construct from recorded game or update on the run.
 */
public class CustomGameRun {
	// Basic features
	private ArrayList<Vector2d> avatarPos;
	private ArrayList<Vector2d> avatarOri;
	private ArrayList<Integer> avatarType;
	private ArrayList<Double> avatarSpeed;
	private ArrayList<HashMap<Integer, Integer>> avatarRes;
	private ArrayList<ArrayList<Observation>[]> npcPos;
	private ArrayList<ArrayList<Observation>[]> immovPos;
	private ArrayList<ArrayList<Observation>[]> movPos;
	private ArrayList<ArrayList<Observation>[]> resPos;
	private ArrayList<ArrayList<Observation>[]> portalPos;
	private ArrayList<Double> worldDimWidth;
	private ArrayList<Double> worldDimHeight;
	private ArrayList<Integer> blockSize;
	private ArrayList<ACTIONS> avatarLastAction;
	// TODO:
	private TreeSet<Event> eventHistory; //mmh...
	private ArrayList<ArrayList<Observation>[]> spritesByAvatar;
	private ArrayList<Double> gameScore;
	private int gameTick;
	
	// Meta-features
	private ArrayList<Integer> numNPCs;
	private ArrayList<Integer> numImmov;
	private ArrayList<Integer> numMov;
	private ArrayList<Integer> numRes;
	private ArrayList<Integer> numPortal;
	
	// constructors
	public CustomGameRun() {
		// Standard features (init arraylists on 2000 for speed)
		avatarPos = new ArrayList<Vector2d>(2000);
		avatarOri = new ArrayList<Vector2d>(2000);
		avatarType = new ArrayList<Integer>(2000);
		avatarSpeed = new ArrayList<Double>(2000);
		avatarRes = new ArrayList<HashMap<Integer, Integer>>(2000);
		npcPos = new ArrayList<ArrayList<Observation>[]>(2000);
		immovPos = new ArrayList<ArrayList<Observation>[]>(2000);
		movPos = new ArrayList<ArrayList<Observation>[]>(2000);
		resPos = new ArrayList<ArrayList<Observation>[]>(2000);
		portalPos = new ArrayList<ArrayList<Observation>[]>(2000);
		worldDimWidth = new ArrayList<Double>(2000);
		worldDimHeight = new ArrayList<Double>(2000);
		blockSize = new ArrayList<Integer>(2000);
		avatarLastAction = new ArrayList<ACTIONS>(2000);
		// TODO
		// eventHistory = so.getEventsHistory(); //mmh...
		spritesByAvatar = new ArrayList<ArrayList<Observation>[]>(2000);
		gameScore = new ArrayList<Double>(2000);
		// Meta-features
		numNPCs = new ArrayList<Integer>(2000);
		numImmov = new ArrayList<Integer>(2000);
		numMov = new ArrayList<Integer>(2000);
		numRes = new ArrayList<Integer>(2000);
		numPortal = new ArrayList<Integer>(2000);
	}
	
	/**
	 * This method updates the object. For proper work, always update in the order
	 * of game-ticks.
	 */
	public void update(StateObservation so) {
		avatarPos.add(so.getAvatarPosition());
		avatarOri.add(so.getAvatarOrientation());
		avatarType.add(so.getAvatarType());
		avatarSpeed.add(so.getAvatarSpeed());
		avatarRes.add(so.getAvatarResources());
		npcPos.add(so.getNPCPositions());
		immovPos.add(so.getImmovablePositions());
		movPos.add(so.getMovablePositions());
		resPos.add(so.getResourcesPositions());
		portalPos.add(so.getPortalsPositions());
		worldDimWidth.add(so.getWorldDimension().getWidth());
		worldDimHeight.add(so.getWorldDimension().getHeight());
		blockSize.add(so.getBlockSize());
		avatarLastAction.add(so.getAvatarLastAction());
		//eventHistory.add(so.getEventsHistory()); //mmh...
		spritesByAvatar.add(so.getFromAvatarSpritesPositions());
		gameScore.add(so.getGameScore());
	}
}
