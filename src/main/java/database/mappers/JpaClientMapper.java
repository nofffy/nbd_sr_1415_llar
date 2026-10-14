package database.mappers;

import database.entities.ClientEnt;
import model.Client;

public class JpaClientMapper {
    public static ClientEnt ObjToEnt(Client client) {
        ClientEnt clientEnt = new ClientEnt();
        clientEnt.setId(client.getId());
        clientEnt.setName(client.getName());
        clientEnt.setMoneySpent(client.getMoneySpent());
        clientEnt.setMember(client.isMember());
        return clientEnt;
    }

    public static Client EntToObj(ClientEnt clientEnt){
        return new Client(
                clientEnt.getId(),
                clientEnt.getName(),
                clientEnt.getMoneySpent(),
                clientEnt.isMember()
        );
    }
}
