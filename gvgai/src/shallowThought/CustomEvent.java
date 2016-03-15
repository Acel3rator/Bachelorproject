package shallowThought;

import core.game.Event;
import core.game.Observation;
import tools.Vector2d;

/**
 * Created by diego on 24/03/14.
 */
public class CustomEvent implements Comparable<CustomEvent>
{
    /**
     * Game step when the event happened.
     */
    public int gameStep;

    /**
     * True if the event is caused by a sprite coming from (or created by) the avatar.
     * False if it's the avatar itself who collides with another sprite.
     */
    public boolean fromAvatar;

    /**
     * Type id of the object that triggers the event (either the avatar,
     * or something created by the avatar).
     */
    public int activeTypeId;

    /**
     * Type id of the object that received the event (what did the avatar,
     * or something created by the avatar, collided with?).
     */
    public int passiveTypeId;

    /**
     * Sprite ID of the object that triggers the event (either the avatar,
     * or something created by the avatar).
     */
    public int activeSpriteId;

    /**
     * Sprite ID of the object that received the event (what did the avatar,
     * or something created by the avatar, collided with?).
     */
    public int passiveSpriteId;


    /**
     * Position where the event took place.
     */
    public Vector2d position;

    /**
     * Constructor
     * @param gameStep when the event happened.
     * @param fromAvatar did the avatar trigger the event (false), or something created by him (true)?
     * @param activeTypeId type of the sprite (avatar or from avatar).
     * @param passiveTypeId type of the sprite that collided with activeTypeId.
     * @param activeSpriteId sprite ID of the avatar (or something created by the avatar).
     * @param passiveSpriteId sprite ID of the other object.
     * @param position where did the event take place.
     */
    public CustomEvent(int gameStep, boolean fromAvatar, int activeTypeId, int passiveTypeId,
                 int activeSpriteId, int passiveSpriteId, Vector2d position)
    {
        this.gameStep = gameStep;
        this.fromAvatar = fromAvatar;
        this.activeTypeId = activeTypeId;
        this.passiveTypeId = passiveTypeId;
        this.activeSpriteId = activeSpriteId;
        this.passiveSpriteId = passiveSpriteId;
        this.position = position;
    }

    /**
     * Creates CustomEvent from "real" event (of framework)
     * @param e event from game
     */
    public CustomEvent(Event e)
    {
    	this.gameStep = e.gameStep;
        this.fromAvatar = e.fromAvatar;
        this.activeTypeId = e.activeTypeId;
        this.passiveTypeId = e.passiveTypeId;
        this.activeSpriteId = e.activeSpriteId;
        this.passiveSpriteId = e.passiveSpriteId;
        this.position = e.position;
    }
    
    /**
     * Reverse function of toString()
     * @param s
     */
    public CustomEvent(String s)
    {
    	String[] split = s.split("\\+");
        this.gameStep = Integer.valueOf(split[0]);
        this.fromAvatar = Boolean.valueOf(split[1]);
        this.activeTypeId = Integer.valueOf(split[2]);
        this.passiveTypeId = Integer.valueOf(split[3]);
        this.activeSpriteId = Integer.valueOf(split[4]);
        this.passiveSpriteId = Integer.valueOf(split[5]);
        this.position = stringToVector(split[6]);
    }
    
    /**
     * Saves CustomEvent to string.
     * format: gameStep+fromAvatar+activeTypeId+passiveTypeId+activeSpriteId+passiveSpriteId+posX : posY
     */
    public String toString() {
    	String result = String.valueOf(gameStep) + "+" +
    			 String.valueOf(fromAvatar) + "+" +
    			 String.valueOf(activeTypeId) + "+" +
    			 String.valueOf(passiveTypeId) + "+" +
    			 String.valueOf(activeSpriteId) + "+" +
    			 String.valueOf(passiveSpriteId) + "+" +
    			 String.valueOf(position);
    	return result;
    }
    
    private Vector2d stringToVector(String s) {
    	String[] splitted = s.split(" : ");
		return new Vector2d(Double.parseDouble(splitted[0]),Double.parseDouble(splitted[1])); 
	}
    
    /**
     * Creates a copy of this event.
     * @return the copy.
     */
    public CustomEvent copy()
    {
        return new CustomEvent(gameStep, fromAvatar, activeTypeId, passiveTypeId, activeSpriteId, passiveSpriteId, position.copy());
    }

    @Override
    public int compareTo(CustomEvent o) {
        if(this.gameStep < o.gameStep)       return -1;   //First tie break: gameStep.
        if(this.gameStep > o.gameStep)       return 1;
        if(this.fromAvatar && !o.fromAvatar) return -1;   //Second tie break: who triggered.
        if(!this.fromAvatar && o.fromAvatar) return 1;
        if(this.passiveTypeId < o.passiveTypeId)     return -1;   //Third tie break: against what.
        if(this.passiveTypeId > o.passiveTypeId)     return 1;
        if(this.activeTypeId < o.activeTypeId)       return -1;   //Fourth tie break: who triggered it
        if(this.activeTypeId > o.activeTypeId)       return 1;
        return 0;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(!(o instanceof CustomEvent)) return false;
        CustomEvent other = (CustomEvent)o;

        if(this.gameStep != other.gameStep) return false;
        if(this.fromAvatar != other.fromAvatar) return false;
        if(this.activeTypeId != other.activeTypeId) return false;
        if(this.passiveTypeId != other.passiveTypeId) return false;
        if(this.activeSpriteId != other.activeSpriteId) return false;
        if(this.passiveSpriteId != other.passiveSpriteId) return false;
        if(! this.position.equals(other.position)) return false;
        return true;
    }
}
