package com.eygsl.cbs.referencemsal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.eygsl.cbs.referencemsal.adapters.FAQListViewAdapter;
import com.eygsl.cbs.referencemsal.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FAQScreenActivity extends AppCompatActivity {

    private ExpandableListView expandableListView;
    private FAQListViewAdapter expandableListViewAdapter;
    private List<String> listDataGroup;
    private HashMap<String, List<String>> listDataChild;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_a_q_screen);
        setTitle("FAQs");

        // initializing the views
        initViews();
        // initializing the listeners
        initListeners();
        // initializing the objects
        initObjects();
        // preparing list data
        initListData();

        findViewById(R.id.morefaq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Map<String, String> properties = new HashMap<>();
              properties.put("Further FAQ link ", Constants.FAQ_LINK);

             // Analytics.trackEvent("FAQ screen - Clicked on further FAQ link ", properties);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.FAQ_LINK));
                startActivity(browserIntent);
            }
        });

    }

    /**
     * method to initialize the views
     */
    private void initViews() {
        expandableListView = findViewById(R.id.expandableListView);
    }

    /**
     * method to initialize the listeners
     */
    private void initListeners() {

        // ExpandableListView on child click listener
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        listDataGroup.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataGroup.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });

        // ExpandableListView Group expanded listener
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });

        // ExpandableListView Group collapsed listener
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

    }

    /**
     * method to initialize the objects
     */
    private void initObjects() {
        // initializing the list of groups
        listDataGroup = new ArrayList<>();
        // initializing the list of child
        listDataChild = new HashMap<>();
        // initializing the adapter object
        expandableListViewAdapter = new FAQListViewAdapter(this, listDataGroup, listDataChild);
        // setting list adapter
        expandableListView.setAdapter(expandableListViewAdapter);
    }

    /*
     * Preparing the list data
     *
     * Dummy Items
     */
    private void initListData() {
        // Adding group data
        listDataGroup.add(getString(R.string.question1));
        listDataGroup.add(getString(R.string.question2));
        listDataGroup.add(getString(R.string.question3));
        listDataGroup.add(getString(R.string.question4));

        // array of strings
        String[] array;

        // list of alcohol
        List<String> alcoholList = new ArrayList<>();
        array = getResources().getStringArray(R.array.answer1);
        for (String item : array) {
            alcoholList.add(item);
        }

        // list of coffee
        List<String> coffeeList = new ArrayList<>();
        array = getResources().getStringArray(R.array.answer2);
        for (String item : array) {
            coffeeList.add(item);
        }

        // list of pasta
        List<String> pastaList = new ArrayList<>();
        array = getResources().getStringArray(R.array.answer3);
        for (String item : array) {
            pastaList.add(item);
        }

        // list of cold drinks
        List<String> coldDrinkList = new ArrayList<>();
        array = getResources().getStringArray(R.array.answer4);
        for (String item : array) {
            coldDrinkList.add(item);
        }

        // Adding child data
        listDataChild.put(listDataGroup.get(0), alcoholList);
        listDataChild.put(listDataGroup.get(1), coffeeList);
        listDataChild.put(listDataGroup.get(2), pastaList);
        listDataChild.put(listDataGroup.get(3), coldDrinkList);

        // notify the adapter
        expandableListViewAdapter.notifyDataSetChanged();
    }

}
