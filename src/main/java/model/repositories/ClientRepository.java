package model.repositories;

import model.Client;

import java.util.List;

public interface ClientRepository {
    Client getClient(int id);
    boolean addClient(Client client);
    boolean updateClient(Client client);
    boolean removeClient(int id);

    boolean saveClients(List<Client> clients);
    List<Client> getClients();
    int sizeClients();
}
