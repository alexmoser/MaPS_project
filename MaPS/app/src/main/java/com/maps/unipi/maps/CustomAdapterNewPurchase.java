package com.maps.unipi.maps;

/**
 * Created by leo on 14/05/16.
 */

/**
 * This class defines a customized adapter for the NewPurchase fragment in ActionSelectionFragmentActivity
 * It uses a custom layout defined in file rowcustom_new_purchase.xml
 * */
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapterNewPurchase extends ArrayAdapter<ShoppingCartElement>{

    private ArrayList<ShoppingCartElement> elementList = new ArrayList<>();
    private Context ctx;

    public CustomAdapterNewPurchase(Context context, int textViewResourceId, ArrayList<ShoppingCartElement> objects) {
        super(context, textViewResourceId, objects);
        elementList = objects;
        ctx = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null)
            convertView = inflater.inflate(R.layout.rowcustom_new_purchase, null);
        final ShoppingCartElement selectedElement = getItem(position);
        final TextView tvTotal = (TextView) ((Activity)ctx).findViewById(R.id.newpurch_tv_totalprice);
        final TextView tvName = (TextView) convertView.findViewById(R.id.row_newpurch_tv_name);
        final TextView tvPrice = (TextView) convertView.findViewById(R.id.row_newpurch_tv_price);
        final ImageButton btRemove = (ImageButton) convertView.findViewById(R.id.row_newpurch_bt_remove);

        tvName.setText(selectedElement.getProduct().getName() + " x" + selectedElement.getQuantity());
        tvPrice.setText(Utilities.roundTwoDecimal(selectedElement.getProduct().getPrice()) + "€");

        tvName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent productInfo_activity = new Intent(getContext(), ProductInformationActivity.class);
                productInfo_activity.putExtra("barcode", selectedElement.getProduct().getBarcode());
                getContext().startActivity(productInfo_activity);
            }
        });

        btRemove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Quantity custom dialog
                final Dialog dialogQuantity = new Dialog(ctx);
                dialogQuantity.setContentView(R.layout.custom_dialog);
                Button btnOK = (Button) dialogQuantity.findViewById(R.id.dialog_bt_ok);
                Button btnCancel = (Button) dialogQuantity.findViewById(R.id.dialog_bt_cancel);
                final NumberPicker np = (NumberPicker) dialogQuantity.findViewById(R.id.dialog_np);
                np.setMaxValue(selectedElement.getQuantity());
                np.setMinValue(1);
                np.setWrapSelectorWheel(false);
                np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                    }
                });
                btnOK.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        ShoppingCartElement element = ActionSelectionFragmentActivity.shoppingCart.get(ActionSelectionFragmentActivity.shoppingCart.indexOf(selectedElement));
                        element.decreaseQuantity(np.getValue());
                        if (element.getQuantity() == 0)
                            ActionSelectionFragmentActivity.shoppingCart.remove(selectedElement);

                        ActionSelectionFragmentActivity.NewPurchaseFragment.adapter.notifyDataSetChanged();

                        float totalPrice = Utilities.computeTotal(ActionSelectionFragmentActivity.shoppingCart);
                        tvTotal.setText(Utilities.roundTwoDecimal(totalPrice) + "€");

                        dialogQuantity.dismiss();
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        dialogQuantity.dismiss();
                    }
                });
                dialogQuantity.show();
            }
        });
        return convertView;
    }

}
