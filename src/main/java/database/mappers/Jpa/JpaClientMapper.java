package database.mappers.Jpa;

import database.entities.Jpa.ClientEnt;
import model.Client;

public class JpaClientMapper {
    public static ClientEnt ObjToEnt(Client client) {
        return new ClientEnt(
                client.getId(),
                client.getName(),
                client.getMoneySpent(),
                client.isMember(),
                client.getVersion());
    }

    public static Client EntToObj(ClientEnt clientEnt){
        return new Client(
                clientEnt.getId(),
                clientEnt.getName(),
                clientEnt.getMoneySpent(),
                clientEnt.isMember(),
                clientEnt.getVersion()
        );
    }
}
