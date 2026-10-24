package database.repositories.Mongo;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.UpdateResult;
import database.entities.Mongo.ClientEnt;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class MongoClientRepository {
    private final MongoCollection<ClientEnt> collection;

    public MongoClientRepository(MongoDatabase database) {
        this.collection = database.getCollection("clients", ClientEnt.class);
    }

    public ClientEnt getClient(ObjectId id) {
        return collection.find(Filters.eq("_id", id)).first();
    }

    public boolean addClient(ClientEnt client) {
        try {
            collection.insertOne(client);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateClient(ClientEnt client) {
        try {
            Bson filter = Filters.eq("_id", client.getId());
            Bson update = Updates.combine(
                    Updates.set("name", client.getName()),
                    Updates.set("moneySpent", client.getMoneySpent()),
                    Updates.set("member", client.isMember()));
            UpdateResult result = collection.updateOne(filter, update);
            return result.getMatchedCount() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateClient(ClientSession session, ClientEnt client) { //to do addSale!!! cala transakcja atomowa
        try {
            Bson filter = Filters.eq("_id", client.getId());
            Bson update = Updates.combine(
                    Updates.set("name", client.getName()),
                    Updates.set("moneySpent", client.getMoneySpent()),
                    Updates.set("member", client.isMember()));
            UpdateResult result = collection.updateOne(session, filter, update);
            return result.getMatchedCount() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean removeClient(ObjectId id) {
        try {
            Bson filter = Filters.eq("_id", id);
            DeleteResult result = collection.deleteOne(filter);
            return result.getDeletedCount() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addClients(List<ClientEnt> clients) {
        try {
            InsertManyResult result = collection.insertMany(clients);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<ClientEnt> getClients() {
        try {
            return collection.find().into(new ArrayList<>());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public int countClients() {
        try {
            return (int) collection.countDocuments();
        } catch (Exception e) {
            return 0;
        }
    }
}
