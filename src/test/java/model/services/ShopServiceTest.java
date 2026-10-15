package model.services;

import database.MockDatabaseManager;
import database.repositories.adapters.JpaClientRepositoryAdapter;
import database.repositories.adapters.JpaPlantRepositoryAdapter;
import database.repositories.adapters.JpaSaleRepositoryAdapter;
import model.Client;
import model.Plant;
import model.Sale;
import model.repositories.ClientRepository;
import model.repositories.PlantRepository;
import model.repositories.SaleRepository;
import model.services.ShopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class ShopSystemTest {

    private PlantRepository plantRepo;
    private ClientRepository clientRepo;
    private SaleRepository saleRepo;
    private ShopService shopService;

    @BeforeEach
    void setUp() {
        // Reset bazy danych przed każdym testem gwarantuje 100% niezależności
        MockDatabaseManager.reset();

        plantRepo = new JpaPlantRepositoryAdapter();
        clientRepo = new JpaClientRepositoryAdapter();
        saleRepo = new JpaSaleRepositoryAdapter();
        shopService = new ShopService(saleRepo);
    }

    @Test
    @DisplayName("1. Stan początkowy z import.sql")
    void shouldLoadInitialStateFromImportSql() {
        assertEquals(5, plantRepo.getPlants().size(), "Wszystkich roślin w bazie powinno być 5");
        assertEquals(5, plantRepo.findAllUnsold().size(), "Niesprzedanych roślin powinno być 5");
        assertEquals(0, plantRepo.findAllSold().size(), "Sprzedanych roślin powinno być 0");
        assertEquals(2, clientRepo.sizeClients(), "Liczba klientów powinna wynosić 2");
    }

    @Test
    @DisplayName("2. Edycja i usuwanie klientów oraz roślin")
    void shouldUpdateAndRemoveClientsAndPlants() {
        // Edycja klienta 1
        Client michal = clientRepo.getClient(1);
        michal.setMember(true);
        michal.setMoneySpent(50.0);
        assertTrue(clientRepo.updateClient(michal), "Update klienta 1 powinien się powieść");

        Client michalPoUpdate = clientRepo.getClient(1);
        assertTrue(michalPoUpdate.isMember(), "Michał powinien mieć status member = true");
        assertEquals(50.0, michalPoUpdate.getMoneySpent(), 0.001, "Wydatki Michała powinny wynosić 50.0");

        // Edycja rośliny 5 (pamiętając o regule biznesowej blokującej edycję sprzedanej rośliny)
        Plant rosiczka = plantRepo.getPlant(5);
        rosiczka.setCost(99.0);
        assertTrue(plantRepo.updatePlant(rosiczka), "Zmiana ceny niesprzedanej rośliny powinna się powieść");
        assertEquals(99.0, plantRepo.getPlant(5).getCost(), 0.001);

        // Usuwanie klientów
        assertTrue(clientRepo.removeClient(2), "Usunięcie istniejącego klienta 2 powinno zwrócić true");
        assertFalse(clientRepo.removeClient(999), "Usunięcie nieistniejącego klienta 999 powinno zwrócić false");
        assertEquals(1, clientRepo.sizeClients(), "Po usunięciu powinien zostać 1 klient");

        // Usuwanie rośliny
        assertTrue(plantRepo.removePlant(5), "Usunięcie rośliny 5 powinno zwrócić true");
        assertEquals(4, plantRepo.getPlants().size(), "Po usunięciu powinny zostać 4 rośliny");
    }

    @Test
    @DisplayName("3. Walidacja błędnego koszyka")
    void shouldRejectInvalidSaleRequests() {
        Client michal = clientRepo.getClient(1);
        Plant roslina1 = plantRepo.getPlant(1);

        assertFalse(shopService.newSale(michal, new ArrayList<>()), "Zakup z pustym koszykiem powinien zostać odrzucony");
        assertFalse(shopService.newSale(null, List.of(roslina1)), "Zakup bez klienta powinien zostać odrzucony");
    }

    @Test
    @DisplayName("4. Normalne zakupy i weryfikacja bazy po zakupach")
    void shouldProcessNormalSalesCorrectly() {
        Client kupujacyMichal = clientRepo.getClient(1); // Na czystej bazie ma 0.0 wydatków
        Plant roslinaDlaMichal1 = plantRepo.getPlant(1); // 350.0 monstera variegata
        Plant roslinaDlaMichal2 = plantRepo.getPlant(2); // 45.0 muchołówka
        Plant roslinaDlaTomasz1 = plantRepo.getPlant(3); // 80.0 kaktus saguaro

        List<Plant> koszykMichal = new ArrayList<>(List.of(roslinaDlaMichal1, roslinaDlaMichal2));
        assertTrue(shopService.newSale(kupujacyMichal, koszykMichal), "Zakup Michała powinien się powieść");

        Client tomasz = new Client("testowy tomasz");
        assertTrue(clientRepo.addClient(tomasz));
        // Pobieramy świeżego Tomasza z bazy (z nadanym ID i wersją)
        List<Client> wszyscyKlienci = clientRepo.getClients();
        Client tomaszZBazy = wszyscyKlienci.get(wszyscyKlienci.size() - 1);

        List<Plant> koszykTomasz = new ArrayList<>(List.of(roslinaDlaTomasz1));
        assertTrue(shopService.newSale(tomaszZBazy, koszykTomasz), "Zakup Tomasza powinien się powieść");

        // Weryfikacja bazy po zakupach (5 roślin na starcie: 3 sprzedane, 2 niesprzedane)
        Client sprawdzonyMichal = clientRepo.getClient(1);
        Client sprawdzonyTomasz = clientRepo.getClient(tomaszZBazy.getId());

        assertEquals(395.0, sprawdzonyMichal.getMoneySpent(), 0.001, "Wydatki Michała: 350.0 + 45.0 = 395.0");
        assertEquals(80.0, sprawdzonyTomasz.getMoneySpent(), 0.001, "Wydatki Tomasza: 80.0");
        assertEquals(3, plantRepo.findAllSold().size(), "Sprzedane powinny być 3 rośliny");
        assertEquals(2, plantRepo.findAllUnsold().size(), "Niesprzedane powinny zostać 2 rośliny (z 5 początkowych)");
        assertEquals(2, saleRepo.sizeSales(), "W bazie powinny być 2 paragony");

        Sale paragon1 = saleRepo.getSale(1);
        assertNotNull(paragon1);
        assertEquals(2, paragon1.getPlants().size(), "Paragon 1 powinien zawierać 2 rośliny");
    }

    @Test
    @DisplayName("5. Test współbieżności (wyścig o Monsterę 4 za 120.0)")
    void shouldAllowOnlyOneClientToBuyPlantInRaceCondition() throws InterruptedException {
        Client michal = clientRepo.getClient(1); // ID 1 z import.sql
        Client klient2 = clientRepo.getClient(2); // ID 2 z import.sql

        Plant monsteraDlaMichala = plantRepo.getPlant(4); // 120.0
        Plant monsteraDlaKlienta2 = plantRepo.getPlant(4); // 120.0

        List<Plant> koszykWyscigMichal = new ArrayList<>(List.of(monsteraDlaMichala));
        List<Plant> koszykWyscigKlient2 = new ArrayList<>(List.of(monsteraDlaKlienta2));

        AtomicBoolean wynikMichal = new AtomicBoolean(false);
        AtomicBoolean wynikKlient2 = new AtomicBoolean(false);

        Thread watekMichal = new Thread(() ->
                wynikMichal.set(shopService.newSale(michal, koszykWyscigMichal))
        );
        Thread watekKlient2 = new Thread(() ->
                wynikKlient2.set(shopService.newSale(klient2, koszykWyscigKlient2))
        );

        watekMichal.start();
        watekKlient2.start();
        watekMichal.join();
        watekKlient2.join();

        // Dokładnie jeden wątek powinien zwrócić true, a drugi false (operacja XOR ^)
        assertTrue(wynikMichal.get() ^ wynikKlient2.get(),
                "Dokładnie jeden klient powinien kupić roślinę, a drugi otrzymać odrzucenie!");

        Client ostatecznyMichal = clientRepo.getClient(1);
        Client ostatecznyKlient2 = clientRepo.getClient(2);

        // Suma wydatków obu klientów musi wynosić dokładnie 120.0 (jeden ma 120.0, drugi 0.0)
        assertEquals(120.0, ostatecznyMichal.getMoneySpent() + ostatecznyKlient2.getMoneySpent(), 0.001,
                "Kwota 120.0 powinna zostać doliczona tylko jednej osobie");
        assertEquals(1, saleRepo.sizeSales(), "Powinien powstać dokładnie 1 paragon");
        assertTrue(plantRepo.getPlant(4).isSold(), "Monstera 4 powinna mieć status sprzedanej");
    }

    @Test
    @DisplayName("6. Usuwanie paragonu")
    void shouldRemoveSale() {
        // Przygotowanie niezależnych danych: tworzymy dwa zakupy
        Client michal = clientRepo.getClient(1);
        assertTrue(shopService.newSale(michal, new ArrayList<>(List.of(plantRepo.getPlant(1)))));

        Client michalSwiezy = clientRepo.getClient(1);
        assertTrue(shopService.newSale(michalSwiezy, new ArrayList<>(List.of(plantRepo.getPlant(2)))));
        assertEquals(2, saleRepo.sizeSales());

        // Właściwy test usuwania paragonu nr 2
        assertTrue(saleRepo.removeSale(2), "Usunięcie paragonu 2 powinno się powieść");
        assertEquals(1, saleRepo.sizeSales(), "Liczba paragonów po usunięciu powinna wynosić 1");
    }

    @Test
    @DisplayName("7. Test próby kupienia już sprzedanej rośliny oraz historia zakupów")
    void shouldRejectBuyingAlreadySoldPlantAndReturnClientHistory() {
        // 1. Najpierw legalnie kupujemy roślinę nr 1, aby w bazie była sprzedana
        Client michal = clientRepo.getClient(1);
        Plant roslina1 = plantRepo.getPlant(1);
        assertTrue(shopService.newSale(michal, new ArrayList<>(List.of(roslina1))));

        // 2. Próba oszustwa: pobieramy klienta i sprzedaną roślinę, ręcznie ustawiamy jej sold = false w pamięci
        Client hacker = clientRepo.getClient(1);
        double michalBefore = hacker.getMoneySpent();
        Plant soldPlant = plantRepo.getPlant(1);
        soldPlant.setSold(false); // Próba ominięcia walidacji w ShopService

        List<Plant> soldKoszyk = new ArrayList<>(List.of(soldPlant));
        boolean sukcesSpozniony = shopService.newSale(hacker, soldKoszyk);

        // 3. Weryfikacja ochrony w bazie danych
        assertFalse(sukcesSpozniony, "Baza danych powinna odrzucić próbę zakupu już sprzedanej rośliny");

        Client verification = clientRepo.getClient(1);
        assertEquals(michalBefore, verification.getMoneySpent(), 0.001,
                "Wydatki Michała nie powinny wzrosnąć po odrzuconym zakupie");

        // 4. Weryfikacja historii zakupów
        List<Sale> zakupyMichal = saleRepo.getClientSales(1);
        assertEquals(1, zakupyMichal.size(), "Michał powinien mieć w historii dokładnie 1 udany zakup");
        assertEquals(1, zakupyMichal.get(0).getPlants().size(), "Paragon Michała powinien zawierać 1 roślinę");
    }
}