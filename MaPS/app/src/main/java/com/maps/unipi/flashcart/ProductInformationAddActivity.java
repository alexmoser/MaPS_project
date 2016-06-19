package com.maps.unipi.flashcart;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class ProductInformationAddActivity extends AppCompatActivity {

    private ShoppingCartElement element;
    private boolean canBuy = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_information_add);
        element = new ShoppingCartElement(ScanProductActivity.productDB);

        final TextView tvName = (TextView) findViewById(R.id.prodinfo_tv_name);
        final TextView tvIngredients = (TextView) findViewById(R.id.prodinfo_tv_listingr);
        final TextView tvPrice = (TextView) findViewById(R.id.prodinfo_tv_price);
        final NumberPicker numberPicker = (NumberPicker) findViewById(R.id.prodinfo_np_quantity);
        final ImageView imgProduct = (ImageView) findViewById(R.id.prodinfo_iv_img);

        tvName.setText(element.getProduct().getName());
        tvPrice.setText(Utilities.roundTwoDecimal(element.getProduct().getPrice()) + "€");
        tvIngredients.setMovementMethod(new ScrollingMovementMethod());

        // Load the image product from the Firebase DB
        Picasso.with(this)
                .load(element.getProduct().getUrl())
                .error(R.drawable.ic_image_not_found)
                .placeholder(R.drawable.img_loading)
                .into(imgProduct);

        // Set Number Picker parameters
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(99);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //Restart when reaching the end
        numberPicker.setWrapSelectorWheel(true);
        // Set a value change listener for NumberPicker
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                element.setQuantity(newVal);
                tvPrice.setText(Utilities.roundTwoDecimal(element.getProduct().getPrice()*element.getQuantity()) + "€");
            }
        });

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

    public void onClickNextScan(View v){
        Intent filters = new Intent(this, ScanProductActivity.class);
        startActivity(filters);
    }

    public void onClickAddProduct(View v) {
        if(!canBuy) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProductInformationAddActivity.this);
            builder.setTitle(R.string.title_add_product_dialog)
                    .setMessage(R.string.message_add_product_dialog)
                    .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked Yes button
                            // Check if product already in the shopping cart
                            if (!ActionSelectionFragmentActivity.shoppingCart.contains(element)) {
                                // Product not in shopping cart
                                ActionSelectionFragmentActivity.shoppingCart.add(element);
                            } else {
                                // Product already in shopping cart
                                ShoppingCartElement oldElement = ActionSelectionFragmentActivity.shoppingCart.get(ActionSelectionFragmentActivity.shoppingCart.indexOf(element));
                                oldElement.increaseQuantity(element.getQuantity());
                            }
                            Intent action_selection = new Intent(ProductInformationAddActivity.this, ActionSelectionFragmentActivity.class);
                            startActivity(action_selection);
                        }
                    })
                    .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked No button
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        // Code repetition, no way to make it reusable both here and above
        else {
            // Check if product already in the shopping cart
            if (!ActionSelectionFragmentActivity.shoppingCart.contains(element)) {
                // Product not in shopping cart
                ActionSelectionFragmentActivity.shoppingCart.add(element);
            } else {
                // Product already in shopping cart
                ShoppingCartElement oldElement = ActionSelectionFragmentActivity.shoppingCart.get(ActionSelectionFragmentActivity.shoppingCart.indexOf(element));
                oldElement.increaseQuantity(element.getQuantity());
            }
            Intent action_selection = new Intent(this, ActionSelectionFragmentActivity.class);
            startActivity(action_selection);
        }
    }
}
