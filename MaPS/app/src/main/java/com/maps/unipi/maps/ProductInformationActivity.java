package com.maps.unipi.maps;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ProductInformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_information);

        ShoppingCartElement element = null;

        for (ShoppingCartElement e : ActionSelectionFragmentActivity.shoppingCart){
            if (e.getProduct().getBarcode().equals(getIntent().getExtras().getString("barcode"))) {
                element = e;
            }
        }

        final TextView prod_name = (TextView) findViewById(R.id.productinfo_tv_name);
        final TextView list_ingr = (TextView) findViewById(R.id.productinfo_tv_listingr);
        final TextView prod_price = (TextView) findViewById(R.id.productinfo_tv_price);
        final ImageView img = (ImageView) findViewById(R.id.productinfo_iv_img);

        prod_name.setText(element.getProduct().getName());
        prod_price.setText(Utilities.roundTwoDecimal(element.getProduct().getPrice()) + "€");
        list_ingr.setMovementMethod(new ScrollingMovementMethod());

        Picasso.with(this)
                .load(element.getProduct().getUrl())
                .error(R.drawable.ic_image_not_found)
                .resize(250, 250)
                .into(img);

        boolean canBuy = true;
        boolean badIngr = false;
        for (String ingredient : element.getProduct().getIngredients()) {
            for(String filter : ActionSelectionFragmentActivity.filters) {
                if (ingredient.contains(filter)) {
                    canBuy = false;
                    badIngr = true;
                }
            }
            if(badIngr) {
                list_ingr.append(Html.fromHtml("<b>" + ingredient + "<b>"));
                badIngr = false;
            }
            else {
                list_ingr.append(ingredient);
            }
            if(element.getProduct().getIngredients().indexOf(ingredient) != (element.getProduct().getIngredients().size() - 1))
                list_ingr.append(", ");
        }
        if(canBuy)
            prod_name.setTextColor(Color.GREEN);
        else
            prod_name.setTextColor(Color.RED);
    }

    public void onClickBack(View v){
        Intent action_selection = new Intent(this, ActionSelectionFragmentActivity.class);
        startActivity(action_selection);
    }
}
