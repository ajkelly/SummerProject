package Network;

import java.util.*;

/**
 * Class which creates a set of data for the network to allow
 * for multiple rows of input to be trained along with its
 * corresponding output.
 *
 * (a set consists of 2D float arrays - input and output -
 * stored at index 0 and 1 of an ArrayList)
 *
 * @author Alex Kelly
 */
public class NetworkData {

    private int input;
    private int output;

    /*
     * AL for storing input and corresponding output
     * so it can be trained as a collective set
     * data(index)[0] -> input data, data(index)[1] -> corresponding target output data
     */
    private List<float[][]> data = new ArrayList<>();

    /**
     * CONSTRUCTOR
     */
    public NetworkData() {

        this.input = 0; //index of input
        this.output = 1; //index of output
    }

    /**
     * Method for adding input and output to the data AL.
     *
     * @param input the new input set to be added
     * @param targetOutput the corresponding output set
     * @throws IllegalArgumentException input/output neurons must match
     * corresponding getSize
     */
    public void addNetworkData(float[] input, float[] targetOutput) {
        //check input size
        if (input.length != NetworkConstants.INPUT_LAYER_SIZE) {
            throw new IllegalArgumentException("input length " + input.length +
                    "does not correspond with number of input neurons " + NetworkConstants.INPUT_LAYER_SIZE);
        }
        //check output size
        if (targetOutput.length != NetworkConstants.OUTPUT_LAYER_SIZE) {
            throw new IllegalArgumentException("output length " + targetOutput.length +
                    "does not correspond with number of output neurons " + NetworkConstants.OUTPUT_LAYER_SIZE);
        }
        //input data added at position 0, output at position 1
        data.add(new float[][]{input, targetOutput});
    }

    /**
     * Method that returns the number of data sets, which
     * is the number of rows of training data.
     *
     * @return number of data sets
     */
    public int getSize() {

        return data.size();
    }

    /**
     * Method that returns the length of inputs
     *
     * @return length of inputs
     */
    public int getInputSize() {

        return data.get(this.input).length;
    }

    /**
     * Method that returns the length of outputs
     *
     * @return length of inputs
     */
    public int getOutputSize() {

        return data.get(this.output).length;
    }

    /**
     * Method for obtaining the input within the set at a given index.
     *
     * @param index the index of the set to obtain
     * @return individual data set input at given index
     * @throws IllegalArgumentException attempt to access invalid index
     */
    public float[] getInput(int index) {

        if (index < 0 || index > getSize()) {
            throw new IllegalArgumentException("invalid index '" + index + "', must be between 0 and " + getSize());
        }

        return data.get(index)[this.input];
    }

    /**
     * Method for returning the entire input array.
     *
     * @return entire input array
     */
    public float[][] getInputArray() {

        float[][] temp = new float[data.size()][NetworkConstants.INPUT_LAYER_SIZE];

        for (int i = 0; i < data.size(); i++) {
            temp[i] = data.get(i)[this.input];
        }

        return temp;
    }

    /**
     * Method for obtaining the output within the set at a given index.
     *
     * @param index the index of the set to obtain
     * @return individual data set input at given index
     * @throws IllegalArgumentException attempt to access invalid index
     */
    public float[] getOutput(int index) {

        if (index < 0 || index > getSize()) {
            throw new IllegalArgumentException("invalid index '" + index + "', must be between 0 and " + getSize());
        }

        return data.get(index)[this.output];
    }

    /**
     * Method for returning the entire output array.
     *
     * @return entire output array
     */
    public float[][] getOutputArray() {

        float[][] temp = new float[data.size()][NetworkConstants.OUTPUT_LAYER_SIZE];

        for (int i = 0; i < data.size(); i++) {
            temp[i] = data.get(i)[this.output];
        }

        return temp;
    }

}
