package com.maps.unipi.maps;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.*;

public class Registration extends AppCompatActivity{

    private final int duration = Toast.LENGTH_SHORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void onClickDone(View v) {
        Context ctx = getApplicationContext();
        Resources myRes = getResources();

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

        if(name.toString().equals("") || surname.toString().equals("") || card.toString().equals("") || pass.toString().equals("") || confpass.toString().equals("")){
            Utilities.showMessage(myRes.getText(R.string.unsuccess), ctx);
            return;
        }

        CharSequence toSubmit = name + " " + surname + " " + card + " " + pass +"\n";

        if(!pass.toString().contentEquals(confpass.toString())){
            Utilities.showMessage(myRes.getText(R.string.passmiss), ctx);
            regPass.setText(null);
            regConfpass.setText(null);
            return;
        }

        if(!this.isExternalStorageWritable()){
            Log.d("Registration", myRes.getText(R.string.exnotava).toString());
            return;
        }

        // Setup the folder for the log file
        File directory = Environment.getExternalStorageDirectory(); //not necessarily the SD card path (!)
        File logDirectory = new File(directory.getAbsolutePath() + "/MaPS");
        logDirectory.mkdirs();

        try{
            File regFile = new File (logDirectory, "users.txt");
            FileOutputStream f = new FileOutputStream(regFile, true);
            OutputStreamWriter sw = new OutputStreamWriter(f);
            sw.append(toSubmit);
            sw.close();
            f.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Utilities.showMessage(myRes.getText(R.string.success), ctx);

        Intent action_selection = new Intent(this, ActionSelection.class);
        startActivity(action_selection);
    }


}
