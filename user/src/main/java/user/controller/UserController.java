package user.controller;


import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import user.dao.StockDao;
import user.dao.UserDao;
import user.model.Portfolio;
import user.model.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RestController
public class UserController {
    private final UserDao userDao;
    private final StockDao stockDao;

    public UserController(UserDao userDao, StockDao stockDao) {
        this.userDao = userDao;
        this.stockDao = stockDao;
    }

    @RequestMapping(value = "/add_user", method = RequestMethod.POST)
    public String addUser(
            @RequestParam String name,
            @RequestParam long balance
    ) {
        User user = new User(name, balance);

        int id = userDao.addUser(user);

        return String.valueOf(id);
    }

    @GetMapping("/user")
    public String getUser(@RequestParam int userId) {
        User user;
        try {
            user = userDao.getUser(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        Gson g = new Gson();
        return g.toJson(user);
    }

    @GetMapping("/get_balance")
    public String getBalance(@RequestParam int userId) {
        return String.valueOf(userDao.getBalance(userId));
    }

    @RequestMapping(value = "/add_balance", method = RequestMethod.POST)
    public String changeBalance(
            @RequestParam int userId,
            @RequestParam long amount
    ) {

        try {
            userDao.changeBalance(userId, amount);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return "";
    }

    @GetMapping("/get_user_portfolios")
    public String getUserPortfolios(@RequestParam int userId) {
        List<Portfolio> portfolios = userDao.getPortfoliosByUserId(userId);
        for (Portfolio portfolio : portfolios) {
            portfolio.setPrice(stockDao.getPrice(portfolio.getStockId()));
        }

        Gson g = new Gson();
        return g.toJson(portfolios);
    }

    @GetMapping("/get_user_total_balance")
    public String getUserTotalBalance(@RequestParam int userId) {
        long totalBalance = userDao.getBalance(userId);
        List<Portfolio> portfolios = userDao.getPortfoliosByUserId(userId);

        for (Portfolio portfolio : portfolios) {
            portfolio.setPrice(stockDao.getPrice(portfolio.getStockId()));
            totalBalance += portfolio.getAmount() * portfolio.getPrice();
        }

        return String.valueOf(totalBalance);
    }

    @RequestMapping(value = "/buy", method = RequestMethod.POST)
    public String buy(
            @RequestParam int userId,
            @RequestParam int stockId,
            @RequestParam long amount
    ) throws IOException, URISyntaxException, InterruptedException {
        long price;
        try {
            price = stockDao.buy(stockId, amount);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        try {
            userDao.changeBalance(userId, -price);
            userDao.updatePortfolio(new Portfolio(userId, stockId, amount));
        } catch (Error e) {
            stockDao.sell(stockId, amount);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return "";
    }

    @RequestMapping(value = "/sell", method = RequestMethod.POST)
    public String sell(
            @RequestParam int userId,
            @RequestParam int stockId,
            @RequestParam long amount
    ) throws IOException, URISyntaxException, InterruptedException {
        try {
            userDao.updatePortfolio(new Portfolio(userId, stockId, -amount));
        } catch (Error e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        long price = stockDao.sell(stockId, amount);
        userDao.changeBalance(userId, price);

        return "";
    }

}