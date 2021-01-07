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

public class WishAdapter extends RecyclerView.Adapter<WishAdapter.WishViewHolder> {

    ArrayList<Product> wishList = new ArrayList<>();
    private onItemClickListener mListener;
    private WishCartViewModel wishCartViewModel;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @NonNull
    @Override
    public WishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wish_item,parent,false);
        return new WishViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final WishViewHolder holder, final int position) {
        Picasso.get().load(wishList.get(position).getPrimary_image_url()).into(holder.product_image);
        holder.product_title.setText(wishList.get(position).getProduct_title());
        holder.product_price.setText(String.format("%s EGP", wishList.get(position).getPrice()));
        SharedPreferences sp = holder.product_image.getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String userJson = sp.getString("login_user", null);
        final User user = gson.fromJson(userJson, User.class);
        if(user.cartList.contains(wishList.get(position).getProduct_code())){
            holder.add_to_cart.setImageResource(R.drawable.ic_shopping_cart_green);
            holder.add_to_cart.setTag("in_cart");
        }

        holder.add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wishCartViewModel = new ViewModelProvider((ViewModelStoreOwner) holder.add_to_cart.getContext()).get(WishCartViewModel.class);
                sharedPreferences = holder.add_to_cart.getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String userJson = sharedPreferences.getString("login_user", null);
                final User user = gson.fromJson(userJson, User.class);
                if(holder.add_to_cart.getTag().equals("in_cart")){
                    holder.add_to_cart.setTag("not_in_cart");
                    holder.add_to_cart.setImageResource(R.drawable.ic_add_shopping_cart_grey);
                    wishCartViewModel.delete_from_cart(user, wishList.get(position).getProduct_code());
                    wishCartViewModel.setOnCartListener(new WishCartRepository.onCartListener() {
                        @Override
                        public void onCartSuccess() {
                            user.cartList.remove(wishList.get(position).getProduct_code());
                            Toast.makeText(holder.add_to_cart.getContext(), "Deleted from Cart", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCartFailure() {
                            Toast.makeText(holder.add_to_cart.getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    holder.add_to_cart.setTag("in_cart");
                    holder.add_to_cart.setImageResource(R.drawable.ic_shopping_cart_green);
                    wishCartViewModel.add_to_cart(user, wishList.get(position).getProduct_code());
                    wishCartViewModel.setOnCartListener(new WishCartRepository.onCartListener() {
                        @Override
                        public void onCartSuccess() {
                            user.cartList.add(wishList.get(position).getProduct_code());
                            Toast.makeText(holder.add_to_cart.getContext(), "Added to Cart", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCartFailure() {
                            Toast.makeText(holder.add_to_cart.getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                userJson = gson.toJson(user);
                editor.putString("login_user", userJson);
                editor.apply();
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wishCartViewModel = new ViewModelProvider((ViewModelStoreOwner) holder.add_to_cart.getContext()).get(WishCartViewModel.class);
                sharedPreferences = holder.add_to_cart.getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String userJson = sharedPreferences.getString("login_user", null);
                final User user = gson.fromJson(userJson, User.class);
                wishCartViewModel.delete_from_wish(user, wishList.get(position).getProduct_code());
                wishCartViewModel.setOnAddCart(new WishCartRepository.onWishListener() {
                    @Override
                    public void onWishSuccess() {
                        Toast.makeText(holder.delete.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        user.wishList.remove(wishList.get(position).getProduct_code());
                        removeAt(position);
                    }

                    @Override
                    public void onWishFailure() {
                        Toast.makeText(holder.delete.getContext(), "Deleted Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                userJson = gson.toJson(user);
                editor.putString("login_user", userJson);
                editor.apply();
            }
        });

    }

    @Override
    public int getItemCount() {
        return wishList.size();
    }

    public void removeAt(int position) {
        wishList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, wishList.size());
    }

    public void setWishList(ArrayList<Product> cartList) {
        this.wishList = cartList;
        notifyDataSetChanged();
    }

    public interface onItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(onItemClickListener listener) {
        this.mListener = listener;
    }

    public static class WishViewHolder extends RecyclerView.ViewHolder {
        ImageView product_image;
        TextView product_title, product_price;
        ImageButton add_to_cart, delete;
        public WishViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);
            product_image = itemView.findViewById(R.id.product_image);
            product_title = itemView.findViewById(R.id.product_title);
            product_price = itemView.findViewById(R.id.product_price);
            delete = itemView.findViewById(R.id.delete_form_wish);
            add_to_cart = itemView.findViewById(R.id.add_to_cart);

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
