package database.mappers.Jpa;

import database.entities.Jpa.PlantEnt;
import database.entities.Jpa.SaleEnt;
import model.Plant;
import model.Sale;

import java.util.ArrayList;
import java.util.List;

public class JpaSaleMapper {
    public static SaleEnt ObjToEnt(Sale sale) {
        SaleEnt ent = new SaleEnt();
        int id = 0;
        if (sale.getId() != null && !sale.getId().isEmpty()) {
            id = Integer.parseInt(sale.getId());
        }
        ent.setId(id);
        ent.setClient(JpaClientMapper.ObjToEnt(sale.getClient()));
        ent.setTimeOfSale(sale.getTimeOfSale());
        if (sale.getPlants() != null) {
            for (Plant plant : sale.getPlants()) {
                ent.addPlant(JpaPlantMapper.ObjToEnt(plant));
            }
        }
        return ent;
    }

    public static Sale EntToObj(SaleEnt saleEnt) {
        List<Plant> list = new ArrayList<>();
        if (saleEnt.getPlants() != null) {
            for (PlantEnt plantEnt : saleEnt.getPlants()) {
                list.add(JpaPlantMapper.EntToObj(plantEnt));
            }
        }
        String idString = String.valueOf(saleEnt.getId());
        return new Sale(
                idString,
                JpaClientMapper.EntToObj(saleEnt.getClient()),
                list,
                saleEnt.getTimeOfSale()
        );
    }
}