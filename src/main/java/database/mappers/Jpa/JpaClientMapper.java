package database.mappers.Jpa;

import database.entities.Jpa.ClientEnt;
import model.Client;

public class JpaClientMapper {
    public static ClientEnt ObjToEnt(Client client) {
        int id = 0;
        if (client.getId() != null && !client.getId().isEmpty()) {
            id = Integer.parseInt(client.getId());
        }
        return new ClientEnt(
                id,
                client.getName(),
                client.getMoneySpent(),
                client.isMember(),
                client.getVersion());
    }

    public static Client EntToObj(ClientEnt clientEnt) {
        String idString = String.valueOf(clientEnt.getId());
        return new Client(
                idString,
                clientEnt.getName(),
                clientEnt.getMoneySpent(),
                clientEnt.isMember(),
                clientEnt.getVersion()
        );
    }
}
