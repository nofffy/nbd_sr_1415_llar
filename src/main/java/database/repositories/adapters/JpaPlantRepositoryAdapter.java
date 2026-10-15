package database.repositories.adapters;

import database.entities.PlantEnt;
import database.mappers.JpaPlantMapper;
import database.repositories.JpaPlantRepository;
import model.Plant;
import model.repositories.PlantRepository;

import java.util.ArrayList;
import java.util.List;

public class JpaPlantRepositoryAdapter implements PlantRepository {
    private JpaPlantRepository repo;

    public JpaPlantRepositoryAdapter() {
        this.repo = new JpaPlantRepository();
    }

    @Override
    public Plant getPlant(int id) {
        PlantEnt ent = repo.getPlant(id);
        if (ent == null) {
            return null;
        }
        return JpaPlantMapper.EntToObj(ent);
    }

    @Override
    public boolean addPlant(Plant plant) {
        PlantEnt ent = JpaPlantMapper.ObjToEnt(plant);
        if (ent == null) {
            return false;
        }
        boolean result = repo.addPlant(ent);
        if (result) {
            plant.setId(ent.getId());
        }
        return result;
    }

    @Override
    public boolean removePlant(int id) {
        return repo.removePlant(id);
    }

    @Override
    public boolean updatePlant(Plant plant) {
        PlantEnt ent = JpaPlantMapper.ObjToEnt(plant);
        return repo.updatePlant(ent);
    }

    @Override
    public boolean addPlants(List<Plant> plants) {
        List<PlantEnt> ents = new ArrayList<>();
        for (Plant plant : plants) {
            ents.add(JpaPlantMapper.ObjToEnt(plant));
        }
        return repo.addPlants(ents);
    }

    @Override
    public List<Plant> getPlants() {
        List<PlantEnt> list = repo.getPlants();
        List<Plant> plants = new ArrayList<>();
        for (PlantEnt ent : list) {
            plants.add(JpaPlantMapper.EntToObj(ent));
        }
        return plants;
    }

    public List<Plant> findAllSoldOrNot(boolean choice) {
        List<PlantEnt> list = repo.findAllSoldOrNot(choice);
        List<Plant> plants = new ArrayList<>();
        for (PlantEnt ent : list) {
            plants.add(JpaPlantMapper.EntToObj(ent));
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
}