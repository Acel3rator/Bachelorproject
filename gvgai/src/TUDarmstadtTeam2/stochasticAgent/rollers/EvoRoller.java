package TUDarmstadtTeam2.stochasticAgent.rollers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import TUDarmstadtTeam2.stochasticAgent.StateRec;
import core.game.StateObservation;

/**
 * Inspired by Diego Perez Algorithm: https://github.com/diegopliebana/EvoMCTS
 */
public class EvoRoller extends AbstractRoller {

	public ArrayList<String> newFeatures;
    public HashMap<String, Integer> featuresMap;

    public double[] params;
    double[] bias;

    int nActions;
    int nFeatures;

    Random rand;

    StateRec features;

	public EvoRoller(StateObservation state, StateRec features, Random rnd) {
		super(state, features, rnd);
		init(state, features);
	}
    
    /**
     * initializes the roller
     * @param state the gameState at current point in time
     * @param features the features in question
     */
    public void init(StateObservation state, StateRec features)
    {
        nActions = state.getAvailableActions().size();
        features.updateState(state);
        nFeatures = features.getFeatureVector().size();
        bias = new double[nActions];
        params = new double[nActions*nFeatures];
    }
    
    /**
     * calculates the feature weights
     * previously getFeatureWeights
     * @param gameState the current state of the game
     * @return the feature weights
     */
    private double[] calcFeatureWeights(StateObservation gameState)
    {
        double weights[] = new double[params.length];

        String[] keys = features.getFeatureVectorKeys(gameState);

        for(String k : keys) {
            if(!featuresMap.containsKey(k)) {
                if(!newFeatures.contains(k))
                    newFeatures.add(k);
            } else {
                int pos = featuresMap.get(k);
                for(int actIdx = 0; actIdx < nActions; actIdx++) {
                    int weightPos = pos*nActions + actIdx;
                    if(weightPos < weights.length) {
                        weights[weightPos] = params[weightPos];
                    }
                }

            }
        }

        return weights;
    }
    
    /**
     * returns the actionIndex for the current rolloutStep
     * @param gameState the gameState in question
     * @return the calculated actions index
     */
    public int roll(StateObservation gameState) {
        double[] biases = calcBiases(gameState);
        // now track relative cumulative probability
        double prob = rand.nextDouble();
        double acc = 0;
        int action = 0;
        for ( ; action<nActions; action++) {
            acc += biases[action];
            if (prob < acc) {
            	return action;
            }
        }
        if (action == nActions) {
            action = rand.nextInt(nActions);
        }
        return action;
    }    

    /**
     * calculates the biases for the current rolloutStep
     * @param gameState the current game state
     * @return the biases for the current rolloutStep
     */
    private double[] calcBiases (StateObservation gameState) {
        double[] biases = new double[bias.length];
        double[] featureWeightVector = calcFeatureWeights(gameState);
        int h = 0; // used to step over params
        double tot = 0;
        for (int i=0; i<nActions; i++) {
            bias[i] = 0;

            for (int j=0; j<nFeatures; j++) {
                bias[i] += params[h] * featureWeightVector[j];
                h++;
            }
            bias[i] = Math.exp(bias[i]);
            tot += bias[i];
        }
        for (int i=0; i<biases.length; i++) {
            biases[i] = bias[i] / tot;
        }
        return biases;
    }    
    
    /**
     * returns the size of the ValueArray
     * @return the size of the ValueArray
     */
    private int nDim() {
        return nActions * nFeatures;
    }

    /**
     * Updates the parameters of the featureMap with the new values
     * @param featuresMap the new featuresMap
     * @param w the values
     */
    public void setParams(HashMap<String, Integer> featuresMap, double[] w) {
        this.featuresMap = featuresMap;
        this.newFeatures = new ArrayList<String>();

        nFeatures = featuresMap.size();
        params = new double[nDim()];
        for (int i=0; i<nDim(); i++) {
            params[i] = w[i];
        }
    }
    
    /**
     * returns true if new features were found
     * @return true if new features were found
     */
    public boolean newFeaturesFound() {
        return this.newFeatures.size() > 0;
    }
    
    //----------------Getters / Setters ----------------
    
    public ArrayList<String> getNewFeatures() {
        return this.newFeatures;
    }
}
