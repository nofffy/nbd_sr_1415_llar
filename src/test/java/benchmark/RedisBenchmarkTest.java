package benchmark;

import com.mongodb.client.MongoDatabase;
import database.MongoDBManager;
import database.RedisCacheManager;
import database.repositories.Mongo.MongoPlantRepository;
import database.repositories.Mongo.adapters.MongoPlantRepositoryAdapter;
import database.repositories.Mongo.adapters.RedisPlantRepositoryAdapter;
import model.Plant;
import model.repositories.PlantRepository;
import model.types.Basic;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 4, time = 1)
@Measurement(iterations = 2, time = 1)
public class RedisBenchmarkTest {

    private PlantRepository mongoRepo;
    private PlantRepository redisRepo;
    private Plant samplePlant;
    private String samplePlantId;

    @Setup(Level.Trial)
    public void setup() {
        MongoDatabase db = MongoDBManager.getDatabase();
        db.getCollection("plants").drop();
        RedisCacheManager.getRedisClient().flushDB();

        mongoRepo = new MongoPlantRepositoryAdapter(new MongoPlantRepository(db));
        redisRepo = new RedisPlantRepositoryAdapter(mongoRepo);

        List<Plant> initialPlants = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            Plant p = new Plant();
            p.setName("p" + i);
            p.setColor("green");
            p.setCost(50.0 + i);
            p.setSold(i % 2 == 0);
            p.setPlantType(new Basic());
            initialPlants.add(p);
        }
        mongoRepo.addPlants(initialPlants);

        samplePlant = mongoRepo.getPlants().getFirst();
        samplePlantId = samplePlant.getId();

        redisRepo.getPlant(samplePlantId);
        redisRepo.getPlants();
    }

    @Benchmark
    public Plant readSingleFromMongo() {
        return mongoRepo.getPlant(samplePlantId);
    }

    @Benchmark
    public Plant readSingleFromRedis_CacheHit() {
        return redisRepo.getPlant(samplePlantId);
    }

    @Benchmark
    public List<Plant> readListFromMongo() {
        return mongoRepo.getPlants();
    }

    @Benchmark
    public List<Plant> readListFromRedis_CacheHit() {
        return redisRepo.getPlants();
    }

    @State(Scope.Thread)
    public static class CacheMissState {
        @Setup(Level.Invocation)
        public void invalidateCache(RedisBenchmarkTest parent) {
            parent.redisRepo.updatePlant(parent.samplePlant);
        }
    }

    @Benchmark
    public Plant readSingleFromRedis_CacheMiss(CacheMissState state) {
        return redisRepo.getPlant(samplePlantId);
    }

    @Benchmark
    public List<Plant> readListFromRedis_CacheMiss(CacheMissState state) {
        return redisRepo.getPlants();
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(RedisBenchmarkTest.class.getSimpleName())
                .result("RedisBenchmark.txt")
                .resultFormat(ResultFormatType.TEXT)
                .build();
        new Runner(opt).run();
    }
}