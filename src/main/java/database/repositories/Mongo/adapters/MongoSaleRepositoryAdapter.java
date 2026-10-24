package database.repositories.Mongo.adapters;

import database.entities.Mongo.ClientEnt;
import database.entities.Mongo.SaleEnt;
import database.mappers.Mongo.MongoClientMapper;
import database.mappers.Mongo.MongoSaleMapper;
import database.repositories.Mongo.MongoClientRepository;
import database.repositories.Mongo.MongoSaleRepository;
import model.Client;
import model.Sale;
import model.repositories.SaleRepository;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MongoSaleRepositoryAdapter implements SaleRepository {
    private final MongoSaleRepository repo;
    private final MongoClientRepository clientRepo;

    public MongoSaleRepositoryAdapter(MongoSaleRepository repo, MongoClientRepository clientRepo) {
        this.repo = repo;
        this.clientRepo = clientRepo;
    }

    @Override
    public Sale getSale(String id) {
        SaleEnt saleEnt = repo.getSale(new ObjectId(id));
        if (saleEnt == null) {
            return null;
        }

        ClientEnt clientEnt = clientRepo.getClient(saleEnt.getClientId());
        Client client = MongoClientMapper.EntToObj(clientEnt);
        return MongoSaleMapper.EntToObj(saleEnt, client);
    }

    @Override
    public boolean addSale(Sale sale) {
        SaleEnt saleEnt = MongoSaleMapper.ObjToEnt(sale);
        ClientEnt clientEnt = MongoClientMapper.ObjToEnt(sale.getClient());
        return repo.addSale(saleEnt, clientEnt);
    }

    @Override
    public boolean removeSale(String id) {
        return repo.removeSale(new ObjectId(id));
    }

    @Override
    public List<Sale> getClientSales(String clientId) {
        ObjectId clientObjectId = new ObjectId(clientId);
        List<SaleEnt> saleEnts = repo.getClientSales(clientObjectId);

        if (saleEnts == null || saleEnts.isEmpty()) {
            return new ArrayList<>();
        }
        ClientEnt clientEnt = clientRepo.getClient(clientObjectId);
        Client client = MongoClientMapper.EntToObj(clientEnt);

        return saleEnts.stream()
                .map(ent -> MongoSaleMapper.EntToObj(ent, client))
                .collect(Collectors.toList());
    }

    @Override
    public int sizeSales() {
        return repo.countSales();
    }
}
