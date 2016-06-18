package com.maps.unipi.maps;

import java.util.ArrayList;

/**
 * Class that represents a Product as instance of the Firebase database, hence the attributes must
 * correspond to the DB structure
 */
public class Product {

    private String barcode;
    private String name;
    private float price;
    private String url;
    private ArrayList<String> ingredients;

    public Product(){
        // empty default constructor, necessary for Firebase to be able to deserialize blog posts
    }

    public Product(String barcode, String name, float price, String url, ArrayList<String> ingredients){
        this.barcode = barcode;
        this.name = name;
        this.price = price;
        this.ingredients = ingredients;
        this.url = url;
    }

    public String getBarcode(){
        return barcode;
    }

    public String getName(){
        return name;
    }

    public float getPrice(){
        return price;
    }

    public String getUrl(){ return url; }

    public ArrayList<String> getIngredients(){
        return ingredients;
    }

    public void setName(String name){ this.name = name; }

    public void setPrice(Float price){ this.price = price; }
}
