package database.entities.Mongo;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SaleEnt {
    @BsonId
    private int id;
    private ObjectId client;
    private LocalDateTime timeOfSale;
    private List<PlantEnt> plants = new ArrayList<>();

    public SaleEnt() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ObjectId getClient() {
        return client;
    }

    public void setClient(ObjectId client) {
        this.client = client;
    }

    public LocalDateTime getTimeOfSale() {
        return timeOfSale;
    }

    public void setTimeOfSale(LocalDateTime timeOfSale) {
        this.timeOfSale = timeOfSale;
    }

    public List<PlantEnt> getPlants() {
        return plants;
    }

    public void setPlants(List<PlantEnt> plants) {
        this.plants = plants;
    }
}
