package shallowThought.offline;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import core.ArcadeMachine;
import shallowThought.Secretary;
import shallowThought.cma.src.fr.inria.optimization.cmaes.CMAEvolutionStrategy;
import shallowThought.cma.src.fr.inria.optimization.cmaes.fitness.IObjectiveFunction;

public class OfflineOptimizer {
	
	static Secretary glados;
	
	public OfflineOptimizer() {
		glados = new Secretary();
	}
	
	public void main() {
		// Step 1: Find optimal parameters for all subagents (with cma-es)
		String[] allGames = new String[]{"aliens", "boulderdash", "butterflies", "chase", "frogs",
                /*5*/"missilecommand", "portals", "sokoban", "survivezombies", "zelda",
                /*10*/"camelRace", "digdug", "firestorms", "infection", "firecaster",
                /*15*/"overload", "pacman", "seaquest", "whackamole", "eggomania",
        		/*20*/"bait", "boloadventures", "brainman", "chipschallenge",  "modality",
        		/*25*/"painter", "realportals", "realsokoban", "thecitadel", "zenpuzzle",
        		/*30*/"roguelike", "surround", "catapults", "plants", "plaqueattack",
        		/*35*/"jaws", "labyrinth", "boulderchase", "escape", "lemmings",
        		/*40*/"solarfox", "defender", "enemycitadel", "crossfire", "lasers",
        		/*45*/"sheriff", "chopper", "superman", "waitforbreakfast", "cakybaky",
        		/*50*/"lasers2", "hungrybirds" ,"cookmepasta", "factorymanager", "racebet2",
                /*55*/"intersection", "blacksmoke", "iceandfire", "gymkhana", "tercio"};
		
		String shallowThought = "shallowThought.Agent";

        String gamesPath = "examples/gridphysics/";
        
        for (int g = 0; g < 60; g++) {
        	for (int l = 0; l < 60; l++) {
        		for (String sA : new String[]{"olmcts", "ga"}) {
        			//Game and level to play
        	        int gameIdx = g;
        	        int levelIdx = l; //level names from 0 to 4 (game_lvlN.txt).
        	        String game = gamesPath + allGames[gameIdx] + ".txt";
        	        String level = gamesPath + allGames[gameIdx] + "_lvl" + levelIdx +".txt";
        			
        			String[] games = new String[]{game};
        			String[] levels = new String[]{level};
        			
        			optimizeCMA(games, levels, sA);

        		}
        	}
        }
  	}	

	public static void optimizeCMA(String[] games, String[] level, String subAgent) {
		IObjectiveFunction fitfun = new agent(games, level, subAgent);

		// new a CMA-ES and set some initial values
		CMAEvolutionStrategy cma = new CMAEvolutionStrategy();
		cma.readProperties(); // read options, see file CMAEvolutionStrategy.properties
		cma.setDimension(glados.getDim(subAgent)); // overwrite dimension with number of params of subagent
		cma.setInitialX(0.05); // in each dimension, also setTypicalX can be used
		cma.setInitialStandardDeviation(0.2); // also a mandatory setting 
		cma.options.stopFitness = 1e-14;       // optional setting

		// initialize cma and get fitness array to fill in later
		double[] fitness = cma.init();  // new double[cma.parameters.getPopulationSize()];

		// initial output to files
		cma.writeToDefaultFilesHeaders(0); // 0 == overwrites old files

		// iteration loop
		while(cma.stopConditions.getNumber() == 0) {

            // --- core iteration step ---
			double[][] pop = cma.samplePopulation(); // get a new population of solutions
			for(int i = 0; i < pop.length; ++i) {    // for each candidate solution i
            	// a simple way to handle constraints that define a convex feasible domain  
            	// (like box constraints, i.e. variable boundaries) via "blind re-sampling" 
            	                                       // assumes that the feasible domain is convex, the optimum is  
				while (!fitfun.isFeasible(pop[i]))     //   not located on (or very close to) the domain boundary,  
					pop[i] = cma.resampleSingle(i);    //   initialX is feasible and initialStandardDeviations are  
                                                       //   sufficiently small to prevent quasi-infinite looping here
                // compute fitness/objective value	
				fitness[i] = fitfun.valueOf(pop[i]); // fitfun.valueOf() is to be minimized
			}
			cma.updateDistribution(fitness);         // pass fitness array to update search distribution
			// --- end core iteration step ---

			// output to files and console 
			cma.writeToDefaultFiles();
			int outmod = 150;
			if (cma.getCountIter() % (15*outmod) == 1)
				cma.printlnAnnotation(); // might write file as well
			if (cma.getCountIter() % outmod == 1)
				cma.println(); 
		}
		// evaluate mean value as it is the best estimator for the optimum
		cma.setFitnessOfMeanX(fitfun.valueOf(cma.getMeanX())); // updates the best ever solution 

		// final output
		cma.writeToDefaultFiles(1);
		cma.println();
		cma.println("Terminated due to");
		for (String s : cma.stopConditions.getMessages())
			cma.println("  " + s);
		cma.println("best function value " + cma.getBestFunctionValue() 
				+ " at evaluation " + cma.getBestEvaluationNumber());
			
		// we might return cma.getBestSolution() or cma.getBestX()
	}
}


class agent implements IObjectiveFunction { // meaning implements methods valueOf and isFeasible
	Secretary glados;
	
	private String[] games;
	private String[] levels;
	private String subAgent;
	private String[][] parameters;
	String shallowThought = "shallowThought.Agent";
	boolean visuals = true;
	
	// Constructor
	public agent(String[] gameX, String[] levelX, String subAgentX) {
		glados = new Secretary();
		games = gameX;
		levels = levelX;
		subAgent = subAgentX;
		readConfig(subAgent);

	}
	
	public double valueOf (double[] x) {
		// 1) denormalize values
		String string = "";
		for (int i = 0; i < x.length; i++){
			double value = (x[i] * (Double.parseDouble(parameters[i][2]) - Double.parseDouble(parameters[i][1]))) + Double.parseDouble(parameters[i][1]);
			String val = "";
			switch (parameters[i][3]) {
				case "int":
					val = (Integer.valueOf((int)Math.round(value))).toString();
					break;
				case "double":
					val = Double.valueOf(value).toString();
					break;
			}
			string += parameters[i][0] +","+ val+":";
		}
		string = subAgent + ":" + string.substring(0, string.length()-1);
		writeTemp(string);
		// 2) Apply cmaes values to bot
		double res = 0;
        int seed = new Random().nextInt();
		for (String game : games) {
			for (String level : levels) {
				// TODO already: if player wins, score is positive. todo: if player loses, score must be negative.
				res -= ArcadeMachine.runOneGame(game, level, visuals, shallowThought, null, seed); // substracrt becuse cma minimizes
			}
		}
		res = res/(games.length * levels.length);
		return res;
	}
	
	public boolean isFeasible(double[] x) {
		for (int i = 0; i < x.length; i++ ) {
			if (x[i] < 0 || x[i] > 1) {return false;}
		}
		return true; }
	
	/*
	 * Read the config for cmaes parameters and split into name, lower, upper bound, value type
	 * and store for given subagent
	 */
	public void readConfig(String subAgent) {
		parameters = glados.getParametersFromConfig(subAgent);		
    }

	/*
	 * Write the temorary parameters to cma_temp.txt
	 * TODO, secretary? outsourcing ftw
	 */
    private void writeTemp(String str) {
    	File tmp = new File("./src/shallowThought/offline/cma_temp.txt");
    	Charset charset = Charset.forName("US-ASCII");
    	String line = null;
    	try (BufferedWriter writer = Files.newBufferedWriter(tmp.toPath(), charset)) {
    	    writer.write(str);
    	} catch (IOException x) {
    	    System.err.format("IOException: %s%n", x);
    	}
    }
}