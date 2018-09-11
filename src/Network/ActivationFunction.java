package Network;

/**
 * Class contains the following algorithm implementations:
 *
 * -> Sigmoid activation function (network transfer function)
 * -> Derivative of the Sigmoid function
 *
 * @author Alex Kelly
 */
public class ActivationFunction {

    /**
     * Method for calculating the sigmoid function of an input value
     *
     * @param x the input value
     * @return the sigmoid value of the input value (as a float)
     */
    public static float sigmoid(float x) {
         double d = 1d / (1 + Math.exp(-x));
         return (float) d;
    }

    /**
     * Method for calculating the derivative of the sigmoid function
     *
     * @param x the input value
     * @return the derivative sigmoid function of the input value (as a float)
     */
    public static float derivativeSigmoid(float x) {
        return x * (1 - x);
    }
}
