package database.adapter;

import database.JpaMapper;
import database.entities.ClientEnt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import model.Client;
import model.repositories.ClientRepository;

import java.util.ArrayList;
import java.util.List;

public class JpaClientRepositoryAdapter implements ClientRepository {
    private EntityManagerFactory emf;

    public JpaClientRepositoryAdapter() {
        this.emf = Persistence.createEntityManagerFactory("default");
    }

    @Override
    public Client getClient(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            ClientEnt ent = em.find(ClientEnt.class, id);
            return JpaMapper.clientEntToObj(ent);
        }
    }

    @Override
    public boolean addClient(Client client) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction txn = em.getTransaction();
            try {
                txn.begin();
                ClientEnt ent = JpaMapper.clientObjToEnt(client);
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

    public void updateClient(Client client, EntityManager em) {
        ClientEnt ent = JpaMapper.clientObjToEnt(client);
        em.merge(ent);
    }

    @Override
    public boolean updateClient(Client client) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction txn = em.getTransaction();
            try {
                txn.begin();
                ClientEnt ent = JpaMapper.clientObjToEnt(client);
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

    @Override
    public boolean removeClient(int id){
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction txn = em.getTransaction();
            try {
                txn.begin();
                ClientEnt ent = em.find(ClientEnt.class, id);
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
    public boolean saveClients(List<Client> clients){
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction txn = em.getTransaction();
            try {
                txn.begin();
                for(Client client:clients){
                    ClientEnt ent = JpaMapper.clientObjToEnt(client);
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
    public List<Client> getClients() {
        String query = "SELECT p FROM ClientEnt p";
        List<ClientEnt> list;
        try (EntityManager em = emf.createEntityManager()) {
            list = em.createQuery(query, ClientEnt.class).getResultList();
            List<Client> clients = new ArrayList<>();
            for(ClientEnt ent:list) {
                clients.add(JpaMapper.clientEntToObj(ent));
            }
            return clients;
        }
    }

    @Override
    public int sizeClients() {
        return getClients().size();
    }

}
