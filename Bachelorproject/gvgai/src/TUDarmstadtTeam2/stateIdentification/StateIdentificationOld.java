package TUDarmstadtTeam2.stateIdentification;

import TUDarmstadtTeam2.standardMCTS.TreeNode;
import core.game.Event;
import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import ontology.sprites.Door;
import tools.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeSet;


public class StateIdentificationOld {
    private static int HASH_IV = 0;

    //public int generateHashedState(StateObservation state) {
    //    return hashState(state);
   // }

    public long hashState(StateObservation state) {
        long hash;
        int temp;
        temp = hashAvatar(state).hashCode();
        hash = temp;
        temp = hashListOfObservations(state.getMovablePositions()).hashCode();
        hash += 23*temp;
        temp = hashListOfObservations(state.getImmovablePositions()).hashCode();
        hash += 31 * temp;
        //temp = hashListOfObservations(state.getFromAvatarSpritesPositions()).hashCode();
        hash += 41* state.getGameScore();
        return hash;

    }
    private long cantor(long x, long y){
        return (y + (x + y) * (x + y + 1) / 2);
    }
    private String cantorAsString(long x, long y){
        return Long.toString(y + (x + y) * (x + y + 1) / 2);
    }
    /**
     * To compute a hash of ArrayList<Observation>[] objects.
     * @param obsArray the array to hash
     * @return the computed hash value
     */
    private String hashListOfObservations(ArrayList<Observation>[] obsArray) {
        String hash = "";
        if(obsArray != null) {
            for (int i = 0; i < obsArray.length; i++) {
                ArrayList<Observation> obsList = obsArray[i];
                for (Observation obs : obsList) {
                    long x = cantor(obs.itype, (long) obs.position.x);
                    hash += cantorAsString(x, (long) obs.position.y);
                }
            }
            return hash;
        }
        else return "empty";
    }
    private String hashAvatar(StateObservation observation){
        String avaHash = "Avatar";
        Vector2d avPos =observation.getAvatarPosition();
        avaHash += cantorAsString((long) avPos.x, (long) avPos.y);
        return avaHash;
    }

    /**
     * To compute a hash of a Vector2d object.
     * @param vector the vector to hash
     * @return the hashed variable
     */
    private int hashVector2d(Vector2d vector, int hash) {
        return Objects.hash(vector.x, vector.y, hash);
    }

    //Stuff currently not used:
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
    private int hashEventHistory(TreeSet<Event> events, int oldHash){
        int hash = oldHash;
        for (Event e : events){
            /*  hash = Objects.hash(e.activeSpriteId,hash);
            hash = Objects.hash(e.passiveSpriteId,hash); */
            hash = hashVector2d(e.position,hash);
        }
        return hash;
    }
}
