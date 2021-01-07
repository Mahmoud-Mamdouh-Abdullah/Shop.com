package com.mahmoudkhalil.shopcom.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.models.Product;
import com.mahmoudkhalil.shopcom.models.User;
import com.mahmoudkhalil.shopcom.repo.WishCartRepository;
import com.mahmoudkhalil.shopcom.view_model.WishCartViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    ArrayList<Product> cartList = new ArrayList<>();
    private onItemClickListener mListener;
    public List<Integer> qtyList = new ArrayList<>();

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item,parent,false);
        return new CartViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, final int position) {
        Picasso.get().load(cartList.get(position).getPrimary_image_url()).into(holder.product_image);
        holder.product_title.setText(cartList.get(position).getProduct_title());
        holder.product_price.setText(String.format("%s EGP", cartList.get(position).getPrice()));
        qtyList.add(position, Integer.parseInt(holder.quantity.getText().toString()));

        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(holder.quantity.getText().toString());
                int actualQty = Integer.parseInt(cartList.get(position).getStock());
                if(qty < actualQty) qty += 1;
                else if(qty == actualQty){
                    Toast.makeText(holder.increase.getContext(), "This is the Max Quantity", Toast.LENGTH_SHORT).show();
                }
                holder.quantity.setText(String.valueOf(qty));
                qtyList.add(position, qty);
            }
        });

        holder.increase.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int actualQty = Integer.parseInt(cartList.get(position).getStock());
                holder.quantity.setText(String.valueOf(actualQty));
                qtyList.add(position, actualQty);
                return true;
            }
        });

        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(holder.quantity.getText().toString());
                if(qty > 1) qty -= 1;
                holder.quantity.setText(String.valueOf(qty));
                qtyList.add(position, qty);
            }
        });

        holder.decrease.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int qty = 1;
                holder.quantity.setText(String.valueOf(qty));
                qtyList.add(position, qty);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void removeAt(int position) {
        cartList.remove(position);
        qtyList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, cartList.size());
    }

    public void setCartList(ArrayList<Product> cartList) {
        this.cartList = cartList;
        notifyDataSetChanged();
    }

    public interface onItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);

    }
    public void setOnItemClickListener(onItemClickListener listener) {
        this.mListener = listener;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView product_image;
        TextView product_title, product_price, quantity;
        ImageButton increase, decrease, delete;
        public CartViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);
            product_image = itemView.findViewById(R.id.product_image);
            product_title = itemView.findViewById(R.id.product_title);
            product_price = itemView.findViewById(R.id.product_price);
            quantity = itemView.findViewById(R.id.quantity);
            increase = itemView.findViewById(R.id.increase);
            decrease = itemView.findViewById(R.id.decrease);
            delete = itemView.findViewById(R.id.delete_form_cart);

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

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }
}
