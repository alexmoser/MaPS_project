package com.maps.unipi.maps;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class ScanProduct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan a product");
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /*
    @Override
    protected void onRestart(){
        super.onRestart();

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan a product");
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
        Log.d("debug", "onRestart");
    }
    */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        String product_name = "", ingredients = "";
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String re = scanResult.getContents();
            if(re != null) {
                Log.d("code", "Read code: " + re);
                File directory = Environment.getExternalStorageDirectory(); //not necessarily the SD card path (!)
                File regFile = new File(directory.getAbsolutePath() + "/MaPS/products.txt");
                Log.d("debug", "4");
                try {
                    FileReader f = new FileReader(regFile);
                    BufferedReader br = new BufferedReader(f);
                    while (true) {
                        CharSequence buff = br.readLine();
                        if (buff == null)
                            break;
                        Log.d("debug", "5");
                        int i = 0;

                        String buffCard = "";

                        //leggo il numero della carta
                        while (!Character.isWhitespace(buff.charAt(i)))
                            buffCard = buffCard + buff.charAt(i++);

                        Log.d("code", "Num card: " + buffCard);

                        if (buffCard.contentEquals(re)) {
                            //leggo il nome del prodotto
                            while (!Character.isWhitespace(buff.charAt(++i)))
                                product_name = product_name + buff.charAt(i);
                            //leggo gli ingredienti
                            for (; i < buff.length(); i++)
                                ingredients = ingredients + buff.charAt(i);

                            Log.d("code", "Prod name: " + product_name);
                            Log.d("code", "Ing: " + ingredients);

                            br.close();
                            f.close();

                            Intent product_info = new Intent(this, ProductInformation.class);
                            product_info.putExtra("name", product_name);
                            product_info.putExtra("ingr", ingredients);
                            startActivity(product_info);
                        }
                    }
                    br.close();
                    f.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // else continue with any other code you need in the method

    }
}
