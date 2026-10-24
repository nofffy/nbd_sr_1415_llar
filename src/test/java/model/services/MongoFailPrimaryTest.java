package model.services;

import com.mongodb.client.MongoDatabase;
import database.MongoDBManager;
import database.repositories.Mongo.MongoClientRepository;
import database.repositories.Mongo.MongoPlantRepository;
import database.repositories.Mongo.MongoSaleRepository;
import database.repositories.Mongo.adapters.MongoClientRepositoryAdapter;
import database.repositories.Mongo.adapters.MongoPlantRepositoryAdapter;
import database.repositories.Mongo.adapters.MongoSaleRepositoryAdapter;
import model.Client;
import model.repositories.ClientRepository;
import model.repositories.PlantRepository;
import model.repositories.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MongoFailPrimaryTest {

    private ClientRepository clientRepo;
    private PlantRepository plantRepo;
    private ShopService shopService;

    @BeforeEach
    void setUp() {
        MongoDatabase db = MongoDBManager.getDatabase();
        MongoDBManager.getDatabase().getCollection("clients").drop();
        MongoDBManager.getDatabase().getCollection("plants").drop();
        MongoDBManager.getDatabase().getCollection("sales").drop();

        clientRepo = new MongoClientRepositoryAdapter(new MongoClientRepository(db));
        plantRepo = new MongoPlantRepositoryAdapter(new MongoPlantRepository(db));
        SaleRepository saleRepo = new MongoSaleRepositoryAdapter(new MongoSaleRepository(db), new MongoClientRepository(db));
        shopService = new ShopService(saleRepo);
    }

    @Test
    @DisplayName("Test odporności klastra na awarię węzła Primary")
    void shouldSurvivePrimaryNodeShutdown() throws InterruptedException {
        Client klient1 = new Client("Przed Awaria");
        assertTrue(clientRepo.addClient(klient1));

        System.out.println("======================================================");
        System.out.println("UWAGA! MASZ 30 SEKUND NA WYŁĄCZENIE WĘZŁA PRIMARY!");
        System.out.println("Wpisz w terminalu: docker kill mongodb1");
        System.out.println("======================================================");

        Thread.sleep(30000);

        System.out.println("Czas minął! Próbuję zapisać dane mimo braku mongodb1...");
        Client klient2 = new Client("Po Awarii");
        boolean sukces = clientRepo.addClient(klient2);

        assertTrue(sukces);
        assertEquals(2, clientRepo.sizeClients());
        System.out.println("TEST ZALICZONY: Klaster przetrwał awarię!");
    }
}
