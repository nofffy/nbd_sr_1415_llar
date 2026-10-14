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

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        PlantRepository plantRepo = new JpaPlantRepositoryAdapter();
        ClientRepository clientRepo = new JpaClientRepositoryAdapter();
        SaleRepository saleRepo = new JpaSaleRepositoryAdapter();
        ShopService shopService = new ShopService(saleRepo);

        System.out.println("=== 1. stan poczatkowy z import.sql ===");
        System.out.println("wszystkie rosliny w bazie: " + plantRepo.getPlants().size() + " -> ma byc: 5");
        System.out.println("rosliny niesprzedane: " + plantRepo.findAllUnsold().size() + " -> ma byc: 5");
        System.out.println("rosliny sprzedane: " + plantRepo.findAllSold().size() + " -> ma byc: 0");
        System.out.println("liczba klientow: " + clientRepo.sizeClients() + " -> ma byc: 2");

        System.out.println("\n=== 2. edycja i usuwanie ===");
        Client michal = clientRepo.getClient(1);
        michal.setMember(true);
        michal.setMoneySpent(50.0);
        boolean updateKlientaOk = clientRepo.updateClient(michal);
        Client MichalPoUpdate = clientRepo.getClient(1);
        System.out.println("update klienta 1: " + (updateKlientaOk ? "ok" : "blad") + " -> ma byc: ok");
        System.out.println("Michal member: " + MichalPoUpdate.isMember() + ", wydatki: " + MichalPoUpdate.getMoneySpent() + " -> ma byc: true i 50.0");

        Plant rosiczka = plantRepo.getPlant(5);
        rosiczka.setSold(true);
        plantRepo.updatePlant(rosiczka);
        System.out.println("rosiczka 1 update (czy sprzedana): " + plantRepo.getPlant(5).isSold() + " -> ma byc: true");

        rosiczka = plantRepo.getPlant(5);
        rosiczka.setSold(false);
        plantRepo.updatePlant(rosiczka);
        System.out.println("rosiczka 2 update (czy sprzedana): " + plantRepo.getPlant(5).isSold() + " -> ma byc: false");

        boolean usunietoKlienta2 = clientRepo.removeClient(2);
        boolean usunietoKlienta999 = clientRepo.removeClient(999);
        System.out.println("usuniecie klienta 2: " + (usunietoKlienta2 ? "ok" : "blad") + " -> ma byc: ok");
        System.out.println("usuniecie klienta 999 (nie istnieje): " + (usunietoKlienta999 ? "blad" : "odrzucono (ok)"));
        System.out.println("liczba klientow po usunieciu: " + clientRepo.sizeClients() + " -> ma byc: 1");

        boolean usunietoRosline5 = plantRepo.removePlant(5);
        System.out.println("usuniecie rosliny 5: " + (usunietoRosline5 ? "ok" : "blad") + " -> ma byc: ok");
        System.out.println("liczba roslin po usunieciu: " + plantRepo.getPlants().size() + " -> ma byc: 4");

        System.out.println("\n=== 3. walidacja blednego koszyka ===");
        boolean pustyKoszyk = shopService.newSale(michal, new ArrayList<>());
        boolean brakKlienta = shopService.newSale(null, List.of(plantRepo.getPlant(1)));
        System.out.println("zakup z pustym koszykiem: " + (pustyKoszyk ? "blad" : "odrzucono (ok)"));
        System.out.println("zakup bez klienta: " + (brakKlienta ? "blad" : "odrzucono (ok)"));

        System.out.println("\n=== 4. normalne zakupy ===");
        Client kupujacyMichal = clientRepo.getClient(1);
        Plant roslinaDlaMichal1 = plantRepo.getPlant(1); // 350.0 monstera variegata
        Plant roslinaDlaMichal2 = plantRepo.getPlant(2); // 45.0 mucholowka
        Plant roslinaDlaTomasz1 = plantRepo.getPlant(3); // 80.0 kaktus saguaro

        List<Plant> koszykMichal = new ArrayList<>();
        koszykMichal.add(roslinaDlaMichal1);
        koszykMichal.add(roslinaDlaMichal2);
        System.out.println("koszyk michala:");
        for (Plant p : koszykMichal) {
            System.out.println(" - " + p.getInfo());
        }
        boolean sukces1 = shopService.newSale(kupujacyMichal, koszykMichal);
        System.out.println("status zakupu michala: " + (sukces1 ? "ok" : "blad") + " -> ma byc: ok");

        Client tomasz = new Client("testowy tomasz");
        clientRepo.addClient(tomasz);
        int tomaszId = tomasz.getId();
        List<Plant> koszykTomasz = new ArrayList<>();
        koszykTomasz.add(roslinaDlaTomasz1);
        System.out.println("\nkoszyk tomasza:");
        System.out.println(" - " + roslinaDlaTomasz1.getInfo());
        boolean sukces2 = shopService.newSale(tomasz, koszykTomasz);
        System.out.println("status zakupu tomasza: " + (sukces2 ? "ok" : "blad") + " -> ma byc: ok");

        System.out.println("\nweryfikacja bazy po zakupach:");
        Client sprawdzonyMichal = clientRepo.getClient(1);
        Client sprawdzonyTomasz = clientRepo.getClient(tomaszId);
        System.out.println("wydatki michala: " + sprawdzonyMichal.getMoneySpent() + " -> ma byc: 445.0 (50.0 + 395.0)"); //50 bylo dodane wczesniej przez setMoney!!
        System.out.println("wydatki tomasza: " + sprawdzonyTomasz.getMoneySpent() + " -> ma byc: 80.0");
        System.out.println("rosliny sprzedane: " + plantRepo.findAllSold().size() + " -> ma byc: 3");
        System.out.println("rosliny niesprzedane: " + plantRepo.findAllUnsold().size() + " -> ma byc: 1");
        System.out.println("liczba paragonow: " + saleRepo.sizeSales() + " -> ma byc: 2");

        Sale paragon1 = saleRepo.getSale(1);
        System.out.println("paragon 1 klient: " + paragon1.getClient().getName()
                + ", kupione rosliny: " + paragon1.getPlants().size() + " -> ma byc: 2");

        System.out.println("\n=== 5. test wspolbieznosci (wyscig o monstere 4 za 120.0) ===");
        Plant monsteraDlaAnny = plantRepo.getPlant(4);
        Plant monsteraDlaTomasza = plantRepo.getPlant(4);

        List<Plant> koszykWyscigMichal = new ArrayList<>();
        koszykWyscigMichal.add(monsteraDlaAnny);
        List<Plant> koszykWyscigTomasz = new ArrayList<>();
        koszykWyscigTomasz.add(monsteraDlaTomasza);

        Thread watekMichal = new Thread(() -> {
            boolean wynik = shopService.newSale(sprawdzonyMichal, koszykWyscigMichal);
            System.out.println("wyscig michal: " + (wynik ? "zakupione" : "blad odrzucono"));
        });
        Thread watekTomasz = new Thread(() -> {
            boolean wynik = shopService.newSale(sprawdzonyTomasz, koszykWyscigTomasz);
            System.out.println("wyscig tomasz: " + (wynik ? "zakupione" : "blad odrzucono"));
        });
        watekMichal.start();
        watekTomasz.start();
        try {
            watekMichal.join();
            watekTomasz.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nweryfikacja po wyscigu (120 doliczone tylko jednej osobie!):");
        Client ostatecznyMichal = clientRepo.getClient(1);
        Client ostatecznyTomasz = clientRepo.getClient(tomaszId);
        System.out.println("ostateczne wydatki michala: " + ostatecznyMichal.getMoneySpent() + " (przed wyscigiem: 445.0)");
        System.out.println("ostateczne wydatki tomasza: " + ostatecznyTomasz.getMoneySpent() + " (przed wyscigiem: 80.0)");
        System.out.println("liczba paragonow po wyscigu: " + saleRepo.getSales().size() + " -> ma byc: 3");
        System.out.println("monstera 4 sprzedana: " + plantRepo.getPlant(4).isSold() + " -> ma byc: true");

        System.out.println("\n=== 6. usuwanie paragonu ===");
        boolean usunietoParagon = saleRepo.removeSale(2);
        System.out.println("usuniecie paragonu 2: " + (usunietoParagon ? "ok" : "blad") + " -> ma byc: ok");
        System.out.println("liczba paragonow na koniec: " + saleRepo.sizeSales() + " -> ma byc: 2");
    }
}