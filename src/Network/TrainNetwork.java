package Network;

/**
 * Class which contains the static methods to train
 * the network.
 *
 * @author Alex Kelly
 */
public class TrainNetwork {

    /**
     * Method that trains the network in the following steps:
     * - calls method to calculateOutput the output
     * - calculates error from this
     * - calls method to backpropagate the error through the network
     * - calls method to adjusts the weights
     *
     * @param input inputs for the network
     * @param targetOutput expected outputFromNeuron for the network
     * @throws IllegalArgumentException input/output neurons must match
     * corresponding getSize
     */
    private static void train(float[] input, float[] targetOutput, NeuralNetwork network) {

        if (input.length != NetworkConstants.INPUT_LAYER_SIZE) {
            throw new IllegalArgumentException("input length: " + input.length +
                    " does not match number of input neurons: " + NetworkConstants.INPUT_LAYER_SIZE);
        }
        if (targetOutput.length != NetworkConstants.OUTPUT_LAYER_SIZE) {
            throw new IllegalArgumentException("output length: " + targetOutput.length +
                    " does not match number of output neurons: " + NetworkConstants.OUTPUT_LAYER_SIZE);
        }

        network.calculateOutput(input);
        network.backpropagateError(targetOutput);
        network.updateWeights();
    }

    /**
     * Method for training an entire set of data using the above train method.
     *
     * @param set the NetworkData to be trained
     * @param epochs the number of training iterations
     * @throws IllegalArgumentException input/output neurons must match
     * corresponding size
     */
    public static void train(NetworkData set, int epochs, NeuralNetwork network) {

        for(int i = 0; i < epochs; i++) { //for as many iterations as specified
            for(int j = 0; j < set.getSize(); j++) { //one iteration is going through the entire set
                train(set.getInput(j), set.getOutput(j), network); //train on individual entry
            }
            //print out which iteration as well as the mean sq error for the set
            System.out.println("epoch " + (i+1) + " - mean sq error --> " + network.meanSqError(set));
        }
    }

}
