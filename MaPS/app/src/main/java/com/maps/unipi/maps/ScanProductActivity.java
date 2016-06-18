package com.maps.unipi.maps;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class ScanProductActivity extends AppCompatActivity {

    // Needs to be declared public static because used also by ProductInformationAddActivity and ProductInformationActivity
    public static Product productScanned;

    // Needed to discriminate whether to go back to action selection or not (on back pressed from scanActivity and ProductInformation)
    public static boolean goBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(!goBack) {
            goBack = true;
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setPrompt("Scan a product");
            integrator.setBeepEnabled(true);
            integrator.setCaptureActivity(CaptureActivityPortrait.class);
            integrator.setOrientationLocked(false);
            integrator.initiateScan();
        }
        else{
            Intent action_selection = new Intent(this, ActionSelectionFragmentActivity.class);
            startActivity(action_selection);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        final Resources myRes = getResources();
        final Context ctx = getApplicationContext();
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanResult != null) {
            final String scannedValue = scanResult.getContents();
            if(scannedValue != null) {
                // Get a reference to the products in the DB
                Firebase ref = MainActivity.rootRef.child("products");
                // Attach a listener to read the data from products reference
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        // Search the DB to find the scanned product
                        for (DataSnapshot prodSnapshot : snapshot.getChildren()){
                            productScanned = prodSnapshot.getValue(Product.class);
                            if(productScanned.getBarcode().equals(scannedValue)){
                                Intent product_info = new Intent(ScanProductActivity.this, ProductInformationAddActivity.class);
                                startActivity(product_info);
                                return;
                            }
                        }
                        Utilities.showMessage(myRes.getText(R.string.prodnotfound), ctx);
                    }
                    public void onCancelled(FirebaseError e){
                        System.out.println("The read failed: " + e.getMessage());
                    }
                });
            }
        }
    }
}

