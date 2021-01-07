package com.mahmoudkhalil.shopcom.models;

public class Product {
    private String category;
    private String description;
    private String price;
    private String primary_image_url;
    private String product_code;
    private String product_title;
    private String secondary_image_url_1;
    private String secondary_image_url_2;
    private String secondary_image_url_3;
    private String stock;

    public Product() {
    }

    public Product(String category, String description, String price, String primary_image_url, String product_code, String product_title, String secondary_image_url_1, String secondary_image_url_2, String secondary_image_url_3, String stock) {
        this.category = category;
        this.description = description;
        this.price = price;
        this.primary_image_url = primary_image_url;
        this.product_code = product_code;
        this.product_title = product_title;
        this.secondary_image_url_1 = secondary_image_url_1;
        this.secondary_image_url_2 = secondary_image_url_2;
        this.secondary_image_url_3 = secondary_image_url_3;
        this.stock = stock;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getPrimary_image_url() {
        return primary_image_url;
    }

    public String getProduct_code() {
        return product_code;
    }

    public String getProduct_title() {
        return product_title;
    }

    public String getSecondary_image_url_1() {
        return secondary_image_url_1;
    }

    public String getSecondary_image_url_2() {
        return secondary_image_url_2;
    }

    public String getSecondary_image_url_3() {
        return secondary_image_url_3;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

}
