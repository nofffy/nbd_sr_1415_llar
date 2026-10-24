package database.mappers.Mongo;

import database.entities.Mongo.ClientEnt;
import database.entities.Mongo.SaleEnt;
import model.Client;
import model.Sale;
import org.bson.types.ObjectId;

import java.util.stream.Collectors;

public class MongoSaleMapper {
    public static SaleEnt ObjToEnt(Sale sale) {
        if (sale == null) return null;

        SaleEnt ent = new SaleEnt();
        if (sale.getId() != null && !sale.getId().isEmpty()) {
            ent.setId(new ObjectId(sale.getId()));
        }
        if (sale.getClient().getId() != null && !sale.getClient().getId().isEmpty()) {
            ent.setClientId(new ObjectId(sale.getClient().getId()));
        }
        ent.setTimeOfSale(sale.getTimeOfSale());
        if (sale.getPlants() != null) {
            ent.setPlants(sale.getPlants().stream()
                    .map(MongoPlantMapper::ObjToEnt)
                    .collect(Collectors.toList()));
        }

        return ent;
    }

    public static Sale EntToObj(SaleEnt saleEnt, Client client) {
        if (saleEnt == null) return null;

        String id = saleEnt.getId() != null ? saleEnt.getId().toHexString() : null;
        return new Sale(
                id,
                client,
                saleEnt.getPlants().stream()
                        .map(MongoPlantMapper::EntToObj)
                        .collect(Collectors.toList()),
                saleEnt.getTimeOfSale()
        );
    }
}
