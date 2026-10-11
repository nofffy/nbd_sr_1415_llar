package database.adapter;

import database.JpaMapper;
import database.entities.SaleEnt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import model.Sale;
import model.repositories.SaleRepository;

import java.util.ArrayList;
import java.util.List;

public class JpaSaleRepositoryAdapter implements SaleRepository {
    private EntityManagerFactory emf;

    public JpaSaleRepositoryAdapter() {
        this.emf = Persistence.createEntityManagerFactory("default");
    }

    @Override
    public Sale getSale(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            SaleEnt ent = em.find(SaleEnt.class, id);
            return JpaMapper.saleEntToObj(ent);
        }
    }

    @Override
    public boolean addSale(Sale sale) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction txn = em.getTransaction();
            try {
                txn.begin();
                SaleEnt ent = JpaMapper.saleObjToEnt(sale);
                em.merge(ent);
                txn.commit();
                return true;
            } catch (Exception e) {
                if (txn.isActive()) {
                    txn.rollback();
                }
                return false;
            }
        }
    }

    public void addSale(Sale sale, EntityManager em) {
        SaleEnt ent = JpaMapper.saleObjToEnt(sale);
        em.merge(ent);
    }

    @Override
    public boolean removeSale(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction txn = em.getTransaction();
            try {
                txn.begin();
                SaleEnt ent = em.find(SaleEnt.class, id);
                if(ent!=null){
                    em.remove(ent);
                    txn.commit();
                    return true;
                }
                else{
                    txn.rollback();
                    return false;
                }
            } catch (Exception e) {
                if (txn.isActive()) {
                    txn.rollback();
                }
                return false;
            }
        }
    }

    @Override
    public List<Sale> getSales() {
        String query = "SELECT p FROM SaleEnt p";
        List<SaleEnt> list;
        try (EntityManager em = emf.createEntityManager()) {
            list = em.createQuery(query, SaleEnt.class).getResultList();
            List<Sale> sales = new ArrayList<>();
            for(SaleEnt ent:list) {
                sales.add(JpaMapper.saleEntToObj(ent));
            }
            return sales;
        }
    }

    @Override
    public int getSalesCount() {
        return getSales().size();
    }
}
