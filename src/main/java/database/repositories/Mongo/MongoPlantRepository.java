package database.repositories.Mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.UpdateResult;
import database.entities.Mongo.CarnivorousEnt;
import database.entities.Mongo.MonsteraEnt;
import database.entities.Mongo.SucculentEnt;
import database.entities.Mongo.PlantEnt;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoPlantRepository {
    private final MongoCollection<PlantEnt> collection;

    public MongoPlantRepository(MongoDatabase database) {
        MongoDatabase mongoDatabase = database;
        this.collection = database.getCollection("plants", PlantEnt.class);
    }

    public PlantEnt getPlant(ObjectId id) { return collection.find(eq("_id", id)).first(); }

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
            Bson filter = eq("_id", id);
            DeleteResult result = collection.deleteOne(filter);
            return result.getDeletedCount() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updatePlant(PlantEnt plant) {
        try {
            Bson filter = Filters.eq("_id", plant.getId());
            List<Bson> updatesList = new ArrayList<>();
            updatesList.add(Updates.set("name", plant.getName()));
            updatesList.add(Updates.set("color", plant.getColor()));
            updatesList.add(Updates.set("cost", plant.getCost()));
            updatesList.add(Updates.set("sold", plant.isSold()));
            updatesList.add(Updates.set("sale", plant.getSale()));
            if (plant instanceof SucculentEnt succulent) {
                updatesList.add(Updates.set("spiky", succulent.isSpiky()));
            }
            else if (plant instanceof CarnivorousEnt carnivorous) {
                updatesList.add(Updates.set("favouriteFood", carnivorous.getFavouriteFood()));
            }
            else if (plant instanceof MonsteraEnt monstera) {
                updatesList.add(Updates.set("leafSize", monstera.getLeafSize()));
            }
            Bson update = Updates.combine(updatesList);
            UpdateResult result = collection.updateOne(filter, update);
            return result.getMatchedCount() > 0;
        } catch (Exception e) {
            return false;
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
        Bson query;
        if(choice) {
            query = eq("sold", true);
        } else {
            query = eq("sold", false);
        }

        try {
            return collection.find().into(new ArrayList<>());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public int countSoldPlantsByType() {
        //TODO
        return 0;
    }
}
