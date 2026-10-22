package database.repositories.Mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.entities.Mongo.PlantEnt;
import database.entities.Mongo.SaleEnt;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class MongoSaleRepository {
    private final MongoCollection<SaleEnt> collection;

    public MongoSaleRepository(MongoDatabase database) {
        MongoDatabase mongoDatabase = database;
        this.collection = database.getCollection("sales", SaleEnt.class);
    }

    public SaleEnt getSale(ObjectId id) { return collection.find(eq("_id", id)).first(); }

    public boolean addSale(SaleEnt sale) {
        //TODO
        return true;
    }


}
