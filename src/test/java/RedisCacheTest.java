import com.mongodb.client.MongoDatabase;
import database.MongoDBManager;
import database.RedisCacheManager;
import database.repositories.Mongo.MongoPlantRepository;
import database.repositories.Mongo.adapters.MongoPlantRepositoryAdapter;
import database.repositories.Mongo.adapters.RedisPlantRepositoryAdapter;
import model.Plant;
import model.repositories.PlantRepository;
import model.types.Basic;
import model.types.Carnivorous;
import model.types.Succulent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.RedisClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RedisCacheTest {
    private RedisClient jedis;
    private PlantRepository repo;

    String plant_prefix = "plant:";
    String plant_all = "plants:all";
    String plant_sold = "plants:sold";
    String plant_unsold = "plants:unsold";
    String plant_type_sold = "plants:count_sold:";
    int ttl = 598;

    String id1;
    String id2;
    String id3;

    MongoDatabase db = MongoDBManager.getDatabase();

    @BeforeEach
    void setUp() {
        db.getCollection("plants").drop();
        jedis = RedisCacheManager.getRedisClient();
        repo = new RedisPlantRepositoryAdapter(new MongoPlantRepositoryAdapter(new MongoPlantRepository(db)));

        Plant p1 = new Plant(); p1.setCost(350.0); p1.setPlantType(new Carnivorous("Fly")); p1.setSold(true);
        Plant p2 = new Plant(); p2.setCost(45.0); p2.setPlantType(new Succulent(true)); p2.setSold(false);
        Plant p3 = new Plant(); p3.setCost(80.0); p3.setPlantType(new Basic()); p3.setSold(true);
        repo.addPlant(p1); id1 = p1.getId();
        repo.addPlant(p2); id2 = p2.getId();
        repo.addPlant(p3); id3 = p3.getId();

        jedis.flushDB();
    }

    @Test
    void getPlant_shouldAddToRedis() {
        assertNull(jedis.get(plant_prefix + id1));

        Plant fetched = repo.getPlant(id1);
        assertNotNull(fetched);
        assertEquals(350.0, fetched.getCost());

        String cachedJson = jedis.get(plant_prefix + id1);
        assertNotNull(cachedJson);
        assertTrue(cachedJson.contains("350.0"));
        assertTrue(jedis.ttl(plant_prefix + id1) > ttl);
    }

    @Test
    void getPlants_shouldAddToRedis() {
        assertNull(jedis.get(plant_all));

        List<Plant> fetched = repo.getPlants();
        assertNotNull(fetched);
        assertEquals(350.0, fetched.getFirst().getCost());
        assertEquals(80.0, fetched.getLast().getCost());

        String cachedJson = jedis.get(plant_all);
        assertNotNull(cachedJson);
        assertTrue(cachedJson.contains("350.0"));
        assertTrue(cachedJson.contains("80.0"));
        assertTrue(jedis.ttl(plant_all) > ttl);
    }

    @Test
    void findSold_shouldAddToRedis() {
        assertNull(jedis.get(plant_sold));

        List<Plant> fetched = repo.findAllSold();
        assertNotNull(fetched);
        assertEquals(350.0, fetched.getFirst().getCost());
        assertEquals(80.0, fetched.getLast().getCost());

        String cachedJson = jedis.get(plant_sold);
        assertNotNull(cachedJson);
        assertTrue(cachedJson.contains("350.0"));
        assertTrue(cachedJson.contains("80.0"));
        assertFalse(cachedJson.contains("45.0"));
        assertTrue(jedis.ttl(plant_sold) > ttl);
    }

    @Test
    void findUnsold_shouldAddToRedis() {
        assertNull(jedis.get(plant_unsold));

        List<Plant> fetched = repo.findAllUnsold();
        assertNotNull(fetched);
        assertEquals(45.0, fetched.getFirst().getCost());

        String cachedJson = jedis.get(plant_unsold);
        assertNotNull(cachedJson);
        assertFalse(cachedJson.contains("350.0"));
        assertFalse(cachedJson.contains("80.0"));
        assertTrue(cachedJson.contains("45.0"));
        assertTrue(jedis.ttl(plant_unsold) > ttl);
    }

    @Test
    void countSoldByType_shouldAddToRedis() {
        assertNull(jedis.get(plant_type_sold+"BASIC"));

        int count = repo.countSoldPlantsByType(Basic.class);
        assertEquals(1, count);

        String value = jedis.get(plant_type_sold+"BASIC");
        assertEquals("1", value);
        assertTrue(jedis.ttl(plant_type_sold+"BASIC") > ttl);
    }

    @Test
    void cacheInvalidationTest() {
        assertNull(jedis.get(plant_all));
        List<Plant> fetcheds = repo.getPlants();
        assertNotNull(jedis.get(plant_all));
        Plant p4 = new Plant(); p4.setCost(350.0); p4.setPlantType(new Carnivorous("Fly")); p4.setSold(true);
        repo.addPlant(p4);
        assertNull(jedis.get(plant_all));

        assertNull(jedis.get(plant_prefix + id1));
        Plant fetched = repo.getPlant(id1);
        assertNotNull(jedis.get(plant_prefix + id1));

        fetched.setSold(false);
        repo.updatePlant(fetched);
        assertNull(jedis.get(plant_prefix + id1));
    }
}
