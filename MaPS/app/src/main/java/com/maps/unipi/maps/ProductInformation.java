package com.maps.unipi.maps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;

public class ProductInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_information);

        Intent intent = getIntent();
        String name = intent.getExtras().getString("name");
        ArrayList<String> ingredients = intent.getStringArrayListExtra("name");

        TextView prod_name = (TextView) findViewById(R.id.prodinfo_tv_prodname);
        TextView list_ingr = (TextView) findViewById(R.id.prodinfo_tv_listingr);

        prod_name.setText(name);
        for (String ingredient : ingredients){
            list_ingr.append(ingredient + "\n");
        }
    }

    public void onClickNextScan(View v){
        Intent filters = new Intent(this, ScanProduct.class);
        startActivity(filters);
    }

    public void onClickAddProduct(View v){
        //TODO inserire prodotto in lista e tornare alla activity NewPurchase
    }
}
