package database.repositories.Mongo;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import database.MongoDBManager;
import database.entities.Mongo.ClientEnt;
import database.entities.Mongo.PlantEnt;
import database.entities.Mongo.SaleEnt;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoSaleRepository {
    private final MongoCollection<SaleEnt> collection;
    private final MongoClientRepository clientRepo;
    private final MongoPlantRepository plantRepo;

    public MongoSaleRepository(MongoDatabase database) {
        this.collection = database.getCollection("sales", SaleEnt.class);
        this.clientRepo = new MongoClientRepository(database);
        this.plantRepo = new MongoPlantRepository(database);
    }

    public SaleEnt getSale(ObjectId id) {
        return collection.find(eq("_id", id)).first();
    }

    public boolean addSale(SaleEnt sale, ClientEnt client) {
        try (ClientSession session = MongoDBManager.getClient().startSession()) {
            session.startTransaction();
            try {
                for (PlantEnt plant : sale.getPlants()) {
                    plantRepo.sellPlant(session, plant);
                }
                collection.insertOne(session, sale);
                clientRepo.updateClient(session, client);
                session.commitTransaction();
                return true;
            } catch (Exception e) {
                session.abortTransaction();
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean removeSale(ObjectId id) {
        try {
            Bson filter = Filters.eq("_id", id);
            DeleteResult result = collection.deleteOne(filter);
            return result.getDeletedCount() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public List<SaleEnt> getClientSales(ObjectId clientId) {
        Bson filter = eq("clientId", clientId);
        try {
            return collection.find(filter).into(new ArrayList<SaleEnt>());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public int countSales() {
        try {
            return (int) collection.countDocuments();
        } catch (Exception e) {
            return 0;
        }
    }
}
