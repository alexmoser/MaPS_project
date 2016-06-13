package com.maps.unipi.maps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class Payment extends AppCompatActivity {

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        final TextView productsNum = (TextView) findViewById(R.id.payment_tv_prod);
        final TextView price = (TextView) findViewById(R.id.payment_tv_price);
        final Button btnPay = (Button) findViewById(R.id.payment_bt_pay);

        btnPay.setClickable(true);
        btnPay.setEnabled(true);

        int count = 0;
        for(ShoppingCartElement element : ActionSelection.shoppingCart){
            count += element.getQuantity();
        }
        productsNum.setText(Integer.toString(count));
        price.setText(Float.toString(Utilities.computeTotal(ActionSelection.shoppingCart)) + "€");

        //NFC
        PackageManager pm = this.getPackageManager();
        // Check whether NFC is available on device
        if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            // NFC is not available on the device.
            Utilities.showErrorDialog(this, "The device does not has NFC hardware!");
        }
        // Check whether device is running Android 4.1 or higher
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            // Android Beam feature is not supported.
            Utilities.showErrorDialog(this, "Android Beam is not supported!");
        }
    }

    @Override
    public void onBackPressed(){
        Intent action_selection = new Intent(this, ActionSelection.class);
        startActivity(action_selection);
    }

    public void onClickPay(View v){

        Button btnPay = (Button)v;
        btnPay.setEnabled(false);
        btnPay.setClickable(false);

        Utilities.showMessage("Please place the phone on the NFC reader to complete the payment", getApplicationContext());

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
            Utilities.showErrorDialog(this, "Please enable NFC!");
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        // Check whether Android Beam feature is enabled on device
        else if(!nfcAdapter.isNdefPushEnabled()) {
            // Android Beam is disabled, show the settings UI
            // to enable Android Beam
            Utilities.showErrorDialog(this, "Please enable Android Beam!");
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        }
        else {
            // NFC and Android Beam both are enabled

            if(!canWriteOnExternalStorage()) {
                Utilities.showErrorDialog(this, "Cannot create ticket file!");
                return;
            }

            //create file to send
            String fileName = "ticket.txt";
            File directory = Environment.getExternalStorageDirectory();
            Log.d("debug1", "directory path: " + directory.getAbsolutePath());
            File fileDirectory = new File(directory.getAbsolutePath() + "/MaPS");
            fileDirectory.mkdirs();
            File fileToTransfer = new File(fileDirectory, fileName);

            try {
                FileOutputStream outputStream = new FileOutputStream(fileToTransfer, false);
                OutputStreamWriter sw = new OutputStreamWriter(outputStream);
                sw.write(getTicket());
                sw.flush();
                sw.close();
                outputStream.close();
            }
            catch(IOException e){
                //Error while creating file
                e.printStackTrace();
            }

            fileToTransfer.setReadable(true, false);

            nfcAdapter.setBeamPushUris(new Uri[]{Uri.fromFile(fileToTransfer)}, this);

            Log.d("debug1", "message sent!");
        }

        //elimino il carrello
        ActionSelection.shoppingCart.clear();

        //TODO send to new activity after successfull beaming
        //Intent paymentSuccessfull = new Intent(this, ActionSelection.class);
        //startActivity(paymentSuccessfull);
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

    public static boolean canWriteOnExternalStorage() {
        // get the state of your external storage
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // if storage is mounted return true
            Log.d("sTag", "Yes, can write to external storage.");
            return true;
        }
        return false;
    }
}
