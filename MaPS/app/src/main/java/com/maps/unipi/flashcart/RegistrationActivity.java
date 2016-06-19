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

import org.mindrot.jbcrypt.BCrypt;


public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    @Override
    protected void onResume(){
        super.onResume();
        final EditText etName = (EditText) findViewById(R.id.reg_et_name);
        final EditText etSurname = (EditText) findViewById(R.id.reg_et_surname);
        final EditText etCard = (EditText) findViewById(R.id.reg_et_card);
        final EditText etPassword = (EditText) findViewById(R.id.reg_et_pass);
        final EditText etPasswordConfirm = (EditText) findViewById(R.id.reg_et_confpass);
        etName.setText(null);
        etSurname.setText(null);
        etCard.setText(null);
        etPassword.setText(null);
        etPasswordConfirm.setText(null);
    }

    public void onClickDone(View v) {
        final Resources myRes = getResources();

        final EditText etName = (EditText) findViewById(R.id.reg_et_name);
        final EditText etSurname = (EditText) findViewById(R.id.reg_et_surname);
        final EditText etCard = (EditText) findViewById(R.id.reg_et_card);
        final EditText etPassword = (EditText) findViewById(R.id.reg_et_pass);
        final EditText etPasswordConfirm = (EditText) findViewById(R.id.reg_et_confpass);

        final CharSequence name = etName.getText();
        final CharSequence surname = etSurname.getText();
        final CharSequence card = etCard.getText();
        final CharSequence password = etPassword.getText();
        final CharSequence confPassword = etPasswordConfirm.getText();

        if(name.toString().isEmpty() || surname.toString().isEmpty() || card.toString().isEmpty() || password.toString().isEmpty() || confPassword.toString().isEmpty()){
            Utilities.showErrorDialog(this, myRes.getText(R.string.empty_fields).toString());
            return;
        }

        if(!password.toString().contentEquals(confPassword.toString())){
            Utilities.showErrorDialog(this, myRes.getText(R.string.passmis).toString());
            etPassword.setText(null);
            etPasswordConfirm.setText(null);
            etPassword.requestFocus();
            return;
        }

        // Encode password (salt is 10 by default)
        final String passDigest = BCrypt.hashpw(password.toString(), BCrypt.gensalt());

        // Get a reference to the users in the DB
        final Firebase ref = MainActivity.rootRef.child("users");
        // Attach a listener to read the data at our users reference
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Check if card already registered
                for (DataSnapshot userSnapshot : snapshot.getChildren()){
                    User user = userSnapshot.getValue(User.class);
                    if(user.getCard().equals(card)){
                        Utilities.showErrorDialog(RegistrationActivity.this, myRes.getText(R.string.already_registered).toString());
                        etCard.setText(null);
                        etCard.requestFocus();
                        return;
                    }
                }

                // Insert user in the DB
                User user = new User (name.toString(), surname.toString(), card.toString(), passDigest);
                ref.push().setValue(user);
                MainActivity.cardNumber = user.getCard();

                // User is automatically logged
                Intent welcome = new Intent(RegistrationActivity.this, WelcomeActivity.class);
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
