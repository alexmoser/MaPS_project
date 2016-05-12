package com.maps.unipi.maps;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class MainActivity extends AppCompatActivity {

    static Firebase rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        //Get a reference to the DB
        rootRef = new Firebase("https://vivid-inferno-9901.firebaseio.com");
    }

    @Override
    protected void onResume(){
        super.onResume();
        EditText logCard = (EditText) findViewById(R.id.main_et_card);
        EditText logPass = (EditText) findViewById(R.id.main_et_pass);
        logCard.setText(null);
        logPass.setText(null);
    }

    public void onClickLogin(View v) {
        final Resources myRes = getResources();
        final Context ctx = getApplicationContext();

        final EditText logCard = (EditText) findViewById(R.id.main_et_card);
        final EditText logPass = (EditText) findViewById(R.id.main_et_pass);
        final CharSequence card = logCard.getText();
        final CharSequence pass = logPass.getText();
        if(pass.toString().equals("") || card.toString().equals("")){
            Utilities.showMessage(myRes.getText(R.string.unsuccess), ctx);
            return;
        }

        // Get a reference to our users
        Firebase ref = rootRef.child("users");
        // Attach an listener to read the data at our users reference
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //Log.d("debug", "There are " + snapshot.getChildrenCount() + " users");
                for (DataSnapshot userSnapshot : snapshot.getChildren()){
                    User user = userSnapshot.getValue(User.class);
                    if(user.getCard().contentEquals(card.toString()) && user.getPassword().contentEquals(pass.toString())){
                        Utilities.showMessage(myRes.getText(R.string.success), ctx);
                        Intent action_selection = new Intent(MainActivity.this, ActionSelection.class);
                        startActivity(action_selection);
                        return;
                    }
                }

                Utilities.showMessage(myRes.getText(R.string.unsuccess), ctx);
                logCard.setText(null);
                logPass.setText(null);
            }
            public void onCancelled(FirebaseError e){
                System.out.println("The read failed: " + e.getMessage());
            }
        });
    }

    public void onClickJoin(View v){
        Intent registration_form = new Intent(this, Registration.class);
        startActivity(registration_form);
    }

    public void onClickScanCard(View v){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan your card");
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        final Resources myRes = getResources();
        final Context ctx = getApplicationContext();

        if (scanResult != null) {
            final String re = scanResult.getContents();
            if(re != null){
                // Get a reference to our users
                Firebase ref = rootRef.child("users");
                // Attach an listener to read the data at our users reference
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        //Log.d("debug", "There are " + snapshot.getChildrenCount() + " users");
                        for (DataSnapshot userSnapshot : snapshot.getChildren()){
                            User user = userSnapshot.getValue(User.class);
                            if(user.getCard().contentEquals(re)){
                                Utilities.showMessage(myRes.getText(R.string.success), ctx);
                                Intent action_selection = new Intent(MainActivity.this, ActionSelection.class);
                                startActivity(action_selection);
                                return;
                            }
                        }

                        Utilities.showMessage(myRes.getText(R.string.noregcard), ctx);
                    }
                    public void onCancelled(FirebaseError e){
                        System.out.println("The read failed: " + e.getMessage());
                    }
                });
            }
        }
    }
 }

