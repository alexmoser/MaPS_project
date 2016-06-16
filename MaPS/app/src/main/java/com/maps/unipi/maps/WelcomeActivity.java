package com.maps.unipi.maps;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class WelcomeActivity extends AppCompatActivity {

    private String card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        final TextView name = (TextView) findViewById(R.id.welcome_tv_name);
        final TextView surname = (TextView) findViewById(R.id.welcome_tv_surname);
        final TextView textViewBarcode = (TextView) findViewById(R.id.welcome_tv_barcode);
        card = getIntent().getExtras().getString("card");
        name.setText(getIntent().getExtras().getString("name"));
        surname.setText(getIntent().getExtras().getString("surname"));
        textViewBarcode.setText(card);
    }

    @Override
    public void onBackPressed(){
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
    }

    public void onClickStartShopping(View v){
        Intent action_selection = new Intent(this, ActionSelectionFragmentActivity.class);
        startActivity(action_selection);
    }

    public void onClickUnsubscribe(View v){
        // confirm unsubscription dialog
        final Boolean confirm = new Boolean(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_unsubscribing)
                .setMessage(R.string.message_unsubscribing)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Yes button
                        //Get a reference to our users
                        final Firebase ref = MainActivity.rootRef.child("users");
                        // Attach a listener to read the data at our users reference
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                //delete a user
                                for (DataSnapshot userSnapshot : snapshot.getChildren()){
                                    User user = userSnapshot.getValue(User.class);
                                    if((user.getCard()).equals(card)){
                                        (ref.child(userSnapshot.getKey())).removeValue();
                                        Utilities.showMessage(getResources().getText(R.string.unsubscribe_success), getApplicationContext());
                                        Intent mainActivity = new Intent(WelcomeActivity.this, MainActivity.class);
                                        startActivity(mainActivity);
                                        return;
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(FirebaseError e){
                                System.out.println("The read failed: " + e.getMessage());
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked No button
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
