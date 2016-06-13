package com.maps.unipi.maps;

import android.util.Log;

/**
 * Created by alex on 08/06/16.
 */
public class ShoppingCartElement {
    private Product product;
    private int quantity;

    public ShoppingCartElement(Product product) {
        this.product = product;
        this.quantity = 1;
    }

    public ShoppingCartElement(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    /**
     * increases product quantity of the specified quantity
     * @param quantity specifies of how much to increase the quantity
     * */
    public void increaseQuantity(int quantity){
        this.quantity += quantity;
    }

    /**
     * Decreases product quantity of the specified quantity
     * @param quantity specifies of how much to decrease the quantity
     * */
    public void decreaseQuantity(int quantity){
        this.quantity -= quantity;
    }

    @Override
    public boolean equals(Object object) {
        Product obj = ((ShoppingCartElement) object).getProduct();
        if(obj.getBarcode().equals(product.getBarcode()))
            return true;
        return false;
    }

}
