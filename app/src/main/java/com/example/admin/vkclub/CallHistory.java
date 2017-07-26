package com.example.admin.vkclub;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by admin on 7/12/2017.
 */

public class CallHistory extends Fragment {
    private ListView mListView;

    private static final String TAG = "CallHistory";
    DataBaseHelper mDataBaseHelper;

    private static DataBaseHelper returnDbHelper;
    Dashboard dashboard;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.call_history, container, false);
        findView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        populateListView();
    }

    private void findView(View view){
        mListView = (ListView)view.findViewById(R.id.call_history);
        mDataBaseHelper = new DataBaseHelper(getContext());
        CallHistory.returnDbHelper = mDataBaseHelper;
        dashboard = (Dashboard) Dashboard.getAppContext();

        populateListView();
    }

    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView");

        // get the data and append to the list
        Cursor data = mDataBaseHelper.getData();
        final ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext()){
            // get the value from the database in column then add it to the arrayList
            listData.add(data.getString(1));
        }

        //instantiate custom adapter
        final Callhistory_item adapter = new Callhistory_item(listData, getContext());

//        // create list adapter and set the adapter
//        final ArrayAdapter listAdapter = new ArrayAdapter<String>(getActivity(), R.layout.activity_listview, listData);
        mListView.setAdapter(adapter);

        // set an onItemClickListener to the ListView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                String name = adapterView.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemClick: You Clicked on " + name);

                final String extension = adapterView.getItemAtPosition(position).toString();
                final CharSequence options[] = new CharSequence[] {"Call", "Delete"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose an option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
                        toastMessage("the user clicked on colors " + options[which]);
                        if (which == 0){
                            Intent in = new Intent(getContext(), Calling.class);
                            in.putExtra("STATE", "DAILING");
                            in.putExtra("CALLEE", extension);
                            startActivity(in);
                            dashboard.initiateCall(extension);
                        }else if (which == 1){
                            Cursor data = mDataBaseHelper.getItemID(extension); // get the id associated with that name
                            int itemID = -1;
                            while (data.moveToNext()){
                                itemID = data.getInt(0);
                            }

                            if (itemID > -1){
                                Log.d(TAG, "onItemClick: The ID is: " + itemID);
                                mDataBaseHelper.deleteSpecificItem(itemID, extension);
                            }else {
                                toastMessage("No ID associated with that name");
                            }
                        }else toastMessage("Something went wrong.");
                    }
                });
                builder.show();
            }
        });
    }

    private void toastMessage(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();;
    }

    public static DataBaseHelper dbHelper(){
        return CallHistory.returnDbHelper;
    }
}
