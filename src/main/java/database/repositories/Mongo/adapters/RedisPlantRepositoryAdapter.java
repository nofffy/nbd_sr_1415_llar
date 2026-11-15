package database.repositories.Mongo.adapters;

import database.RedisCacheManager;
import model.Plant;
import model.repositories.PlantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.RedisClient;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static redis.clients.jedis.params.SetParams.setParams;

public class RedisPlantRepositoryAdapter implements PlantRepository {
    private static final Logger log = LoggerFactory.getLogger(RedisPlantRepositoryAdapter.class);

    private final RedisClient jedis;
    private final PlantRepository plantRepository;
    private final ObjectMapper objectMapper;
    private final int TTLvalue = 600;

    public RedisPlantRepositoryAdapter(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
        this.jedis = RedisCacheManager.getRedisClient();
        this.objectMapper = new ObjectMapper();
    }

    //do testow
    public RedisPlantRepositoryAdapter(PlantRepository plantRepository, RedisClient jedis) {
        this.plantRepository = plantRepository;
        this.jedis = jedis;
        this.objectMapper = new ObjectMapper();
    }

    private final String plant_prefix = "plant:";
    private final String plant_all = "plants:all";
    private final String plant_sold = "plants:sold";
    private final String plant_unsold = "plants:unsold";
    private final String plant_type_sold = "plants:count_sold:";

    private void delCache(Plant plant) {
        try {
            jedis.del(plant_all, plant_sold, plant_unsold);
            if (plant != null) {
                if (plant.getId() != null) {
                    jedis.del(plant_prefix+plant.getId());
                }
                if (plant.getPlantType() != null) {
                    jedis.del(plant_type_sold + plant.getPlantType().getInfo());
                }
            }

        } catch (Exception e) {
            log.warn("Failed to delete from cache (single plant method).");
        }
    }

    private void delCache(List<Plant> plants) {
        try {
            Set<String> keysToDelete = new HashSet<>(Set.of(plant_all, plant_sold, plant_unsold));
            if (plants != null) {
                for (Plant plant : plants) {
                    if (plant != null) {
                        if (plant.getId() != null) {
                            keysToDelete.add(plant_prefix + plant.getId());
                        }
                        if (plant.getPlantType() != null) {
                            keysToDelete.add(plant_type_sold + plant.getPlantType().getInfo());
                        }
                    }
                }
            }
            jedis.del(keysToDelete.toArray(new String[0]));
        } catch (Exception e) {
            log.warn("Failed to delete from cache (plant list method).");
        }
    }

    @Override
    public Plant getPlant(String id) {
        String key = plant_prefix + id;
        try {
            String value = jedis.get(key);
            if (value != null) {
                return objectMapper.readValue(value, Plant.class);
            }
        } catch (Exception e) {
            log.warn("Redis failed while fetching plant with id {}, fetching from Mongo.", id, e);
        }
        Plant plant = plantRepository.getPlant(id);
        if (plant != null) {
            try {
                jedis.set(key, objectMapper.writeValueAsString(plant), setParams().ex(TTLvalue));
            } catch (Exception e) {
                log.warn("Failed to write to cache. Plant with id: {}", id, e);
            }
        }
        return plant;
    }

    @Override
    public boolean addPlant(Plant plant) {
        boolean success = plantRepository.addPlant(plant);
        if(success) {
            delCache(plant);
        }
        return success;
    }

    @Override
    public boolean removePlant(String id) {
        Plant plant = getPlant(id);
        boolean success = plantRepository.removePlant(id);
        if (success) {
            delCache(plant);
        }
        return success;
    }

    @Override
    public boolean updatePlant(Plant plant) {
        boolean success = plantRepository.updatePlant(plant);
        if (success) {
            delCache(plant);
        }
        return success;
    }

    @Override
    public boolean addPlants(List<Plant> plants) {
        boolean success = plantRepository.addPlants(plants);
        if (success) {
            delCache(plants);
        }
        return success;
    }

    @Override
    public List<Plant> getPlants() {
        try {
            String json = jedis.get(plant_all);
            if (json != null && !json.isEmpty()) {
                return objectMapper.readValue(json, new TypeReference<>(){});
            }
        } catch (Exception e) {
            log.warn("Redis failed while fetching plants list, fetching from Mongo.", e);
        }
        List<Plant> plants = plantRepository.getPlants();
        if (plants != null) {
            try {
                jedis.set(plant_all, objectMapper.writeValueAsString(plants), setParams().ex(TTLvalue));
            } catch (Exception e) {
                log.warn("Failed to write to cache (list of plants).", e);
            }
        }
        return plants;
    }

    @Override
    public List<Plant> findAllUnsold() {
        try {
            String json = jedis.get(plant_unsold);
            if (json != null && !json.isEmpty()) {
                return objectMapper.readValue(json, new TypeReference<>(){});
            }
        } catch (Exception e) {
            log.warn("Redis failed while fetching unsold plants list, fetching from Mongo.", e);
        }
        List<Plant> plants = plantRepository.findAllUnsold();
        if (plants != null) {
            try {
                jedis.set(plant_unsold, objectMapper.writeValueAsString(plants), setParams().ex(TTLvalue));
            } catch (Exception e) {
                log.warn("Failed to write to cache (list of unsold plants).", e);
            }
        }
        return plants;
    }

    @Override
    public List<Plant> findAllSold() {
        try {
            String json = jedis.get(plant_sold);
            if (json != null && !json.isEmpty()) {
                return objectMapper.readValue(json, new TypeReference<>(){});
            }
        } catch (Exception e) {
            log.warn("Redis failed while fetching sold plants list, fetching from Mongo.", e);
        }
        List<Plant> plants = plantRepository.findAllSold();
        if (plants != null) {
            try {
                jedis.set(plant_sold, objectMapper.writeValueAsString(plants), setParams().ex(TTLvalue));
            } catch (Exception e) {
                log.warn("Failed to write to cache (list of sold plants).", e);
            }
        }
        return plants;
    }

    @Override
    public int countSoldPlantsByType(Class<?> modelClass) {
        String name = modelClass.getSimpleName().toUpperCase();
        String key = plant_type_sold + name;
        try {
            String value = jedis.get(key);
            if (value != null) {
                return Integer.parseInt(value);
            }
        } catch (Exception e) {
            log.warn("Redis failed while fetching number of plants sold of {} class, fetching from Mongo.", name,  e);
        }
        int result = plantRepository.countSoldPlantsByType(modelClass);
        try {
            jedis.set(key, String.valueOf(result), setParams().ex(TTLvalue));
        } catch (Exception e) {
            log.warn("Failed to write to cache (number of sold plants of {} class).", name, e);
        }
        return result;
    }
}
