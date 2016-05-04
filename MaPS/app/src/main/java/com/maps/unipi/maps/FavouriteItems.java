package com.maps.unipi.maps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class FavouriteItems extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_items);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.favouriteitems_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //Handle item selection
        switch (item.getItemId()){
            case R.id.favouriteitems_menu_1:
                //TODO operazioni per aggiungere oggetti
                return true;
            case R.id.favouriteitems_menu_2:
                //TODO operazioni per rimuovere oggetti
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
