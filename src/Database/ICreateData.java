package Database;

import java.util.ArrayList;

public interface ICreateData {

    public ArrayList<Float> wPercentageFirstServesWon(String tableName);

    public ArrayList<Float> wPercentageSecondServesWon(String tableName);

    public ArrayList<Integer> wServiceBreaksAgainst(String tableName);

    public ArrayList<Float> lPercentageFirstServesWon(String tableName);

    public ArrayList<Float> lPercentageSecondServesWon(String tableName);

    public ArrayList<Integer> lServiceBreaksAgainst(String tableName);

}