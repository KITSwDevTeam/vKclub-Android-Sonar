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

public class Service extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "Service";

    private static String[] Title;
    private static String[] Content;
    private static int[] Image;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.service, container, false);
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

        Title = new String[]{"Pine View Restaurant", "Mountain Bike Rental", "Massage", "Shuttle Bus", "Conference Room",
                "Banquet", "Color Ball Fighting", "Bubble Soccer", "Nice Holes Disc Golf Course", "Bicycle Race", "Go Cycling",
        "Field Day", "Multiple Court", "Hit The Clay Pot", "Drone Flying Lesson", "Struck Out", "Petanque", "Treasure Hunting",
        "Rope Climbing", "Three Legged Race", "Chambok Waterfall Tour", "Juggle Trekking", "Half Day Sightseeing", "Tour"};
        Content = new String[]{
                "Serves the best foods with our experienced chefs among all the pine trees.",
                "Do you prefer riding a mountain bike to see the sights around Kirirom? You can rent the vehicles here, " +
                        "please ask the reception for more details.",
                "You can feel the traditional massage from our professional staffs on top of Kirirom mountain. Available only in vKirirom.",
                "Resort provides transportation service to customers with acceptable and reasonal price rate.",
                "For seminar meeting, conference etc. Share your ideas in the mountain.",
                "Pine View Kitchen has around 160 seats, where you can have a big party. Buffet can also be available.",
                "Let's ball fighting! How good are you at hiding, ducking and throwing a ball? Let's find it out here at vKirirom Pine Resort.",
        "Bubble Soccer is becoming popular around the world. Come and try the first bubble soccer in Cambodia.",
        "Dics golf, also known as Frisbee golf, is a flying disc game, as well as a precision and accuracy sport.",
        "Ready Set GO! Come with your friends and challenge them to an exciting bicycle race.",
        "Cycling is the very popular leisure activity while in Kirirom Pine Resort. Ask our staff for a map.",
        "Field Trip? Visit us at vKirirom Pine Resort and enjoy a variety at team building activities.",
        "We have a volleyball, netball, futsal, and dodge ball",
        "One of Cambodia's favourite popular game is here. Blind folded with a bat and a group of directors. Will you hit the pot?",
        "Do you want to experience being a pilot? Learn how to fly and maneuver this drone with us, available only at vKirirom Pine Resort.",
        "9 out of 9? You won $300 in cold cash. How good is your aim? You might be our next struck out $300 winner.",
        "Now it's about the time for you to try one of Europes most popular outdoor strategy game. Let's knock them out.",
        "How good are you at reading and finding clues? Come and try our Treasure Hunting game and win an exciting prices in our century treasure.",
        "Strap and buckle that hardness and climb that rope.",
        "A game of cooperation between partners as mush as it is one of speed.",
        "Join our Chambok water fall tour and experience the magnification beauty of the fal as well as the native around.",
        "Jungle treks can be taken throughout the year. In the green season when rain is plentiful the forest is awash with lush.",
        "We will show you around the Kirirom National Park, including king Shihanouk's royal palace ruins."};
        Image = new int[]{R.drawable.pineresort, R.drawable.bikerental, R.drawable.massage,
                R.drawable.vkirirombus, R.drawable.conferenceroom, R.drawable.banquet, R.drawable.colorballfighting,
                R.drawable.bubblesoccer, R.drawable.discgolf, R.drawable.bikerace, R.drawable.cycling, R.drawable.pullbat,
                R.drawable.multipleourt, R.drawable.hitclaypot, R.drawable.drone, R.drawable.struckout, R.drawable.petanque,
                R.drawable.treasurehunting, R.drawable.ropeclimbing, R.drawable.walkracing, R.drawable.chambok, R.drawable.jungletrekking, R.drawable.sightseeing};

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);
    }

    private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();
        for (int index = 0; index < 23; index++) {
            DataObject obj = new DataObject(Image[index],
                    Title[index], Content[index]);
            results.add(index, obj);
        }
        return results;
    }
}
