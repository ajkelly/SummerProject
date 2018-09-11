package Network;

import Utils.NeuralNetworkUtils;
import Utils.RandomNumberGenerator;

import java.io.*;
import java.util.Arrays;

/**
 * Class that contains the implementation of the
 * neural network that uses backpropagation for the
 * training.
 *
 * @author Alex Kelly
 */
public class NeuralNetwork implements Serializable, INeuralNetwork {

    private final int[] neuronsPerLayer; //number of neurons in each layer of the network
    private final int numLayers; //number of layers in the network

    /*
     * stores the output from a given neuron:
     * 2D array as the first dimension is the layer, and the second
     * relates to the specific neuron
     */
    private float[][] outputFromNeuron;
    /*
     * weights, one for each neuron:
     * 3D array as the first is for the layer, the second is for the
     * specific neuron, and the third is for the neuron in the previous
     * layer which is connected to the current neuron
     */
    private float[][][] weights;
    private float[][] bias; //every neuron has one bias

    /*
     * difference between expected and actual output, to be used in
     * the backprop process for updating weights accordingly
     */
    private float[][] error;
    private float[][] outputDerivativeSigmoid; //derivative sigmoid of outputs

    /**
     * CONSTRUCTOR
     *
     * @param npl the number of neurons in each layer,
     *            specified in the NetworkConstants class
     */
    public NeuralNetwork(int[] npl) {
        //check parameter passed is the same as NEURONS_PER_LAYER
        if (!(Arrays.equals(npl, NetworkConstants.NEURONS_PER_LAYER))) {
            throw new IllegalArgumentException("pass parameter 'NetworkConstants.NEURONS_PER_LAYER' when" +
                    " initialising NeuralNetwork. To modify the network dimensions, edit 'NEURONS_PER_LAYER'" +
                    " within NetworkConstants class.");
        }

        //initialise neuronsPerLayer to be easily referred to within this class
        this.neuronsPerLayer = NetworkConstants.NEURONS_PER_LAYER;
        //initialise numLayers to be easily referred to within this class
        this.numLayers = NetworkConstants.NUM_LAYERS;

        //initialise 1st dimension to be the number of layers network consists of
        this.outputFromNeuron = new float[numLayers][];
        this.weights = new float[numLayers][][];
        this.bias = new float[numLayers][];

        this.error = new float[numLayers][];
        this.outputDerivativeSigmoid = new float[numLayers][];

        //second dimension is the number of neurons at each layer
        for(int i = 0; i < numLayers; i++) {
            //all initialised to default 0.0 values, will be updated in methods
            this.outputFromNeuron[i] = new float[neuronsPerLayer[i]];
            this.error[i] = new float[neuronsPerLayer[i]];
            this.outputDerivativeSigmoid[i] = new float[neuronsPerLayer[i]];
            this.bias[i] = new float[neuronsPerLayer[i]];
        }

        //bias and weights initialised randomly
        initialiseBias();
        initialiseWeights();
    }

    /**
     * Method which initialises the biases to random float
     * values between 0-1.
     */
    private void initialiseBias() {
        for(int i = 0; i < numLayers; i++) {
            for (int j = 0; j < neuronsPerLayer[i]; j++) {
                this.bias[i][j] = RandomNumberGenerator.randomise();
            }
        }
//        System.out.print("\nbiases: " + Arrays.deepToString(bias)); //test
    }

    /**
     * Method which initialises the weights to random float
     * values between 0-1.
     */
    private void initialiseWeights() {
        for(int i = 0; i < numLayers; i++) {
            //not the input layer as no previous layer neurons
            if (i > 0) {
                weights[i] = RandomNumberGenerator.randomArray(neuronsPerLayer[i], neuronsPerLayer[i-1]);
            }
//            System.out.println("weights -> " + Arrays.deepToString(weights)); //test
        }
    }

    /**
     * Method for calculating the output. Achieved in two steps:
     * - firstly, calculates the sum of the inputs from the previous
     * layer, multiplied by the current weight.
     * - secondly, applies the sigmoid activation function to the total.
     *
     * The output from the final layer [numLayers-1] is returned.
     *
     * @param input the network training/test inputs
     * @return actualOutput, an array of outputs from the output layer
     * of the network
     */
    @Override
    public float[] calculateOutput(float[] input) {

        //set the input to the output of the first (input) layer for each row
        this.outputFromNeuron[0] = input;
        //iterate through every other layer from first hidden layer
        for(int layer = 1; layer < numLayers; layer ++) {
            for(int neuron = 0; neuron < neuronsPerLayer[layer]; neuron ++) {
                //now have specific neuron accessed by layer and neuron index
                float sum = 0;
                //iterate through previous layer and sum outputs * current weight
                for(int prevNeuron = 0; prevNeuron < neuronsPerLayer[layer-1]; prevNeuron ++) {
                    sum += outputFromNeuron[layer-1][prevNeuron] * weights[layer][neuron][prevNeuron];
                }
                sum += bias[layer][neuron]; //plus current bias
                //apply activation function and store result in outputFromNeuron
                outputFromNeuron[layer][neuron] = ActivationFunction.sigmoid(sum);

                //calculate output derivative here for use in backprop process
                outputDerivativeSigmoid[layer][neuron] = ActivationFunction.derivativeSigmoid(outputFromNeuron[layer][neuron]);
            }
        }
//        System.out.println(Arrays.toString(outputFromNeuron[numLayers -1]));
        return outputFromNeuron[numLayers -1]; //return the output from the output layer
    }

    /**
     * Method for storing the error of each neuron. Implemented using
     * back-propagation, starting at the output layer and propagating the
     * error back through the hidden layers and stopping at the first
     * hidden layer (no error for input layer as no calculations occur).
     *
     * @param targetOutput the actual expected output
     */
    @Override
    public void backpropagateError(float[] targetOutput) {

        //start by looping through output neurons
        for(int neuron = 0; neuron < neuronsPerLayer[numLayers -1]; neuron++) {
            //set error at output layer to the actual error * derivative sigmoid of output
            error[numLayers -1][neuron] = (outputFromNeuron[numLayers -1][neuron] - targetOutput[neuron])
                    * outputDerivativeSigmoid[numLayers -1][neuron];
        }

        //loop through all hidden layers (stop at input layer -> layer = 0)
        for(int layer = numLayers -2; layer > 0; layer--) {
            //access individual neurons
            for(int neuron = 0; neuron < neuronsPerLayer[layer]; neuron++) {
                float err = 0; //set error to 0 initially
                //iterate through next layer where error has already been calculated
                for(int nextLayerNeuron = 0; nextLayerNeuron < neuronsPerLayer[layer +1]; nextLayerNeuron++) {
                    //increase sum by weight that connects current and next neuron -> [layer +1] * error at next
                    err += weights[layer +1][nextLayerNeuron][neuron] * error[layer +1][nextLayerNeuron];
                }
                //set current neurons error
                this.error[layer][neuron] = err * outputDerivativeSigmoid[layer][neuron];
            }
        }
    }

    /**
     * Method for updating the weights and the biases by the delta
     * value, which can be calculated using the following formula:
     * - learning rate * prev neuron output * current neuron error
     */
    @Override
    public void updateWeights() {

        //start at first hidden layer up to output
        for(int layer = 1; layer < numLayers; layer++) {
            for(int neuron = 0; neuron < neuronsPerLayer[layer]; neuron++) {
                //iterate through every weight that connects previous and current neuron (prev layer -> layer-1)
                for(int prevLayerNeuron = 0; prevLayerNeuron < neuronsPerLayer[layer-1]; prevLayerNeuron++) {
                //calc change of weight: - lr * output from prev neuron * error current neuron
                float weightChange = - NetworkConstants.LEARNING_RATE * outputFromNeuron[layer-1][prevLayerNeuron] * error[layer][neuron];
                //update current neuron with weight change value
                weights[layer][neuron][prevLayerNeuron] += weightChange;
                }
                /*
                 * each has only one bias so update this outside loop,
                 * also bias is not connected to prev neurons so just lr * current error
                 */
                float biasChange = - NetworkConstants.LEARNING_RATE * error[layer][neuron];
                bias[layer][neuron] += biasChange;
            }
        }
    }

    /**
     * Method for calculating the mean squared error of the
     * network.
     *
     * @param input inputs to the network
     * @param targetOutput expected outputFromNeuron
     * @return the mean squared error
     * @throws IllegalArgumentException input/output neurons must match
     * corresponding getSize
     */
    @Override
    public float meanSqError(float[] input, float[] targetOutput) {

        if (input.length != NetworkConstants.INPUT_LAYER_SIZE) {
            throw new IllegalArgumentException("input length: " + input.length +
                    " does not match number of input neurons: " + NetworkConstants.INPUT_LAYER_SIZE);
        }
        if (targetOutput.length != NetworkConstants.OUTPUT_LAYER_SIZE) {
            throw new IllegalArgumentException("output length: " + targetOutput.length +
                    " does not match number of output neurons: " + NetworkConstants.OUTPUT_LAYER_SIZE);
        }

        calculateOutput(input);

        float mse = 0;
        for(int i = 0; i < targetOutput.length; i++) {
            //(target output - output from the final layer at i) ^2
            mse += NeuralNetworkUtils.square(targetOutput[i] - outputFromNeuron[numLayers -1][i]);
        }
        return  mse / (2f * targetOutput.length);
    }

    /**
     * Method that calculates mean square error of each data set and
     * calculates the average.
     *
     * @param set NetworkData having mean sq error calculated
     * @return the mean sq error of the set
     */
    public float meanSqError(NetworkData set) {

        float mse = 0;
        for(int i = 0; i < set.getSize(); i++) {
            mse += meanSqError(set.getInput(i), set.getOutput(i));
        }
        return mse / set.getSize();
    }

    /**
     * Method for saving a neural network  by taking a file name
     * and the name of the trained network object, and writing the
     * network object to the file.
     *
     * @param fileName to write network to
     * @param network the neural network object being saved
     */
    public void saveANN(String fileName, NeuralNetwork network) {

        try (FileOutputStream f = new FileOutputStream(fileName);
             ObjectOutputStream os = new ObjectOutputStream(f)) {

            os.writeObject(network); //write neural network to the file
            os.flush();

            System.out.println("Network saved successfully to " + fileName);

        } catch (IOException e) {
            System.err.println("Error saving network to " + fileName + ":" + e);
            e.printStackTrace();
        }
    }

    /**
     * Method for loading a previously trained network by reading
     * a Network object from a file and returning it.
     *
     * @param  fileName name of file where the object is stored
     * @return The Network object
     */
    public static NeuralNetwork loadANN(String fileName) {

        if (!(new File(fileName)).exists()) {
            System.err.println("No such file '" + fileName + "' exists..");
        }

        try (FileInputStream i = new FileInputStream(fileName);
             ObjectInputStream is = new ObjectInputStream(i)) {

            NeuralNetwork ann = (NeuralNetwork) is.readObject(); //reads in the network

            System.out.println("Successfully loaded the neural network!");
            return ann;

        } catch (ClassNotFoundException | IOException e) {
            System.err.println("error loading file: '" + fileName + "'");
            e.printStackTrace();
            return null;
        }
    }


}
