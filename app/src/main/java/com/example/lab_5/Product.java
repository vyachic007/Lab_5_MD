package com.example.lab_5;

public class Product {

    private long id;
    private String article;
    private String name;
    private String manufacturer;
    private String country;
    private String category;
    private double price;
    private int quantity;

    public Product() {
    }

    public Product(
            long id,
            String article,
            String name,
            String manufacturer,
            String country,
            String category,
            double price,
            int quantity
    ) {
        this.id = id;
        this.article = article;
        this.name = name;
        this.manufacturer = manufacturer;
        this.country = country;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

    public Product(
            String article,
            String name,
            String manufacturer,
            String country,
            String category,
            double price,
            int quantity
    ) {
        this.article = article;
        this.name = name;
        this.manufacturer = manufacturer;
        this.country = country;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public String getArticle() {
        return article;
    }

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getCountry() {
        return country;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String toShareText() {
        return "Артикул: " + article + "\n"
                + "Наименование: " + name + "\n"
                + "Производитель: " + manufacturer + "\n"
                + "Страна производства: " + country + "\n"
                + "Категория: " + category + "\n"
                + "Цена: " + price + "\n"
                + "Остаток: " + quantity;
    }
}