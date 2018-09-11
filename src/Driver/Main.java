package Driver;

import Database.*;
import Network.*;
import Utils.DBUtils;
import Utils.NeuralNetworkUtils;

import java.util.Arrays;

/**
 * Contains main method which drives program.
 *
 * @author Alex Kelly
 */
public class Main {

    public static void main(String[] args) {

        //create database tables:
        CreateTables tableCreator = new CreateTables(); //create object
        tableCreator.createTrainingData();

        //read in the CSV files:
        ReadFiles fileReader = new ReadFiles(); //create object
        fileReader.readCSVTraining(); //read in training data

///////////////////////////////////////////////////////////////////////

        //clean the database:
        CleanDatabase DBCleaner = new CleanDatabase(); //create object

        DBCleaner.deleteUnneededColumns();

        DBCleaner.removeLowerRankedPlayers(); //removes players below rank 50

        DBCleaner.convertBlankToNull("winner_seed"); //winner seed blanks to null
        DBCleaner.convertBlankToNull("loser_seed"); //loser seed blanks to null

        DBCleaner.removeBlankValues(); //removes blank vals from all but w/l_seed

        DBCleaner.addUniqueMatchID(); //add pkey

        DBCleaner.createOutputColumns(); //creates two output columns

/////////////////////////////////////////////////////////////////////////////

        //standardise non-numeric data:
        StandardiseNonNumeric nonNumeric = new StandardiseNonNumeric(); //create object

        nonNumeric.standardiseIndividualCol("tourney_level");
        nonNumeric.standardiseIndividualCol("surface");
        nonNumeric.standardiseIndividualCol("tourney_name");
        nonNumeric.standardiseIndividualCol("round");
        //winner and loser updated
        nonNumeric.standardiseWinnerLoserCol("hand");
        nonNumeric.standardiseWinnerLoserCol("ioc");

///////////////////////////////////////////////////////////////////////////
        //normalise numeric data
        Normalise numeric = new Normalise(); //create object

        numeric.unseeded(); //update unseeded players to seed 40

        numeric.updateColType("loser_ht");
        numeric.updateColType("winner_ht");
        numeric.updateColType("draw_size");
        numeric.updateColType("winner_rank");
        numeric.updateColType("loser_rank");
        numeric.updateColType("winner_seed");
        numeric.updateColType("loser_seed");
        numeric.updateColType("best_of");

        numeric.normaliseValues("draw_size");
        numeric.normaliseValues("best_of");
        //winner and loser updated
        numeric.normaliseWinnerLoser("rank");
        numeric.normaliseWinnerLoser("seed");
        numeric.normaliseWinnerLoser( "ht");

/////////////////////////////////////////////////////////////////////////////

        //finalise:
        FinaliseDatabase finaliser = new FinaliseDatabase(); //create object

        finaliser.addTestData();

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

        //NEURAL NETWORK:
        /*
         * number of layers can be specified within the NetworkConstants class
         * by modifying the variable NEURONS_PER_LAYER
         */
        NeuralNetwork network = new NeuralNetwork(NetworkConstants.NEURONS_PER_LAYER);

        /* ************************************** TRAINING *************************************** */
        CreateNetworkData networkDataCreator = new CreateNetworkData(); //initialise create network data

        NetworkData trainingSet = new NetworkData(); //initialise training data
        networkDataCreator.createNetworkData(trainingSet, "training_data"); //create data to train network on

        NetworkData testSet = new NetworkData(); //initialise test data
        networkDataCreator.createNetworkData(testSet, "test_data"); //create data to test network on

        //print the set all together
//        NeuralNetworkUtils.print2DArray(trainingSet.getInputArray(), "training input", trainingSet.getOutputArray(), "training output");
//        NeuralNetworkUtils.print2DArray(testSet.getInputArray(), "test input", testSet.getOutputArray(), "test output");

        //TRAIN NETWORK...
        int epochs = 100; //set training epochs here

        TrainNetwork.train(trainingSet, epochs, network);

        //needed to print expected and actual output for each row
        int trainingRows = DBUtils.countRows("training_data");
        //post training: iterate through actual outputs and print
        for (int i = 0; i < trainingRows; i++) {
            if (i % 100 == 0) {
                System.out.print("row: " + (i + 1) + " expected output -> " + Arrays.toString(trainingSet.getOutput(i)) + " ----- ");
                System.out.println(" actual output -> " + Arrays.toString(network.calculateOutput(trainingSet.getInput(i))));
            }
        }
        System.out.println("******************************************************************************");
        System.out.println("                           " + epochs + " training epochs");
        System.out.println("******************************************************************************");
        System.out.println("                        | Test Set Predictions |");
        System.out.println("                        V  ------------------- V");
        System.out.println("******************************************************************************");
        /* *********************************** END OF TRAINING ************************************ */

        /* *************************************** TESTING **************************************** */

        //TEST NETWORK...

        TestNetwork.testAccuracy(network, testSet);
//
//        //test due to surprisingly accurate results:
//        float[][] input = trainingSet.getInputArray();
//        float[][] output = trainingSet.getOutputArray();
//        System.out.println("are input and output the same? " + Arrays.deepEquals(input, output));
//
//        /* ************************************ END OF TESTING ************************************* */
//
//        /* *************************************** SAVE/LOAD **************************************** */
//
        //named after the amount of training epochs
        network.saveANN("Files/ANN" + epochs + ".txt", network); //save network
//
/////////////////////////////////////////////////////////////////////////////
        NeuralNetwork loaded = NeuralNetwork.loadANN("Files/ANN" + epochs + ".txt"); //load trained network
//
//        /* ************************************ TESTING LOADED ************************************* */
//
//        //TEST LOADED NETWORK...
        TestNetwork.testAccuracy(loaded, testSet); //demonstrate exactly the same accuracy
//
/////////////////////////////////////////////////////////////////////////////






//          //(leave commented out unless running logical operator code)
//
//        /* ************************************ LOGICAL OPERATORS ************************************* */
//
//        //The following is to test the network on Logical operators:
//        //must modify NetworkConstants.NEURONS_PER_LAYER to the param below
//        NeuralNetwork network = new NeuralNetwork(new int[]{2, 2, 1});
//
//        float[][] input = new float[][]{
//                {1f,1f},
//                {1f,0f},
//                {0f,1f},
//                {0f,0f}
//        };
//        /*
//         * modify these depending on which logical operation being learned:
//         *
//         * LINEARLY SEPARABLE:
//         * 0,0,0,1 => AND
//         * 1,1,1,0 => OR
//         *
//         * NON-LINEARLY SEPARABLE:
//         * 0,1,1,0 => XOR (should require one hidden layer)
//         */
//        float[][] output = new float[][]{
//                {0f},
//                {1f},
//                {1f},
//                {0f}
//        };
//
//        NetworkData ts = new NetworkData();
//        for (int i = 0; i < input.length; i ++) {
//            ts.addNetworkData(input[i], output[i]);
//        }
//
//        TrainNetwork.train(ts, 100000, network);
//
//        TestNetwork.testLogical(network, ts, 0.01);
//
//        //remove "else" part from print2DArray method on line 55 of NeuralNetworkUtils
//        //copy this in instead of line 53: System.out.print("--- " + arrayName2 + " -> " + arr2[i][j] + "\n");
//        NeuralNetworkUtils.print2DArray(input, "input", output, "target output");

    }

}
