package shallowThought.offline;
import java.util.Random;

import core.ArcadeMachine;
import shallowThought.cma.src.fr.inria.optimization.cmaes.CMAEvolutionStrategy;
import shallowThought.cma.src.fr.inria.optimization.cmaes.fitness.IObjectiveFunction;

public class OfflineOptimizer {
	
	public void main() {
		// Step 1: Find optimal parameters for all subagents (with cma-es)
		String[] allGames = new String[]{"aliens", "boulderdash", "butterflies", "chase", "frogs",
                /*5*/"missilecommand", "portals", "sokoban", "survivezombies", "zelda",
                /*10*/"camelRace", "digdug", "firestorms", "infection", "firecaster",
                "overload", "pacman", "seaquest", "whackamole", "eggomania",
        		/*20*/"bait", "boloadventures", "brainman", "chipschallenge",  "modality",
        		"painter", "realportals", "realsokoban", "thecitadel", "zenpuzzle",
        		/*30*/"roguelike", "surround", "catapults", "plants", "plaqueattack",
        		"jaws", "labyrinth", "boulderchase", "escape", "lemmings",
        		/*40*/"solarfox", "defender", "enemycitadel", "crossfire", "lasers",
        		"sheriff", "chopper", "superman", "waitforbreakfast", "cakybaky",
        		/*50*/"lasers2", "hungrybirds" ,"cookmepasta", "factorymanager", "racebet2",
                "intersection", "blacksmoke", "iceandfire", "gymkhana", "tercio"};
		
		String shallowThought = "shallowThought.Agent";
	}	

	public static void optimizeCMA(String[] gamesX, String[] levelX, String subAgent) {
		IObjectiveFunction fitfun = new agent(gamesX, levelX, subAgent);

		// new a CMA-ES and set some initial values
		CMAEvolutionStrategy cma = new CMAEvolutionStrategy();
		cma.readProperties(); // read options, see file CMAEvolutionStrategy.properties
		cma.setDimension(10); // overwrite some loaded properties
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
	private String[] games;
	private String[] levels;
	private String subAgent;
	String shallowThought = "shallowThought.Agent";
	boolean visuals = true;
	public agent(String[] gameX, String[] levelX, String subAgentX) {
		games = gameX;
		levels = levelX;
		subAgent = subAgentX;
	}
	public double valueOf (double[] x) {
		double res = 0;
        int seed = new Random().nextInt();
		for (String game : games) {
			for (String level : levels) {
				res += ArcadeMachine.runOneGame(game, level, visuals, shallowThought, null, seed);
			}
		}
		res = res/(games.length * levels.length);
		return res;
	}
	public boolean isFeasible(double[] x) {return true; } // entire R^n is feasible
}