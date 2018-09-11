package Utils;

import java.util.Random;

/**
 * Class for generating random float numbers either as
 * an array, 2D array or just single number.
 *
 * @author Alex Kelly
 */
public class RandomNumberGenerator {

    /**
     * Method for generating a random number between 0 and 1.
     *
     * @return a random float between 0 an 1
     */
    public static float randomise() {

        Random random = new Random();
//        System.out.println(random.nextFloat()); //test
        return random.nextFloat();
    }

    /**
     * Method for generating a random array of floats.
     *
     * @param size the getSize of the array to be returned
     * @return an array of random floats between 0 an 1
     */
    public static float[] randomArray(int size) {

        Random random = new Random();
        float[] x = new float[size];

        for (int i = 0; i < size; i++) {
            x[i] = random.nextFloat();
        }
//        System.out.println(Arrays.toString(x)); //test
        return x;
    }

    /**
     * Method for generating a random 2D array of floats.
     *
     * @param rowSize amount of rows
     * @param colSize amount of columns
     * @return a 2D array of random floats between 0 an 1
     */
    public static float[][] randomArray(int rowSize, int colSize) {

        float[][] x = new float[rowSize][colSize];

        for (int i = 0; i < rowSize; i++) {
            x[i] = randomArray(colSize);
        }
//        System.out.println(Arrays.deepToString(x)); //test
        return x;
    }

}
