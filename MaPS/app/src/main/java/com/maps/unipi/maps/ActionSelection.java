package com.maps.unipi.maps;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
    static ArrayList<ShoppingCartElement> shoppingCart = new ArrayList<>();

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
        final ActionBar actionBar = getActionBar(); //vedere se si puo' eliminare

        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
        //actionBar.setDisplayHomeAsUpEnabled(true); //se lo metto da errore

        // Set up the ViewPager, attaching the adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(myCollectionPagerAdapter);
    }

    @Override
    public void onBackPressed(){
        // logout dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_logout_dialog)
                .setMessage(R.string.message_logout_dialog)
                .setPositiveButton(R.string.confirm_logout_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Confirm button
                        Intent main_activity = new Intent(ActionSelection.this, MainActivity.class);
                        startActivity(main_activity);
                        //svuoto il carrello e i filtri
                        shoppingCart.clear();
                        filters.clear();
                        FiltersFragment.firstCreationView = true;//altrimenti i filtri non vengono caricati al successivo login
                    }
                })
                .setNegativeButton(R.string.cancel_logout_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Cancel button
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
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
                SharedPreferences sharedPref = getActivity().getSharedPreferences(MainActivity.cardNumber, Context.MODE_PRIVATE);
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
                    // confirm deletion dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.title_delete_item_dialog)
                            .setMessage(R.string.message_delete_item_dialog)
                            .setPositiveButton(R.string.confirm_delete_item_dialog, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked Yes button
                                    filters.remove(filter);
                                    //è come aggiornare la lista..la collego infatti con il nuovo adapter
                                    filtersList.setAdapter(adapter);
                                }
                            })
                            .setNegativeButton(R.string.cancel_delete_item_dialog, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked No button
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
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
                else {
                    //filter already existent
                    Utilities.showErrorDialog(getActivity(), getResources().getText(R.string.filter_existent).toString());
                }
                filter.setText(null);
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
            ArrayList<ShoppingCartElement> lastPurchase = new ArrayList<>();
            Product product = new Product();
            //carico i prodotti salvati nelle shared preference
            SharedPreferences sharedPref = getActivity().getSharedPreferences(MainActivity.cardNumber, Context.MODE_PRIVATE);
            int numProducts = sharedPref.getInt("#products", 0);
            for(int key = 0; key < numProducts; key++){
                product.setName(sharedPref.getString("n" + Integer.toString(key), "product"));
                int quantity = sharedPref.getInt("q" + Integer.toString(key), 0);
                product.setPrice(sharedPref.getFloat("p" + Integer.toString(key), 0));
                lastPurchase.add(new ShoppingCartElement(product, quantity));
            }
            final TextView total = (TextView) rootView.findViewById(R.id.lastpurch_tv_totalprice);
            //Calcolo il prezzo totale e lo mostro in una text view
            float totalPrice = Utilities.computeTotal(lastPurchase);
            total.setText(Float.toString(totalPrice) + "€");
            //collego la lista di prodotti all'adaptor
            ListView productsList = (ListView) rootView.findViewById(R.id.lastpurch_lv_products);
            final CustomAdapterLastPurchase adapter = new CustomAdapterLastPurchase(getActivity(), R.layout.rowcustom_last_purchase, lastPurchase);
            productsList.setAdapter(adapter);
            return rootView;
        }
    }

    public static class NewPurchaseFragment extends Fragment {

        public static final String ARG_OBJECT = "object";
        View rootView;
        ListView productsList;
        TextView total;
        CustomAdapterNewPurchase adapter;

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
            adapter = new CustomAdapterNewPurchase(getActivity(), R.layout.rowcustom_new_purchase, shoppingCart);
            productsList.setAdapter(adapter);
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
                if(shoppingCart.isEmpty()) {
                    Utilities.showErrorDialog(getActivity(), "Shopping cart is empty!");
                    return;
                }
                Intent payment = new Intent(getActivity(), Payment.class);
                startActivity(payment);
                mViewPager.setCurrentItem(0);
            }
        };
    }
}
