package database.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Monstera")
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
