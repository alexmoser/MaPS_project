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



public class ScanProduct extends AppCompatActivity {

    static Firebase rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        //Get a reference to the DB
        rootRef = new Firebase("https://vivid-inferno-9901.firebaseio.com");

        /*
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan a product");
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
        */
    }

    @Override
    protected void onResume(){
        super.onResume();

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan a product");
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        final Resources myRes = getResources();
        final Context ctx = getApplicationContext();

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            final String re = scanResult.getContents();
            if(re != null) {
                // Get a reference to our users
                Firebase ref = rootRef.child("products");
                // Attach an listener to read the data at our users reference
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        //Log.d("debug", "There are " + snapshot.getChildrenCount() + " users");
                        for (DataSnapshot prodSnapshot : snapshot.getChildren()){
                            Product prod = prodSnapshot.getValue(Product.class);
                            if(prod.getBarcode().equals(re)){
                                Intent product_info = new Intent(ScanProduct.this, ProductInformation.class);
                                product_info.putExtra("name", prod.getName());
                                product_info.putStringArrayListExtra("ingredients", prod.getIngredients());
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

