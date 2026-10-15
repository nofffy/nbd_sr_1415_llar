package model.repositories;

import model.Sale;

import java.util.List;

public interface SaleRepository {
    Sale getSale(int id);
    boolean addSale(Sale sale);
    boolean removeSale(int id);

    List<Sale> getClientSales(int clientId);
    int sizeSales();
}
