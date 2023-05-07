package user.model;

import java.util.Objects;

public class Portfolio {
    private int id;
    private int userId;
    private int stockId;
    private long amount;
    private long price;

    public Portfolio() {
    }

    public Portfolio(int userId, int stockId, long amount) {
        this.userId = userId;
        this.stockId = stockId;
        this.amount = amount;
    }

    public Portfolio(int userId, int stockId, long amount, long price) {
        this.userId = userId;
        this.stockId = stockId;
        this.amount = amount;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public void changeAmount(long amount) {
        if (this.amount + amount < 0) {
            throw new AssertionError("Negative amount");
        }
        this.amount += amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Portfolio portfolio = (Portfolio) o;
        return userId == portfolio.userId && stockId == portfolio.stockId && amount == portfolio.amount && price == portfolio.price;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, stockId, amount, price);
    }
}