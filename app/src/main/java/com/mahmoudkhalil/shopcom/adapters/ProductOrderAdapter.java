package com.mahmoudkhalil.shopcom.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.models.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductOrderAdapter extends RecyclerView.Adapter<ProductOrderAdapter.OrderViewHolder> {

    private List<Product> orderList = new ArrayList<>();

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,parent,false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Picasso.get().load(orderList.get(position).getPrimary_image_url()).into(holder.productImage);
        holder.product_title.setText(orderList.get(position).getProduct_title());
        holder.quantity.setText(orderList.get(position).getStock());
        holder.price.setText(String.format("%s EGP", orderList.get(position).getPrice()));

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setOrderList(List<Product> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView product_title , quantity, price;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            product_title = itemView.findViewById(R.id.product_title);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.product_price);

        }
    }
}
