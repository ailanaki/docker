package stock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stock.dao.StockDao;
import stock.dao.StockInMemoryDao;

@Configuration
public class InMemoryDaoContextConfiguration {
    @Bean
    public StockDao stockInMemoryDao() {
        return new StockInMemoryDao();
    }
}