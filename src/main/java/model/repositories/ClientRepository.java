package model.repositories;

import model.Client;

import java.util.List;

public interface ClientRepository {
    Client getClient(String id);
    boolean addClient(Client client);
    boolean updateClient(Client client);
    boolean removeClient(String id);

    boolean addClients(List<Client> clients);
    List<Client> getClients();
    int sizeClients();
}
