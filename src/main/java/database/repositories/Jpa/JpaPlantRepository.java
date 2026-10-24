package database.repositories.Jpa;

import database.JpaDBManager;
import database.entities.Jpa.CarnivorousEnt;
import database.entities.Jpa.MonsteraEnt;
import database.entities.Jpa.PlantEnt;
import database.entities.Jpa.SucculentEnt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class JpaPlantRepository {
    private EntityManagerFactory emf;

    public JpaPlantRepository() {
        this.emf = JpaDBManager.getEmf();
    }

    public PlantEnt getPlant(String id) {
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

    public boolean removePlant(String id) {
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
                updatePlant(ent, em);
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
        PlantEnt dbPlant = em.find(PlantEnt.class, ent.getId());
        if (dbPlant == null || dbPlant.isSold() || dbPlant.getVersion() != ent.getVersion()) {
            throw new jakarta.persistence.OptimisticLockException("Roslina jest juz sprzedana");
        }
        dbPlant.setName(ent.getName());
        dbPlant.setColor(ent.getColor());
        dbPlant.setCost(ent.getCost());
        dbPlant.setSold(ent.isSold());
        if (dbPlant instanceof SucculentEnt dbSucculent) {
            SucculentEnt MainEnt = (SucculentEnt) ent;
            dbSucculent.setSpiky(MainEnt.isSpiky());
        }
        else if (dbPlant instanceof CarnivorousEnt dbCarnivorous) {
            CarnivorousEnt MainEnt = (CarnivorousEnt) ent;
            dbCarnivorous.setFavouriteFood(MainEnt.getFavouriteFood());
        }
        else if (dbPlant instanceof MonsteraEnt dbMonstera) {
            MonsteraEnt MainEnt = (MonsteraEnt) ent;
            dbMonstera.setLeafSize(MainEnt.getLeafSize());
        }
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

    public int countSoldPlantsByType(Class<? extends PlantEnt> plantClass) {
        String query = "SELECT COUNT(p) FROM PlantEnt p WHERE TYPE(p) = :plantClass AND p.sold = true";
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(query, Long.class)
                    .setParameter("plantClass", plantClass)
                    .getSingleResult().intValue();
        }
    }
}