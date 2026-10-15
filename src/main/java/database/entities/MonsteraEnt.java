package database.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Monstera")
public class MonsteraEnt extends PlantEnt {
    private int leafSize;

    public MonsteraEnt() {}
    public MonsteraEnt(int id, String name, String color, double cost, boolean sold, SaleEnt sale, int leafSize,int version) {
        super(id, name, color, cost, sold, sale, version);
        this.leafSize = leafSize;
    }
    public int getLeafSize() {
        return leafSize;
    }
    public void setLeafSize(int leafSize) {
        this.leafSize = leafSize;
    }

}
