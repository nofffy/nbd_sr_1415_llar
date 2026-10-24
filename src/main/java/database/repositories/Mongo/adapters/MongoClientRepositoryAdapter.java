package database.repositories.Mongo.adapters;

import database.entities.Mongo.ClientEnt;
import database.mappers.Mongo.MongoClientMapper;
import database.repositories.Mongo.MongoClientRepository;
import model.Client;
import model.repositories.ClientRepository;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class MongoClientRepositoryAdapter implements ClientRepository {
    private final MongoClientRepository repo;

    public MongoClientRepositoryAdapter(MongoClientRepository repo) {
        this.repo = repo;
    }

    @Override
    public Client getClient(String id) {
        if (!ObjectId.isValid(id)) {
            return null;
        }
        ClientEnt clientEnt = repo.getClient((new ObjectId(id)));
        if (clientEnt == null) {
            return null;
        }
        return MongoClientMapper.EntToObj(clientEnt);
    }

    @Override
    public boolean addClient(Client client) {
        ClientEnt clientEnt = MongoClientMapper.ObjToEnt(client);
        boolean result = repo.addClient(clientEnt);
        if (result) {
            client.setId(clientEnt.getId().toHexString());
        }
        return result;
    }

    @Override
    public boolean updateClient(Client client) {
        ClientEnt clientEnt = MongoClientMapper.ObjToEnt(client);
        return repo.updateClient(clientEnt);
    }

    @Override
    public boolean removeClient(String id) { return repo.removeClient(new ObjectId(id)); }

    @Override
    public boolean addClients(List<Client> clients) {
        List<ClientEnt> clientEnts = new ArrayList<>();
        for (Client client : clients) {
            clientEnts.add(MongoClientMapper.ObjToEnt(client));
        }
        return repo.addClients(clientEnts);
    }

    @Override
    public List<Client> getClients() {
        List<ClientEnt> clientEnts = repo.getClients();
        List<Client> clients = new ArrayList<>();
        for (ClientEnt clientEnt : clientEnts) {
            clients.add(MongoClientMapper.EntToObj(clientEnt));
        }
        return clients;
    }

    @Override
    public int sizeClients() { return repo.countClients(); }
}
