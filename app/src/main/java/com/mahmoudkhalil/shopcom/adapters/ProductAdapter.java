package com.mahmoudkhalil.shopcom.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
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

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements Filterable {

    private ArrayList<Product> productsList = new ArrayList<>();
    private List<Product> copyList;
    private onItemClickListener mListener;
    private WishCartViewModel wishCartViewModel;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,parent,false);
        return new ProductViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, final int position) {
        Picasso.get().load(productsList.get(position).getPrimary_image_url()).into(holder.product_image);
        holder.product_title.setText(productsList.get(position).getProduct_title());
        holder.product_price.setText(String.format("%s EGP", productsList.get(position).getPrice()));
        if(Integer.parseInt(productsList.get(position).getStock()) < 1) {
            holder.add_cart.setEnabled(false);
        }
        SharedPreferences sp = holder.add_favourite.getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String userJson = sp.getString("login_user", null);
        final User user = gson.fromJson(userJson, User.class);
        if(user.wishList.contains(productsList.get(position).getProduct_code())){
            holder.add_favourite.setImageResource(R.drawable.ic_favorite);
            holder.add_favourite.setTag("filled");
        }
        if(user.cartList.contains(productsList.get(position).getProduct_code())){
            holder.add_cart.setEnabled(false);
        }

        holder.add_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wishCartViewModel = new ViewModelProvider((ViewModelStoreOwner) holder.product_image.getContext()).get(WishCartViewModel.class);
                sharedPreferences = holder.add_favourite.getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String userJson = sharedPreferences.getString("login_user", null);
                final User user = gson.fromJson(userJson, User.class);
                if(holder.add_favourite.getTag().equals("outLined")){
                    holder.add_favourite.setImageResource(R.drawable.ic_favorite);
                    holder.add_favourite.setTag("filled");
                    wishCartViewModel.add_to_wish(user, productsList.get(position).getProduct_code());
                    wishCartViewModel.setOnAddCart(new WishCartRepository.onWishListener() {
                        @Override
                        public void onWishSuccess() {
                            Toast.makeText(holder.add_favourite.getContext(), "added", Toast.LENGTH_SHORT).show();
                            user.wishList.add(productsList.get(position).getProduct_code());
                        }

                        @Override
                        public void onWishFailure() {
                            Toast.makeText(holder.add_favourite.getContext(), "failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    holder.add_favourite.setImageResource(R.drawable.ic_outlined_favorite);
                    holder.add_favourite.setTag("outLined");
                    wishCartViewModel.delete_from_wish(user, productsList.get(position).getProduct_code());
                    wishCartViewModel.setOnAddCart(new WishCartRepository.onWishListener() {
                        @Override
                        public void onWishSuccess() {
                            Toast.makeText(holder.add_favourite.getContext(), "deleted", Toast.LENGTH_SHORT).show();
                            user.wishList.remove(productsList.get(position).getProduct_code());
                        }

                        @Override
                        public void onWishFailure() {
                            Toast.makeText(holder.add_favourite.getContext(), "fail", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                userJson = gson.toJson(user);
                editor.putString("login_user", userJson);
                editor.apply();
            }

        });

        holder.add_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wishCartViewModel = new ViewModelProvider((ViewModelStoreOwner) holder.product_image.getContext()).get(WishCartViewModel.class);
                sharedPreferences = holder.add_favourite.getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String userJson = sharedPreferences.getString("login_user", null);
                final User user = gson.fromJson(userJson, User.class);
                wishCartViewModel.add_to_cart(user, productsList.get(position).getProduct_code());
                wishCartViewModel.setOnCartListener(new WishCartRepository.onCartListener() {
                    @Override
                    public void onCartSuccess() {
                        Toast.makeText(holder.add_favourite.getContext(), "added", Toast.LENGTH_SHORT).show();
                        user.cartList.add(productsList.get(position).getProduct_code());
                    }

                    @Override
                    public void onCartFailure() {
                    }
                });
                userJson = gson.toJson(user);
                editor.putString("login_user", userJson);
                editor.apply();
                holder.add_cart.setEnabled(false);
            }
        });


    }

    public void setProductsList(ArrayList<Product> productsList) {
        this.productsList = productsList;
        copyList = new ArrayList<>(productsList);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return copyFilter;
    }

    private Filter copyFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Product> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0) {
                filteredList.addAll(copyList);
            } else {
               String filterPattern = constraint.toString().toLowerCase().trim();
               for(Product product : copyList) {
                   if(product.getProduct_title().toLowerCase().contains(filterPattern)) {
                       filteredList.add(product);
                   }
               }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            productsList.clear();
            productsList.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public interface onItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.mListener = listener;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageButton add_favourite;
        ImageView product_image;
        TextView product_title,product_price;
        Button add_cart;
        public ProductViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);
            product_image = itemView.findViewById(R.id.product_image);
            product_title = itemView.findViewById(R.id.product_title);
            product_price = itemView.findViewById(R.id.product_price);
            add_favourite = itemView.findViewById(R.id.add_favourite);
            add_cart = itemView.findViewById(R.id.add_cart);

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
