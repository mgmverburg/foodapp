package nl.pharmit.foodapp;

/**
 * Created by Michiel on 7/22/2016.
 */
public class FoodItem {
    private String foodID;
    private String foodName;


    public FoodItem(String foodID, String foodName) {
        this.foodID = foodID;
        this.foodName = foodName;
    }

    public String getFoodID() {
        return  foodID;
    }

    public String getFoodName() {
        return foodName;
    }


    @Override
    public String toString() {
        return foodName;
    }

    @Override
    public boolean equals(Object otherItem) {
        if (otherItem instanceof FoodItem) {
            if (((FoodItem) otherItem).getFoodID().equals(this.getFoodID())) {
                return true;
            }
        }

        return false;
    }
}
