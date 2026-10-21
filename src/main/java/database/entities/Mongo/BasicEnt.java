package database.entities.Mongo;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator("BASIC")
public class BasicEnt extends PlantEnt {
    public BasicEnt() {}
}
