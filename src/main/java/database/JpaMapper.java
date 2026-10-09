package database;

import database.entities.*;
import model.Client;
import model.Plant;
import model.PlantType;
import model.Sale;
import model.types.Basic;
import model.types.Carnivorous;
import model.types.Monstera;
import model.types.Succulent;

import java.util.ArrayList;
import java.util.List;

public class JpaMapper {
    public static SaleEnt saleObjToEnt(Sale sale) {
        if(sale == null) {
            return null;
        }
        SaleEnt ent = new SaleEnt();
        ent.setId(sale.getId());
        ClientEnt clientEnt = JpaMapper.clientObjToEnt(sale.getClient());
        ent.setClient(clientEnt);
        ent.setTimeOfSale(sale.getTimeOfSale());
        if (sale.getPlants() != null) {
            for (Plant plant : sale.getPlants()) {
                ent.addPlant(JpaMapper.plantObjToEnt(plant));
            }
        }
        return ent;
    }

    public static Sale saleEntToObj(SaleEnt saleEnt) {
        if(saleEnt == null) {
            return null;
        }
        List<Plant> list = new ArrayList<>();
        if(saleEnt.getPlants() != null) {
            for(PlantEnt plantEnt : saleEnt.getPlants()) {
                list.add(JpaMapper.plantEntToObj(plantEnt));
            }
        }
        return new Sale(
                saleEnt.getId(),
                JpaMapper.clientEntToObj(saleEnt.getClient()),
                list,
                saleEnt.getTimeOfSale()
        );
    }

    public static ClientEnt clientObjToEnt(Client client) {
        if(client==null){
            return null;
        }
        ClientEnt clientEnt = new ClientEnt();
        clientEnt.setId(client.getId());
        clientEnt.setName(client.getName());
        clientEnt.setMoneySpent(client.getMoneySpent());
        clientEnt.setMember(client.isMember());
        return clientEnt;
    }

    public static Client clientEntToObj(ClientEnt clientEnt){
        if(clientEnt==null){
            return null;
        }
        return new Client(
                clientEnt.getId(),
                clientEnt.getName(),
                clientEnt.getMoneySpent(),
                clientEnt.isMember()
        );
    }

    public static PlantEnt plantObjToEnt(Plant plant) {
        if(plant==null){
            return null;
        }
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

    public static Plant plantEntToObj(PlantEnt plantEnt) {
        if(plantEnt==null){
            return null;
        }
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
