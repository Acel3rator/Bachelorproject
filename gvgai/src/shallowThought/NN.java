package shallowThought;

import java.util.Arrays;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.data.norm.MaxNormalizer;
import org.neuroph.util.data.norm.Normalizer;

public class NN {

    /**
     * This sample shows how to train  neural network for iris classification problem using Neuroph
     * For more details about training process, error, iterations use NeurophStudio which provides rich environment  for
     * training and inspecting neural networks
     * @author Zoran Sevarac <sevarac@gmail.com>
     */
    static class LearningListener implements LearningEventListener {

        long start = System.currentTimeMillis();

        public void handleLearningEvent(LearningEvent event) {
            BackPropagation bp = (BackPropagation) event.getSource();
            System.out.println("Current iteration: " + bp.getCurrentIteration());
            System.out.println("Error: " + bp.getTotalNetworkError());
            System.out.println((System.currentTimeMillis() - start) / 1000.0);
            start = System.currentTimeMillis();
        }
    }

    // Runs the Network
    public static void main(String[] args) {    
        // get the path to file with data
        String inputFileName = "./src/shallowThought/data_set/game_classification.txt";
        int inputsCount = 5;
        int outputsCount = 10;
            
        // create MultiLayerPerceptron neural network
        MultiLayerPerceptron neuralNet = new MultiLayerPerceptron(inputsCount, 16, outputsCount);
        // create training set from file
        DataSet agentDataSet = DataSet.createFromFile(inputFileName, inputsCount, outputsCount, ",", false);
        
        //Normalizing data set
        Normalizer normalizer = new MaxNormalizer();
        normalizer.normalize(agentDataSet);
        
        //Creating training set (70%) and test set (30%)
        DataSet[] trainingAndTestSet = agentDataSet.createTrainingAndTestSubsets(70, 30);
        DataSet trainingSet = trainingAndTestSet[0];
        DataSet testSet = trainingAndTestSet[1];
        
        // train the network with training set
        neuralNet.getLearningRule().addListener(new LearningListener());
        neuralNet.getLearningRule().setLearningRate(0.01);
        // neuralNet.getLearningRule().setMaxError(0.001);     --- optional
        neuralNet.getLearningRule().setMaxIterations(30000);

        neuralNet.learn(agentDataSet);

        neuralNet.save("agentNet.nnet");
            
        System.out.println("Done training.");
        System.out.println("Testing network...");
    }
    
    /**
     * This is going to be the Method, that uses the neural network to predict which agent
     * to choose 
     * @param neuralNet neural network
     * @param testSet test data set
    */
    public static void testNeuralNetwork(NeuralNetwork neuralNet, DataSet testSet) {

        for(DataSetRow testSetRow : testSet.getRows()) {
            neuralNet.setInput(testSetRow.getInput());
            neuralNet.calculate();
            double[] networkOutput = neuralNet.getOutput();

            System.out.print("Input: " + Arrays.toString( testSetRow.getInput() ) );
            System.out.println(" Output: " + Arrays.toString( networkOutput) );
        }
    }
   
    
}
