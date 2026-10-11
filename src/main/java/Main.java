import model.Client;
import model.Plant;
import model.types.Carnivorous;
import model.types.Monstera;
import database.adapter.JpaClientRepositoryAdapter;
import database.adapter.JpaPlantRepositoryAdapter;
import model.types.Succulent;
import services.ShopService;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        JpaPlantRepositoryAdapter plantRepo = new JpaPlantRepositoryAdapter();
        JpaClientRepositoryAdapter clientRepo = new JpaClientRepositoryAdapter();
        ShopService shopService = new ShopService();

        Plant p1 = new Plant("monstera variegata", "bialo zielony", 350.0, false, new Monstera(120));
        Plant p2 = new Plant("mucholowka", "zielono czerwony", 45.0, false, new Carnivorous("fly"));
        Plant p3 = new Plant("kaktus saguaro", "zielony", 80.0, false, new Succulent(true));
        Plant p4 = new Plant("monstera deliciosa", "ciemno zielony", 120.0, false, new Monstera(45));
        Plant p5 = new Plant("rosiczka", "czerwony", 55.0, false, new Carnivorous("mosquito"));

        plantRepo.addPlant(p1);
        plantRepo.addPlant(p2);
        plantRepo.addPlant(p3);
        plantRepo.addPlant(p4);
        plantRepo.addPlant(p5);
        System.out.println("dodano 5 roslin");

        Client c1 = new Client("anna kowalska");
        Client c2 = new Client("jan nowak");
        clientRepo.addClient(c1);
        clientRepo.addClient(c2);
        System.out.println("dodano 2 klientow");

        Client kupujacyAnna = clientRepo.getClient(1);
        Client kupujacyJan = clientRepo.getClient(2);

        Plant roslinaDlaAnny1 = plantRepo.getPlant(1);
        Plant roslinaDlaAnny2 = plantRepo.getPlant(2);
        Plant roslinaDlaJana1 = plantRepo.getPlant(3);

        System.out.println("\ntransakcja 1 anna");
        List<Plant> koszykAnny = new ArrayList<>();
        koszykAnny.add(roslinaDlaAnny1);
        koszykAnny.add(roslinaDlaAnny2);

        System.out.println("zakupy anny");
        for(Plant p : koszykAnny) {
            System.out.println(p.getInfo());
        }

        boolean sukces1 = shopService.newSale(kupujacyAnna, koszykAnny);
        System.out.println("status anna: " + (sukces1 ? "ok" : "blad"));

        System.out.println("\ntransakcja 2 jan");
        List<Plant> koszykJana = new ArrayList<>();
        koszykJana.add(roslinaDlaJana1);

        System.out.println("zakupy jana");
        System.out.println(roslinaDlaJana1.getInfo());

        boolean sukces2 = shopService.newSale(kupujacyJan, koszykJana);
        System.out.println("status jan: " + (sukces2 ? "ok" : "blad"));

        System.out.println("\n\nweryfikacja bazy:");
        Client sprawdzonaAnna = clientRepo.getClient(1);
        Client sprawdzonyJan = clientRepo.getClient(2);

        System.out.println("wydatki anna " + sprawdzonaAnna.getMoneySpent() + " (ma byc 395)");
        System.out.println("wydatki jan " + sprawdzonyJan.getMoneySpent() + " (ma byc 80)");

        Plant sprawdzonaMonstera = plantRepo.getPlant(1);
        Plant niesprzedanaMonstera = plantRepo.getPlant(4);

        System.out.println("monstera 1 sprzedana: " + (sprawdzonaMonstera.isSold() ? "tak" : "nie"));
        System.out.println("monstera 4 niesprzedana: " + (!niesprzedanaMonstera.isSold() ? "tak" : "nie"));
    }
}