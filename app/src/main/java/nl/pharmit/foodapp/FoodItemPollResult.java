package nl.pharmit.foodapp;

/**
 * Created by s157218 on 31-7-2016.
 */
public class FoodItemPollResult extends FoodItem {
    int rank;
    int firstChoice;
    int secondChoice;
    public FoodItemPollResult (String foodID, String foodName, int rank, int firstChoice, int secondChoice) {
            super (foodID, foodName);
            this.rank = rank;
            this.firstChoice = firstChoice;
            this.secondChoice = secondChoice;
    }
}
