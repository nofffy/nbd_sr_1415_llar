package database.entities.Jpa;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Basic")
public class BasicEnt extends PlantEnt {
    public BasicEnt() {}
    public BasicEnt(int id, String name, String color, double cost, boolean sold, SaleEnt sale, int version) {
        super(id, name, color, cost, sold, sale, version);
    }
}
