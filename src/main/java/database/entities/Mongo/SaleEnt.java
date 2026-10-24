package database.entities.Mongo;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SaleEnt {
    @BsonId
    private ObjectId id;
    private ObjectId clientId; //tu dajemy id bo client moze np zmienic mail, wiec to powinno sie zmienic wszedzie
    private LocalDateTime timeOfSale;
    private List<PlantEnt> plants = new ArrayList<>(); //tu dajemy cale obiekty bo moga sie zmienic, np cena kiedys byla 100 a teraz jest 200, w sale nadal powinno byc 100

    public SaleEnt() {}

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getClientId() {
        return clientId;
    }

    public void setClientId(ObjectId clientId) {
        this.clientId = clientId;
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
