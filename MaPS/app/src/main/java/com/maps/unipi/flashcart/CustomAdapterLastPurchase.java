package com.maps.unipi.flashcart;

/**
 * This class defines a customized adapter for the LastPurchase fragment in ActionSelectionFragmentActivity
 * It uses a custom layout defined in file rowcustom_last_purchase.xml
 * */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapterLastPurchase extends ArrayAdapter<ShoppingCartElement>{

    public CustomAdapterLastPurchase(Context context, int textViewResourceId, ArrayList<ShoppingCartElement> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null)
            convertView = inflater.inflate(R.layout.rowcustom_last_purchase, null);

        final TextView tvName = (TextView) convertView.findViewById(R.id.textViewName);
        final TextView tvPrice = (TextView) convertView.findViewById(R.id.textViewPrice);
        final ShoppingCartElement element = getItem(position);
        tvName.setText(element.getProduct().getName() + " x" + element.getQuantity());
        tvPrice.setText(Utilities.roundTwoDecimal(element.getProduct().getPrice()) + "€");
        return convertView;
    }
}
