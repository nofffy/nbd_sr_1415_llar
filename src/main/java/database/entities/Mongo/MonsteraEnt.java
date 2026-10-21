package database.entities.Mongo;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator("MONSTERA")
public class MonsteraEnt extends PlantEnt {
    private int leafSize;

    public MonsteraEnt() {}

    public int getLeafSize() {
        return leafSize;
    }

    public void setLeafSize(int leafSize) {
        this.leafSize = leafSize;
    }
}
