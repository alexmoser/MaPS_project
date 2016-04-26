package com.maps.unipi.maps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button login = (Button) findViewById(R.id.main_b_login);
        Button join  = (Button) findViewById(R.id.main_b_join);
        login.setOnClickListener(this);
        join.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){

        if(v.getId() == R.id.main_b_login){

        }
        else if(v.getId() == R.id.main_b_join){

            Intent registration_form = new Intent(this, Registration.class);
            startActivity(registration_form);
        }

    }
}
