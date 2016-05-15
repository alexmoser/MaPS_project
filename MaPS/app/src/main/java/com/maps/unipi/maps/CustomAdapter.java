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

public class CustomAdapter extends ArrayAdapter<Product>{

    public CustomAdapter(Context context, int textViewResourceId, ArrayList<Product> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.rowcustom, null);
        TextView name = (TextView)convertView.findViewById(R.id.textViewName);
        TextView price = (TextView)convertView.findViewById(R.id.textViewPrice);
        Product c = getItem(position);
        name.setText(c.getName());
        price.setText(Float.toString(c.getPrice()) + "€");
        return convertView;
    }

}
