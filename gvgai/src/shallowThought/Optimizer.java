package shallowThought;

import java.util.Random;

import core.ArcadeMachine;
import core.VGDLFactory;
import core.VGDLParser;
import core.VGDLRegistry;
import core.competition.CompetitionParameters;
import core.game.Game;
import core.player.AbstractPlayer;
import tools.StatSummary;

public class Optimizer extends ArcadeMachine {

	
    /**
     * Optimizes specified parameters for ONE subAgent on ONE specified level 
     * @param subAgent instance of subAgent to be optimized
     * @param 
     * @return An action for the current state
     */
    public void optimizeParameters(AbstractPlayer subAgent, String game, String level, String[] parameters, int timeForOptimization) {
        String gamesPath = "examples/gridphysics/";
        int seed = new Random().nextInt();

        //Game and level to play
        game = gamesPath + game + ".txt";
        level =  gamesPath + game + "_lvl" + level +".txt" ;
        int numRepeats = 5;
        
        
        // Actual gameplay (mostly from ArcadeMachine):
        VGDLFactory.GetInstance().init(); //This always first thing to do.
        VGDLRegistry.GetInstance().init();
        
        StatSummary scores = new StatSummary();
        Game toPlay = new VGDLParser().parseGame(game);

        // implement loop somewhere here
        
        System.out.println(" ** Playing game " + game + ", level " + level + "other stuff");

        //build the level in the game.
        toPlay.buildLevel(level);

        String filename = null;

        //Warm the game up.
        ArcadeMachine.warmUp(toPlay, CompetitionParameters.WARMUP_TIME);

        //Determine the random seed, different for each game to be played.
        int randomSeed = new Random().nextInt();

        //Create the player.
        AbstractPlayer player = ArcadeMachine.createPlayer("shallowThought.Agent", filename, toPlay.getObservation(), randomSeed);

        double score = -1;
        if(player == null)
        {
            //Something went wrong in the constructor, controller disqualified
            toPlay.disqualify();

            //Get the score for the result.
            score = toPlay.handleResult();

        }else{

            //Then, play the game.
            score = toPlay.playGame(player, randomSeed);
        }
        scores.add(score);

        toPlay.reset();
    }
}
