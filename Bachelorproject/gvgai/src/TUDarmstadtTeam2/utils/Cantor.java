package TUDarmstadtTeam2.utils;

/**
 * taken from
 * http://de.wikipedia.org/wiki/Cantorsche_Paarungsfunktion#Umkehrfunktion
 * 
 * @author tobi
 *
 */
public class Cantor {
	public static int compute(int x, int y) {
		return (x + y) * (x + y + 1) / 2 + y;

	}

	public static int computeX(int z) {
		int j = (int) Math.floor(Math.sqrt(0.25 + 2 * z) - 0.5);
		return j - (z - j * (j + 1) / 2);
	}

	public static int computeY(int z) {
		int j = (int) Math.floor(Math.sqrt(0.25 + 2 * z) - 0.5);
		return z - j * (j + 1) / 2;
	}
}