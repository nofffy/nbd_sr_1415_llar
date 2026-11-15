package database.mappers.Mongo;

import database.entities.Mongo.PlantEnt;
import database.entities.Mongo.PlantTypeEnt;
import model.Plant;
import model.PlantType;
import model.types.Basic;
import model.types.Carnivorous;
import model.types.Monstera;
import model.types.Succulent;
import org.bson.types.ObjectId;

public class MongoPlantMapper {
    public static PlantEnt ObjToEnt(Plant plant) {
        if (plant == null) {
            return null;
        }

        PlantEnt ent = new PlantEnt();
        if (plant.getId() != null && ObjectId.isValid(plant.getId())) {
            ent.setId(new ObjectId(plant.getId()));
        }
        ent.setName(plant.getName());
        ent.setColor(plant.getColor());
        ent.setCost(plant.getCost());
        ent.setSold(plant.isSold());
        ent.setPlantType(typeToEnt(plant.getPlantType()));

        return ent;
    }

    public static Plant EntToObj(PlantEnt plantEnt) {
        if (plantEnt == null) {
            return null;
        }

        String id = plantEnt.getId() != null ? plantEnt.getId().toHexString() : null;
        return new Plant(
                id,
                plantEnt.getName(),
                plantEnt.getColor(),
                plantEnt.getCost(),
                plantEnt.isSold(),
                typeToModel(plantEnt.getPlantType())
        );
    }

    private static PlantTypeEnt typeToEnt(PlantType type) {
        if (type == null) {
            return null;
        }

        PlantTypeEnt ent = new PlantTypeEnt();
        ent.setPlantType(type.getInfo());
        if (type instanceof Succulent) {
            ent.setSpiky(((Succulent) type).isSpiky());
        } else if (type instanceof Monstera) {
            ent.setLeafSize(((Monstera) type).getLeafSize());
        } else if (type instanceof Carnivorous) {
            ent.setFavouriteFood(((Carnivorous) type).getFavouriteFood());
        } else if (type instanceof Basic) {
            ;
        } else {
            throw new IllegalArgumentException("Nieznany typ");
        }

        return ent;
    }

    private static PlantType typeToModel(PlantTypeEnt ent) {
        if (ent == null || ent.getPlantType() == null) {
            return null;
        }

        if (ent.getPlantType().equals("SUCCULENT")) {
            if (ent.getSpiky() != null) {
                return new Succulent(ent.getSpiky());
            } throw new IllegalArgumentException("Zle dane Succulent");
        } else if (ent.getPlantType().equals("MONSTERA")) {
            if (ent.getLeafSize() != null) {
                return new Monstera(ent.getLeafSize());
            } throw new IllegalArgumentException("Zle dane Monstera");
        } else if (ent.getPlantType().equals("CARNIVOROUS")) {
            if (ent.getFavouriteFood() != null) {
                return new Carnivorous(ent.getFavouriteFood());
            } throw new IllegalArgumentException("Zle dane Carnivorous");
        } else if (ent.getPlantType().equals("BASIC")) {
            return new Basic();
        } else {
            throw new IllegalArgumentException("Nieznany typ");
        }
    }
}
