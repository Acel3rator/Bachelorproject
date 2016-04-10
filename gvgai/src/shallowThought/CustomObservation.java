package shallowThought;

import core.game.Observation;
import tools.Vector2d;

public class CustomObservation implements Comparable<CustomObservation>
{

    /**
     * Category of this observation (static, resource, npc, etc.).
     */
    public int category;

    /**
     * Type of sprite of this observation.
     */
    public int itype;

    /**
     * unique ID for this observation
     */
    public int obsID;

    /**
     * Position of the observation.
     */
    public Vector2d position;

    /**
     * Reference to the position used for comparing this
     * observation with others.
     */
    public Vector2d reference;

    /**
     * Distance from this observation to the reference.
     */
    public double sqDist;

    /**
     * New observation. It is the observation of a sprite, recording its ID and position.
     * @param itype type of the sprite of this observation
     * @param id ID of the observation.
     * @param pos position of the sprite.
     * @param posReference reference to compare this position to others.
     * @param category category of this observation (NPC, static, resource, etc.)
     */
    public CustomObservation(int itype, int id, Vector2d pos, Vector2d posReference, int category)
    {
        this.itype = itype;
        this.obsID = id;
        this.position = pos;
        this.reference = posReference;
        sqDist = pos.sqDist(posReference);
        this.category = category;
    }
    
    public CustomObservation(Observation o)
    {
        this.itype = o.itype;
        this.obsID = o.obsID;
        this.position = o.position;
        this.reference = o.reference;
        sqDist = o.position.sqDist(o.reference);
        this.category = o.category;
    }
    
    public CustomObservation(String s)
    {
    	String[] split = s.split("\\+");
        this.itype = Integer.valueOf(split[0]);
        this.obsID = Integer.valueOf(split[1]);
        this.position = stringToVector(split[2]);
        this.reference = stringToVector(split[3]);
        sqDist = position.sqDist(reference);
        this.category = Integer.valueOf(split[4]);
    }
    
    /**
     * itype+pbsID+posX : posY+refX : refY+category
     */
    public String toString() {
    	String result = String.valueOf(itype) + "+" +
    			 String.valueOf(obsID) + "+" +
    			 String.valueOf(position) + "+" +
    			 String.valueOf(reference) + "+" +
    			 String.valueOf(category);
    	return result;
    }
    
    private Vector2d stringToVector(String s) {
    	String[] splitted = s.split(" : ");
		return new Vector2d(Double.parseDouble(splitted[0]),Double.parseDouble(splitted[1])); 
	}

    /**
     * Updates this observation
     * @param itype type of the sprite of this observation
     * @param id ID of the observation.
     * @param pos position of the sprite.
     * @param posReference reference to compare this position to others.
     * @param category category of this observation (NPC, static, resource, etc.)
     */
    public void update(int itype, int id, Vector2d pos, Vector2d posReference, int category)
    {
        this.itype = itype;
        this.obsID = id;
        this.position = pos;
        this.reference = posReference;
        this.sqDist = pos.sqDist(posReference);
        this.category = category;
    }

    /**
     * Compares this observation to others, using distances to the reference position.
     * @param o other observation.
     * @return -1 if this precedes o, 1 if same distance or o is closer to reference.
     */
    @Override
    public int compareTo(CustomObservation o) {
        double oSqDist = o.position.sqDist(reference);
        if(sqDist < oSqDist)        return -1;
        else if(sqDist > oSqDist)   return 1;
        return 0;
    }

    /**
     * Compares two Observations to check if they are equal. The reference attribute is NOT
     * compared in this object.
     * @param other the other observation.
     * @return true if both objects are the same Observation.
     */
    public boolean equals(Object other)
    {
        if(other == null || !(other instanceof CustomObservation))
            return false;

        CustomObservation o = (CustomObservation) other;
        if(this.itype != o.itype) return false;
        if(this.obsID != o.obsID) return false;
        if(!this.position.equals(o.position)) return false;
        if(this.category != o.category) return false;
        return true;
    }
}