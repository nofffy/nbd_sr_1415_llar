package database.repositories;

import database.DatabaseManager;
import database.entities.ClientEnt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class JpaClientRepository {
    private EntityManagerFactory emf;

    public JpaClientRepository() {
        this.emf = DatabaseManager.getEmf();
    }

    public ClientEnt getClient(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(ClientEnt.class, id);
        }
    }

    public boolean addClient(ClientEnt ent) {
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

    public void updateClient(ClientEnt ent, EntityManager em) {
        ClientEnt dbClient = em.find(ClientEnt.class, ent.getId());
        if (dbClient == null) {
            throw new jakarta.persistence.OptimisticLockException("Klient został zmodyfikowany");
        }
        dbClient.setName(ent.getName());
        dbClient.setMoneySpent(ent.getMoneySpent());
        dbClient.setMember(ent.isMember());
    }

    public boolean updateClient(ClientEnt ent) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction txn = em.getTransaction();
            try {
                txn.begin();
                ClientEnt dbClient = em.find(ClientEnt.class, ent.getId());
                dbClient.setName(ent.getName());
                dbClient.setMoneySpent(ent.getMoneySpent());
                dbClient.setMember(ent.isMember());
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

    public boolean removeClient(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction txn = em.getTransaction();
            try {
                txn.begin();
                ClientEnt ent = em.find(ClientEnt.class, id);
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

    public boolean addClients(List<ClientEnt> ents) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction txn = em.getTransaction();
            try {
                txn.begin();
                for (ClientEnt ent : ents) {
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

    public List<ClientEnt> getClients() {
        String query = "SELECT p FROM ClientEnt p";
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(query, ClientEnt.class).getResultList();
        }
    }

    public int countClients() {
        String query = "SELECT COUNT(p) FROM ClientEnt p";
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(query, Long.class).getSingleResult().intValue();
        }
    }
}