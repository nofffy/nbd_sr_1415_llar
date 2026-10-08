package model.repositories;

import database.entities.SaleEnt;
import model.Sale;

import java.util.List;

public interface SaleRepository {
    Sale getSale(int id);
    boolean addSale(Sale sale);
    boolean removeSale(int id);

    List<Sale> getSales();
    int getSalesCount();
}
