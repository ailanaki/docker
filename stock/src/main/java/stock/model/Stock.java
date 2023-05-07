package stock.model;

import java.util.Objects;

public class Stock {
    private int id;
    private String companyName;
    private long price;
    private long amount;


    public Stock() {}

    public Stock(String companyName, long price, long amount) {
        this.companyName = companyName;
        this.price = price;
        this.amount = amount;
    }

    public Stock(int id, String companyName, long price, long amount) {
        this.id = id;
        this.companyName = companyName;
        this.price = price;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
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
        Stock stock = (Stock) o;
        return id == stock.id && price == stock.price && amount == stock.amount && Objects.equals(companyName, stock.companyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, companyName, price, amount);
    }
}