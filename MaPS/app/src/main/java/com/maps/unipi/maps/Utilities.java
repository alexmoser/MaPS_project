package com.maps.unipi.maps;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by leo on 08/05/16.
 */
public class Utilities {

    private static final int duration = Toast.LENGTH_SHORT;

    public static void showMessage (CharSequence msg, Context ctx){

        Toast toast = Toast.makeText(ctx, msg, duration);
        toast.show();
    }
}
