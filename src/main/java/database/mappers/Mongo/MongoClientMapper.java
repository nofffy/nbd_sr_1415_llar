package database.mappers.Mongo;

import database.entities.Mongo.ClientEnt;
import model.Client;
import org.bson.types.ObjectId;

public class MongoClientMapper {
    public static ClientEnt ObjToEnt(Client client) {
        if (client == null) {
            return null;
        }

        ClientEnt ent = new ClientEnt();
        if (client.getId() != null && !client.getId().isEmpty()) {
            ent.setId(new ObjectId(client.getId()));
        }
        ent.setName(client.getName());
        ent.setMoneySpent(client.getMoneySpent());
        ent.setMember(client.isMember());

        return ent;
    }

    public static Client EntToObj(ClientEnt clientEnt) {
        if (clientEnt == null) {
            return null;
        }

        String id = clientEnt.getId() != null ? clientEnt.getId().toHexString() : null;
        return new Client(
                id,
                clientEnt.getName(),
                clientEnt.getMoneySpent(),
                clientEnt.isMember()
        );
    }
}
