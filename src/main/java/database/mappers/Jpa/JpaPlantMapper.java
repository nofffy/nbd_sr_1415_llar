package database.mappers.Jpa;

import database.entities.Jpa.*;
import model.Plant;
import model.PlantType;
import model.types.Basic;
import model.types.Carnivorous;
import model.types.Monstera;
import model.types.Succulent;

public class JpaPlantMapper {
    public static PlantEnt ObjToEnt(Plant plant) {
        if (plant == null) return null;

        int id = 0;
        if (plant.getId() != null && !plant.getId().isEmpty()) {
            id = Integer.parseInt(plant.getId());
        }
        String name = plant.getName();
        String color = plant.getColor();
        double cost = plant.getCost();
        boolean sold = plant.isSold();
        SaleEnt sale = new SaleEnt();
        int version = plant.getVersion();

        PlantType type = plant.getPlantType();

        if (type instanceof Basic) {
            return new BasicEnt(id, name, color, cost, sold, sale, version);
        }
        else if (type instanceof Carnivorous carnivorous) {
            return new CarnivorousEnt(
                    id, name, color, cost, sold, sale,
                    carnivorous.getFavouriteFood(),
                    version
            );
        }
        else if (type instanceof Monstera monstera) {
            return new MonsteraEnt(
                    id, name, color, cost, sold, sale,
                    monstera.getLeafSize(),
                    version
            );
        }
        else if (type instanceof Succulent succulent) {
            return new SucculentEnt(
                    id, name, color, cost, sold, sale,
                    succulent.isSpiky(),
                    version
            );
        }

        return null;
    }

    public static Plant EntToObj(PlantEnt plantEnt) {
        PlantType type;
        if(plantEnt instanceof BasicEnt) {
            type = new Basic();
        }
        else if(plantEnt instanceof CarnivorousEnt) {
            type = new Carnivorous(((CarnivorousEnt) plantEnt).getFavouriteFood());
        }
        else if(plantEnt instanceof MonsteraEnt) {
            type = new Monstera(((MonsteraEnt) plantEnt).getLeafSize());
        }
        else if(plantEnt instanceof SucculentEnt) {
            type = new Succulent(((SucculentEnt) plantEnt).isSpiky());
        }
        else {
            return null;
        }
        String idString = String.valueOf(plantEnt.getId());
        return new Plant(
                idString,
                plantEnt.getName(),
                plantEnt.getColor(),
                plantEnt.getCost(),
                plantEnt.isSold(),
                type,
                plantEnt.getVersion()
        );
    }
}
