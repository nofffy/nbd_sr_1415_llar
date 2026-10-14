package database.mappers;

import database.entities.PlantEnt;
import database.entities.SaleEnt;
import model.Plant;
import model.Sale;

import java.util.ArrayList;
import java.util.List;

public class JpaSaleMapper {
    public static SaleEnt ObjToEnt(Sale sale) {
        SaleEnt ent = new SaleEnt();
        ent.setId(sale.getId());
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
        return new Sale(
                saleEnt.getId(),
                JpaClientMapper.EntToObj(saleEnt.getClient()),
                list,
                saleEnt.getTimeOfSale()
        );
    }
}