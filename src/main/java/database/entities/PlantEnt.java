package database.entities;

import jakarta.persistence.*;

@Entity
@DiscriminatorColumn(name="plantType")
public abstract class PlantEnt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String color;
    private double cost;
    private boolean sold;

    @ManyToOne
    @JoinColumn(name = "sale_id")
    private SaleEnt sale;

    @Version
    private int version;

    public PlantEnt() {}

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public SaleEnt getSale() {
        return sale;
    }

    public void setSale(SaleEnt sale) {
        this.sale = sale;
    }
}
