package model.repositories;

import model.Sale;

import java.util.List;

public interface SaleRepository {
    Sale getSale(String id);
    boolean addSale(Sale sale);
    boolean removeSale(String id);

    List<Sale> getClientSales(String clientId);
    int sizeSales();
}
