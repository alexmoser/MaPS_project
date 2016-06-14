package com.maps.unipi.maps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by leo on 08/05/16.
 */
public class Utilities {

    private static final int duration = Toast.LENGTH_LONG;

    public static void showMessage (CharSequence msg, Context ctx){

        Toast toast = Toast.makeText(ctx, msg, duration);
        toast.show();
    }

    public static void showErrorDialog(Activity activity, String msg){
        //default alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(msg)
                .setTitle("Error!")
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showConfirmDialog(){

    }
    public static float computeTotal(ArrayList<ShoppingCartElement> shoppingCart){
        float total = 0;
        for(ShoppingCartElement element : shoppingCart){
            total += (element.getProduct().getPrice()*element.getQuantity());
        }
        return total;
    }

    public static String roundTwoDecimal(float number) {
        return Double.toString(Math.round(number*100)/100.0d);
    }

}
