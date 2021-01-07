package com.mahmoudkhalil.shopcom.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    List<Order> orderList = new ArrayList<>();
    private onItemClickListener mListener;

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_fragment_item, parent, false);
        return new OrderViewHolder(view, mListener);
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.orderID.setText(orderList.get(position).getOrderID());
        holder.orderDate.setText(orderList.get(position).getOrderDate());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public interface onItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        mListener = listener;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderID, orderDate;
        public OrderViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);
            orderID = itemView.findViewById(R.id.orderID);
            orderDate = itemView.findViewById(R.id.orderDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
