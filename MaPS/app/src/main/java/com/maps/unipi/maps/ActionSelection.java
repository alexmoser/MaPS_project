package com.maps.unipi.maps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ActionSelection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_selection);
    }

    public void onClickFilters(View v){
        Intent filters = new Intent(this, Filters.class);
        startActivity(filters);
    }

    public void onClickFavouriteItems(View v){
        Intent favourite_items = new Intent(this, FavouriteItems.class);
        startActivity(favourite_items);
    }

    public void onClickLastPurchase(View v) {
        Intent last_purchase = new Intent(this, LastPurchase.class);
        startActivity(last_purchase);
    }

    public void onClickShoppingCart(View v){
        Intent shopping_cart = new Intent(this, ShoppingCart.class);
        startActivity(shopping_cart);
    }

    public void onClickNewPurchase(View v){
        Intent purchase = new Intent(this, NewPurchase.class);
        startActivity(purchase);
    }
}
