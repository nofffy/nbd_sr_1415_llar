package database.repositories;

import database.entities.PlantEnt;
import database.entities.SaleEnt;
import jakarta.persistence.*;

import java.util.List;

public class JpaSaleRepository {
    private EntityManagerFactory emf;
    private final JpaClientRepository clientRepo;
    private final JpaPlantRepository plantRepo;

    public JpaSaleRepository() {
        this.emf = database.DatabaseManager.getEmf();
        this.clientRepo = new JpaClientRepository();
        this.plantRepo = new JpaPlantRepository();
    }

    public SaleEnt getSale(int id) {
        String query = "SELECT p FROM SaleEnt p JOIN FETCH p.plants WHERE p.id = :id";
        try (EntityManager em = emf.createEntityManager()) {
            try {
                return em.createQuery(query, SaleEnt.class).setParameter("id", id).getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        }
    }

    public boolean addSale(SaleEnt saleEnt) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction txn = em.getTransaction();
            try {
                txn.begin();
                clientRepo.updateClient(saleEnt.getClient(), em);
                for (PlantEnt plantEnt : saleEnt.getPlants()) {
                    plantRepo.updatePlant(plantEnt, em);
                }
                em.merge(saleEnt);
                txn.commit();
                return true;
            } catch (OptimisticLockException | RollbackException e) {
                if (txn.isActive()) {
                    txn.rollback();
                }
                System.out.println("Błąd transakcji: " + e.getMessage());
                return false;
            } catch (Exception e) {
                if (txn.isActive()) {
                    txn.rollback();
                }
                return false;
            }
        }
    }

    public boolean removeSale(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction txn = em.getTransaction();
            try {
                txn.begin();
                SaleEnt ent = em.find(SaleEnt.class, id);
                if (ent != null) {
                    em.remove(ent);
                    txn.commit();
                    return true;
                } else {
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

    public List<SaleEnt> getClientSales(int clientId) {
        String query = "SELECT DISTINCT s FROM SaleEnt s JOIN FETCH s.plants WHERE s.client.id = :clientId";
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(query, SaleEnt.class)
                    .setParameter("clientId", clientId)
                    .getResultList();
        }
    }

    public int countSales() {
        String query = "SELECT COUNT(p) FROM SaleEnt p";
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(query, Long.class).getSingleResult().intValue();
        }
    }
}