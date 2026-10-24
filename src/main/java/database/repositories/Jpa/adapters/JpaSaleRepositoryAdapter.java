package database.repositories.Jpa.adapters;

import database.entities.Jpa.SaleEnt;
import database.mappers.Jpa.JpaSaleMapper;
import database.repositories.Jpa.JpaSaleRepository;
import model.Sale;
import model.repositories.SaleRepository;

import java.util.ArrayList;
import java.util.List;

public class JpaSaleRepositoryAdapter implements SaleRepository {
    private final JpaSaleRepository repo;

    public JpaSaleRepositoryAdapter() {
        this.repo = new JpaSaleRepository();
    }

    @Override
    public Sale getSale(String id) {
        SaleEnt ent = repo.getSale(id);
        if (ent == null) {
            return null;
        }
        return JpaSaleMapper.EntToObj(ent);
    }

    @Override
    public boolean addSale(Sale sale) {
        SaleEnt saleEnt = JpaSaleMapper.ObjToEnt(sale);
        return repo.addSale(saleEnt);
    }

    @Override
    public boolean removeSale(String id) {
        return repo.removeSale(id);
    }

    @Override
    public List<Sale> getClientSales(String clientId) {
        List<SaleEnt> list = repo.getClientSales(clientId);
        List<Sale> sales = new ArrayList<>();
        for (SaleEnt ent : list) {
            sales.add(JpaSaleMapper.EntToObj(ent));
        }
        return sales;
    }

    @Override
    public int sizeSales() {
        return repo.countSales();
    }
}