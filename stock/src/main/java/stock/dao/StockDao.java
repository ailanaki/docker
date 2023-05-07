package stock.dao;


import stock.model.Stock;

public interface StockDao {

    int addStock(Stock stock);

    void changeAmount(int stockId, long amount);

    void setPrice(int stockId, long newPrice);

    Stock getStock(int id);

}