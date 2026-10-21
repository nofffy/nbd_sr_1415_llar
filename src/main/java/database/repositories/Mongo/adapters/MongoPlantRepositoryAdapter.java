package database.repositories.Mongo.adapters;

import model.Plant;
import model.repositories.PlantRepository;

import java.util.List;

public class MongoPlantRepositoryAdapter implements PlantRepository {
    @Override
    public Plant getPlant(int id) {

    }

    @Override
    public boolean addPlant(Plant plant) {

    }

    @Override
    public boolean removePlant(int id) {

    }

    @Override
    public boolean updatePlant(Plant plant) {

    }

    @Override
    public boolean addPlants(List<Plant> plants) {

    }

    @Override
    public List<Plant> getPlants() {

    }

    public List<Plant> findAllSoldOrNot(boolean choice) {

    }

    @Override
    public List<Plant> findAllUnsold() {

    }

    @Override
    public List<Plant> findAllSold() {

    }

    @Override
    public int countSoldPlantsByType(Class<?> modelClass) {

    }
}
