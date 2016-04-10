package TUDarmstadtTeam2.stochasticAgent;

import java.util.ArrayList;
import java.util.HashSet;

import ontology.Types;
import ontology.Types.ACTIONS;
import TUDarmstadtTeam2.utils.Config;
import TUDarmstadtTeam2.utils.Pair;
import TUDarmstadtTeam2.utils.TuUtils;
import core.game.Observation;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public class A_star {

	private static ArrayList<MapNode> openList;
	private static HashSet<MapNode> closedList;
	private static ArrayList<Observation>[][] observationGrid;
	public static boolean finished = true;
	private static Pair<Integer, Integer> start;
	private static Pair<Integer, Integer> goal;
	private static KnowledgeBase memory;
	private static int blockSize;
	
	private static boolean validUp = false;
	private static boolean validDown = false;
	private static boolean validLeft = false;
	private static boolean validRight = false;
	
	private static void addToOpen(MapNode current) {
		if (openList.isEmpty()) {
			openList.add(current);
			return;
		}
		int x = 0;
		for (MapNode n : openList) {
			if (n.compareTo(current) != -1) {
				openList.add(x, current);
				return;
			}
			x++;
		}
		//no bigger element found, add to end.
		openList.add(current);
	}

	/**
	 * 
	 * returns null and sets "A_star.finished" to "true" if no path could be
	 * found.
	 * 
	 * we use the observationGrid and memory to figure out where we can and
	 * can't go.
	 * 
	 * @param startPos
	 * @param goalPos
	 * @param observationGrid
	 * @param memory
	 * @param timer
	 *            if less time than GOAL_SEARCH_ABORT_TIME is left than the
	 *            search is aborted, but it can be restarted later.
	 * @param availableActions
	 * @return
	 */
	public static ArrayList<Vector2d> findPathFromTo(Vector2d startPos,
			Vector2d goalPos, ArrayList<Observation>[][] observationGrid,
			KnowledgeBase memory, ElapsedCpuTimer timer,
			ACTIONS[] availableActions, int bSize) {
		if (finished) {
			finished = false;
		}

		for (Types.ACTIONS a : availableActions) {
			switch (a) {
			case ACTION_DOWN:
				validDown = true;
				break;
			case ACTION_LEFT:
				validLeft = true;
				break;
			case ACTION_RIGHT:
				validRight = true;
				break;
			case ACTION_UP:
				validUp = true;
				break;
			case ACTION_NIL:
			case ACTION_ESCAPE:
			case ACTION_USE:
			default:
				continue;
			
			}
		}
		A_star.memory = memory;
		blockSize = bSize;
		start = convertStatePositionToMapPosition(startPos);
		goal = convertStatePositionToMapPosition(goalPos);
		// TODO make it so that the search does not restart (maybe do this in
		// MCTSSearch: calling search or continueSearch
		A_star.observationGrid = observationGrid;
		MapNode startingNode = new MapNode(start, null);

		closedList = new HashSet<MapNode>();
		openList = new ArrayList<MapNode>();
		openList.add(startingNode);

		return continueSearch(timer);
	}

	/**
	 * returns null and sets "A_star.finished" to "true" if now path could be
	 * found.
	 * 
	 * @param timer
	 * @return
	 */
	public static ArrayList<Vector2d> continueSearch(ElapsedCpuTimer timer) {
		while (timer.remainingTimeMillis() > Config.GOAL_SEARCH_ABORT_TIME) {
			if (openList.isEmpty()) {
				finished = true;
				return null;
			}

			if (Config.DEBUGGING) {
				printSearch();
			}

			// remove minimum
			MapNode current = openList.remove(0);	

			if (current.isAt(goal)) {
				finished = true;
				return convertToPositions(current);
			}

			closedList.add(current);
			if (!isBlockingOrDeadly(current, memory)) {
				expand(current);
			}
		}

		System.out.println(timer.remainingTimeMillis());
		System.out.println(openList.isEmpty());
		return null;
	}

	/**
	 * returns true if an object that kills or blocks us is in the
	 * observationGrid at the position represented by currentNode
	 * 
	 * @param currentNode
	 * @param memory
	 * @return
	 */
	private static boolean isBlockingOrDeadly(MapNode currentNode,
			KnowledgeBase memory) {
		Pair<Integer, Integer> pos = currentNode.getPosition();
		if (TuUtils.isPositionOnGrid(A_star.observationGrid,pos)) {
			ArrayList<Observation> observation = A_star.observationGrid[pos
					.getFirst()][pos.getSecond()];
			return memory.isBlockingOrDeadly(observation);
		}
		// outside the field
		return true;
	}



	/**
	 * puts the successors of currentNode in the openList if these positions
	 * aren't in there already or if they are in there already with longer
	 * ExpectedDistances(heuristic + stepsTaken) they are replaced with the
	 * successors with shorter distances.
	 * 
	 * @param currentNode
	 * @param goal
	 */
	private static void expand(MapNode currentNode) {
		for (MapNode n : successors(currentNode)) {
			if (closedList.contains(n)) {
				continue;
			}
			n.update(currentNode, goal);
			MapNode found = null;
			int foundId = openList.indexOf(n);
			if (foundId != -1) {
				found = openList.get(foundId);
				if (found.compareTo(n) != 1) {
					continue;
				} else {
					openList.remove(n);
				}
			}
			addToOpen(n);
		}
	}
	
	private static ArrayList<MapNode> successors(MapNode currentNode) {
		ArrayList<MapNode> succs = new ArrayList<MapNode>();
		int x = currentNode.getPosition().getFirst();
		int y = currentNode.getPosition().getSecond();
		if (validUp) {
			succs.add(new MapNode(new Pair<Integer, Integer>(x,y-1), Types.ACTIONS.ACTION_UP));
		}
		if (validDown) {
			succs.add(new MapNode(new Pair<Integer, Integer>(x,y+1), Types.ACTIONS.ACTION_DOWN));
		}
		if (validLeft) {
			succs.add(new MapNode(new Pair<Integer, Integer>(x-1,y), Types.ACTIONS.ACTION_LEFT));
		}
		if (validRight) {
			succs.add(new MapNode(new Pair<Integer, Integer>(x+1,y), Types.ACTIONS.ACTION_RIGHT));
		}
		return succs;
	}

	private static ArrayList<ACTIONS> convertToActions(MapNode goalNode) {
		ArrayList<ACTIONS> actions = new ArrayList<ACTIONS>();

		MapNode currentNode = goalNode;
		
		if (currentNode != null) {
			do {
				actions.add(currentNode.getprevAction());
				currentNode = currentNode.getPredecessor();
			} while (currentNode.getPredecessor() != null);
		}
		
		java.util.Collections.reverse(actions);

		return actions;
	}
	private static ArrayList<Vector2d> convertToPositions(MapNode goalNode) {
		ArrayList<Vector2d> positions = new ArrayList<Vector2d>();
		MapNode currentNode = goalNode;

		if (currentNode != null) {
			while (currentNode.getPredecessor() != null) {
				positions.add(convertMapPositionToStatePosition(currentNode.getPosition()));
				currentNode = currentNode.getPredecessor();
			}
		}

		java.util.Collections.reverse(positions);

		return positions;
	}
	
	private static Pair<Integer, Integer> convertStatePositionToMapPosition(Vector2d pos) {
		Pair<Integer, Integer> newPos = new Pair<Integer, Integer>();
		newPos.setFirst((int) Math.floor(pos.x / blockSize));
		newPos.setSecond((int) Math.floor(pos.y / blockSize));
		return newPos;
	}
	private static Vector2d convertMapPositionToStatePosition(Pair<Integer, Integer> pos){
		Vector2d newPos = new Vector2d();
		newPos.set(pos.getFirst() * blockSize, pos.getSecond()*blockSize);
		return newPos;
	}

	/*********************************** Debugging ***********************************/

	private static void printSearch() {
		String[][] visMap = new String[A_star.observationGrid.length][];
		for (int x = 0; x < visMap.length; x++) {
			visMap[x] = new String[A_star.observationGrid[x].length];
			for (int y = 0; y < visMap[x].length; y++) {
				visMap[x][y] = "[ ]";
			}
		}
		for (MapNode n : closedList) {
			visMap[n.getPosition().getFirst()][n.getPosition().getSecond()] = "[-]";
		}
		for (MapNode n : openList) {
			if (visMap[n.getPosition().getFirst()][n.getPosition().getSecond()].equals("[ ]")) {
				visMap[n.getPosition().getFirst()][n.getPosition().getSecond()] = "[+]";
			} else {
				visMap[n.getPosition().getFirst()][n.getPosition().getSecond()] = "[?]";
			}
		}

		String searchVisualization = "";
		for (int j = 0; j < visMap[0].length; j++) {
			for (int i = 0; i < visMap.length; i++) {
				searchVisualization += visMap[i][j];
			}
			searchVisualization += "\n";
		}
		System.out.println(searchVisualization);
	}
}
