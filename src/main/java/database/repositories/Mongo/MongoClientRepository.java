package database.repositories.Mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import database.entities.Mongo.ClientEnt;
import org.bson.conversions.Bson;
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

    public boolean updateClient(ClientEnt ent) {
        try {
            Bson filter = Filters.eq("_id", ent.getId());
            Bson update = Updates.combine(
                    Updates.set("name", ent.getName()),
                    Updates.set("moneySpent", ent.getMoneySpent()),
                    Updates.set("member", ent.isMember()));
            UpdateResult result = collection.updateOne(filter, update);
            return result.getMatchedCount() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean removeClient(ObjectId id) {
        try {
            Bson filter = eq("_id", id);
            DeleteResult result = collection.deleteOne(filter);
            return result.getDeletedCount() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
