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
import com.journeyapps.barcodescanner.Util;

import org.mindrot.jbcrypt.BCrypt;


public class Registration extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    @Override
    protected void onResume(){
        super.onResume();
        EditText regName = (EditText) findViewById(R.id.reg_et_name);
        EditText regSurname = (EditText) findViewById(R.id.reg_et_surname);
        EditText regCard = (EditText) findViewById(R.id.reg_et_card);
        EditText regPass = (EditText) findViewById(R.id.reg_et_pass);
        EditText regConfpass = (EditText) findViewById(R.id.reg_et_confpass);
        regName.setText(null);
        regSurname.setText(null);
        regCard.setText(null);
        regPass.setText(null);
        regConfpass.setText(null);
    }

    public void onClickDone(View v) {
        final Context ctx = getApplicationContext();
        final Resources myRes = getResources();

        final EditText regName = (EditText) findViewById(R.id.reg_et_name);
        final EditText regSurname = (EditText) findViewById(R.id.reg_et_surname);
        final EditText regCard = (EditText) findViewById(R.id.reg_et_card);
        final EditText regPass = (EditText) findViewById(R.id.reg_et_pass);
        final EditText regConfpass = (EditText) findViewById(R.id.reg_et_confpass);

        final CharSequence name = regName.getText();
        final CharSequence surname = regSurname.getText();
        final CharSequence card = regCard.getText();
        final CharSequence pass = regPass.getText();
        final CharSequence confpass = regConfpass.getText();

        if(name.toString().isEmpty() || surname.toString().isEmpty() || card.toString().isEmpty() || pass.toString().isEmpty() || confpass.toString().isEmpty()){
            Utilities.showMessage(myRes.getText(R.string.unsuccess), ctx);
            return;
        }

        if(!pass.toString().contentEquals(confpass.toString())){
            Utilities.showMessage(myRes.getText(R.string.passmiss), ctx);
            regPass.setText(null);
            regConfpass.setText(null);
            regPass.requestFocus();
            return;
        }

        //encoding password (salt is 10 by default)
        final String passDigest = BCrypt.hashpw(pass.toString(), BCrypt.gensalt());

        //Get a reference to our users
        final Firebase ref = MainActivity.rootRef.child("users");

        //Check if card already registered
        // Attach a listener to read the data at our users reference
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()){
                    User user = userSnapshot.getValue(User.class);
                    if(user.getCard().equals(card.toString())){
                        Utilities.showMessage(myRes.getText(R.string.already_registered), ctx);
                        regCard.setText(null);
                        regCard.requestFocus();
                        return;
                    }
                }
                //Insert user in the DB
                User user = new User (name.toString(), surname.toString(), card.toString(), passDigest);
                ref.push().setValue(user);
                MainActivity.cardNumber = user.getCard();

                Utilities.showMessage(myRes.getText(R.string.success), ctx);
                //User is automatically logged
                Intent welcome = new Intent(Registration.this, Welcome.class);
                welcome.putExtra("name", user.getName());
                welcome.putExtra("surname", user.getSurname());
                welcome.putExtra("card", user.getCard());
                startActivity(welcome);
            }
            @Override
            public void onCancelled(FirebaseError e){
                System.out.println("The read failed: " + e.getMessage());
            }
        });
    }
}
