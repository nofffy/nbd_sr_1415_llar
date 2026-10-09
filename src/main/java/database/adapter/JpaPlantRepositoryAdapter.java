package database.adapter;

import database.JpaMapper;
import database.entities.*;
import jakarta.persistence.*;
import model.Client;
import model.Plant;
import model.repositories.PlantRepository;

import java.util.ArrayList;
import java.util.List;

public class JpaPlantRepositoryAdapter implements PlantRepository {
    private EntityManagerFactory emf;

    public JpaPlantRepositoryAdapter() {
        this.emf = Persistence.createEntityManagerFactory("default");
    }

    @Override
    public Plant getPlant(int id) { //UUID?
        try (EntityManager em = emf.createEntityManager()) {
            PlantEnt ent = em.find(PlantEnt.class, id);
            return JpaMapper.plantEntToObj(ent);
        }
    }

    @Override
    public boolean addPlant(Plant plant) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction txn = em.getTransaction();
            try {
                txn.begin();
                PlantEnt ent = JpaMapper.plantObjToEnt(plant);
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

    @Override
    public boolean removePlant(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction txn = em.getTransaction();
            try {
                txn.begin();
                PlantEnt ent = em.find(PlantEnt.class, id);
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
    public boolean updatePlant(Plant plant) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction txn = em.getTransaction();
            try {
                txn.begin();
                PlantEnt ent = JpaMapper.plantObjToEnt(plant);
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

    public void updatePlant(Plant plant, EntityManager em) {
        PlantEnt ent = JpaMapper.plantObjToEnt(plant);
        em.merge(ent);
    }

    @Override
    public boolean savePlants(List<Plant> plants) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction txn = em.getTransaction();
            try {
                txn.begin();
                for(Plant plant:plants){
                    PlantEnt ent = JpaMapper.plantObjToEnt(plant);
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

    @Override
    public List<Plant> getPlants() {
        String query = "SELECT p FROM PlantEnt p";
        List<PlantEnt> list;
        try (EntityManager em = emf.createEntityManager()) {
            list = em.createQuery(query, PlantEnt.class).getResultList();
            List<Plant> plants = new ArrayList<>();
            for(PlantEnt ent:list) {
                plants.add(JpaMapper.plantEntToObj(ent));
            }
            return plants;
        }
    }

    public List<Plant> findAllSoldOrNot(boolean choice) {
        String query = "SELECT p FROM PlantEnt p WHERE p.sold=:choice";
        List<PlantEnt> list;
        try (EntityManager em = emf.createEntityManager()) {
            list = em.createQuery(query, PlantEnt.class).setParameter("choice", choice).getResultList();

            List<Plant> plants = new ArrayList<>();
            for(PlantEnt ent:list) {
                plants.add(JpaMapper.plantEntToObj(ent));
            }
            return plants;
        }
    }

    @Override
    public List<Plant> findAllUnsold() {
        return findAllSoldOrNot(false);
    }

    @Override
    public List<Plant> findAllSold() {
        return findAllSoldOrNot(true);
    }
}
