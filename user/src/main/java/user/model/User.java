package user.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class User {
    private int id;
    private String name;
    private long balance;
    private Map<Integer, Portfolio> portfolios;

    public User(String companyName, long balance) {
        this.name = companyName;
        this.balance = balance;
        this.portfolios = new HashMap<>();
    }

    public User(int id, String name, long balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.portfolios = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public Map<Integer, Portfolio> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(Map<Integer, Portfolio>  portfolios) {
        this.portfolios = portfolios;
    }

    public void changeBalance(long balance) {
        if (this.balance + balance < 0) {
            throw new AssertionError("Negative balance");
        }
        this.balance += balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && balance == user.balance && Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, balance);
    }
}