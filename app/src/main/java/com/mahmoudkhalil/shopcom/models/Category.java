package com.mahmoudkhalil.shopcom.models;

public class Category {
    private String category_code;
    private String category_title;
    private int category_image_resource;

    public Category() {
    }

    public Category(String category_code, String category_title, int category_image_resource) {
        this.category_code = category_code;
        this.category_title = category_title;
        this.category_image_resource = category_image_resource;
    }

    public String getCategory_title() {
        return category_title;
    }

    public int getCategory_url() {
        return category_image_resource;
    }

    public String getCategory_code() {
        return category_code;
    }
}
