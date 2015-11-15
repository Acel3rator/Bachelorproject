package TUDarmstadtTeam2.stochasticAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

import TUDarmstadtTeam2.utils.Cantor;
import tools.Vector2d;
import core.game.StateObservation;
import core.game.Observation;

/**
 * Inspired by Diego Perez Algorithm: https://github.com/diegopliebana/EvoMCTS
 */
public class StateRec {
	// TODO try NPC count
	// TODO Test if
	// "closestNPC, closestRes, closestPort, closestMov, closestFix" improve
	// recognizing if two states are equal.
	// Features
	private Vector2d playerPos;
	private Vector2d playerOrientation;
	private double gameScore;
	private HashMap<Integer, Integer> inventory;
	private HashMap<Integer, Double> dist2NearestNPC;
	private HashMap<Integer, Double> dist2NearestImmovable;
	private HashMap<Integer, Double> dist2NearestMovable;
	private HashMap<Integer, Double> dist2NearestResource;
	private HashMap<Integer, Double> dist2NearestPortal;
	private HashMap<Integer, Vector2d> iTypePossitions;

	/**
	 * creates a new instance of the StateRec and initializes the values from
	 * the given state
	 * 
	 * @param state
	 *            the state in question
	 */
	public StateRec(StateObservation state) {
		iTypePossitions = new HashMap<Integer, Vector2d>();
		updateState(state);
	}

	/**
	 * updates the features with values from the given state
	 * 
	 * @param state
	 *            the state in question
	 */
	public void updateState(StateObservation state) {
		playerPos = state.getAvatarPosition();
		gameScore = state.getGameScore();
		playerOrientation = state.getAvatarOrientation();
		inventory = state.getAvatarResources();

		dist2NearestNPC = calcNearestDists(state.getNPCPositions(playerPos));
		dist2NearestImmovable = calcNearestDists(state
				.getImmovablePositions(playerPos));
		dist2NearestMovable = calcNearestDists(state
				.getMovablePositions(playerPos));
		dist2NearestResource = calcNearestDists(state
				.getResourcesPositions(playerPos));
		dist2NearestPortal = calcNearestDists(state
				.getPortalsPositions(playerPos));
	}

	/**
	 * calculates the nearests Dists for a given Array of Lists of Observations.
	 * Make sure the List of Observations has been set with the right reference!
	 * 
	 * @param positions
	 *            The array of lists of observations in question
	 * @return a Hashmap containing the nearest entry of every list in the array
	 *         with the lists position in the array as key
	 */
	private HashMap<Integer, Double> calcNearestDists(
			ArrayList<Observation>[] positions) {
		if (positions == null) {
			return null;
		}

		HashMap<Integer, Double> distances = new HashMap<Integer, Double>();
		for (int x = 0; x < positions.length; x++) {
			if (positions[x] != null && positions[x].size() != 0) {
				Observation closestObs = positions[x].get(0); 
				iTypePossitions.put(closestObs.itype, closestObs.position);
				distances.put(closestObs.itype, closestObs.sqDist);
			}
		}

		return distances;
	}

	/**
	 * Checks whether the given state is equal to the current state (according
	 * to the features of this class)
	 * 
	 * @param state
	 *            the other state in question
	 * @return true if all features are equal for both states
	 */
	public boolean equalsState(StateObservation state) {
		if (playerPos != state.getAvatarPosition()
				|| gameScore != state.getGameScore()
				|| playerOrientation != state.getAvatarOrientation()) {
			return false;
		} else if (!compareHashMaps(inventory, state.getAvatarResources())) {
			return false;
		}

		StateRec other = new StateRec(state);

		return (compareHashMaps(dist2NearestNPC, other.getDist2NearestNPC())
				&& compareHashMaps(dist2NearestImmovable,
						other.getDist2NearestImmovable())
				&& compareHashMaps(dist2NearestMovable,
						other.getDist2NearestMovable())
				&& compareHashMaps(dist2NearestResource,
						other.getDist2NearestResource()) && compareHashMaps(
					dist2NearestPortal, other.getDist2NearestPortal()));
	}

	/**
	 * compares 2 Hashmaps for containing the same elements 2 null hashmaps are
	 * considered to be different!
	 * 
	 * @param m1
	 *            the first Hashmap in question
	 * @param m2
	 *            the second Hashmap in question
	 * @return true if the Hashmaps are of equal size and all elements of m1 are
	 *         found in m2
	 */
	private <A, B> boolean compareHashMaps(HashMap<A, B> m1, HashMap<A, B> m2) {
		if (m1 == null || m2 == null || m1.size() != m2.size()) {
			return false;
		}
		for (A a : m1.keySet()) {
			if (m1.get(a) != m2.get(a)) {
				return false;
			}
		}
		return true;
	}



	/**
	 * Transforms the features of the stateRec into a single Hashmap
	 * 
	 * @return the features of this stateRec as single Hashmap
	 */
	public HashMap<Integer, Double> getFeatureVector() {
		HashMap<Integer, Double> features = new HashMap<Integer, Double>();
		if (dist2NearestNPC != null)
			addFeaturesFromMap(dist2NearestNPC, features, 2);
		if (dist2NearestResource != null)
			addFeaturesFromMap(dist2NearestResource, features, 3);
		if (dist2NearestPortal != null)
			addFeaturesFromMap(dist2NearestPortal, features, 5);
		if (dist2NearestImmovable != null)
			addFeaturesFromMap(dist2NearestImmovable, features, 7);
		if (dist2NearestMovable != null)
			addFeaturesFromMap(dist2NearestMovable, features, 11);
		if (inventory != null)
			addResourcesToFeatures(features, 13);
		return features;
	}

	/**
	 * Transforms the features of the stateRec into a single Hashmap after
	 * updating
	 * 
	 * @param state
	 *            the state with which the StateRec should be updated with.
	 * @return the features of this stateRec as single Hashmap
	 */
	public HashMap<Integer, Double> getFeatureVector(StateObservation state) {
		updateState(state);
		return getFeatureVector();
	}



	private void addFeaturesFromMap(HashMap<Integer, Double> distance,
			HashMap<Integer, Double> features, int prefix) {
		for(int itype : distance.keySet()) {
			int hashKey = Cantor.compute(prefix, itype);
			features.put(hashKey, distance.get(itype));
		}
	}

	private void addResourcesToFeatures(HashMap<Integer, Double> features,
			int prefix) {
		for(int itype : inventory.keySet()) {
			int hashKey = Cantor.compute(prefix, itype);
			features.put(hashKey, (double) inventory.get(itype));
		}

	}


    /**
     * Calculates the keys of the featureVector after updating the features with
     * values from the given state.
     * @param state the state in question
     * @return an array containing the keys
     */
    public String[] getFeatureVectorKeys(StateObservation state)
    {
    	updateState(state);
        HashMap<Integer, Double> featuresMap = getFeatureVector();
        String[] featureNames = new String[featuresMap.size()];
        featuresMap.keySet().toArray(featureNames);

        return featureNames;
    }
	
           // for Goal generator
           public HashMap<Integer, Double> getDistToObjects(){
                   HashMap<Integer,Double> map = new HashMap<Integer, Double>();
                   if(dist2NearestNPC != null) {
                           map.putAll(dist2NearestNPC);
                   }
                   if(dist2NearestImmovable != null) {
                           map.putAll(dist2NearestImmovable);
                   }
                   if(dist2NearestMovable != null) {
                           map.putAll(dist2NearestMovable);
                   }
                   if(dist2NearestResource != null) {
                           map.putAll(dist2NearestResource);
                   }
                   if(dist2NearestPortal != null) {
                           map.putAll(dist2NearestPortal);
                   }
                   return map;
           }
           public Vector2d getPosFromType(int type){
                   return iTypePossitions.get(type);
           }

	
	//----------- Getters/Setters -----------------
	public double[] getFeatureVectorAsArray() {
		HashMap<Integer, Double> featuresMap = getFeatureVector();
		double[] features = new double[featuresMap.size()];

		int i = 0;
		for(double feature : featuresMap.values())
			features[i++] = feature;

		return features;
	}

	/**
	 * Calculates the keys of the featureVector after updating the features with
	 * values from the given state.
	 * 
	 * @param state
	 *            the state in question
	 * @return an array containing the keys
	 */


	// ----------- Getters/Setters -----------------
	public HashMap<Integer, Double> getDist2NearestNPC() {
		return dist2NearestNPC;
	}

	public HashMap<Integer, Double> getDist2NearestImmovable() {
		return dist2NearestImmovable;
	}

	public HashMap<Integer, Double> getDist2NearestMovable() {
		return dist2NearestMovable;
	}

	public HashMap<Integer, Double> getDist2NearestResource() {
		return dist2NearestResource;
	}

	public HashMap<Integer, Double> getDist2NearestPortal() {
		return dist2NearestPortal;
	}
}
