package com.maps.unipi.maps;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.mindrot.jbcrypt.BCrypt;


public class MainActivity extends AppCompatActivity {

    static Firebase rootRef;
    static String cardNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_launch_no_circle);*/
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

    //in questo modo l'applicazione viene chiusa in ogni caso (senza se ad esempio premo back in action selection arrivo qua ma se premo di nuovo back si apre di nuovo action selection)
    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public void onClickLogin(View v) {
        final Resources myRes = getResources();
        final Context ctx = getApplicationContext();
        final EditText logCard = (EditText) findViewById(R.id.main_et_card);
        final EditText logPass = (EditText) findViewById(R.id.main_et_pass);
        final CharSequence card = logCard.getText();
        final CharSequence pass = logPass.getText();

        if(pass.toString().isEmpty() || card.toString().isEmpty()){
            Utilities.showErrorDialog(this, myRes.getText(R.string.unsuccess).toString());
            return;
        }
        // Get a reference to our users
        Firebase ref = rootRef.child("users");
        // Attach a listener to read the data at our users reference
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //Log.d("debug", "There are " + snapshot.getChildrenCount() + " users");
                for (DataSnapshot userSnapshot : snapshot.getChildren()){
                    User user = userSnapshot.getValue(User.class);
                    boolean passOK = BCrypt.checkpw(pass.toString(), user.getPassword());
                    if(user.getCard().contentEquals(card.toString()) && passOK){
                        Intent welcome = new Intent(MainActivity.this, Welcome.class);
                        welcome.putExtra("name", user.getName());
                        welcome.putExtra("surname", user.getSurname());
                        welcome.putExtra("card", user.getCard());
                        startActivity(welcome);
                        cardNumber = user.getCard();
                        return;
                    }
                }
                Utilities.showErrorDialog(MainActivity.this, myRes.getText(R.string.unsuccess).toString());
                logCard.setText(null);
                logPass.setText(null);
            }
            @Override
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
                        for (DataSnapshot userSnapshot : snapshot.getChildren()){
                            User user = userSnapshot.getValue(User.class);
                            if(user.getCard().contentEquals(re)){
                                Intent welcome = new Intent(MainActivity.this, Welcome.class);
                                welcome.putExtra("name", user.getName());
                                welcome.putExtra("surname", user.getSurname());
                                welcome.putExtra("card", user.getCard());
                                startActivity(welcome);
                                cardNumber = user.getCard();
                                return;
                            }
                        }
                        Utilities.showErrorDialog(MainActivity.this, myRes.getText(R.string.noregcard).toString());
                    }
                    @Override
                    public void onCancelled(FirebaseError e){
                        System.out.println("The read failed: " + e.getMessage());
                    }
                });
            }
        }
    }
}

