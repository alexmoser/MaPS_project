package com.maps.unipi.flashcart;

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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class PaymentActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        final TextView tvTotProducts = (TextView) findViewById(R.id.payment_tv_prod);
        final TextView tvPrice = (TextView) findViewById(R.id.payment_tv_price);
        final Button btnPay = (Button) findViewById(R.id.payment_bt_pay);

        btnPay.setClickable(true);
        btnPay.setEnabled(true);

        // Count total number of products
        int count = 0;
        for(ShoppingCartElement element : ActionSelectionFragmentActivity.shoppingCart){
            count += element.getQuantity();
        }

        tvTotProducts.setText(Integer.toString(count));
        tvPrice.setText(Utilities.roundTwoDecimal(Utilities.computeTotal(ActionSelectionFragmentActivity.shoppingCart)) + "€");
    }

    @Override
    public void onBackPressed() {
        Intent action_selection = new Intent(this, ActionSelectionFragmentActivity.class);
        startActivity(action_selection);
    }

    public void onClickPay(View v){
        // TODO provide an alternative way to pay/print the ticket to whose who don't have NFC?
        // TODO save purchase anyway?

        // Check whether NFC is available on device
        PackageManager pm = this.getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            // NFC is not available on the device
            Utilities.showErrorDialog(this, getResources().getText(R.string.no_NFC));
            return;
        }
        // Check whether device is running Android 4.1 or higher
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            // Android Beam feature is not supported.
            Utilities.showErrorDialog(this, getResources().getText(R.string.no_android_beam));
            return;
        }

        // Disable the button
        Button btnPay = (Button)v;
        btnPay.setEnabled(false);
        btnPay.setClickable(false);

        // Save data of the new purchase to the shared preference (this is going to be the "last purchase" next time
        SharedPreferences sharedPref = getSharedPreferences("p" + MainActivity.cardNumber, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        // Clear the memorized last purchase
        editor.clear();
        // Store the new one
        int key = 0;
        for(ShoppingCartElement element : ActionSelectionFragmentActivity.shoppingCart){
            editor.putString("n" + Integer.toString(key), element.getProduct().getName());
            editor.putInt("q" + Integer.toString(key), element.getQuantity());
            editor.putFloat("p" + Integer.toString(key++), element.getProduct().getPrice());
        }
        editor.putInt("#products", key);
        editor.commit();

        /* *
         * NOTE: the code below uses Android Beam to send a file "ticket.txt" to another phone provided
         * with NFC. In a real-life scenario we would need to change this part of the code with a
         * simpler implementation that only sends an NFC tag (e.g. a sequence of Bytes[] representing
         * the ticket as structured in "getTicket()" to a NFC reader.
         * It will be this device function to manage the printing of a real ticket and handle the
         * user payment.
         * */

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Check whether NFC is enabled on device
        if(!nfcAdapter.isEnabled()){
            // NFC is disabled, show the settings UI to enable NFC
            Utilities.showMessage(getResources().getText(R.string.enable_NFC), getApplicationContext());
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        // Check whether Android Beam feature is enabled on device
        else if(!nfcAdapter.isNdefPushEnabled()) {
            // Android Beam is disabled, show the settings UI to enable Android Beam
            Utilities.showMessage(getResources().getText(R.string.enable_android_beam), getApplicationContext());
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        }
        else {
            // NFC and Android Beam are both enabled
            if(!canWriteOnExternalStorage()) {
                // No permission to write on external storage
                Utilities.showErrorDialog(this, getResources().getText(R.string.error_ticket_creation));
                return;
            }

            // Everything is fine, continue
            Utilities.showLongMessage(getResources().getText(R.string.place_to_NFC), getApplicationContext());

            /* *
             * Create the ticket file to send. Such file is called "ticket.txt" and is stored on the
             * external storage, in the directory "FlashCartTicket". This is because sending a file
             * that is stored in the private memory of the application is not possible.
             * */
            String fileName = "ticket.txt";
            File directory = Environment.getExternalStorageDirectory();
            File fileDirectory = new File(directory.getAbsolutePath() + "/FlashCartTicket");
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
                // Error while creating file
                e.printStackTrace();
            }

            // Set file permission as world-readable to make the receiving device be able to open it
            fileToTransfer.setReadable(true, false);

            // Send the file
            nfcAdapter.setBeamPushUris(new Uri[]{Uri.fromFile(fileToTransfer)}, this);

            // TODO get a reply from the NFC reader for confirmation?

            // Clear the shopping cart
            ActionSelectionFragmentActivity.shoppingCart.clear();
        }
    }

    /* *
     * Constructs the ticket to be sent to the NFC reader machine.
     * The adopted syntax is the following: "productName,quantity,price;\n"
     * With the final row to be: "TOT.  totalPrice"
     * @return the ticket as String
     * */
    private String getTicket(){

        String ticket = "";

        for(ShoppingCartElement element : ActionSelectionFragmentActivity.shoppingCart){
            ticket += element.getProduct().getName() + ";";
            ticket += element.getQuantity() + ";";
            ticket += Utilities.roundTwoDecimal(element.getProduct().getPrice()*element.getQuantity()) + ";\n";
        }
            ticket += "TOT.\t" + Utilities.roundTwoDecimal(Utilities.computeTotal(ActionSelectionFragmentActivity.shoppingCart));

        return ticket;
    }

    /* *
     * Checks whether the external storage is available or not
     * @return true if available, false otherwise
     * */
    public static boolean canWriteOnExternalStorage() {
        // get the state of your external storage
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // if storage is mounted return true
            return true;
        }
        return false;
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Utilities.showLongMessage(getResources().getText(R.string.place_to_NFC), getApplicationContext());
    }
}
