package TUDarmstadtTeam2.stateIdentification;

import TUDarmstadtTeam2.utils.Cantor;
import core.game.Event;
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
     * To compute the hash of a given game state and to
     * give the hash of the features used for hashing.
     * This may be used for checking if the game is
     * stochastic.
     * @param state the game state to hash
     * @return the hash given as an TraceableHashedState object
     */
//    public TraceableHashedState generateTrackableHashedState (StateObservation state) {
//        int position = Objects.hash(hashVector2d(state.getAvatarPosition()));
//        int movables = Objects.hash(hashListOfObservations(state.getMovablePositions()));
//        int npc = Objects.hash(hashListOfObservations(state.getNPCPositions()));
//        int inventory = Objects.hash(hashResources(state.getAvatarResources(), HASH_IV));
//        return new TraceableHashedState(hashState(state, 0),position,npc,movables,inventory);
//    }

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
//        return hashGridOfObservations(state.getObservationGrid(), HASH_IV);
//        hash = Objects.hash(hashVector2d(state.getAvatarPosition(), HASH_IV));
//        int movables = Objects.hash(hashListOfObservations(state.getMovablePositions(), HASH_IV));
//        hash = Objects.hash(movables, hash);
//        int npcs = Objects.hash(hashListOfObservations(state.getNPCPositions(), hash));
//        hash = Objects.hash(npcs, hash);
//        int immovables = Objects.hash(hashListOfObservations(state.getImmovablePositions(), HASH_IV));
//        hash = Objects.hash(immovables, hash);
//        return Objects.hash(hashResources(state.getAvatarResources(), hash));
    }

    /**
     * To compute a hash of a tree set of Events.
     * Mainly used to hash the history of game events.
     *
     * @param set the set of events
     * @param hash the initialisation vector
     * @return the hash value
     */
    private int hashTreeSet(TreeSet<Event> set, int hash) {
        for(Event evt : set) {
            hash = hashEvent(evt, hash);
        }
        return hash;
    }

    /**
     * To hash a game event.
     * @param evt the event
     * @param hash the initialisation vector
     * @return the hash value
     */
    private int hashEvent(Event evt, int hash) {
        hash = Objects.hash(hash, evt.fromAvatar);
        hash = Objects.hash(hash, evt.activeTypeId);
        hash = Objects.hash(hash, evt.passiveSpriteId);
        hash = Objects.hash(hash, evt.passiveTypeId);
        hash = Objects.hash(hashVector2d(evt.position), hash);

        return hash;
    }

    /**
     * To compute a hash value of the agents resources.
     * @param map the resources given as a HashMap.
     * @param hash the initialisation vector
     * @return the hash value
     */
    private int hashResources(HashMap<Integer, Integer> map, int hash) {
        for(int key : map.keySet()) {
            hash = Objects.hash(key,hash);
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