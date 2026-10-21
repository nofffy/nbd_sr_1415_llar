package database.repositories.Mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.entities.Mongo.ClientEnt;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class MongoClientRepository {
    private MongoCollection<ClientEnt> collection;

    public MongoClientRepository(MongoDatabase database) {
        MongoDatabase mongoDatabase = database;
        this.collection = database.getCollection("clients", ClientEnt.class);
    }

    public ClientEnt getClient(ObjectId id) {
        return collection.find(eq("_id", id)).first();
    }

    public boolean addClient(ClientEnt client) {
        try {
            collection.insertOne(client);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
