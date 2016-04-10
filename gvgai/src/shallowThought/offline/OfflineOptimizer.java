package shallowThought.offline;
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

	public static void optimizeCMA(String[] gamesX, String[] levelX, String subAgent) {
		IObjectiveFunction fitfun = new agent(gamesX, levelX, subAgent);

		// new a CMA-ES and set some initial values
		CMAEvolutionStrategy cma = new CMAEvolutionStrategy();
		cma.readProperties(); // read options, see file CMAEvolutionStrategy.properties
		cma.setDimension(5); // overwrite some loaded properties TODO add dimension erkennungsfunction 
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
		
		// write best parameters in a separate file, so we have them in one place
		
	}
}


class agent implements IObjectiveFunction { // meaning implements methods valueOf and isFeasible
	private String[] games;
	private String[] levels;
	private String subAgent;
	private String[][] parameters;
	String shallowThought = "shallowThought.Agent";
    protected Secretary glados = new Secretary();
	boolean visuals = true;
	private int numRuns = 0;
	
	// Constructor
	public agent(String[] gameX, String[] levelX, String subAgentX) {
		games = gameX;
		levels = levelX;
		subAgent = subAgentX;
		readConfig();
	}
	
	public double valueOf (double[] x) {
		numRuns++;
		// 1) denormalize values
		String saveParas = "";
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
			saveParas += parameters[i][0] +","+ val+":";
		}
		saveParas = subAgent + ":" + saveParas.substring(0, saveParas.length()-1);
		writeTemp(saveParas, "./src/shallowThought/offline/cma_temp.txt");
		
		// 2) Apply cmaes values to bot + save all stuff in log file
		double res = 0;
        int seed = new Random().nextInt();
		for (String game : games) {
			for (String level : levels) {
				System.out.println(game.split("/")[2]);
				String game1 = game.split("/")[2].split("\\.")[0];
				String level1 = level.split("/")[2].split("\\.")[0].split("lvl")[1];
				String actionDir = "./src/shallowThought/records/" + game1 + "/" + level1 + "/shallowThought/"
   							 + subAgent;
				new File(actionDir).mkdirs();
				String actionFile = actionDir + "/" + "run_" + numRuns + ".txt";
   				writeTemp(saveParas, actionFile);
				res -= ArcadeMachine.runOneGame(game, level, visuals, shallowThought, actionFile, seed); // substract score becuse cma minimizes

				if (!glados.didWin(actionFile)) {
					res += 1000;		// We assume, that score never falls bellow -1000, so if game is lost we add 1000 to indicate bad result
					System.out.println("Result:" + res);
				}
			}
		}
		res = res/(games.length * levels.length);
		return res;
	}
	
	
	public boolean isFeasible(double[] x) {
		for (int i = 0; i < x.length; i++ ) {
			if (x[i] < 0 || x[i] > 1) {return false;}
		}
		return true; } // [0,1] is feasible
	
	/*
	 * Read the config for cmaes parameters and split into name, lower, upper bound, value type
	 * and store 
	 */
	public void readConfig() {
		// TODO (IMPORTANT) read the correct values (we have a number of possible subagents)
		File config = new File("./src/shallowThought/offline/config.txt");
    	Charset charset = Charset.forName("US-ASCII");
    	String line = null;
    	try (BufferedReader reader = Files.newBufferedReader(config.toPath(), charset)) {
    	    line = reader.readLine();  // TODO atm this reads only one line
    	} catch (IOException x) {
    	    System.err.format("IOException: %s%n", x);
    	}
    	// Use regex to split config in its components
    	String[] parameters_pre = line.split(":", 0);
    	subAgent = parameters_pre[0];
    	parameters = new String[parameters_pre.length-1][4];
    	for (int i = 1; i < parameters_pre.length; i++) {
    		parameters[i-1] = parameters_pre[i].split(",", 0);
    	}		
    }

	/*
	 * Write the temorary parameters to cma_temp.txt
	 */
    private void writeTemp(String str, String file) {
    	File tmp = new File(file);
    	Charset charset = Charset.forName("US-ASCII");
    	String line = null;
    	try (BufferedWriter writer = Files.newBufferedWriter(tmp.toPath(), charset)) {
    	    writer.write(str);
    	} catch (IOException x) {
    	    System.err.format("IOException: %s%n", x);
    	}
    }
}