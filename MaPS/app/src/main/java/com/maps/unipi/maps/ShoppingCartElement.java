package com.maps.unipi.maps;

/**
 * Created by alex on 08/06/16.
 */
public class ShoppingCartElement {
    private Product product;
    private int quantity;

    public ShoppingCartElement(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void increaseQuantity(){
        quantity++;
    }

    public void increaseQuantity(int quantity){
        this.quantity = quantity;
    }

    public void decreaseQuantity(){
        quantity--;
    }

    public void decreaseQuantity(int quantity){
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object object) {
        Product obj = ((ShoppingCartElement) object).getProduct();
        if(obj.getBarcode().equals(product.getBarcode()))
            return true;
        return false;
    }

}
