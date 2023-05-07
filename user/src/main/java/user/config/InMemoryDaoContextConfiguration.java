package user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import user.dao.UserDao;
import user.dao.UserInMemoryDao;

@Configuration
public class InMemoryDaoContextConfiguration {
    @Bean
    public UserDao userController() {
        return new UserInMemoryDao();
    }
}