package database.repositories.adapters;

import database.entities.ClientEnt;
import database.entities.PlantEnt;
import database.entities.SaleEnt;
import database.mappers.JpaClientMapper;
import database.mappers.JpaPlantMapper;
import database.mappers.JpaSaleMapper;
import database.repositories.JpaClientRepository;
import database.repositories.JpaPlantRepository;
import database.repositories.JpaSaleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.RollbackException;
import model.Plant;
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
    public Sale getSale(int id) {
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
    public boolean removeSale(int id) {
        return repo.removeSale(id);
    }

    @Override
    public List<Sale> getSales() {
        List<SaleEnt> list = repo.getSales();
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