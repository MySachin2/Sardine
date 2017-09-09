package com.fish.sardine.sardine;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class MainListFragment extends Fragment {
    ViewPager viewpager;
    CircleIndicator indicator;
    DatabaseReference mRef;
    List<FishClass> fish_list = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_list, container, false);
        mRef = FirebaseDatabase.getInstance().getReference();
        viewpager = (ViewPager) view.findViewById(R.id.viewpager);
        indicator = (CircleIndicator) view.findViewById(R.id.indicator);

        mRef.child("Fish").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    FishClass fish = new FishClass();
                    fish.eng = postSnapshot.child("English Name").getValue(String.class);
                    fish.mal = postSnapshot.child("Malayalam Name").getValue(String.class);
                    fish.img = postSnapshot.child("Image").getValue(String.class);
                    fish.price = postSnapshot.child("Price").getValue(String.class);
                    Log.d("DS",fish.toString());
                    fish_list.add(fish);
                }
                ScreenSlidePagerAdapter screenSlidePagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
                viewpager.setPageTransformer(true, new DepthPageTransformer());
                viewpager.setAdapter(screenSlidePagerAdapter);
                indicator.setViewPager(viewpager);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
                MainListCardFragment mainListFragment = new MainListCardFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("fish",fish_list.get(position));
                mainListFragment.setArguments(bundle);
                return mainListFragment;
        }

        @Override
        public int getCount() {
            return fish_list.size();
        }
    }
}
