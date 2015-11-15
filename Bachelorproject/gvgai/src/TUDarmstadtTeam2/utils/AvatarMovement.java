package TUDarmstadtTeam2.utils;

import core.game.StateObservation;
import ontology.Types;
import tools.Vector2d;

public class AvatarMovement {
	private Vector2d prevAvatarPosition;
	private Vector2d prevAvatarOrientation;
	private Vector2d newAvatarPosition;
	private Vector2d newAvatarOrientation;
	private Types.ACTIONS lastAction;

	public AvatarMovement(StateObservation prev, StateObservation next, Types.ACTIONS lastAction) {
		this(prev, lastAction);
		newAvatarPosition = next.getAvatarPosition();
		newAvatarOrientation = next.getAvatarOrientation();
	}

	public AvatarMovement(StateObservation prev, Types.ACTIONS lastAction) {
		prevAvatarPosition = prev.getAvatarPosition();
		prevAvatarOrientation = prev.getAvatarOrientation();
		this.lastAction = lastAction;
	}

	public boolean avatarDidNotMoved() {
		return (prevAvatarOrientation.x == newAvatarOrientation.x
				&& prevAvatarOrientation.y == newAvatarOrientation.y
				&& prevAvatarPosition.x == newAvatarPosition.x && prevAvatarPosition.y == newAvatarPosition.y);
	}

	public boolean avatarDidMovementAction() {
		if(lastAction == null){
			return false;
		}
		return (!lastAction.equals(Types.ACTIONS.ACTION_NIL) && !lastAction
				.equals(Types.ACTIONS.ACTION_USE));
	}

	public void setNewState(StateObservation soNext) {
		newAvatarPosition = soNext.getAvatarPosition();
		newAvatarOrientation = soNext.getAvatarOrientation();
	}

}
