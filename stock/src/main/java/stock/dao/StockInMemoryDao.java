package stock.dao;


import stock.model.Stock;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StockInMemoryDao implements StockDao {
    private final List<Stock> stocks = new CopyOnWriteArrayList<>();

    @Override
    public int addStock(Stock stock) {
        int id = stocks.size();
        stock.setId(id);
        stocks.add(stock);
        return id;
    }

    @Override
    public void changeAmount(int stockId, long amount) {
        stocks.get(stockId).changeAmount(amount);
    }

    @Override
    public void setPrice(int stockId, long newPrice) {
        stocks.get(stockId).setPrice(newPrice);
    }

    @Override
    public Stock getStock(int stockId) {
        return stocks.get(stockId);
    }
}