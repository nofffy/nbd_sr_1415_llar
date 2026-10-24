package database.repositories.Mongo.adapters;

import database.entities.Mongo.PlantEnt;
import database.mappers.Mongo.MongoPlantMapper;
import database.repositories.Mongo.MongoPlantRepository;
import model.Plant;
import model.repositories.PlantRepository;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class MongoPlantRepositoryAdapter implements PlantRepository {
    private MongoPlantRepository repo;

    public MongoPlantRepositoryAdapter(MongoPlantRepository repo) {
        this.repo = repo;
    }

    @Override
    public Plant getPlant(String id) {
        if (!ObjectId.isValid(id)) {
            return null;
        }
        PlantEnt plantEnt = repo.getPlant(new ObjectId(id));
        if (plantEnt == null) {
            return null;
        }
        return MongoPlantMapper.EntToObj(plantEnt);
    }

    @Override
    public boolean addPlant(Plant plant) {
        PlantEnt plantEnt = MongoPlantMapper.ObjToEnt(plant);
        if (plantEnt == null) {
            return false;
        }
        boolean result = repo.addPlant(plantEnt);
        if (result) {
            plant.setId(plantEnt.getId().toHexString());
        }
        return result;
    }

    @Override
    public boolean removePlant(String id) { return repo.removePlant(new ObjectId(id)); }

    @Override
    public boolean updatePlant(Plant plant) {
        PlantEnt plantEnt = MongoPlantMapper.ObjToEnt(plant);
        return repo.updatePlant(plantEnt);
    }

    @Override
    public boolean addPlants(List<Plant> plants) {
        List<PlantEnt> plantEnts = new ArrayList<>();
        for (Plant plant : plants) {
            plantEnts.add(MongoPlantMapper.ObjToEnt(plant));
        }
        return repo.addPlants(plantEnts);
    }

    @Override
    public List<Plant> getPlants() {
        List<PlantEnt> plantEnts = repo.getPlants();
        List<Plant> plants = new ArrayList<>();
        for (PlantEnt plantEnt: plantEnts) {
            plants.add(MongoPlantMapper.EntToObj(plantEnt));
        }
        return plants;
    }

    public List<Plant> findAllSoldOrNot(boolean choice) {
        List<PlantEnt> plantEnts = repo.findAllSoldOrNot(choice);
        List<Plant> plants = new ArrayList<>();
        for (PlantEnt plantEnt: plantEnts) {
            plants.add(MongoPlantMapper.EntToObj(plantEnt));
        }
        return plants;
    }

    @Override
    public List<Plant> findAllUnsold() {
        return findAllSoldOrNot(false);
    }

    @Override
    public List<Plant> findAllSold() {
        return findAllSoldOrNot(true);
    }

    @Override
    public int countSoldPlantsByType(Class<?> modelClass) {
        return repo.countSoldPlantsByType(modelClass.getSimpleName());
    }
}
