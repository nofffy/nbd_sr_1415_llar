package model.repositories;

import model.Plant;

import java.util.List;

public interface PlantRepository {
    Plant getPlant(String id); //UUID?
    boolean addPlant(Plant plant);
    boolean removePlant(String id);
    boolean updatePlant(Plant plant);

    boolean addPlants(List<Plant> plants);
    List<Plant> getPlants();

    List<Plant> findAllUnsold();
    List<Plant> findAllSold();
    int countSoldPlantsByType(Class<?> plantType);
}
