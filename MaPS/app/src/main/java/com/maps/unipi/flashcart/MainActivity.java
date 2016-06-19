package com.maps.unipi.flashcart;

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

import org.mindrot.jbcrypt.BCrypt;

public class MainActivity extends AppCompatActivity {

    // DataBase reference
    public static Firebase rootRef;
    public static String cardNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        // Get a reference to the DB
        rootRef = new Firebase("https://project-8777103889904424829.firebaseio.com");
    }

    @Override
    protected void onResume(){
        super.onResume();
        final EditText etCard = (EditText) findViewById(R.id.main_et_card);
        final EditText etPassword = (EditText) findViewById(R.id.main_et_password);
        etCard.setText(null);
        etPassword.setText(null);
    }

    @Override
    public void onBackPressed(){
        // Close the application when going back from MainActivity
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public void onClickLogin(View v) {

        final Resources myRes = getResources();
        final EditText etCard = (EditText) findViewById(R.id.main_et_card);
        final EditText etPassword = (EditText) findViewById(R.id.main_et_password);
        final CharSequence card = etCard.getText();
        final CharSequence password = etPassword.getText();

        if(password.toString().isEmpty() || card.toString().isEmpty()){
            Utilities.showErrorDialog(this, myRes.getText(R.string.unsuccess).toString());
            return;
        }
        // Get a reference to the users in the DB
        final Firebase ref = rootRef.child("users");
        // Attach a listener to read the data from users reference
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()){
                    User user = userSnapshot.getValue(User.class);
                    /* Check inserted data correctness */
                    boolean passOK = BCrypt.checkpw(password.toString(), user.getPassword());
                    if(user.getCard().contentEquals(card) && passOK){
                        cardNumber = user.getCard();
                        Intent welcome = new Intent(MainActivity.this, WelcomeActivity.class);
                        /* Welcome activity parameters */
                        welcome.putExtra("name", user.getName());
                        welcome.putExtra("surname", user.getSurname());
                        welcome.putExtra("card", user.getCard());
                        startActivity(welcome);
                        return;
                    }
                }
                Utilities.showErrorDialog(MainActivity.this, myRes.getText(R.string.unsuccess).toString());
                etCard.setText(null);
                etPassword.setText(null);
            }
            @Override
            public void onCancelled(FirebaseError e){
                System.out.println("The read failed: " + e.getMessage());
            }
        });
    }

    public void onClickJoin(View v){
        Intent registration_form = new Intent(this, RegistrationActivity.class);
        startActivity(registration_form);
    }

    public void onClickScanCard(View v){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan your card")
                    .setBeepEnabled(true)
                    .setCaptureActivity(CaptureActivityPortrait.class)
                    .initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanResult != null) {

            final String scannedValue = scanResult.getContents();

            if(scannedValue != null){
                // Get a reference to the users in the DB
                final Firebase ref = rootRef.child("users");
                // Attach a listener to read the data from users reference
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        // Check if the scanned card is in the DB
                        for (DataSnapshot userSnapshot : snapshot.getChildren()){
                            User user = userSnapshot.getValue(User.class);
                            /**
                             * The barcode scanner does not recognize the first digit of the code
                             * when it is a 0 and the barcode follows a different standard (EAN-13)
                             * Check if both valid
                             * NB: scanner might still not work for different standards
                             * */
                            String cardDB = Utilities.removeInitialZero(user.getCard());
                            if(user.getCard().contentEquals(scannedValue) || cardDB.contentEquals(scannedValue)){
                                cardNumber = user.getCard();
                                Intent welcome = new Intent(MainActivity.this, WelcomeActivity.class);
                                welcome.putExtra("name", user.getName());
                                welcome.putExtra("surname", user.getSurname());
                                welcome.putExtra("card", user.getCard());
                                startActivity(welcome);
                                return;
                            }
                        }
                        Utilities.showErrorDialog(MainActivity.this, getResources().getText(R.string.noregcard).toString());
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

