package database.mappers;

import database.entities.*;
import model.Plant;
import model.PlantType;
import model.types.Basic;
import model.types.Carnivorous;
import model.types.Monstera;
import model.types.Succulent;

public class JpaPlantMapper {
    public static PlantEnt ObjToEnt(Plant plant) {
        PlantEnt ent;
        PlantType type = plant.getPlantType();
        if(type instanceof Basic) {
            ent = new BasicEnt();
        }
        else if(type instanceof Carnivorous) {
            Carnivorous carnivorous = (Carnivorous) type;

            CarnivorousEnt ent1 = new CarnivorousEnt();
            ent1.setFavouriteFood(carnivorous.getFavouriteFood());
            ent = ent1;
        }
        else if(type instanceof Monstera) {
            Monstera monstera = (Monstera) type;

            MonsteraEnt ent1 = new MonsteraEnt();
            ent1.setLeafSize(monstera.getLeafSize());
            ent = ent1;
        }
        else if(type instanceof Succulent) {
            Succulent succulent = (Succulent) type;

            SucculentEnt ent1 = new SucculentEnt();
            ent1.setSpiky(succulent.isSpiky());
            ent = ent1;
        }
        else {
            return null;
        }
        ent.setId(plant.getId());
        ent.setName(plant.getName());
        ent.setColor(plant.getColor());
        ent.setSold(plant.isSold());
        ent.setCost(plant.getCost());
        return ent;
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
        return new Plant(
                plantEnt.getId(),
                plantEnt.getName(),
                plantEnt.getColor(),
                plantEnt.getCost(),
                plantEnt.isSold(),
                type
        );
    }
}
