package user.dao;

import user.model.Portfolio;
import user.model.User;

import java.util.List;

public interface UserDao {

    int addUser(User user);

    void changeBalance(int userId, long balance);

    long getBalance(int userId);

    User getUser(int id);

    void updatePortfolio(Portfolio portfolio);

    List<Portfolio> getPortfoliosByUserId(int userId);

}