package Network;

/**
 * Class which contains the constant values for the network.
 * The size of the network and number of neurons in each
 * layer is set here.
 *
 * @author Alex Kelly
 */
public class NetworkConstants {

    //learning rate used to update weights
    public static final float LEARNING_RATE = 0.3f;

    /**
     * Set the size of the network here:
     * this will influence the remaining variables.
     */
    public static final int[] NEURONS_PER_LAYER = new int[]{16, 10, 7, 2};
    //number of layers in the network
    public static final int NUM_LAYERS = NEURONS_PER_LAYER.length;

    public static final int INPUT_LAYER_SIZE = NEURONS_PER_LAYER[0];
    public static final int OUTPUT_LAYER_SIZE = NEURONS_PER_LAYER[NUM_LAYERS -1];

    /**
     * Private constructor so cannot be initialised
     */
    private NetworkConstants() {
    }

}
