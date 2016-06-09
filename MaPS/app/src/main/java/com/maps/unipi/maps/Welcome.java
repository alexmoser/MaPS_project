package com.maps.unipi.maps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;

public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView name = (TextView) findViewById(R.id.welcome_tv_name);
        TextView surname = (TextView) findViewById(R.id.welcome_tv_surname);
        name.setText(getIntent().getExtras().getString("name"));
        surname.setText(getIntent().getExtras().getString("surname"));
    }

    public void onClickStartShopping(View v){
        Intent action_selection = new Intent(this, ActionSelection.class);
        startActivity(action_selection);
    }
}
