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

import java.util.ArrayList;


public class ScanProduct extends AppCompatActivity {

    //dichiarato static per lo stesso motivo dei filtri e il carrello ci accedo nella activity ProductInformation
    static Product productScanned;

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
        //TODO trovare il modo dopo che parte lo scanner di tornare alla activity ActionSelection
    }

    /*
    @Override
    protected void onResume(){
        super.onResume();

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan a product");
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        final Resources myRes = getResources();
        final Context ctx = getApplicationContext();
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanResult != null) {
            final String re = scanResult.getContents();
            if(re != null) {
                // Get a reference to our products
                Firebase ref = MainActivity.rootRef.child("products");
                // Attach an listener to read the data at our users reference
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot prodSnapshot : snapshot.getChildren()){
                            productScanned = prodSnapshot.getValue(Product.class);
                            if(productScanned.getBarcode().equals(re)){
                                Intent product_info = new Intent(ScanProduct.this, ProductInformation.class);
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

