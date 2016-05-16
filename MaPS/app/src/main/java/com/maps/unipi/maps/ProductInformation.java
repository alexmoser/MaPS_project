package com.maps.unipi.maps;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;

public class ProductInformation extends AppCompatActivity {

    Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_information);
        product = ScanProduct.productScanned;

        TextView prod_name = (TextView) findViewById(R.id.prodinfo_tv_prodname);
        TextView list_ingr = (TextView) findViewById(R.id.prodinfo_tv_listingr);
        TextView prod_price = (TextView) findViewById(R.id.prodinfo_tv_prodprice);

        prod_name.setText(product.getName());
        prod_price.setText(Float.toString(product.getPrice()) + "€");

        boolean canBuy = true;

        for (String ingredient : product.getIngredients()) {
            list_ingr.append(ingredient + "\n");
            for(String filter : ActionSelection.filters) {
                if (filter.equalsIgnoreCase(ingredient))
                    canBuy = false;
            }
        }

        if(canBuy)
            prod_name.setTextColor(Color.GREEN);
        else
            prod_name.setTextColor(Color.RED);

    }

    public void onClickNextScan(View v){
        Intent filters = new Intent(this, ScanProduct.class);
        startActivity(filters);
    }

    public void onClickAddProduct(View v){
        Intent action_selection = new Intent(this, ActionSelection.class);
        ActionSelection.shoppingCart.add(product);
        startActivity(action_selection);
    }
}
