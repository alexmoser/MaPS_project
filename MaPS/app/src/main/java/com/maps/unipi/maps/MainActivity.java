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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private final int duration = Toast.LENGTH_SHORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onClickLogin(View v) {
        CharSequence buff;
        String buffPass, buffCard;

        int i;
        CharSequence msg;
        Toast toast;
        Resources myRes = getResources();
        Context context = getApplicationContext();

        EditText logCard = (EditText) findViewById(R.id.main_et_card);
        EditText logPass = (EditText) findViewById(R.id.main_et_pass);

        File directory = Environment.getExternalStorageDirectory(); //not necessarily the SD card path (!)
        File regFile = new File(directory.getAbsolutePath() + "/MaPS/users.txt");

        try {
            CharSequence card = logCard.getText();
            CharSequence pass = logPass.getText();
            if(pass.toString().equals("") || card.toString().equals("")){
                msg = myRes.getText(R.string.unsuccess);
                toast = Toast.makeText(context, msg, duration);
                toast.show();
                return;
            }

            FileReader f = new FileReader(regFile);
            BufferedReader br = new BufferedReader(f);

            while (true) {
                buff = br.readLine();
                if (buff == null)
                    break;

                i = buff.length();

                buffPass = buffCard = "";
                while (!Character.isWhitespace(buff.charAt(--i)))
                    buffPass = buff.charAt(i) + buffPass;
                Log.d("debug", buffPass);

                while (!Character.isWhitespace(buff.charAt(--i)))
                    buffCard = buff.charAt(i) + buffCard;
                Log.d("debug", buffCard);

                if (buffCard.contentEquals(card.toString()) && buffPass.contentEquals(pass.toString())) {

                    msg = myRes.getText(R.string.success);
                    toast = Toast.makeText(context, msg, duration);
                    toast.show();
                    br.close();
                    f.close();
                    Intent action_selection = new Intent(this, ActionSelection.class);
                    startActivity(action_selection);
                }
            }
            Log.d("debug", "1");
            msg = myRes.getText(R.string.unsuccess);
            toast = Toast.makeText(context, msg, duration);
            toast.show();
            logCard.setText(null);
            logPass.setText(null);
            br.close();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickJoin(View v){
        Intent registration_form = new Intent(this, Registration.class);
        startActivity(registration_form);
    }

    public void onClickScanCard(View v){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String re = scanResult.getContents();
            EditText logCard = (EditText) findViewById(R.id.main_et_card);
            logCard.setText(re);

            File directory = Environment.getExternalStorageDirectory(); //not necessarily the SD card path (!)
            File regFile = new File(directory.getAbsolutePath() + "/MaPS/users.txt");

            try {
                FileReader f = new FileReader(regFile);
                BufferedReader br = new BufferedReader(f);

                while (true) {
                    CharSequence buff = br.readLine();
                    if (buff == null)
                        break;

                    int i = buff.length();

                    String buffCard = "";

                    //salto la password
                    while (!Character.isWhitespace(buff.charAt(--i)));

                    while (!Character.isWhitespace(buff.charAt(--i)))
                        buffCard = buff.charAt(i) + buffCard;

                    if (buffCard.contentEquals(re)) {
                        br.close();
                        f.close();
                        Intent action_selection = new Intent(this, ActionSelection.class);
                        startActivity(action_selection);
                    }
                }

                Resources myRes = getResources();
                Context context = getApplicationContext();
                CharSequence msg = myRes.getText(R.string.noregcard);
                Toast toast = Toast.makeText(context, msg, duration);
                toast.show();
                logCard.setText(null);
                br.close();
                f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // else continue with any other code you need in the method

    }
}
