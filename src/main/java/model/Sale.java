package model;

import java.time.LocalDateTime;
import java.util.List;

public class Sale {
    private int id;
    private Client client;
    private List<Plant> plants;
    private LocalDateTime timeOfSale;

    public Sale(int id, Client client, List<Plant> plants) {
        this.id = id;
        this.client = client;
        this.plants = plants;
        this.timeOfSale = LocalDateTime.now();
    }

    public Sale(Client client, List<Plant> plants) {
        this.id = 0;
        this.client = client;
        this.plants = plants;
        this.timeOfSale = LocalDateTime.now();
    }

    public Sale(int id, Client client, List<Plant> plants, LocalDateTime timeOfSale) {
        this.id = id;
        this.client = client;
        this.plants = plants;
        this.timeOfSale = timeOfSale;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
    }

    public LocalDateTime getTimeOfSale() {
        return timeOfSale;
    }

    public void setTimeOfSale(LocalDateTime timeOfSale) {
        this.timeOfSale = timeOfSale;
    }
}
