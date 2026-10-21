package database;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaDBManager {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");

    public static EntityManagerFactory getEmf() {
        return emf;
    }
}