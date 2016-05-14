package com.maps.unipi.maps;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
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
        View rootView;
        ListView filtersList;
        ArrayAdapter<String> adapter;//serve per collegare la lista all'array string filters

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.filters, container, false);
            //imposto un listener sul bottone add filter
            final Button add = (Button) rootView.findViewById(R.id.filters_b_addfilter);
            add.setOnClickListener(addFilter);
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
            return rootView;
        }

        View.OnClickListener addFilter = new View.OnClickListener() {
            public void onClick(View v) {

                final EditText filter = (EditText) rootView.findViewById(R.id.filters_et_filter);
                final CharSequence  filter_name = filter.getText();
                if(!filters.contains(filter_name.toString())) {
                    filters.add(filter_name.toString());
                    filtersList.setAdapter(adapter);
                }
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
            return rootView;
        }
    }

    public static class NewPurchaseFragment extends Fragment {

        public static final String ARG_OBJECT = "object";
        View rootView;
        ListView productsList;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.new_purchase, container, false);
            final Button button1 = (Button) rootView.findViewById(R.id.newpurch_b_addprod);
            button1.setOnClickListener(addProduct);
            //collego la lista di prodotti all'adaptor
            productsList = (ListView) rootView.findViewById(R.id.newpurch_lv_products);
            final CustomAdapter adapter = new CustomAdapter(getActivity(), R.layout.rowcustom, shoppingCart);
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
    }
}
