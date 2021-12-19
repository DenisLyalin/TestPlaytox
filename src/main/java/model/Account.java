package model;

public class Account {

    public Account(final String id) {
        this.id = id;
    }

    private String id;
    private int money = 10000;

    public String getId() {
        return id;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getMoney() {
        return money;
    }
}
