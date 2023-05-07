package user.dao;

import user.model.Portfolio;
import user.model.User;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserInMemoryDao implements UserDao {
    private final List<User> users = new CopyOnWriteArrayList<>();

    public int addUser(User user) {
        int id = users.size();
        user.setId(id);
        users.add(user);
        return id;
    }

    @Override
    public void changeBalance(int userId, long balance) {
        users.get(userId).changeBalance(balance);
    }

    @Override
    public long getBalance(int userId) {
        return users.get(userId).getBalance();
    }

    @Override
    public User getUser(int id) {
        return users.get(id);
    }

    @Override
    public void updatePortfolio(Portfolio portfolio) {
        if (portfolio.getAmount() > 0) {
            Portfolio existPortfolio =
                    users.get(portfolio.getUserId())
                            .getPortfolios()
                            .putIfAbsent(portfolio.getStockId(), portfolio);
            if (existPortfolio != null) {
                existPortfolio.changeAmount(portfolio.getAmount());
            }
        } else {
            Portfolio existPortfolio =
                    users.get(portfolio.getUserId())
                            .getPortfolios()
                            .get(portfolio.getStockId());

            if (existPortfolio != null) {
                existPortfolio.changeAmount(portfolio.getAmount());
            } else {
                throw new AssertionError("Negative amount");
            }
        }
    }

    @Override
    public List<Portfolio> getPortfoliosByUserId(int userId) {
        return users.get(userId).getPortfolios().values().stream().toList();
    }
}