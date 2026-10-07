package database.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SaleEnt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientEnt client;

    private LocalDateTime timeOfSale;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private List<PlantEnt> plants = new ArrayList<>();

    public void addPlant(PlantEnt plant) {
        plants.add(plant);
        plant.setSale(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ClientEnt getClient() {
        return client;
    }

    public void setClient(ClientEnt client) {
        this.client = client;
    }

    public LocalDateTime getTimeOfSale() {
        return timeOfSale;
    }

    public void setTimeOfSale(LocalDateTime timeOfSale) {
        this.timeOfSale = timeOfSale;
    }

    public List<PlantEnt> getPlants() {
        return plants;
    }

    public void setPlants(List<PlantEnt> plants) {
        this.plants = plants;
    }
}
