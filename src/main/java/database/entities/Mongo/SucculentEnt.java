package database.entities.Mongo;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator("SUCCULENT")
public class SucculentEnt extends PlantEnt {
    private boolean spiky;

    public SucculentEnt() {}

    public boolean isSpiky() {
        return spiky;
    }

    public void setSpiky(boolean spiky) {
        this.spiky = spiky;
    }
}
