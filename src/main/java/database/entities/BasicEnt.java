package database.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Basic")
public class BasicEnt extends PlantEnt {
    public BasicEnt() {}
}
