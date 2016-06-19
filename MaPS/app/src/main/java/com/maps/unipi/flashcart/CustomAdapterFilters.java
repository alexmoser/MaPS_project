package com.maps.unipi.flashcart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by alex on 16/06/16.
 */
/**
 * This class defines a customized adapter for the Filters fragment in ActionSelectionFragmentActivity
 * It uses a custom layout defined in file rowcustom_filters.xml
 * */
public class CustomAdapterFilters extends ArrayAdapter<String> {

    private Context ctx;

    public CustomAdapterFilters(Context context, int textViewResourceId, ArrayList<String> objects) {
        super(context, textViewResourceId, objects);
        ctx = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null)
            convertView = inflater.inflate(R.layout.rowcustom_filters, null);

        final TextView tvName = (TextView) convertView.findViewById(R.id.textViewName);
        final ImageButton btRemove = (ImageButton) convertView.findViewById(R.id.buttonRemove);
        final String filter = getItem(position);
        tvName.setText(filter);
        btRemove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Confirm deletion dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle(R.string.title_delete_item_dialog)
                        .setMessage(R.string.message_delete_item_dialog)
                        .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked Yes button
                                ActionSelectionFragmentActivity.filters.remove(filter);
                                ActionSelectionFragmentActivity.FiltersFragment.adapter.notifyDataSetChanged();
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
        });
        return convertView;
    }
}
