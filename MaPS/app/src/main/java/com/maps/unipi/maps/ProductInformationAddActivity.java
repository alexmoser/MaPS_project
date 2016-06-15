package com.maps.unipi.maps;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ProductInformationAddActivity extends AppCompatActivity {

    ShoppingCartElement element;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_information_add);
        element = new ShoppingCartElement(ScanProductActivity.productScanned);

        final TextView prod_name = (TextView) findViewById(R.id.prodinfo_tv_prodname);
        final TextView list_ingr = (TextView) findViewById(R.id.prodinfo_tv_listingr);
        final TextView prod_price = (TextView) findViewById(R.id.prodinfo_tv_prodprice);
        final NumberPicker numberPicker = (NumberPicker) findViewById(R.id.prodinfo_np_quantity);
        final ImageView img = (ImageView) findViewById(R.id.prodinfo_iv_img);

        prod_name.setText(element.getProduct().getName());
        prod_price.setText(Float.toString(element.getProduct().getPrice()) + "€");

        Picasso.with(this)
                .load(element.getProduct().getUrl())
                .error(R.drawable.ic_image_not_found)
                .resize(250, 250)
                .into(img);

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(99);
        //restart when reaching the end
        numberPicker.setWrapSelectorWheel(true);
        //set a value change listener for NumberPicker
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                element.setQuantity(newVal);
                prod_price.setText(Utilities.roundTwoDecimal(element.getProduct().getPrice()*element.getQuantity()) + "€");
            }
        });

        boolean canBuy = true;

        for (String ingredient : element.getProduct().getIngredients()) {
            list_ingr.append(ingredient + "\n");
            for(String filter : ActionSelectionFragmentActivity.filters) {
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
        Intent filters = new Intent(this, ScanProductActivity.class);
        startActivity(filters);
    }

    public void onClickAddProduct(View v) {
        Intent action_selection = new Intent(this, ActionSelectionFragmentActivity.class);
        //check if product already in the shopping cart
        if(!ActionSelectionFragmentActivity.shoppingCart.contains(element)){ //product not in shopping cart
            ActionSelectionFragmentActivity.shoppingCart.add(element);
        }
        else { //product already in shopping cart
            ShoppingCartElement oldElement = ActionSelectionFragmentActivity.shoppingCart.get(ActionSelectionFragmentActivity.shoppingCart.indexOf(element));
            oldElement.increaseQuantity(element.getQuantity());
        }
        startActivity(action_selection);
    }
}
