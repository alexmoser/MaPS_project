package com.maps.unipi.maps;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.firebase.client.Firebase;


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

        EditText regName = (EditText) findViewById(R.id.reg_et_name);
        EditText regSurname = (EditText) findViewById(R.id.reg_et_surname);
        EditText regCard = (EditText) findViewById(R.id.reg_et_card);
        EditText regPass = (EditText) findViewById(R.id.reg_et_pass);
        EditText regConfpass = (EditText) findViewById(R.id.reg_et_confpass);

        CharSequence name = regName.getText();
        CharSequence surname = regSurname.getText();
        CharSequence card = regCard.getText();
        CharSequence pass = regPass.getText();
        CharSequence confpass = regConfpass.getText();

        if(name.toString().isEmpty() || surname.toString().isEmpty() || card.toString().isEmpty() || pass.toString().isEmpty() || confpass.toString().isEmpty()){
            Utilities.showMessage(myRes.getText(R.string.unsuccess), ctx);
            return;
        }

        if(!pass.toString().contentEquals(confpass.toString())){
            Utilities.showMessage(myRes.getText(R.string.passmiss), ctx);
            regPass.setText(null);
            regConfpass.setText(null);
            return;
        }

        //Get a reference to our users
        Firebase ref = MainActivity.rootRef.child("users");
        //Insert user in the DB
        User user = new User (name.toString(), surname.toString(), card.toString(), pass.toString());
        ref.push().setValue(user);
        MainActivity.cardNumber = user.getCard();

        Utilities.showMessage(myRes.getText(R.string.success), ctx);
        //User is automatically logged
        Intent welcome = new Intent(this, Welcome.class);
        welcome.putExtra("name", user.getName());
        welcome.putExtra("surname", user.getSurname());
        startActivity(welcome);
    }
}
