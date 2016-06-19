package com.maps.unipi.flashcart;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Class that groups static utility methods that are used from all the activities
 */
public class Utilities {

    private static final int duration_short = Toast.LENGTH_SHORT;
    private static final int duration_long = Toast.LENGTH_LONG;

    /**
     * Shows a short Toast with the specified message
     * @param msg is the message to show
     * @param ctx is the context
     * */
    public static void showMessage (CharSequence msg, Context ctx){

        Toast toast = Toast.makeText(ctx, msg, duration_short);
        toast.show();
    }

    /**
     * Shows a long Toast with the specified message
     * @param msg is the message to show
     * @param ctx is the context
     * */
    public static void showLongMessage (CharSequence msg, Context ctx){

        Toast toast = Toast.makeText(ctx, msg, duration_long);
        toast.show();
    }

    /**
     * Creates an AlertDialog with title Error, a specified message and a button OK to confirm
     * @param activity is the activity to open the dialog in
     * @param msg is the message to show
     * */
    public static void showErrorDialog(Activity activity, CharSequence msg){
        //default alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(msg)
                .setTitle("Error")
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Computes the amount of the total cost of the elements in the specified list as number of
     * products times the quantity
     * @param shoppingCart is the list containing all the ShoppingCartElements
     * @return the total cost of the products in the list
     * */
    public static float computeTotal(ArrayList<ShoppingCartElement> shoppingCart){
        float total = 0;
        for(ShoppingCartElement element : shoppingCart){
            total += (element.getProduct().getPrice()*element.getQuantity());
        }
        return total;
    }

    /**
     * Formats the specified number to have 2 decimal places
     * @param number is the number that needs formatting
     * @return a String representing the number with exactly 2 decimal digit/places
     * */
    public static String roundTwoDecimal(float number) {
        return String.format(Locale.getDefault(), "%.2f", number);
    }

    public static String removeInitialZero(CharSequence text) {
        String ret = text.toString();
        if(text.charAt(0) == '0') {
            ret = text.subSequence(1, text.length()).toString();
        }
        return ret;
    }
}
