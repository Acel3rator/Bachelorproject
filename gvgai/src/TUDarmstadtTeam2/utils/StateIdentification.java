package TUDarmstadtTeam2.utils;

import core.game.Observation;
import core.game.StateObservation;
import tools.Vector2d;

import java.util.*;

/**
 * This class gives some methods to identify a GameState
 * with a pseudo-unique hash value.
 *
 * It has also a method to get trackable states, which
 * means that it is easy to check for example, if the
 * player has moved.
 * It is also possilby to detect if npcs have moved
 * stochastic.
 */
public class StateIdentification {

    // The initialization vector
    private static int HASH_IV = 0;
    /**
     * Constructor to initialize the StateIdentification
     * Object.
     */
    public StateIdentification() {
    }

    /**
     * To compute the hash of a given game state.
     *
     * @param state the game state to hash
     * @return the hashed gamestate
     */
    public int generateHashedState(StateObservation state) {
        return hashState(state, HASH_IV);
    }

    /**
     * This function composes the hashed features.
     *
     * @param state a game state
     * @param hash the initialization vector
     * @return the computed hash
     */
    private int hashState(StateObservation state, int hash) {
        HashMap<Integer,LinkedList<Observation>> map = sortGrid(state.getObservationGrid());
        LinkedList<Observation> list;
        for(int category : map.keySet()) {
            list = map.get(category);
            hash = Objects.hash(Cantor.compute(hashListOfObservations(list),category), hash);
        }
        return hash;
    }

    /**
     * To compute a hash of ArrayList<Observation>[] objects.
     * @param list the array to hash
     * @return the computed hash value
     */
    private int hashListOfObservations(LinkedList<Observation> list) {
        if(list == null || list.isEmpty()) {
            return Objects.hash(0);
        }
        else {
            int hash = HASH_IV;
            int size = 0;

            for(Observation item : list) {
                size++;
                hash = Objects.hash(Cantor.compute(hashVector2d(item.position), item.itype), hash);
            }
            return Objects.hash(size,hash);
        }
    }

    /**
     * To compute a hash of a Vector2d object.
     * @param vector the vector to hash
     * @return the hashed variable
     */
    private int hashVector2d(Vector2d vector) {
        return Objects.hash(Cantor.compute((int) vector.x, (int) vector.y));
    }

    private HashMap<Integer,LinkedList<Observation>> sortGrid(ArrayList<Observation>[][] grid) {
        HashMap<Integer, LinkedList<Observation>> map = new HashMap<Integer, LinkedList<Observation>>();
        LinkedList<Observation> bucket;
        for(ArrayList<Observation>[] array : grid) {
            for(ArrayList<Observation> list : array) {
                for(Observation obs : list) {
                    bucket = map.get(obs.category);
                    if(bucket == null) {
                        bucket = new LinkedList<Observation>();
                        bucket.add(obs);
                        map.put(obs.category, bucket);
                    } else {
                        bucket.add(obs);
                    }
                }
            }
        }
        return map;
    }
}