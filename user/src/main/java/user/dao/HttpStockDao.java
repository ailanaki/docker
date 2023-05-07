package user.dao;


import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.RequestFailedException;
import com.google.gson.Gson;
import stock.model.Stock;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpStockDao implements StockDao {
    private final HttpClient client;
    private final String url;

    public HttpStockDao(String url) {
        this.url = url;
        this.client = HttpClient.newHttpClient();
    }

    @Override
    public long getPrice(long stockId) {
        try {
            String rawStock = get(url + "/stock?stockId=" + stockId);
            Stock actualStock = new Gson().fromJson(rawStock, Stock.class);

            return actualStock.getPrice();
//            return Long.parseLong(get(url + "/stock?stockId=" + stockId));
        } catch (Exception e) {
            throw new AssertionError(e.getMessage());
        }
    }

    @Override
    public long buy(int stockId, long amount) throws IOException, URISyntaxException, InterruptedException {
        String price = post(url + "/buy?stockId=" + stockId + "&amount=" + amount);

        return Long.parseLong(price);
    }

    @Override
    public long sell(int stockId, long amount) throws IOException, URISyntaxException, InterruptedException {
        String price = post(url + "/sell?stockId=" + stockId + "&amount=" + amount);

        return Long.parseLong(price);
    }

    private String post(String uri) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new RequestFailedException(response.body());
        return response.body();
    }

    private String get(String uri) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new RequestFailedException(response.body());
        return response.body();
    }
}