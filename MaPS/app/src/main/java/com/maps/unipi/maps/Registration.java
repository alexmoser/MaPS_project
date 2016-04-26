package com.maps.unipi.maps;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.*;

public class Registration extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Button done = (Button) findViewById(R.id.reg_b_done);
        done.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {



        EditText reg_name = (EditText) findViewById(R.id.reg_et_name);
        String name = reg_name.getText().toString();

        try{

            FileOutputStream f = openFileOutput("prova.txt", Context.MODE_PRIVATE);
            f.write(name.getBytes());
            f.close();
        }
        catch (IOException e){}

    }
}
