package com.maps.unipi.maps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Payment extends AppCompatActivity {

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        final TextView productsNum = (TextView) findViewById(R.id.payment_tv_prod);
        final TextView price = (TextView) findViewById(R.id.payment_tv_price);

        int count = 0;
        for(ShoppingCartElement element : ActionSelection.shoppingCart){
            count += element.getQuantity();
        }
        productsNum.setText(Integer.toString(count));
        price.setText(Float.toString(Utilities.computeTotal(ActionSelection.shoppingCart)));

        //NFC
        PackageManager pm = this.getPackageManager();
        // Check whether NFC is available on device
        if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            // NFC is not available on the device.
            Utilities.showMessage("The device does not has NFC hardware!", getApplicationContext());
        }
        // Check whether device is running Android 4.1 or higher
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            // Android Beam feature is not supported.
            Utilities.showMessage("Android Beam is not supported!", getApplicationContext());
        }
    }

    @Override
    public void onBackPressed(){
        Intent action_selection = new Intent(this, ActionSelection.class);
        startActivity(action_selection);
    }

    public void onClickPay(View v){

        //salvo i dati dell'ultima spesa nelle shared preference
        SharedPreferences sharedPref = getSharedPreferences(MainActivity.cardNumber, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //rimuovo la vecchia spesa e vecchi filtri
        editor.clear();
        //aggiungo nuova spesa e nuovi filtri
        int key = 0;
        for(ShoppingCartElement element : ActionSelection.shoppingCart){
            editor.putString("n" + Integer.toString(key), element.getProduct().getName());
            editor.putInt("q" + Integer.toString(key), element.getQuantity());
            editor.putFloat("p" + Integer.toString(key++), element.getProduct().getPrice());
        }
        editor.putInt("#products", key);
        key = 0;
        for(String filter : ActionSelection.filters)
            editor.putString("f" + Integer.toString(key++), filter);
        editor.putInt("#filters", key);
        editor.commit();

        //Gestire pagamento con NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Check whether NFC is enabled on device
        if(!nfcAdapter.isEnabled()){
            // NFC is disabled, show the settings UI
            // to enable NFC
            Utilities.showMessage("Please enable NFC!", getApplicationContext());
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        // Check whether Android Beam feature is enabled on device
        else if(!nfcAdapter.isNdefPushEnabled()) {
            // Android Beam is disabled, show the settings UI
            // to enable Android Beam
            Utilities.showMessage("Please enable Android Beam!", getApplicationContext());
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        }
        else {
            // NFC and Android Beam both are enabled

            //create file to send
            File fileDirectory = getApplicationContext().getFilesDir();
            String fileName = "ticket.txt";
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                outputStream.write(getTicket().getBytes());
                outputStream.close();
            }
            catch(Exception e){
                //Error while creating file
                e.printStackTrace();
            }

            File fileToTransfer = new File(fileDirectory, fileName);
            fileToTransfer.setReadable(true, false);

            nfcAdapter.setBeamPushUris(
                    new Uri[]{Uri.fromFile(fileToTransfer)}, this);

            //fileToTransfer.delete();
        }

        //elimino il carrello
        ActionSelection.shoppingCart.clear();
    }

    private String getTicket(){

        String ticket = "";

        for(ShoppingCartElement element : ActionSelection.shoppingCart){
            ticket += element.getProduct().getName() + ",";
            ticket += element.getQuantity() + ",";
            ticket += element.getProduct().getPrice()*element.getQuantity() + ";\n";
        }
            ticket += "TOT.\t" + Utilities.computeTotal(ActionSelection.shoppingCart);

        return ticket;
    }
}
