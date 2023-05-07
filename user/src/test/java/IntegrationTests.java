import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import stock.model.Stock;
import user.config.HttpDaoContextConfiguration;
import user.controller.UserController;
import user.dao.HttpStockDao;
import user.dao.UserInMemoryDao;
import user.model.Portfolio;
import user.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.Assert.*;
public class IntegrationTests {
    private static final int PORT = 8080;
    private static final String URL = "http://localhost:" + PORT + "/";

    @ClassRule
    public static GenericContainer simpleWebServer
            = new FixedHostPortGenericContainer("stock:1.0-SNAPSHOT")
            .withFixedExposedPort(PORT, PORT)
            .withExposedPorts(PORT);

    private UserController controller;

    @Before
    public void initController() {
        HttpStockDao httpStockDao = new HttpDaoContextConfiguration().stockInMemoryDao();
        controller = new UserController(new UserInMemoryDao(), httpStockDao);
    }

    @Test
    public void addUserTest() {
        String name = "name";
        long balance = 100;
        int uid = Integer.parseInt(controller.addUser(name, balance));

        User expectedUser = new User(uid, name, balance);

        String rawUser = controller.getUser(uid);
        User actualUser = new Gson().fromJson(rawUser, User.class);

        assertEquals(expectedUser, actualUser);


        assertEquals(String.valueOf(balance), controller.getBalance(uid));

        List<Portfolio> actualPortfolios = new Gson().fromJson(controller.getUserPortfolios(uid), List.class);

        assertEquals(List.of(), actualPortfolios);

        assertEquals(String.valueOf(balance), controller.getUserTotalBalance(uid));
    }

    @Test
    public void changeUserBalanceTest() {
        String name = "name";
        long balance = 100;
        int uid = Integer.parseInt(controller.addUser(name, balance));

        long addBalance = 400;

        controller.changeBalance(uid, addBalance);

        User expectedUser = new User(uid, name, balance + addBalance);

        String rawUser = controller.getUser(uid);
        User actualUser = new Gson().fromJson(rawUser, User.class);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void addCompanyTest() throws IOException, URISyntaxException, InterruptedException {
        String companyName = "company";
        long price = 300;
        long amount = 200;

        int sid = Integer.parseInt(post(addCompanyUri(companyName, price, amount)));

        Stock expectedStock = new Stock(sid, companyName, price, amount);

        String rawStock = get(getStockUri(sid));

        Stock actualStock = new Gson().fromJson(rawStock, Stock.class);

        assertEquals(expectedStock, actualStock);
    }

    @Test
    public void changeStockAmountTest() throws IOException, URISyntaxException, InterruptedException {
        String companyName = "company";
        long price = 300;
        long amount = 200;

        int sid = Integer.parseInt(post(addCompanyUri(companyName, price, amount)));

        long addAmount = 300;
        post(URL + "change_amount?stockId=" + sid + "&amount=" +  addAmount);

        Stock expectedStock = new Stock(sid, companyName, price, amount + addAmount);

        String rawStock = get(getStockUri(sid));
        Stock actualStock = new Gson().fromJson(rawStock, Stock.class);

        assertEquals(expectedStock, actualStock);
    }


    @Test
    public void buyStockTest() throws IOException, URISyntaxException, InterruptedException {
        String name = "name";
        long balance = 110;
        int uid = Integer.parseInt(controller.addUser(name, balance));
        User expectedUser = new User(uid, name, balance);

        String companyName = "company";
        long price = 10;
        long amount = 200;
        int sid = Integer.parseInt(post(addCompanyUri(companyName, price, amount)));
        Stock expectedStock = new Stock(sid, companyName, price, amount);

        long boughtAmount = 10;
        controller.buy(uid, sid, boughtAmount);

        expectedUser.changeBalance(-boughtAmount * price);
        String rawUser = controller.getUser(uid);
        User actualUser = new Gson().fromJson(rawUser, User.class);
        assertEquals(expectedUser, actualUser);


        expectedStock.changeAmount(-boughtAmount);
        String rawStock = get(getStockUri(sid));
        Stock actualStock = new Gson().fromJson(rawStock, Stock.class);
        assertEquals(expectedStock, actualStock);

        assertEquals(balance, Long.parseLong(controller.getUserTotalBalance(uid)));

        List<Portfolio> expectedPortfolios = List.of(new Portfolio(uid, sid, boughtAmount, price));
        String rawPortfolios = controller.getUserPortfolios(uid);
        List<Portfolio> actualPortfolios =
                new Gson().fromJson(rawPortfolios, new TypeToken<List<Portfolio>>() {
                }.getType());

        assertEquals(expectedPortfolios, actualPortfolios);
    }

    @Test
    public void buyOverAmountStocksTest() throws IOException, URISyntaxException, InterruptedException {
        String name = "name";
        long balance = 110;
        int uid = Integer.parseInt(controller.addUser(name, balance));
        User expectedUser = new User(uid, name, balance);

        String companyName = "company";
        long price = 10;
        long amount = 1;
        int sid = Integer.parseInt(post(addCompanyUri(companyName, price, amount)));
        Stock expectedStock = new Stock(sid, companyName, price, amount);


        long boughtAmount = 10;
        assertThrows(ResponseStatusException.class, () -> controller.buy(uid, sid, boughtAmount));


        String rawUser = controller.getUser(uid);
        User actualUser = new Gson().fromJson(rawUser, User.class);
        assertEquals(expectedUser, actualUser);


        String rawStock = get(getStockUri(sid));
        Stock actualStock = new Gson().fromJson(rawStock, Stock.class);
        assertEquals(expectedStock, actualStock);


        assertEquals(balance, Long.parseLong(controller.getUserTotalBalance(uid)));


        List<Portfolio> expectedPortfolios = List.of();
        String rawPortfolios = controller.getUserPortfolios(uid);
        List<Portfolio> actualPortfolios =
                new Gson().fromJson(rawPortfolios, new TypeToken<List<Portfolio>>() {
                }.getType());

        assertEquals(expectedPortfolios, actualPortfolios);
    }

    @Test
    public void poorBuyStockTest() throws IOException, URISyntaxException, InterruptedException {
        String name = "name";
        long balance = 0;
        int uid = Integer.parseInt(controller.addUser(name, balance));
        User expectedUser = new User(uid, name, balance);

        String companyName = "company";
        long price = 10;
        long amount = 200;
        int sid = Integer.parseInt(post(addCompanyUri(companyName, price, amount)));
        Stock expectedStock = new Stock(sid, companyName, price, amount);


        long boughtAmount = 10;
        assertThrows(ResponseStatusException.class, () -> controller.buy(uid, sid, boughtAmount));


        String rawUser = controller.getUser(uid);
        User actualUser = new Gson().fromJson(rawUser, User.class);
        assertEquals(expectedUser, actualUser);


        String rawStock = get(getStockUri(sid));
        Stock actualStock = new Gson().fromJson(rawStock, Stock.class);
        assertEquals(expectedStock, actualStock);

        assertEquals(balance, Long.parseLong(controller.getUserTotalBalance(uid)));

        List<Portfolio> expectedPortfolios = List.of();
        String rawPortfolios = controller.getUserPortfolios(uid);
        List<Portfolio> actualPortfolios =
                new Gson().fromJson(rawPortfolios, new TypeToken<List<Portfolio>>() {
                }.getType());

        assertEquals(expectedPortfolios, actualPortfolios);
    }

    @Test
    public void buySeveralStockTest() throws IOException, URISyntaxException, InterruptedException {
        long initBalance = 1000;
        User user = new User("name", initBalance);
        user.setId(Integer.parseInt(controller.addUser(user.getName(), user.getBalance())));

        Stock stock1 = new Stock("company1", 10, 200);
        stock1.setId(Integer.parseInt(post(addCompanyUri(
                stock1.getCompanyName(), stock1.getPrice(), stock1.getAmount()))
        ));

        Stock stock2 = new Stock("company2", 20, 150);
        stock2.setId(Integer.parseInt(post(addCompanyUri(
                stock2.getCompanyName(), stock2.getPrice(), stock2.getAmount()))
        ));

        long boughtAmount1 = 10;
        controller.buy(user.getId(), stock1.getId(), boughtAmount1);
        user.changeBalance(-boughtAmount1 * stock1.getPrice());
        stock1.changeAmount(-boughtAmount1);

        long boughtAmount2 = 15;
        controller.buy(user.getId(), stock2.getId(), boughtAmount2);
        user.changeBalance(-boughtAmount2 * stock2.getPrice());
        stock2.changeAmount(-boughtAmount2);

        String rawUser = controller.getUser(user.getId());
        User actualUser = new Gson().fromJson(rawUser, User.class);
        assertEquals(user, actualUser);

        String rawStock1 = get(getStockUri(stock1.getId()));
        Stock actualStock1 = new Gson().fromJson(rawStock1, Stock.class);
        assertEquals(stock1, actualStock1);

        String rawStock2 = get(getStockUri(stock2.getId()));
        Stock actualStock2 = new Gson().fromJson(rawStock2, Stock.class);
        assertEquals(stock2, actualStock2);

        assertEquals(initBalance, Long.parseLong(controller.getUserTotalBalance(user.getId())));


        List<Portfolio> expectedPortfolios = List.of(
                new Portfolio(user.getId(), stock1.getId(), boughtAmount1, stock1.getPrice()),
                new Portfolio(user.getId(), stock2.getId(), boughtAmount2, stock2.getPrice())
        );
        String rawPortfolios = controller.getUserPortfolios(user.getId());
        List<Portfolio> actualPortfolios =
                new Gson().fromJson(rawPortfolios, new TypeToken<List<Portfolio>>() {
                }.getType());

        assertEquals(expectedPortfolios, actualPortfolios);
    }

    @Test
    public void sellStockTest() throws IOException, URISyntaxException, InterruptedException {
        String name = "name";
        long balance = 110;
        int uid = Integer.parseInt(controller.addUser(name, balance));
        User expectedUser = new User(uid, name, balance);

        String companyName = "company";
        long price = 10;
        long amount = 200;
        int sid = Integer.parseInt(post(addCompanyUri(companyName, price, amount)));
        Stock expectedStock = new Stock(sid, companyName, price, amount);


        long boughtAmount = 10;
        controller.buy(uid, sid, boughtAmount);


        long soldAmount = 5;
        controller.sell(uid, sid, soldAmount);

        long expectedAmount = boughtAmount - soldAmount;

        expectedUser.changeBalance(-expectedAmount * price);
        String rawUser = controller.getUser(uid);
        User actualUser = new Gson().fromJson(rawUser, User.class);
        assertEquals(expectedUser, actualUser);


        expectedStock.changeAmount(-expectedAmount);
        String rawStock = get(getStockUri(sid));
        Stock actualStock = new Gson().fromJson(rawStock, Stock.class);
        assertEquals(expectedStock, actualStock);

        assertEquals(balance, Long.parseLong(controller.getUserTotalBalance(uid)));


        List<Portfolio> expectedPortfolios = List.of(new Portfolio(uid, sid, expectedAmount, price));
        String rawPortfolios = controller.getUserPortfolios(uid);
        List<Portfolio> actualPortfolios =
                new Gson().fromJson(rawPortfolios, new TypeToken<List<Portfolio>>() {
                }.getType());

        assertEquals(expectedPortfolios, actualPortfolios);
    }

    @Test
    public void sellOverAmountStocksTest() throws IOException, URISyntaxException, InterruptedException {
        String name = "name";
        long balance = 110;
        int uid = Integer.parseInt(controller.addUser(name, balance));
        User expectedUser = new User(uid, name, balance);

        String companyName = "company";
        long price = 10;
        long amount = 10;
        int sid = Integer.parseInt(post(addCompanyUri(companyName, price, amount)));
        Stock expectedStock = new Stock(sid, companyName, price, amount);


        long boughtAmount = 5;
        controller.buy(uid, sid, boughtAmount);

        long soldAmount = 10;
        assertThrows(ResponseStatusException.class, () -> controller.sell(uid, sid, soldAmount));

        long expectedAmount = boughtAmount;

        expectedUser.changeBalance(-expectedAmount * price);
        String rawUser = controller.getUser(uid);
        User actualUser = new Gson().fromJson(rawUser, User.class);
        assertEquals(expectedUser, actualUser);


        expectedStock.changeAmount(-expectedAmount);
        String rawStock = get(getStockUri(sid));
        Stock actualStock = new Gson().fromJson(rawStock, Stock.class);
        assertEquals(expectedStock, actualStock);


        assertEquals(balance, Long.parseLong(controller.getUserTotalBalance(uid)));


        List<Portfolio> expectedPortfolios = List.of(new Portfolio(uid, sid, expectedAmount, price));
        String rawPortfolios = controller.getUserPortfolios(uid);
        List<Portfolio> actualPortfolios =
                new Gson().fromJson(rawPortfolios, new TypeToken<List<Portfolio>>() {
                }.getType());

        assertEquals(expectedPortfolios, actualPortfolios);
    }

    @Test
    public void changeStockPriceTest() throws IOException, URISyntaxException, InterruptedException {
        long initBalance = 110;
        User user = new User("name", initBalance);
        user.setId(Integer.parseInt(controller.addUser(user.getName(), user.getBalance())));

        String companyName = "company";
        long price = 10;
        long amount = 200;
        int sid = Integer.parseInt(post(addCompanyUri(companyName, price, amount)));
        Stock expectedStock = new Stock(sid, companyName, price, amount);


        long boughtAmount = 5;
        controller.buy(user.getId(), sid, boughtAmount);

        long newPrice = price + 10;
        post(URL + "set_price?stockId=" + sid + "&newPrice=" + newPrice);


        user.changeBalance(-boughtAmount * price);
        String rawUser = controller.getUser(user.getId());
        User actualUser = new Gson().fromJson(rawUser, User.class);
        assertEquals(user, actualUser);


        expectedStock.changeAmount(-boughtAmount);
        expectedStock.setPrice(newPrice);
        String rawStock = get(getStockUri(sid));
        Stock actualStock = new Gson().fromJson(rawStock, Stock.class);
        assertEquals(expectedStock, actualStock);


        long newTotalBalance = user.getBalance() + newPrice * boughtAmount;
        assertEquals(newTotalBalance, Long.parseLong(controller.getUserTotalBalance(user.getId())));


        List<Portfolio> expectedPortfolios = List.of(new Portfolio(user.getId(), sid, boughtAmount, newPrice));
        String rawPortfolios = controller.getUserPortfolios(user.getId());
        List<Portfolio> actualPortfolios =
                new Gson().fromJson(rawPortfolios, new TypeToken<List<Portfolio>>() {
                }.getType());

        assertEquals(expectedPortfolios, actualPortfolios);
    }


    private String post(String uri) throws IOException, URISyntaxException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        return response.body();
    }

    private String get(String uri) throws IOException, URISyntaxException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        return response.body();
    }


    private static String addCompanyUri(String companyName, long price, long amount) {
        return URL + "add_company?companyName=" + companyName + "&price=" + price + "&amount=" + amount;
    }

    private static String getStockUri(int stockId) {
        return URL + "stock?stockId=" + stockId;
    }

}