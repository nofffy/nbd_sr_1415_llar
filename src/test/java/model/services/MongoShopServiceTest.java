package model.services;

import database.MongoDBManager;
import database.repositories.Mongo.MongoClientRepository;
import database.repositories.Mongo.MongoPlantRepository;
import database.repositories.Mongo.MongoSaleRepository;
import database.repositories.Mongo.adapters.MongoClientRepositoryAdapter;
import database.repositories.Mongo.adapters.MongoPlantRepositoryAdapter;
import database.repositories.Mongo.adapters.MongoSaleRepositoryAdapter;
import model.Client;
import model.Plant;
import model.Sale;
import model.repositories.ClientRepository;
import model.repositories.PlantRepository;
import model.repositories.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class MongoShopServiceTest {

    private PlantRepository plantRepo;
    private ClientRepository clientRepo;
    private SaleRepository saleRepo;
    private ShopService shopService;

    @BeforeEach
    void setUp() {
        // 1. ZAMIENNIK DLA MockDatabaseManager - Ręczne czyszczenie bazy Mongo
        MongoDatabase db = MongoDBManager.getDatabase();
        db.getCollection("clients").drop();
        db.getCollection("plants").drop();
        db.getCollection("sales").drop();

        // 2. Inicjalizacja adapterów MongoDB
        plantRepo = new MongoPlantRepositoryAdapter(new MongoPlantRepository(db));
        clientRepo = new MongoClientRepositoryAdapter(new MongoClientRepository(db));
        saleRepo = new MongoSaleRepositoryAdapter(new MongoSaleRepository(db), new MongoClientRepository(db));
        shopService = new ShopService(saleRepo);

        // 3. ZAMIENNIK DLA import.sql - Ręczne dodawanie danych początkowych
        clientRepo.addClient(new Client("Michal"));
        clientRepo.addClient(new Client("Klient 2"));

        Plant p1 = new Plant(); p1.setCost(350.0); p1.setSold(false); // Monstera Variegata
        Plant p2 = new Plant(); p2.setCost(45.0);  p2.setSold(false); // Muchołówka
        Plant p3 = new Plant(); p3.setCost(80.0);  p3.setSold(false); // Kaktus Saguaro
        Plant p4 = new Plant(); p4.setCost(120.0); p4.setSold(false); // Monstera
        Plant p5 = new Plant(); p5.setCost(50.0);  p5.setSold(false); // Rosiczka

        plantRepo.addPlant(p1);
        plantRepo.addPlant(p2);
        plantRepo.addPlant(p3);
        plantRepo.addPlant(p4);
        plantRepo.addPlant(p5);
    }

    @Test
    @DisplayName("1. Stan początkowy z seedera MongoDB")
    void shouldLoadInitialStateFromSeeder() {
        assertEquals(5, plantRepo.getPlants().size(), "Wszystkich roślin w bazie powinno być 5");
        assertEquals(5, plantRepo.findAllUnsold().size(), "Niesprzedanych roślin powinno być 5");
        assertEquals(0, plantRepo.findAllSold().size(), "Sprzedanych roślin powinno być 0");
        assertEquals(2, clientRepo.sizeClients(), "Liczba klientów powinna wynosić 2");
    }

    @Test
    @DisplayName("2. Edycja i usuwanie klientów oraz roślin")
    void shouldUpdateAndRemoveClientsAndPlants() {
        List<Client> clients = clientRepo.getClients();
        Client michal = clients.get(0);

        michal.setMember(true);
        michal.setMoneySpent(50.0);
        assertTrue(clientRepo.updateClient(michal), "Update klienta 1 powinien się powieść");

        Client michalPoUpdate = clientRepo.getClient(michal.getId());
        assertTrue(michalPoUpdate.isMember(), "Michał powinien mieć status member = true");
        assertEquals(50.0, michalPoUpdate.getMoneySpent(), 0.001, "Wydatki Michała powinny wynosić 50.0");

        List<Plant> plants = plantRepo.getPlants();
        Plant rosiczka = plants.get(4);
        rosiczka.setCost(99.0);
        assertTrue(plantRepo.updatePlant(rosiczka), "Zmiana ceny niesprzedanej rośliny powinna się powieść");
        assertEquals(99.0, plantRepo.getPlant(rosiczka.getId()).getCost(), 0.001);

        Client klientDoUsuniecia = clients.get(1);
        assertTrue(clientRepo.removeClient(klientDoUsuniecia.getId()), "Usunięcie istniejącego klienta powinno zwrócić true");

        // Dowolny losowy poprawny ObjectId (żeby nie rzuciło wyjątku, ale zwróciło false)
        assertFalse(clientRepo.removeClient("507f1f77bcf86cd799439011"), "Usunięcie nieistniejącego klienta powinno zwrócić false");
        assertEquals(1, clientRepo.sizeClients(), "Po usunięciu powinien zostać 1 klient");

        assertTrue(plantRepo.removePlant(rosiczka.getId()), "Usunięcie rośliny powinno zwrócić true");
        assertEquals(4, plantRepo.getPlants().size(), "Po usunięciu powinny zostać 4 rośliny");
    }

    @Test
    @DisplayName("3. Walidacja błędnego koszyka")
    void shouldRejectInvalidSaleRequests() {
        Client michal = clientRepo.getClients().get(0);
        Plant roslina1 = plantRepo.getPlants().get(0);

        assertFalse(shopService.newSale(michal, new ArrayList<>()), "Zakup z pustym koszykiem powinien zostać odrzucony");
        assertFalse(shopService.newSale(null, List.of(roslina1)), "Zakup bez klienta powinien zostać odrzucony");
    }

    @Test
    @DisplayName("4. Normalne zakupy i weryfikacja transakcji w bazie dokumentowej")
    void shouldProcessNormalSalesCorrectly() {
        List<Client> initialClients = clientRepo.getClients();
        Client kupujacyMichal = initialClients.get(0);

        List<Plant> initialPlants = plantRepo.getPlants();
        Plant roslinaDlaMichal1 = initialPlants.get(0); // 350.0
        Plant roslinaDlaMichal2 = initialPlants.get(1); // 45.0
        Plant roslinaDlaTomasz1 = initialPlants.get(2); // 80.0

        List<Plant> koszykMichal = new ArrayList<>(List.of(roslinaDlaMichal1, roslinaDlaMichal2));
        assertTrue(shopService.newSale(kupujacyMichal, koszykMichal), "Zakup Michała w Mongo powinien się powieść");

        Client tomasz = new Client("testowy tomasz");
        assertTrue(clientRepo.addClient(tomasz));

        List<Client> wszyscyKlienci = clientRepo.getClients();
        Client tomaszZBazy = wszyscyKlienci.get(wszyscyKlienci.size() - 1);

        List<Plant> koszykTomasz = new ArrayList<>(List.of(roslinaDlaTomasz1));
        assertTrue(shopService.newSale(tomaszZBazy, koszykTomasz), "Zakup Tomasza powinien się powieść");

        Client sprawdzonyMichal = clientRepo.getClient(kupujacyMichal.getId());
        Client sprawdzonyTomasz = clientRepo.getClient(tomaszZBazy.getId());

        assertEquals(395.0, sprawdzonyMichal.getMoneySpent(), 0.001, "Wydatki Michała: 350.0 + 45.0 = 395.0");
        assertEquals(80.0, sprawdzonyTomasz.getMoneySpent(), 0.001, "Wydatki Tomasza: 80.0");
        assertEquals(3, plantRepo.findAllSold().size(), "Sprzedane powinny być 3 rośliny");
        assertEquals(2, plantRepo.findAllUnsold().size(), "Niesprzedane powinny zostać 2 rośliny");
        assertEquals(2, saleRepo.sizeSales(), "W bazie powinny być 2 paragony");

        Sale paragon1 = saleRepo.getClientSales(sprawdzonyMichal.getId()).get(0);
        assertNotNull(paragon1);
        assertEquals(2, paragon1.getPlants().size(), "Paragon 1 powinien zawierać 2 zdenormalizowane rośliny w Mongo");
    }

    @Test
    @DisplayName("5. Test współbieżności i transakcji wielodokumentowych Mongo (wyścig o roślinę)")
    void shouldAllowOnlyOneClientToBuyPlantInRaceCondition() throws InterruptedException {
        List<Client> clients = clientRepo.getClients();
        Client michal = clients.get(0);
        Client klient2 = clients.get(1);

        Plant monsteraWyscig = plantRepo.getPlants().get(3); // 120.0

        List<Plant> koszykWyscigMichal = new ArrayList<>(List.of(monsteraWyscig));
        List<Plant> koszykWyscigKlient2 = new ArrayList<>(List.of(monsteraWyscig));

        AtomicBoolean wynikMichal = new AtomicBoolean(false);
        AtomicBoolean wynikKlient2 = new AtomicBoolean(false);

        Thread watekMichal = new Thread(() -> wynikMichal.set(shopService.newSale(michal, koszykWyscigMichal)));
        Thread watekKlient2 = new Thread(() -> wynikKlient2.set(shopService.newSale(klient2, koszykWyscigKlient2)));

        watekMichal.start();
        watekKlient2.start();
        watekMichal.join();
        watekKlient2.join();

        // W Mongo transakcja ClientSession rzuci WriteConflictException w drugim wątku, a adapter to złapie.
        assertTrue(wynikMichal.get() ^ wynikKlient2.get(),
                "Transakcja Mongo powinna zablokować wyścig. Tylko jedna przechodzi!");

        Client ostatecznyMichal = clientRepo.getClient(michal.getId());
        Client ostatecznyKlient2 = clientRepo.getClient(klient2.getId());

        assertEquals(120.0, ostatecznyMichal.getMoneySpent() + ostatecznyKlient2.getMoneySpent(), 0.001,
                "Kwota 120.0 powinna zostać doliczona tylko jednej osobie");
        assertEquals(1, saleRepo.sizeSales(), "Powinien powstać dokładnie 1 paragon");
        assertTrue(plantRepo.getPlant(monsteraWyscig.getId()).isSold(), "Roślina powinna mieć status sprzedanej");
    }

    @Test
    @DisplayName("6. Usuwanie paragonu z bazy dokumentowej")
    void shouldRemoveSale() {
        List<Client> clients = clientRepo.getClients();
        Client michal = clients.get(0);
        List<Plant> plants = plantRepo.getPlants();

        assertTrue(shopService.newSale(michal, new ArrayList<>(List.of(plants.get(0)))));

        Client michalZaktualizowany = clientRepo.getClient(michal.getId());
        assertTrue(shopService.newSale(michalZaktualizowany, new ArrayList<>(List.of(plants.get(1)))));

        assertEquals(2, saleRepo.sizeSales());

        Sale paragonDoUsuniecia = saleRepo.getClientSales(michal.getId()).get(1);

        assertTrue(saleRepo.removeSale(paragonDoUsuniecia.getId()), "Usunięcie paragonu powinno się powieść");
        assertEquals(1, saleRepo.sizeSales(), "Liczba paragonów po usunięciu powinna wynosić 1");
    }

    @Test
    @DisplayName("7. Test próby kupienia już sprzedanej rośliny")
    void shouldRejectBuyingAlreadySoldPlantAndReturnClientHistory() {
        Client michal = clientRepo.getClients().get(0);
        Plant roslina1 = plantRepo.getPlants().get(0);

        assertTrue(shopService.newSale(michal, new ArrayList<>(List.of(roslina1))));

        Client hacker = clientRepo.getClient(michal.getId());
        double michalBefore = hacker.getMoneySpent();

        Plant soldPlant = plantRepo.getPlant(roslina1.getId());
        soldPlant.setSold(false);

        List<Plant> soldKoszyk = new ArrayList<>(List.of(soldPlant));
        boolean sukcesSpozniony = shopService.newSale(hacker, soldKoszyk);

        assertFalse(sukcesSpozniony, "Transakcja Mongo powinna zostać wycofana (rollback)");

        Client verification = clientRepo.getClient(michal.getId());
        assertEquals(michalBefore, verification.getMoneySpent(), 0.001,
                "Wydatki Michała nie powinny wzrosnąć po odrzuconym zakupie");

        List<Sale> zakupyMichal = saleRepo.getClientSales(michal.getId());
        assertEquals(1, zakupyMichal.size(), "Michał powinien mieć w historii dokładnie 1 udany zakup");
        assertEquals(1, zakupyMichal.get(0).getPlants().size(), "Paragon Michała powinien zawierać 1 roślinę");
    }
}