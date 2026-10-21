package database.entities.Mongo;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator("CARNIVOROUS")
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
