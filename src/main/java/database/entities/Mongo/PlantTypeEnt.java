package database.entities.Mongo;

public class PlantTypeEnt {
    private String type;

    private Boolean spiky;
    private String favouriteFood;
    private Integer leafSize;

    public PlantTypeEnt() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getSpiky() {
        return spiky;
    }

    public void setSpiky(Boolean spiky) {
        this.spiky = spiky;
    }

    public String getFavouriteFood() {
        return favouriteFood;
    }

    public void setFavouriteFood(String favouriteFood) {
        this.favouriteFood = favouriteFood;
    }

    public Integer getLeafSize() {
        return leafSize;
    }

    public void setLeafSize(Integer leafSize) {
        this.leafSize = leafSize;
    }
}
