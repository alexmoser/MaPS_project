package com.maps.unipi.maps;

import android.app.ActionBar;
import android.content.Intent;
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
                    fragment = new FiltersFragment();
                    return fragment;
                case 1:
                    fragment = new LastPurchaseFragment();
                    return fragment;
                case 2:
                    fragment = new NewPurchaseFragment();
                    return fragment;
                default:
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            // For this contrived example, we have a 100-object collection.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Filters";

                case 1:
                    return "Last Purchase";

                case 2:
                    return "New Purchase";

                default:
                    return "";

            }
        }
    }

    public static class FiltersFragment extends Fragment {

        public static final String ARG_OBJECT = "object";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Bundle args = getArguments();
            View rootView;
            rootView = inflater.inflate(R.layout.filters, container, false);
            final Button button1 = (Button) rootView.findViewById(R.id.filters_b_addfilter);
            button1.setOnClickListener(addFilter);
            final Button button2 = (Button) rootView.findViewById(R.id.filters_b_delfilter);
            button2.setOnClickListener(delFilter);
            return rootView;
        }

        View.OnClickListener delFilter = new View.OnClickListener() {
            public void onClick(View v) {
                // do something here
                mViewPager.setCurrentItem(0);
            }
        };

        View.OnClickListener addFilter = new View.OnClickListener() {
            public void onClick(View v) {
                // do something here
                mViewPager.setCurrentItem(0);
            }
        };
    }

    public static class LastPurchaseFragment extends Fragment {

        public static final String ARG_OBJECT = "object";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Bundle args = getArguments();
            View rootView;
            rootView = inflater.inflate(R.layout.last_purchase, container, false);
            return rootView;
        }


    }

    public static class NewPurchaseFragment extends Fragment {

        public static final String ARG_OBJECT = "object";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Bundle args = getArguments();
            View rootView;
            rootView = inflater.inflate(R.layout.new_purchase, container, false);
            final Button button1 = (Button) rootView.findViewById(R.id.newpurch_b_addprod);
            button1.setOnClickListener(addProduct);
            final Button button2 = (Button) rootView.findViewById(R.id.newpurch_b_remprod);
            button2.setOnClickListener(delProduct);
            return rootView;
        }

        View.OnClickListener addProduct = new View.OnClickListener() {
            public void onClick(View v) {
                Intent scan_product = new Intent(getActivity(), ScanProduct.class);
                startActivity(scan_product);
                mViewPager.setCurrentItem(2);
            }
        };


        View.OnClickListener delProduct = new View.OnClickListener() {
            public void onClick(View v) {
                //TODO gestire rimozione prodotti
                mViewPager.setCurrentItem(2);
            }
        };

    }
}
