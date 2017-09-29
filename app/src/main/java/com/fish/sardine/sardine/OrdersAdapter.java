package com.fish.sardine.sardine;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by GD on 9/29/2017.
 */

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {

    private List<Orders> ordersList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, quantity, total,status;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            quantity = (TextView) view.findViewById(R.id.quantity);
            total = (TextView) view.findViewById(R.id.total);
            status = (TextView) view.findViewById(R.id.pay_status);
        }
    }


    public OrdersAdapter(List<Orders> ordersList) {
        this.ordersList = ordersList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orders_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Orders order = ordersList.get(position);
        holder.title.setText(order.fishClass.mal + "(" + order.fishClass.eng + ")");
        holder.quantity.setText(order.quantity);
        holder.total.setText(order.total);
        holder.status.setText(order.payment_status);
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }
}
