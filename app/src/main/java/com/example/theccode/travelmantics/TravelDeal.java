package com.example.theccode.travelmantics;

import java.io.Serializable;

public class TravelDeal implements Serializable  {
    private String id;
    private String title;
    private String description;
    private String price;
    private String img_url;
    private String image_name;

    public  TravelDeal(){}

    public TravelDeal( String title, String description, String price, String img_url, String image_name) {
        this.setTitle(title);
        this.setDescription(description);
        this.setPrice(price);
        this.setImg_url(img_url);
        this.setImage_name(image_name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }
}
