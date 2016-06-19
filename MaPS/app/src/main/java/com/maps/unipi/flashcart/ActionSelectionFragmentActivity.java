package com.maps.unipi.flashcart;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class ActionSelectionFragmentActivity extends FragmentActivity {
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

    /* Lists of filters and elements in the shopping cart
    *  defined static in order to grant access from all the activities */
    public static ArrayList<String> filters = new ArrayList<>();
    public static ArrayList<ShoppingCartElement> shoppingCart = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_selection);

        ScanProductActivity.goBack = false;

        /**
         * Create an adapter that when requested, will return a fragment representing an object in
         * the collection.
         *
         * ViewPager and its adapters use support library fragments, so we must use
         * getSupportFragmentManager.
         */
        myCollectionPagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager, attaching the adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(myCollectionPagerAdapter);
    }

    @Override
    public void onBackPressed(){
        // Logout dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_logout_dialog)
                .setMessage(R.string.message_logout_dialog)
                .setPositiveButton(R.string.dialog_logout, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Confirm button
                        Intent main_activity = new Intent(ActionSelectionFragmentActivity.this, MainActivity.class);
                        startActivity(main_activity);
                        // Clear shopping cart and filter list
                        shoppingCart.clear();
                        filters.clear();
                        // In order to load the filters again at the next login
                        FiltersFragment.firstCreationView = true;
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
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
            Fragment fragment;
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
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Returns the number of fragment
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

        /* Used in order to prevent loading filters from shared preference every time
         * the fragment is created */
        private static boolean firstCreationView = true;
        private SharedPreferences sharedPref;
        private View rootView;
        private ListView filtersList;
        public static CustomAdapterFilters adapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            rootView = inflater.inflate(R.layout.filters, container, false);
            final ImageButton btAdd = (ImageButton) rootView.findViewById(R.id.filters_b_addfilter);
            final Button btSave = (Button) rootView.findViewById(R.id.filters_b_savefilter);
            sharedPref = getActivity().getSharedPreferences("f" + MainActivity.cardNumber, Context.MODE_PRIVATE);

            // Set Listeners on add and save buttons
            btAdd.setOnClickListener(addFilter);
            btSave.setOnClickListener(saveFilter);

            if(firstCreationView) {
                // Load filters saved in the shared preference
                int numFilters = sharedPref.getInt("#filters", 0);
                for (int key = 0; key < numFilters; key++)
                    filters.add(sharedPref.getString("f" + Integer.toString(key), "filter"));
            }

            // Link filters list to the adapter
            filtersList = (ListView) rootView.findViewById(R.id.filters_lv_fillist);
            adapter = new CustomAdapterFilters(getActivity(), R.layout.rowcustom_filters, filters);
            filtersList.setAdapter(adapter);

            firstCreationView = false;
            return rootView;
        }

        View.OnClickListener addFilter = new View.OnClickListener() {
            public void onClick(View v) {

                final EditText etFilter = (EditText) rootView.findViewById(R.id.filters_et_filter);
                /* Normalize filter by lower casing it and trimming blanks at the beginning and the end */
                String filterName = etFilter.getText().toString().toLowerCase();
                filterName = filterName.trim();
                if(filterName.isEmpty()) {
                    Utilities.showErrorDialog(getActivity(), getResources().getText(R.string.empty_field).toString());
                    return;
                }
                if(filters.contains(filterName)) {
                    // Filter already existent
                    Utilities.showErrorDialog(getActivity(), getResources().getText(R.string.filter_existent).toString());
                }
                else {
                    filters.add(filterName);
                    filtersList.setAdapter(adapter);
                }

                etFilter.setText(null);
                mViewPager.setCurrentItem(2);
            }
        };

        View.OnClickListener saveFilter = new View.OnClickListener() {
            public void onClick(View v) {
                // Save filter list to the shared preference
                SharedPreferences.Editor editor = sharedPref.edit();
                // First remove all the stored filters
                editor.clear();
                // Add the active filters
                int key = 0;
                for(String filter : ActionSelectionFragmentActivity.filters)
                    editor.putString("f" + Integer.toString(key++), filter);
                editor.putInt("#filters", key);
                editor.commit();
                Utilities.showMessage(getResources().getText(R.string.filters_saved), getContext());
            }
        };
    }

    public static class LastPurchaseFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.last_purchase, container, false);
            ArrayList<ShoppingCartElement> lastPurchase = new ArrayList<>();
            final CustomAdapterLastPurchase adapter;

            // Load products stored in shared preference
            SharedPreferences sharedPref = getActivity().getSharedPreferences("p" + MainActivity.cardNumber, Context.MODE_PRIVATE);
            int numProducts = sharedPref.getInt("#products", 0);
            for(int key = 0; key < numProducts; key++){
                Product product = new Product();
                product.setName(sharedPref.getString("n" + Integer.toString(key), "product"));
                int quantity = sharedPref.getInt("q" + Integer.toString(key), 0);
                product.setPrice(sharedPref.getFloat("p" + Integer.toString(key), 0));
                lastPurchase.add(new ShoppingCartElement(product, quantity));
            }

            // Compute and show total price
            final TextView tvTotal = (TextView) rootView.findViewById(R.id.lastpurch_tv_totalprice);
            float totalPrice = Utilities.computeTotal(lastPurchase);
            tvTotal.setText(Utilities.roundTwoDecimal(totalPrice) + "€");

            // Link products list to the adapter
            ListView productsList = (ListView) rootView.findViewById(R.id.lastpurch_lv_products);
            adapter = new CustomAdapterLastPurchase(getActivity(), R.layout.rowcustom_last_purchase, lastPurchase);
            productsList.setAdapter(adapter);
            return rootView;
        }
    }

    public static class NewPurchaseFragment extends Fragment {

        private View rootView;
        private ListView productsList;
        public static CustomAdapterNewPurchase adapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            rootView = inflater.inflate(R.layout.new_purchase, container, false);
            final Button btAdd = (Button) rootView.findViewById(R.id.newpurch_b_addprod);
            final Button btEnd = (Button) rootView.findViewById(R.id.newpurch_b_endspending);
            final TextView tvTotal = (TextView) rootView.findViewById(R.id.newpurch_tv_totalprice);

            // Set button Listeners
            btAdd.setOnClickListener(addProduct);
            btEnd.setOnClickListener(endShopping);

            // Compute and show final price
            float totalPrice = Utilities.computeTotal(shoppingCart);
            tvTotal.setText(Utilities.roundTwoDecimal(totalPrice) + "€");

            // Link products list to the adapter
            productsList = (ListView) rootView.findViewById(R.id.newpurch_lv_products);
            adapter = new CustomAdapterNewPurchase(getActivity(), R.layout.rowcustom_new_purchase, shoppingCart);
            productsList.setAdapter(adapter);
            return rootView;
        }

        View.OnClickListener addProduct = new View.OnClickListener() {
            public void onClick(View v) {
                Intent scan_product = new Intent(getActivity(), ScanProductActivity.class);
                startActivity(scan_product);
                mViewPager.setCurrentItem(0);
            }
        };

        View.OnClickListener endShopping = new View.OnClickListener() {
            public void onClick(View v) {
                if(shoppingCart.isEmpty()) {
                    Utilities.showErrorDialog(getActivity(), getResources().getText(R.string.empty_cart));
                    return;
                }
                Intent payment = new Intent(getActivity(), PaymentActivity.class);
                startActivity(payment);
                mViewPager.setCurrentItem(0);
            }
        };
    }
}
