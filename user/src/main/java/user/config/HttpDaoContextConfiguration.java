package user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import user.dao.HttpStockDao;

@Configuration
public class HttpDaoContextConfiguration {
    @Bean
    public HttpStockDao stockInMemoryDao() {
        return new HttpStockDao("http://localhost:8080");
    }

}