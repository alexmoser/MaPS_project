package com.maps.unipi.maps;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.utilities.*;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.util.LinkedList;
import java.util.List;

import java.util.ArrayList;


public class ActionSelection extends FragmentActivity {
    /**
     * The {@link PagerAdapter} that will provide fragments representing
     * each object in a collection. We use a {@link FragmentStatePagerAdapter}
     * derivative, which will destroy and re-create fragments as needed, saving and restoring their
     * state in the process. This is important to conserve memory and is a best practice when
     * allowing navigation between objects in a potentially large collection.
     */
    CollectionPagerAdapter myCollectionPagerAdapter;

    /**
     * The {@link ViewPager} that will display the object collection.
     */
    static ViewPager mViewPager;
    //ho definito i filtri e il carrello statici cosi non c'è bisogno di inviarli da una activity all'altra
    static ArrayList<String> filters = new ArrayList<>();
    static ArrayList<Product> shoppingCart = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_selection);

        ScanProduct.goBack = false;

        // Create an adapter that when requested, will return a fragment representing an object in
        // the collection.
        //
        // ViewPager and its adapters use support library fragments, so we must use
        // getSupportFragmentManager.
        myCollectionPagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager());

        // Set up action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
        //actionBar.setDisplayHomeAsUpEnabled(true); //se lo metto da errore

        // Set up the ViewPager, attaching the adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(myCollectionPagerAdapter);
    }

    @Override
    public void onBackPressed(){
        //TODO mettere un messaggio di conferma
        Intent main_activity = new Intent(this, MainActivity.class);
        startActivity(main_activity);
        //svuoto il carrello e i filtri
        shoppingCart.clear();
        filters.clear();
        FiltersFragment.firstCreationView = true;//altrimenti i filtri non vengono caricati al successivo login
    }

    /**
     * A {@link FragmentStatePagerAdapter} that returns a fragment
     * representing an object in the collection.
     */
    public static class CollectionPagerAdapter extends FragmentStatePagerAdapter {

        public CollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            switch (i) {
                case 0:
                    fragment = new NewPurchaseFragment();
                    return fragment;
                case 1:
                    fragment = new LastPurchaseFragment();
                    return fragment;
                case 2:
                    fragment = new FiltersFragment();
                    return fragment;
                default:
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            //Return the number of fragment
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "New Purchase";

                case 1:
                    return "Last Purchase";

                case 2:
                    return "Filters";

                default:
                    return "";

            }
        }
    }

    public static class FiltersFragment extends Fragment {

        public static final String ARG_OBJECT = "object";
        static boolean firstCreationView = true;//serve perche altrimenti ogni volta che viene creato il frammento vengono aggiunti i filtri della shared preferece e quindi anche quelli che magari l'utente ha eliminato
        View rootView;
        ListView filtersList;
        ArrayAdapter<String> adapter;//serve per collegare la lista all'array string filters

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.filters, container, false);
            //imposto un listener sul bottone add filter
            final Button add = (Button) rootView.findViewById(R.id.filters_b_addfilter);
            add.setOnClickListener(addFilter);
            if(firstCreationView) {
                //carico i filtri salvati nelle shared preference
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                int numFilters = sharedPref.getInt("#filters", 0);
                for (int key = 0; key < numFilters; key++)
                    filters.add(sharedPref.getString("f" + Integer.toString(key), "filter"));
            }
            //collego la lista di filtri all'adaptor
            filtersList = (ListView) rootView.findViewById(R.id.filters_lv_fillist);
            adapter = new ArrayAdapter<> (getActivity(), android.R.layout.simple_list_item_1, filters);
            filtersList.setAdapter(adapter);
            //imposto un listener sul click di un elemento della lista
            filtersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, final View component, int pos, long id){
                    // recupero il nome del filtro memorizzato nella riga tramite l'ArrayAdapter
                    final String filter = (String) adapterView.getItemAtPosition(pos);
                    //TODO trovare il modo di far comparire un messaggio di conferma cancellazione
                    filters.remove(filter);
                    //è come aggiornare la lista..la collego infatti con il nuovo adapter
                    filtersList.setAdapter(adapter);
                }
            });
            firstCreationView = false;
            return rootView;
        }

        View.OnClickListener addFilter = new View.OnClickListener() {
            public void onClick(View v) {

                final EditText filter = (EditText) rootView.findViewById(R.id.filters_et_filter);
                final CharSequence  filter_name = filter.getText();
                if(!filters.contains(filter_name.toString()) && !filter_name.toString().isEmpty()) {
                    filters.add(filter_name.toString());
                    filtersList.setAdapter(adapter);
                }
                filter.setText(null);
                //TODO trovare il modo di far comparire un messaggio nel caso in cui il filtro è gia presente
                mViewPager.setCurrentItem(2);
            }
        };
    }

    public static class LastPurchaseFragment extends Fragment {

        public static final String ARG_OBJECT = "object";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView;
            rootView = inflater.inflate(R.layout.last_purchase, container, false);
            ArrayList<Product> lastPurchase = new ArrayList<>();
            Product product = new Product();
            //carico i prodotti salvati nelle shared preference
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            int numProducts = sharedPref.getInt("#products", 0);
            for(int key = 0; key < numProducts; key++){
                product.setName(sharedPref.getString("n" + Integer.toString(key), "product"));//TODO aggiungere come ID anche il numero della carta cosi da poter salvare spese diverse in base alla carta usata
                product.setPrice(sharedPref.getFloat("p" + Integer.toString(key), 0));          //TODO invece che aggiungerlo qua forse conviene creare un file nuovo per ogni carta (basta usare getSharedPreference e mettere nel nome del file l id della carta)
                lastPurchase.add(product);
            }
            final TextView total = (TextView) rootView.findViewById(R.id.lastpurch_tv_totalprice);
            //Calcolo il prezzo totale e lo mostro in una text view
            float totalPrice = Utilities.computeTotal(lastPurchase);
            total.setText(Float.toString(totalPrice) + "€");
            //collego la lista di prodotti all'adaptor
            ListView productsList = (ListView) rootView.findViewById(R.id.lastpurch_lv_products);
            final CustomAdapter adapter = new CustomAdapter(getActivity(), R.layout.rowcustom, lastPurchase);
            productsList.setAdapter(adapter);
            return rootView;
        }
    }

    public static class NewPurchaseFragment extends Fragment {

        public static final String ARG_OBJECT = "object";
        View rootView;
        ListView productsList;
        TextView total;
        CustomAdapter adapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.new_purchase, container, false);
            final Button button1 = (Button) rootView.findViewById(R.id.newpurch_b_addprod);
            button1.setOnClickListener(addProduct);
            final Button button2 = (Button) rootView.findViewById(R.id.newpurch_b_endspending);
            button2.setOnClickListener(endSpending);
            total = (TextView) rootView.findViewById(R.id.newpurch_tv_totalprice);
            //Calcolo il prezzo totale e lo mostro in una text view
            float totalPrice = Utilities.computeTotal(shoppingCart);
            total.setText(Float.toString(totalPrice) + "€");
            //collego la lista di prodotti all'adaptor
            productsList = (ListView) rootView.findViewById(R.id.newpurch_lv_products);
            adapter = new CustomAdapter(getActivity(), R.layout.rowcustom, shoppingCart);
            productsList.setAdapter(adapter);
            //imposto un listener sul click di un elemento della lista
            productsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, final View component, int pos, long id){
                    // recupero il prodotto memorizzato nella riga tramite l'ArrayAdapter
                    final Product product = (Product) adapterView.getItemAtPosition(pos);
                    //TODO trovare il modo di far comparire un messaggio di conferma cancellazione
                    shoppingCart.remove(product);
                    productsList.setAdapter(adapter);
                    //Aggiorno il prezo totale
                    float updateTotalPrice = Utilities.computeTotal(shoppingCart);
                    total.setText(Float.toString(updateTotalPrice) + "€");
                }
            });
            return rootView;
        }

        View.OnClickListener addProduct = new View.OnClickListener() {
            public void onClick(View v) {
                Intent scan_product = new Intent(getActivity(), ScanProduct.class);
                startActivity(scan_product);
                mViewPager.setCurrentItem(0);
            }
        };

        View.OnClickListener endSpending = new View.OnClickListener() {
            public void onClick(View v) {
                //TODO gestire la fine della spesa (se si deve fare altro)
                //salvo i dati dell'ultima spesa nelle shared preference
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                //rimuovo la vecchia spesa e vecchi filtri
                editor.clear();
                editor.commit();//TODO capire se serve oppure basta quello dopo
                //aggiungo nuova spesa e nuovi filtri
                int key = 0;
                for(Product product : shoppingCart){
                    editor.putString("n" + Integer.toString(key), product.getName());
                    editor.putFloat("p" + Integer.toString(key++), product.getPrice());
                }
                editor.putInt("#products", key);
                key = 0;
                for(String filter : filters)
                    editor.putString("f" + Integer.toString(key++), filter);
                editor.putInt("#filters", key);
                editor.commit();
                //elimino il carrello e aggiorno la lista
                shoppingCart.clear();
                productsList.setAdapter(adapter);
                //Aggiorno il prezzo totale
                total.setText(Float.toString(0) + "€");
                mViewPager.setCurrentItem(0);
            }
        };
    }
}
