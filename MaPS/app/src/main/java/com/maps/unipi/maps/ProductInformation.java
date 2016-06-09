package com.maps.unipi.maps;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import java.util.ArrayList;

public class ProductInformation extends AppCompatActivity {

    ShoppingCartElement element;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_information);
        element = new ShoppingCartElement(ScanProduct.productScanned);

        TextView prod_name = (TextView) findViewById(R.id.prodinfo_tv_prodname);
        TextView list_ingr = (TextView) findViewById(R.id.prodinfo_tv_listingr);
        TextView prod_price = (TextView) findViewById(R.id.prodinfo_tv_prodprice);
        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.prodinfo_np_quantity);

        prod_name.setText(element.getProduct().getName());
        prod_price.setText(Float.toString(element.getProduct().getPrice()) + "€");

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(99);
        //restart when reaching the end
        numberPicker.setWrapSelectorWheel(true);
        //set a value change listener for NumberPicker
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                element.setQuantity(newVal);
            }
        });

        boolean canBuy = true;

        for (String ingredient : element.getProduct().getIngredients()) {
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

    public void onClickAddProduct(View v) {
        Intent action_selection = new Intent(this, ActionSelection.class);
        //check if product already in the shopping cart
        if(!ActionSelection.shoppingCart.contains(element)){ //product not in shopping cart
            ActionSelection.shoppingCart.add(element);
        }
        else { //product already in shopping cart
            ShoppingCartElement oldElement = ActionSelection.shoppingCart.get(ActionSelection.shoppingCart.indexOf(element));
            oldElement.increaseQuantity(element.getQuantity());
        }
        /*boolean elementFound = false;
        for(ShoppingCartElement element : ActionSelection.shoppingCart) {
            if(element.equals(new ShoppingCartElement(product, 1))) {
                element.increaseQuantity(1);
                elementFound = true;
            }
        }
        if(!elementFound) {
            ShoppingCartElement element = new ShoppingCartElement(product, 1);
            ActionSelection.shoppingCart.add(element);
        }*/
        startActivity(action_selection);
    }
}
