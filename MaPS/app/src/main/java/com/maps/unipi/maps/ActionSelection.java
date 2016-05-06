package com.maps.unipi.maps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;

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

    public void onClickLastPurchase(View v) {
        Intent last_purchase = new Intent(this, LastPurchase.class);
        startActivity(last_purchase);
    }

    public void onClickNewPurchase(View v){
        Intent new_purchase = new Intent(this, NewPurchase.class);
        startActivity(new_purchase);
    }
}
