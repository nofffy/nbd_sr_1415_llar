package database;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class MockDatabaseManager {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");

    public static EntityManagerFactory getEmf() {
        return emf;
    }

    public static void reset() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        emf = jakarta.persistence.Persistence.createEntityManagerFactory("default");
    }
}
