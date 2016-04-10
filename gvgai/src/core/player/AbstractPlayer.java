package core.player;

import core.game.Game;
import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import shallowThought.CustomGameRun;
import shallowThought.CustomState;
import tools.ElapsedCpuTimer;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 13:42
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public abstract class AbstractPlayer {

    /**
     * File where the actions played in a given game are stored.
     */
    private String actionFile;

    /**
     * Writer for the actions file.
     */
    private BufferedWriter writer;

    /**
     * File for level-log
     */
    private File envLog;

    /**
     * CustomStateSaving (for levellogging)
     */
    private CustomGameRun cgr;

    /**
     * Set this variable to FALSE to avoid logging the actions to a file.
     */
    private static final boolean SHOULD_LOG = true;

    /**
     * Last action executed by this agent.
     */
    private Types.ACTIONS lasAction = null;


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player. The action returned must be contained in the
     * actions accessible from stateObs.getAvailableActions(), or no action
     * will be applied.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public abstract Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer);


    /**
     * This function sets up the controller to save the actions executed in a given game.
     * @param actionFile file to save the actions to.
     * @param randomSeed Seed for the sampleRandom generator of the game to be played.
     */
    final public void setup(String actionFile, int randomSeed) {
        this.actionFile = actionFile;

        try {
            if(this.actionFile!=null && SHOULD_LOG)
            {
                writer = new BufferedWriter(new FileWriter(new File(this.actionFile)));
                writer.write(randomSeed + "\r\n");
    			envLog = new File(actionFile.substring(0, actionFile.length()-11) + "env.txt"); // -11 for actions.txt (replace suiffix)
    			cgr = new CustomGameRun();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the agent, writing actions to file.
     */
    final public void teardown() {
        try {
            if(writer!=null) {
                writer.close();
                cgr.writeToFile(envLog);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs a single action
     * @param action the action to log.
     */
    final public void logAction(Types.ACTIONS action) {

        lasAction = action;
        if(writer!=null && SHOULD_LOG) {
            try {
                writer.write(action.toString() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Gets the last action executed by this controller.
     * @return the last action
     */
    public Types.ACTIONS getLastAction()
    {
        return lasAction;
    }

    /**
     * Gets the player the control to draw something on the screen.
     * It can be used for debug purposes.
     * @param g Graphics device to draw to.
     */
    public void draw(Graphics2D g)
    {
        //Overwrite this method in your controller to draw on the screen.
        //This method should be left empty in this class.
    }

	public void logStuff(Types.ACTIONS action, StateObservation observation) {
        if(writer!=null && SHOULD_LOG) {
            try {
                writer.write(observation.getGameTick() + "," + observation.getGameScore() 
                + "," + action.toString() + "\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		
	}

	public void logWinner(double score, Game game) {
        if(writer!=null && SHOULD_LOG) {
            try {
                writer.write("finalScore: " + score + "\r\n");
                writer.write("didWin: ");
                if(game.getWinner() == Types.WINNER.PLAYER_WINS) {
                	writer.write(1 + "\r\n");
                } else {
                	writer.write(0 + "\r\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}


	public void logLevel(StateObservation observation) {
		if(writer!=null && SHOULD_LOG) {
	        cgr.update(observation);
		}
	}	
}
