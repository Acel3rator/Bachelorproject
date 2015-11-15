package TUDarmstadtTeam2.utils;

import java.util.ArrayList;
import java.util.List;

import TUDarmstadtTeam2.stochasticAgent.A_star;
import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.Vector2d;

public class TuUtils {

	public static Vector2d vectorFromAction(Types.ACTIONS action) {
		switch (action) {
		case ACTION_DOWN:
			return Types.DOWN;
		case ACTION_UP:
			return Types.UP;
		case ACTION_LEFT:
			return Types.LEFT;
		case ACTION_RIGHT:
			return Types.RIGHT;

		default:
			return Types.NONE;

		}
	}

	// Normalizes a value between its MIN and MAX.
	public static double normalise(double a_value, double a_min, double a_max) {
		if (a_min < a_max)
			return (a_value - a_min) / (a_max - a_min);
		else
			// if bounds are invalid, then return same value
			return a_value;
	}

	public static ACTIONS ActionFromVector(Vector2d orientation) {
		if(orientation.x == Types.DOWN.x && orientation.y == Types.DOWN.y) {
			return ACTIONS.ACTION_DOWN;
		}
		if(orientation.x == Types.UP.x && orientation.y == Types.UP.y) {
			return ACTIONS.ACTION_UP;
		}
		if(orientation.x == Types.LEFT.x && orientation.y == Types.LEFT.y) {
			return ACTIONS.ACTION_LEFT;
		}
		if(orientation.x == Types.RIGHT.x && orientation.y == Types.RIGHT.y) {
			return ACTIONS.ACTION_RIGHT;
		}
		return null;
	}
	
	public static ACTIONS getActionForInt(int action,StateObservation state){
		return state.getAvailableActions().get(action);
	}
	
	/**
	 * converts avatars pixel position into grid position
	 * @param state
	 * @return
	 */
	public static Vector2d getAvatarPositionDownscaled(StateObservation state){
		return new Vector2d(state.getAvatarPosition().x / state.getBlockSize(), state.getAvatarPosition().y / state.getBlockSize());
	}
	
	public static Vector2d getNextPositionForAction(StateObservation state, int action){
		return getAvatarPositionDownscaled(state).add(vectorFromAction(getActionForInt(action,state)));
	}
	
	public static ArrayList<Observation> getObservationsOnPosition(StateObservation state, Vector2d position){
//		System.out.println("gridsize: "  + state.getObservationGrid()[0].length + " " + state.getObservationGrid().length);
		if(isPositionOnGrid(state.getObservationGrid(), position)){
		return state.getObservationGrid()[(int)position.x][(int)position.y];
		}
		return null;
	}

	public static boolean isPositionOnGrid(ArrayList<Observation>[][] grid, Pair<Integer,Integer> pos){
		return isPositionOnGrid(grid, (int)pos.getFirst(), (int)pos.getSecond());
	}
	
	public static boolean isPositionOnGrid(ArrayList<Observation>[][] grid, Vector2d pos){
		return isPositionOnGrid(grid, (int)pos.x, (int)pos.y);
	}
	
	private static boolean isPositionOnGrid(ArrayList<Observation>[][] grid, int x, int y) {
		return 0 <= x
				&& x < grid.length
				&& 0 <= y
				&& y < grid[0].length;
	}
	

	
	

}
