package com.maps.unipi.maps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ProductInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_information);

        Intent intent = getIntent();
        String name = intent.getExtras().getString("name");
        String ingredients = intent.getExtras().getString("ingr");

        EditText prod_name = (EditText) findViewById(R.id.prodinfo_et_name);
        EditText ingr = (EditText) findViewById(R.id.prodinfo_et_ingr);

        prod_name.setText(name);
        ingr.setText(ingredients);
    }

    public void onClickNextScan(View v){
        Intent filters = new Intent(this, ScanProduct.class);
        startActivity(filters);
    }

    public void onClickAddProduct(View v){
        //TODO inserire prodotto in lista e tornare alla activity NewPurchase
    }
}
