package database.entities.Jpa;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Succulent")
public class SucculentEnt extends PlantEnt {
    private boolean spiky;

    public SucculentEnt() {}
    public SucculentEnt(int id, String name, String color, double cost, boolean sold, SaleEnt sale, boolean spiky, int version) {
        super(id, name, color, cost, sold, sale, version);
        this.spiky = spiky;
    }
    public boolean isSpiky() {
        return spiky;
    }
    public void setSpiky(boolean spiky) {
        this.spiky = spiky;
    }
}
