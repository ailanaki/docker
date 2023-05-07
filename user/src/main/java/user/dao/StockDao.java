package user.dao;

import java.io.IOException;
import java.net.URISyntaxException;

public interface StockDao {

    long getPrice(long stockId);

    long buy(int stockId, long amount) throws IOException, URISyntaxException, InterruptedException;

    long sell(int stockId, long amount) throws IOException, URISyntaxException, InterruptedException;

}