package Network;

import Utils.NeuralNetworkUtils;

import java.util.Arrays;

/**
 * Contains single method for testing the accuracy of
 * the Neural networks predictions.
 *
 * As a test, also added a test for logical operators.
 *
 * @author Alex Kelly
 */
public class TestNetwork {

    /**
     * Method for testing the network by iterating through the target
     * outputs and the expected output and comparing whether they
     * correspond. As we are aware that the target output is either an
     * array [1,0] or [0,1] and the actual output will not quite
     * converge to those exact values, the method simply checks at
     * which position the higher value is at, and if it is the same
     * for both the target and actual output, it means the network
     * predicted correctly. The number of correct predictions are
     * recorded as count, and turned into a percentage.
     *
     * @param network the trained neural network
     * @param set the test data set
     */
    public static void testAccuracy(NeuralNetwork network, NetworkData set) {
        //number of correct values predicted, initially 0
        int count = 0;

        for (int i = 0; i < set.getSize(); i++) {

            float actualHighestIndex = NeuralNetworkUtils.compareIndexes(network.calculateOutput(set.getInput(i)));
            float expectedHighestIndex = NeuralNetworkUtils.compareIndexes(set.getOutput(i));

//            if (i % 10 == 0) { //print every 10th prediction
//                System.out.print("row " + i + ": expected -> " + Arrays.toString(set.getOutput(i)));
//                System.out.println(" ----- actual -> " + Arrays.toString(network.calculateOutput(set.getInput(i))));
//            }

            if (actualHighestIndex == expectedHighestIndex) {
                count++;
            }

//            //alternative testing (test due to surprising network accuracy)
//            float[] a = network.calculateOutput(set.getInput(i));
//            float[] b = set.getOutput(i);
//            if (Math.abs(a[0] - b[0]) < 0.1) {
//                count++;
//            }

            else { //print incorrect predictions
                System.out.print("row " + i + ": expected -> " + Arrays.toString(set.getOutput(i)));
                System.out.println(" ----- actual -> " + Arrays.toString(network.calculateOutput(set.getInput(i))));
            }
        }

        System.out.println("\n******************************************************************************");
        System.out.println();
        System.out.println("                            TESTING COMPLETE");
        System.out.println("                            ----------------\n");
        System.out.println("                   Network predicted " + count + "/" + set.getSize() + " correctly");
        float percent = ((float) count / (float) set.getSize()) * 100;
        System.out.print("                            accuracy = "); System.out.printf("%.2f", percent); System.out.println("%");
        System.out.println("");
        System.out.println("******************************************************************************");
    }

    /**
     * Method which used to test whether the network was functional
     * and able to learn logical operators.
     * Method takes the actual and expected outputs, which are arrays
     * containing one element, accesses that one element (index [0])
     * and calculates whether they are closer than a specified
     * tolerance.
     *
     * @param net neural network object
     * @param set the network data object being learned and tested
     * @param tolerance the maximum error between actual and expected output
     */
    public static void testLogical(NeuralNetwork net, NetworkData set, double tolerance) {

        if (tolerance < 0 || tolerance > 0.2) {
            throw new IllegalArgumentException("please provide tolerance between approximately 0.01 and 0.1");
        }

        int correct = 0;

        for (int i = 0; i < set.getSize(); i++) {
            float[] actual = net.calculateOutput(set.getInput(i));
            float[] target = set.getOutput(i);

            float a = actual[0];
            float t = target[0];

            //check whether the expected and actual outputs are within the tolerance
            if (Math.abs(a - t) < tolerance) {
                correct++;
            }
        }
        System.out.println("\n******************************************************");
        System.out.println();
        System.out.println("                  TESTING COMPLETE");
        System.out.println("                  ----------------\n");
        System.out.println("         Network predicted " + correct + "/" + set.getSize() + " correctly");
        float percent = ((float) correct / (float) set.getSize()) * 100;
        System.out.print("                  accuracy = "); System.out.printf("%.2f", percent); System.out.println("%");
        System.out.println("");
        System.out.println("******************************************************");
    }

}
