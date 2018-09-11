package Network;

/**
 * Interface which contains the method signatures that
 * must be implemented within a NeuralNetwork class.
 *
 * @author Alex Kelly
 */
public interface INeuralNetwork {

    /**
     * Feed-forward process
     *
     * @param input input data network is to be trained on
     * @return the input from the output layer of the network
     */
    float[] calculateOutput(float[] input);

    /**
     * Backpropagation process for calculating the error at each
     * neuron, starting from the output layer.
     *
     * @param targetOutput the expected output for the network
     */
    void backpropagateError(float[] targetOutput);

    /**
     * Method for updating the weights dependent on the error.
     */
    void updateWeights();

    /**
     * Method for calculating the mean square error of a data input.
     *
     * @param input the input data for the network
     * @param targetOutput expected output
     * @return the mean square error of the row
     */
    float meanSqError(float[] input, float[] targetOutput);

}
