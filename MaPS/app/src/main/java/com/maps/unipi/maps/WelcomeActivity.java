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

        final TextView tvName = (TextView) findViewById(R.id.welcome_tv_name);
        final TextView tvSurname = (TextView) findViewById(R.id.welcome_tv_surname);
        final TextView tvBarcode = (TextView) findViewById(R.id.welcome_tv_barcode);

        tvName.setText(getIntent().getExtras().getString("name"));
        tvSurname.setText(getIntent().getExtras().getString("surname"));
        card = getIntent().getExtras().getString("card");
        tvBarcode.setText(card);
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
        // Confirm unsubscription dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_unsubscribe_dialog)
                .setMessage(R.string.message_unsubscribe_dialog)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Yes button
                        // Get a reference to the users in the DB
                        final Firebase ref = MainActivity.rootRef.child("users");
                        // Attach a listener to read the data from users reference
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                // Remove user from DB
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
