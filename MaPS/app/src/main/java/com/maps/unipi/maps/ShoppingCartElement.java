package com.maps.unipi.maps;

/**
 * Class that represents an element of the shopping cart. It is the combination of a Product with
 * relative quantity
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
        if(object == null)
            return false;
        if(!(object instanceof ShoppingCartElement))
            return false;
        Product obj = ((ShoppingCartElement) object).getProduct();
        return (obj.getBarcode().equals(product.getBarcode()));
    }

}
