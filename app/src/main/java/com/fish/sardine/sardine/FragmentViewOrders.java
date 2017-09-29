package com.fish.sardine.sardine;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class FragmentViewOrders extends Fragment {
    RecyclerView recyclerView;
    DatabaseReference mRef;
    List<Orders> ordersList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_orders, container, false);

        mRef = FirebaseDatabase.getInstance().getReference();
        recyclerView = (RecyclerView) view.findViewById(R.id.orders_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mRef.child("Orders").orderByChild("Order Date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    Orders orders = new Orders();
                    FishClass fishClass = new FishClass();
                    fishClass.eng = postSnapshot.child("English Name").getValue(String.class);
                    fishClass.mal = postSnapshot.child("Malayalam Name").getValue(String.class);
                    fishClass.price = postSnapshot.child("Price").getValue(String.class);
                    orders.fishClass = fishClass;
                    orders.payment_status = postSnapshot.child("Status").getValue(String.class);
                    orders.quantity = postSnapshot.child("Quantity").getValue(String.class);
                    orders.total = postSnapshot.child("Total").getValue(String.class);
                    ordersList.add(orders);
                }
                recyclerView.setAdapter(new OrdersAdapter(ordersList));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }
}
