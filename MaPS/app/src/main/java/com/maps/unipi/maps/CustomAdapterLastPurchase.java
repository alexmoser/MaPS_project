package com.maps.unipi.maps;

/**
 * Created by leo on 14/05/16.
 */

//questa classe serve per la lista dei prodotti nella schermata New Purchase, visto che voglio mostrare Nome e prezzo non basta l'adapter usato
//per i filtri ma bisogna definirne uno customzizzato legato ad un layout che ho definito nel file rowcustom.xml
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
        convertView = inflater.inflate(R.layout.rowcustom_last_purchase, null);
        TextView name = (TextView)convertView.findViewById(R.id.textViewName);
        TextView price = (TextView)convertView.findViewById(R.id.textViewPrice);
        ShoppingCartElement c = getItem(position);
        name.setText(c.getProduct().getName() + " x" + c.getQuantity());
        price.setText(Float.toString(c.getProduct().getPrice()) + "€");
        return convertView;
    }

}
