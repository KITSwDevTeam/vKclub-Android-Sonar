package com.example.admin.vkclub;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by admin on 7/22/2017.
 */

public class Property extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "Propery";

    private static String[] Title;
    private static String[] Content;
    private static int[] Image;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.property, container, false);
        findView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((RecyclerAdapter) mAdapter).setOnItemClickListener(new RecyclerAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(LOG_TAG, " Clicked on Item " + position);
            }
        });
    }

    private void findView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        Title = new String[]{"Orchid Hills", "Villa Jasmine"};
        Content = new String[]{"Our resort provides this special house for a big family and it is suitable for a holiday party.",
        "Studio designed holiday homes for everyone. With a spacious built up area of 36 square meters, these units are perfect" +
                " for couples or small families wanting a private retreat away from the city."};
        Image = new int[]{R.drawable.orchidhill, R.drawable.villajasmine};

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);
    }

    private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();
        for (int index = 0; index < 2; index++) {
            DataObject obj = new DataObject(Image[index],
                    Title[index], Content[index]);
            results.add(index, obj);
        }
        return results;
    }
}
