package database.repositories.Jpa.adapters;

import database.entities.Jpa.ClientEnt;
import database.mappers.Jpa.JpaClientMapper;
import database.repositories.Jpa.JpaClientRepository;
import model.Client;
import model.repositories.ClientRepository;

import java.util.ArrayList;
import java.util.List;

public class JpaClientRepositoryAdapter implements ClientRepository {
    private JpaClientRepository repo;

    public JpaClientRepositoryAdapter() {
        this.repo = new JpaClientRepository();
    }

    @Override
    public Client getClient(String id) {
        ClientEnt ent = repo.getClient(id);
        if (ent == null) {
            return null;
        }
        return JpaClientMapper.EntToObj(ent);
    }

    @Override
    public boolean addClient(Client client) {
        ClientEnt ent = JpaClientMapper.ObjToEnt(client);
        boolean result = repo.addClient(ent);
        if (result) {
            client.setId(String.valueOf(ent.getId()));
        }
        return result;
    }

    @Override
    public boolean updateClient(Client client) {
        ClientEnt ent = JpaClientMapper.ObjToEnt(client);
        return repo.updateClient(ent);
    }

    @Override
    public boolean removeClient(String id) {
        return repo.removeClient(id);
    }

    @Override
    public boolean addClients(List<Client> clients) {
        List<ClientEnt> ents = new ArrayList<>();
        for (Client client : clients) {
            ents.add(JpaClientMapper.ObjToEnt(client));
        }
        return repo.addClients(ents);
    }

    @Override
    public List<Client> getClients() {
        List<ClientEnt> list = repo.getClients();
        List<Client> clients = new ArrayList<>();
        for (ClientEnt ent : list) {
            clients.add(JpaClientMapper.EntToObj(ent));
        }
        return clients;
    }

    @Override
    public int sizeClients() {
        return repo.countClients();
    }
}