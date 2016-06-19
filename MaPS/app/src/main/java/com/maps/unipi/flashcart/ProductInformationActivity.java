package com.maps.unipi.flashcart;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
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

        final TextView tvName = (TextView) findViewById(R.id.productinfo_tv_name);
        final TextView tvIngredients = (TextView) findViewById(R.id.productinfo_tv_listingr);
        final TextView tvPrice = (TextView) findViewById(R.id.productinfo_tv_price);
        final ImageView imgProduct = (ImageView) findViewById(R.id.productinfo_iv_img);

        tvName.setText(element.getProduct().getName());
        tvPrice.setText(Utilities.roundTwoDecimal(element.getProduct().getPrice()) + "€");
        tvIngredients.setMovementMethod(new ScrollingMovementMethod());

        // Load the image product from the Firebase DB
        Picasso.with(this)
                .load(element.getProduct().getUrl())
                .error(R.drawable.ic_image_not_found)
                .placeholder(R.drawable.img_loading)
                .into(imgProduct);

        // Boolean variables to handle non-desired ingredients
        boolean canBuy = true;
        boolean badIngredient = false;

        for (String ingredient : element.getProduct().getIngredients()) {
            for(String filter : ActionSelectionFragmentActivity.filters) {
                if (ingredient.contains(filter)) {
                    canBuy = false;
                    badIngredient = true;
                }
            }
            if(badIngredient) {
                // Use fromHtml() function in order to bold the non-desired ingredients
                tvIngredients.append(Html.fromHtml("<b>" + ingredient + "<b>"));
                badIngredient = false;
            }
            else {
                tvIngredients.append(ingredient);
            }
            // Add ',' character to separate ingredients (unless it is the last one
            if(element.getProduct().getIngredients().indexOf(ingredient) != (element.getProduct().getIngredients().size() - 1))
                tvIngredients.append(", ");
        }
        // Product name is colored in GREEN if safe to buy, RED otherwise
        if(canBuy)
            tvName.setTextColor(Color.GREEN);
        else
            tvName.setTextColor(Color.RED);
    }

    public void onClickBack(View v){
        Intent action_selection = new Intent(this, ActionSelectionFragmentActivity.class);
        startActivity(action_selection);
    }
}
