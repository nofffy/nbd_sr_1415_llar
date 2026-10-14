package database.repositories;

import database.entities.PlantEnt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class JpaPlantRepository {
    private EntityManagerFactory emf;

    public JpaPlantRepository() {
        this.emf = database.DatabaseManager.getEmf();
    }

    public PlantEnt getPlant(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(PlantEnt.class, id);
        }
    }

    public boolean addPlant(PlantEnt ent) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction txn = em.getTransaction();
            try {
                txn.begin();
                em.persist(ent);
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

    public boolean removePlant(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction txn = em.getTransaction();
            try {
                txn.begin();
                PlantEnt ent = em.find(PlantEnt.class, id);
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

    public boolean updatePlant(PlantEnt ent) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction txn = em.getTransaction();
            try {
                txn.begin();
                PlantEnt dbPlant = em.find(PlantEnt.class, ent.getId());
                if (dbPlant == null) {
                    txn.rollback();
                    return false;
                }
                dbPlant.setName(ent.getName());
                dbPlant.setColor(ent.getColor());
                dbPlant.setCost(ent.getCost());
                dbPlant.setSold(ent.isSold());
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

    public void updatePlant(PlantEnt ent, EntityManager em) {
        PlantEnt Pent = em.find(PlantEnt.class, ent.getId());
        Pent.setSold(ent.isSold());
    }

    public boolean addPlants(List<PlantEnt> ents) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction txn = em.getTransaction();
            try {
                txn.begin();
                for (PlantEnt ent : ents) {
                    em.persist(ent);
                }
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

    public List<PlantEnt> getPlants() {
        String query = "SELECT p FROM PlantEnt p";
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(query, PlantEnt.class).getResultList();
        }
    }

    public List<PlantEnt> findAllSoldOrNot(boolean choice) {
        String query = "SELECT p FROM PlantEnt p WHERE p.sold=:choice";
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(query, PlantEnt.class)
                    .setParameter("choice", choice)
                    .getResultList();
        }
    }
}