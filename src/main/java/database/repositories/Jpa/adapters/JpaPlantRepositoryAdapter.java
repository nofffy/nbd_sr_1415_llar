package database.repositories.Jpa.adapters;

import database.entities.Jpa.*;
import database.mappers.Jpa.JpaPlantMapper;
import database.repositories.Jpa.JpaPlantRepository;
import model.Plant;
import model.repositories.PlantRepository;
import model.types.*;

import java.util.ArrayList;
import java.util.List;

public class JpaPlantRepositoryAdapter implements PlantRepository {
    private JpaPlantRepository repo;

    public JpaPlantRepositoryAdapter() {
        this.repo = new JpaPlantRepository();
    }

    @Override
    public Plant getPlant(String id) {
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
            plant.setId(String.valueOf(ent.getId()));
        }
        return result;
    }

    @Override
    public boolean removePlant(String id) {
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

    @Override
    public int countSoldPlantsByType(Class<?> modelClass) {
        Class<? extends PlantEnt> entClass = null;

        if (modelClass == Basic.class) {
            entClass = BasicEnt.class;
        } else if (modelClass == Monstera.class) {
            entClass = MonsteraEnt.class;
        } else if (modelClass == Carnivorous.class) {
            entClass = CarnivorousEnt.class;
        } else if (modelClass == Succulent.class) {
            entClass = SucculentEnt.class;
        }

        if (entClass == null) {
            return 0;
        }

        return repo.countSoldPlantsByType(entClass);
    }
}