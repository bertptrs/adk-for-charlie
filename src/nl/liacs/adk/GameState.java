package nl.liacs.adk;

public final class GameState {
	public static int UNDEFINED = 0; // Yes, I defined undefined. Peculiar.
	public static int INITIALIZING = 1;
	public static int GAME_OVER = 2;
	public static int RUNNING = 4;
	public static int PAUSED = 8;
}
