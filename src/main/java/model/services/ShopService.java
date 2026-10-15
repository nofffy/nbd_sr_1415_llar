package model.services;

import model.Client;
import model.Plant;
import model.Sale;
import model.repositories.SaleRepository;

import java.util.List;

public class ShopService {
    private final SaleRepository saleRepo;

    public ShopService(SaleRepository saleRepo) {
        this.saleRepo = saleRepo;
    }

    public boolean newSale(Client client, List<Plant> plants) {
        if (client == null || plants == null || plants.isEmpty()) {
            return false;
        }
        for (Plant plant : plants) {
            if(plant.isSold()) {
                return false;
            }
        }
        double cost = 0.0;
        for (Plant plant : plants) {
            plant.setSold(true);
            cost += plant.getCost();
        }
        client.setMoneySpent(client.getMoneySpent() + cost);

        Sale sale = new Sale(client, plants);

        return saleRepo.addSale(sale);
    }
}