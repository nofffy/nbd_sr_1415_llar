package database.entities.Mongo;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

@BsonDiscriminator(key = "plantType")
public abstract class PlantEnt {
    @BsonId
    private ObjectId id;
    private String name;
    private String color;
    private double cost;
    private boolean sold;
    private ObjectId sale;

    public PlantEnt() {}

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public ObjectId getSale() {
        return sale;
    }

    public void setSale(ObjectId sale) {
        this.sale = sale;
    }
}
