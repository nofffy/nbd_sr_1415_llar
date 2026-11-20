package model.services;

import database.repositories.Mongo.adapters.RedisPlantRepositoryAdapter;
import model.Client;
import model.Plant;
import model.Sale;
import model.repositories.PlantRepository;
import model.repositories.SaleRepository;

import java.util.List;

public class ShopService {
    private final SaleRepository saleRepo;
    private final RedisPlantRepositoryAdapter plantRepo;

    public ShopService(SaleRepository saleRepo, RedisPlantRepositoryAdapter plantRepo) {
        this.saleRepo = saleRepo;
        this.plantRepo = plantRepo;
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

        boolean validSale = saleRepo.addSale(sale);
        if (validSale) {
            plantRepo.delCache(plants);
        }
        return validSale;
    }
}