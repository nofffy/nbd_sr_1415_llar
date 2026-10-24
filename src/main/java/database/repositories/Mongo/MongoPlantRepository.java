package database.repositories.Mongo;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.UpdateResult;
import database.entities.Mongo.PlantEnt;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class MongoPlantRepository {
    private final MongoCollection<PlantEnt> collection;

    public MongoPlantRepository(MongoDatabase database) {
        this.collection = database.getCollection("plants", PlantEnt.class);
    }

    public PlantEnt getPlant(ObjectId id) { return collection.find(Filters.eq("_id", id)).first(); }

    public boolean addPlant(PlantEnt plant) {
        try {
            collection.insertOne(plant);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean removePlant(ObjectId id) {
        try {
            Bson filter = Filters.eq("_id", id);
            DeleteResult result = collection.deleteOne(filter);
            return result.getDeletedCount() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updatePlant(PlantEnt plant) {
        try {
            Bson filter = Filters.eq("_id", plant.getId());
            Bson update = Updates.combine(
                    Updates.set("name", plant.getName()),
                    Updates.set("color", plant.getColor()),
                    Updates.set("cost", plant.getCost()),
                    Updates.set("sold", plant.isSold()),
                    Updates.set("sale", plant.getSale()),
                    Updates.set("plantType", plant.getType())
            );
            UpdateResult result = collection.updateOne(filter, update);
            return result.getMatchedCount() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void sellPlant(ClientSession session, PlantEnt ent) {
        Bson filter = Filters.and(
                Filters.eq("_id", ent.getId()),
                Filters.eq("sold", false)
        );

        Bson update = Updates.set("sold", true);
        UpdateResult result = collection.updateOne(session, filter, update);

        if (result.getMatchedCount() == 0) {
            throw new RuntimeException("Roslina jest juz sprzedana");
        }
    }

    public boolean addPlants(List<PlantEnt> plants) {
        try {
            InsertManyResult result = collection.insertMany(plants);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<PlantEnt> getPlants() {
        try {
            return collection.find().into(new ArrayList<>());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<PlantEnt> findAllSoldOrNot(boolean choice) {
        Bson filter = Filters.eq("sold", choice);

        try {
            return collection.find(filter).into(new ArrayList<>());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public int countSoldPlantsByType(String type) {
        Bson filter = Filters.and(
                Filters.eq("plantType.type", type),
                Filters.eq("sold", true)
        );
        try {
            return (int) collection.countDocuments(filter);
        } catch (Exception e) {
            return 0;
        }
    }
}
