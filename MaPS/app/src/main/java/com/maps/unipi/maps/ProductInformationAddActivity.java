package com.maps.unipi.maps;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
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

        final TextView prod_name = (TextView) findViewById(R.id.prodinfo_tv_name);
        final TextView list_ingr = (TextView) findViewById(R.id.prodinfo_tv_listingr);
        final TextView prod_price = (TextView) findViewById(R.id.prodinfo_tv_price);
        final NumberPicker numberPicker = (NumberPicker) findViewById(R.id.prodinfo_np_quantity);
        final ImageView img = (ImageView) findViewById(R.id.prodinfo_iv_img);

        prod_name.setText(element.getProduct().getName());
        prod_price.setText(Float.toString(element.getProduct().getPrice()) + "€");
        list_ingr.setMovementMethod(new ScrollingMovementMethod());

        Picasso.with(this)
                .load(element.getProduct().getUrl())
                .error(R.drawable.ic_image_not_found)
                .resize(250, 250)
                .into(img);

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(99);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
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
