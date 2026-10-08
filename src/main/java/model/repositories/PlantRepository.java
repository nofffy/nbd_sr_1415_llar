package model.repositories;

import model.Plant;

import java.util.List;

public interface PlantRepository {
    Plant getPlant(int id); //UUID?
    boolean addPlant(Plant plant);
    boolean removePlant(int id);
    boolean updatePlant(Plant plant);

    boolean savePlants(List<Plant> plants);
    List<Plant> getPlants();

    List<Plant> findAllUnsold();
    List<Plant> findAllSold();
}
