package database.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Carnivorous")
public class CarnivorousEnt extends PlantEnt {
    private String favouriteFood;

    public CarnivorousEnt() {}
    public CarnivorousEnt(int id, String name, String color, double cost, boolean sold, SaleEnt sale, String favouriteFood, int version) {
        super(id, name, color, cost, sold, sale, version);
        this.favouriteFood = favouriteFood;
    }
    public String getFavouriteFood() {
        return favouriteFood;
    }
    public void setFavouriteFood(String favouriteFood) {
        this.favouriteFood = favouriteFood;
    }
}
