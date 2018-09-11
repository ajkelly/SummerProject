package Utils;

import java.util.Arrays;

/**
 * Class which contains the utility methods to help
 * with the Neural Network component.
 *
 * @author Alex Kelly
 */
public class NeuralNetworkUtils {

    /**
     * Method for printing a 2D array of the input or output
     * line by line (usually printed side by side which is
     * not useful with large arrays).
     *
     * @param arr 2D array to be printed
     * @param arrayName the heading of the data being printed
     */
    public static void print2DArray(float arr[][], String arrayName) {
        // Loop through all rows
        int count = 1;
        for (float[] row : arr) {
            System.out.println("row: " + count + " ---- " + arrayName + " -> " +  Arrays.toString(row));
            count++;
        }
    }

    /**
     * Method for printing out 2, 2D arrays next to each other.
     * name of arr1, followed by its contents, followed by name
     * of arr2, followed by its contents.
     *
     * @param arr1 first 2D array
     * @param arrayName1 title to display next to the first 2D array
     * @param arr2 second 2D array
     * @param arrayName2 title to display next to the second 2D array
     */
    public static void print2DArray(float arr1[][], String arrayName1, float[][] arr2, String arrayName2) {

        for (int i = 0; i < arr1.length; i++) {

//            if (i % 10 == 0) {
                for (int j = 0; j < arr1[i].length; j++) {
                    if (j == 0) {
                        System.out.print("row " + i + ": " + arrayName1 + " -> " + arr1[i][j] + ", ");
                    } else System.out.print(arr1[i][j] + ", ");
                }

                for (int j = 0; j < arr2[i].length; j++) {
                    if (j == 0) {
                        System.out.print("--- " + arrayName2 + " -> " + arr2[i][j] + ", ");
                    }
                    else System.out.print(arr2[i][j] + "\n");
                }
//            }
        }
    }

    /**
     * Method for squaring a given float value.
     *
     * @param a float to be squared
     * @return a * a
     */
    public static float square(float a) {
        return a * a;
    }

    /**
     * Method for finding the index of the highest value in an
     * array of float values. To be used to check the accuracy
     * of the neural network on the test data set.
     *
     * @param array the input array
     * @return the index of the highest value
     */
    public static int compareIndexes(float[] array) {
        //empty or single element array
        if (array.length != 2) {
            throw new IllegalArgumentException("input array must contain 2 elements");
        }

        int index = 0;

        for (int i = 1; i < array.length; i++) {

            if (array[i] > array[index]) {
                index = i;
            }
            //if values are the same network has failed to predict
            else if (array[i] == array[index]) {
                return -1;
            }
        }
        return index;
    }

}
