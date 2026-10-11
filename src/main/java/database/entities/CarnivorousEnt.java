package database.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Carnivorous")
public class CarnivorousEnt extends PlantEnt {
    private String favouriteFood;

    public CarnivorousEnt() {}
    public String getFavouriteFood() {
        return favouriteFood;
    }
    public void setFavouriteFood(String favouriteFood) {
        this.favouriteFood = favouriteFood;
    }
}
