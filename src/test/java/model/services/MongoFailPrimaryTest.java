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

    @BeforeEach
    void setUp() {
        MongoDatabase db = MongoDBManager.getDatabase();
        MongoDBManager.getDatabase().getCollection("clients").drop();
        MongoDBManager.getDatabase().getCollection("plants").drop();
        MongoDBManager.getDatabase().getCollection("sales").drop();

        clientRepo = new MongoClientRepositoryAdapter(new MongoClientRepository(db));
    }

    /* EKSPERYMENT PRZYGOTOWANIE
    1. reset
    -docker compose -f MONGOdocker-compose.yml down
    -docker compose -f MONGOdocker-compose.yml up -d
    2. przygotowac 3 okna terminala, w 1 wykonac:
    docker exec -it mongodb2 mongosh --port 27018 -u admin -p adminpassword --authenticationDatabase admin --eval "rs.status()"
    (latwo znalezc informacje o tym ktory jest primary, ewentualnie w komendzie wyzej zmienic ktory mongodb oraz port!)
    3. w 2 pozostale okna wkleic ale jeszcze nie enter
    -docker logs -f mongodb2 | grep -E "ELECTION" (wybrac te wezly ktore nie sa primary!!)
    4. wykonujemy ten wlasnie test w intellij
    5. w terminalu enter na komendy z logami
    6. w tym jednym pozostalym okienku
    -docker kill mongodb(cyferka tego primary)
    7. obserwujemy jak w ktoryms okienku powstaja logi o wygranym ELECTION
    8. na koniec mozna jeszcze raz sprawdzic komenda z punktu 3
     */

    @Test
    @DisplayName("Test odporności klastra na awarię węzła Primary")
    void shouldSurvivePrimaryNodeShutdown() throws InterruptedException {
        Client klient1 = new Client("Przed Awaria");
        assertTrue(clientRepo.addClient(klient1));

        System.out.println("======================================================");
        System.out.println("UWAGA! MASZ 40 SEKUND NA WYŁĄCZENIE WĘZŁA PRIMARY!");
        System.out.println("Wpisz w terminalu: docker kill mongodb(ten primary)");
        System.out.println("======================================================");

        Thread.sleep(40000);

        System.out.println("Czas minął! Próbuję zapisać dane mimo braku mongodb primary.");
        Client klient2 = new Client("Po Awarii");
        boolean sukces = clientRepo.addClient(klient2);

        assertTrue(sukces);
        assertEquals(2, clientRepo.sizeClients());
        System.out.println("TEST ZALICZONY: Klaster przetrwał awarię!");
    }
}
