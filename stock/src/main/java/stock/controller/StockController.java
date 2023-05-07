package stock.controller;

import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import stock.dao.StockDao;
import stock.model.Stock;

@RestController
public class StockController {
    private final StockDao stockDao;

    public StockController(StockDao stockDao) {
        this.stockDao = stockDao;
    }

    @RequestMapping(value = "/add_company", method = RequestMethod.POST)
    public String addCompany(
            @RequestParam String companyName,
            @RequestParam long price,
            @RequestParam long amount
    ) {
        Stock stock = new Stock(companyName, price, amount);

        int id = stockDao.addStock(stock);

        return String.valueOf(id);
    }

    @RequestMapping(value = "/change_amount", method = RequestMethod.POST)
    public String changeAmount(
            @RequestParam int stockId,
            @RequestParam long amount
    ) {
        try {
            stockDao.changeAmount(stockId, amount);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return "";
    }

    @GetMapping("/stock")
    public String getStock(@RequestParam int stockId) {
        Stock stock;
        try {
            stock = stockDao.getStock(stockId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        Gson g = new Gson();
        return g.toJson(stock);
    }

    @RequestMapping(value = "/buy", method = RequestMethod.POST)
    @ResponseBody
    public String buy(
            @RequestParam int stockId,
            @RequestParam long amount
    ) {
        long price;
        try {
            price = stockDao.getStock(stockId).getPrice() * amount;
            stockDao.changeAmount(stockId, -amount);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return String.valueOf(price);
    }

    @RequestMapping(value = "/sell", method = RequestMethod.POST)
    @ResponseBody
    public String sell(
            @RequestParam int stockId,
            @RequestParam long amount
    ) {
        long price;
        try {
            price = stockDao.getStock(stockId).getPrice() * amount;
            stockDao.changeAmount(stockId, amount);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return String.valueOf(price);
    }

    @RequestMapping(value = "/set_price", method = RequestMethod.POST)
    public String setPrice(
            @RequestParam int stockId,
            @RequestParam long newPrice
    ) {
        stockDao.setPrice(stockId, newPrice);
        return "";
    }

}