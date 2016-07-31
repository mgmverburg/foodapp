package nl.pharmit.foodapp;

/**
 * Created by s157218 on 31-7-2016.
 */
public class FoodItemPollResult extends FoodItem implements Comparable<FoodItemPollResult> {
//    public int rank;
    private int firstChoiceAmount, secondChoiceAmount , weightedRating;
    private boolean firstChoiceSet, secondChoiceSet;

    public FoodItemPollResult (String foodID, String foodName) {
        super (foodID, foodName);
    }

    public FoodItemPollResult (String foodID, String foodName, int firstChoiceAmount, int secondChoiceAmount) {
            super (foodID, foodName);
//            this.rank = rank;
            this.firstChoiceSet = true;
            this.secondChoiceSet = true;
            this.firstChoiceAmount = firstChoiceAmount;
            this.secondChoiceAmount = secondChoiceAmount;
            this.weightedRating = 2*firstChoiceAmount + secondChoiceAmount;
    }

//    public void setWeightedRating(int weightedRating) {
//        this.weightedRating = weightedRating;
//    }

    public boolean getChoicesSet() {
        return (firstChoiceSet && secondChoiceSet);
    }

    public int getFirstChoiceAmount() {
        return firstChoiceAmount;
    }

    public int getSecondChoiceAmount() {
        return secondChoiceAmount;
    }

    public void setChoiceAmount(int choiceAmount, boolean firstChoice) {
        if (firstChoice) {
            this.firstChoiceAmount = choiceAmount;
            this.firstChoiceSet = true;
        } else {
            this.secondChoiceAmount = choiceAmount;
            this.secondChoiceSet = true;
        }
    }

    public void setSecondChoiceAmount(int secondChoiceAmount) {
        this.secondChoiceAmount = secondChoiceAmount;
    }

    public int getWeightedRating() {
        this.weightedRating = 2*firstChoiceAmount + secondChoiceAmount;
        return weightedRating;
    }

    @Override
    public int compareTo(FoodItemPollResult foodItem) {
        int result = ((Integer) (foodItem.getWeightedRating())).compareTo(this.getWeightedRating());
        if (result == 0) {
            result = this.getFoodName().compareTo(foodItem.getFoodName());
        }
        return result;
    }



}
