package services;

import database.adapter.JpaClientRepositoryAdapter;
import database.adapter.JpaPlantRepositoryAdapter;
import database.adapter.JpaSaleRepositoryAdapter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import model.Client;
import model.Plant;
import model.Sale;

import java.util.List;

public class ShopService {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
    private JpaClientRepositoryAdapter clientRepo;
    private JpaPlantRepositoryAdapter plantRepo;
    private JpaSaleRepositoryAdapter saleRepo;

    public ShopService() {
        this.clientRepo = new JpaClientRepositoryAdapter();
        this.plantRepo = new JpaPlantRepositoryAdapter();
        this.saleRepo = new JpaSaleRepositoryAdapter();
    }

    public boolean newSale(Client client, List<Plant> plants) {
        if (client == null || plants == null || plants.isEmpty()) {
            return false;
        }

        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction txn = em.getTransaction();
            try
            {
                txn.begin();
                double cost = 0.0;
                for (Plant plant: plants) {
                    plant.setSold(true);
                    plantRepo.updatePlant(plant, em);
                    cost += plant.getCost();
                }
                client.setMoneySpent(client.getMoneySpent()+cost);
                clientRepo.updateClient(client, em);
                Sale sale = new Sale(client, plants);
                saleRepo.addSale(sale, em);
                txn.commit();
                return true;
            } catch (Exception e) {
                if (txn.isActive()) {
                    txn.rollback();
                }
                e.printStackTrace();
                return false;
            }
        }
    }

}
