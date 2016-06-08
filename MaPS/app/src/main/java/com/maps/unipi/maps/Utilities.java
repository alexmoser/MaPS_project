package com.maps.unipi.maps;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by leo on 08/05/16.
 */
public class Utilities {

    private static final int duration = Toast.LENGTH_SHORT;

    public static void showMessage (CharSequence msg, Context ctx){

        Toast toast = Toast.makeText(ctx, msg, duration);
        toast.show();
    }

    public static float computeTotal(ArrayList<ShoppingCartElement> shoppingCart){
        float total = 0;
        for(ShoppingCartElement element : shoppingCart){
            total += (element.getProduct().getPrice()*element.getQuantity());
        }
        return total;
    }
}
