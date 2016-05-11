package com.maps.unipi.maps;

import java.util.ArrayList;

/**
 * Created by leo on 11/05/16.
 */
public class Product {

    private String barcode;
    private String name;
    private float price;
    private ArrayList<String> ingredients;

    public Product(){

    }

    public Product(String barcode, String name, float price, ArrayList<String> ingredients){
        this.barcode = barcode;
        this.name = name;
        this.price = price;
        this.ingredients = ingredients;
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

    public ArrayList<String> getIngredients(){
        return ingredients;
    }
}
