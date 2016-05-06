package com.maps.unipi.maps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class NewPurchase extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_purchase);

    }

    public void onClickScanProduct(View v){
        Intent scan_product = new Intent(this, ScanProduct.class);
        startActivity(scan_product);
    }

    public void onClickRemoveProduct(View v){
        //TODO gestire rimozione prodotti
    }
}
