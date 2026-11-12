package database;

import redis.clients.jedis.RedisClient;
import java.io.InputStream;
import java.util.Properties;

public class RedisCacheManager {
    private static final RedisClient jedis;
    static String url;
    static {
        try (InputStream input = RedisCacheManager.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                url = prop.getProperty("redis.url");
            }
        } catch (Exception e) {
            System.err.println("Error loading redis connection: " + e.getMessage());
        }
        jedis = RedisClient.create(url);
    }

    public static RedisClient getRedisClient() {
        return jedis;
    }
}
