package model;

public class Client {
    private int id;
    private String name;
    private double moneySpent;
    private boolean member;

    public Client(int id, String name, double moneySpent, boolean member) {
        this.id = id;
        this.name = name;
        this.moneySpent = moneySpent;
        this.member = member;
    }

    public Client(String name, double moneySpent, boolean member) {
        this.id = 0;
        this.name = name;
        this.moneySpent = moneySpent;
        this.member = member;
    }

    public Client(String name) {
        this.id = 0;
        this.name = name;
        this.moneySpent = 0;
        this.member = false;
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

    public double getMoneySpent() {
        return moneySpent;
    }

    public void setMoneySpent(double moneySpent) {
        this.moneySpent = moneySpent;
    }

    public boolean isMember() {
        return member;
    }

    public void setMember(boolean member) {
        this.member = member;
    }
}
