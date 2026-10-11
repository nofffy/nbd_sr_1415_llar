package database.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Succulent")
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
