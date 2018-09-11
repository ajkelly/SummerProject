package UnitTests;

import Network.ActivationFunction;
import Utils.NeuralNetworkUtils;
import Utils.RandomNumberGenerator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Contains unit tests for the program.
 *
 * @author Alex Kelly
 */
public class Tests {

    NeuralNetworkUtils utils = new NeuralNetworkUtils();

    /**
     * Tests the method that squares a given input value.
     */
    @Test
    public void squareTest() {

        //positive
        float sq1 = utils.square(5);
        assertEquals(25, sq1, 0);

        //negative
        float sq2 = utils.square(-3);
        assertEquals(9, sq2, 0);

        //number with dp
        float sq3 = utils.square(2.4f);
        assertEquals(5.76f, sq3, 0);
    }

    /**
     * Tests the method that compares indexes when testing the
     * networks prediction accuracy.
     */
    @Test
    public void compareIndexesTest() {

        //highest index 0
        float[] arr1 = new float[]{0, 1};
        assertEquals(1, utils.compareIndexes(arr1));

        //highest index 1
        float[] arr2 = new float[]{1, 0};
        assertEquals(0, utils.compareIndexes(arr2));

        //both equal
        float[] arr3 = new float[]{0.5f, 0.5f};
        assertEquals(-1, utils.compareIndexes(arr3));

        //negative value
        float[] arr4 = new float[]{-1, 1};
        assertEquals(1, utils.compareIndexes(arr4));

    }

    /**
     * Tests the sigmoid activation function accuracy.
     */
    @Test
    public void sigmoidTest() {

        //input 0
        float sig = ActivationFunction.sigmoid(0);
        assertEquals(0.5, sig, 0.01);

        //positive
        float sig1 = ActivationFunction.sigmoid(0.22f);
        assertEquals(0.55477923, sig1, 0.01);

        //negative
        float sig2 = ActivationFunction.sigmoid(-1.1f);
        assertEquals(0.24973989, sig2, 0.01);

    }

    /**
     * Tests that the random numbers generated are between
     * 0 and 1 as expected.
     */
    @Test
    public void randomNoGeneratorTest() {

        int min = 0;
        int max = 1;
        float r1 = RandomNumberGenerator.randomise();
        float r2 = RandomNumberGenerator.randomise();

        //check random number generated is between min/max range expected
        assertTrue(min <= r1 && r1 <= max);
        assertTrue(min <= r2 && r2 <= max);

    }

}
