package TUDarmstadtTeam2.utils;

import tools.Vector2d;

public class FootPrint {
	private Vector2d pos;
	private int number_of_times_player_was_on_this_position = 0;
	private int lastChanged;

	public FootPrint(Vector2d avatarPosition, int tick) {
		pos = avatarPosition;
		number_of_times_player_was_on_this_position = 1;
		lastChanged = tick;
	}

	public boolean isPosition(Vector2d avatarPosition) {
		return avatarPosition.x == pos.x && avatarPosition.y == pos.y;
	}

	public void increaseFootprint(int tick) {
		number_of_times_player_was_on_this_position++;
		lastChanged = tick;
	}

	public int getValue() {
		return number_of_times_player_was_on_this_position;
	}

	public int getLastChange() {
		return lastChanged;
	}

}
