package com.mahmoudkhalil.shopcom.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.adapters.CartAdapter;
import com.mahmoudkhalil.shopcom.adapters.WishAdapter;
import com.mahmoudkhalil.shopcom.models.Product;
import com.mahmoudkhalil.shopcom.models.User;
import com.mahmoudkhalil.shopcom.view_model.ProductViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FavouriteFragment extends Fragment {

    private RecyclerView wish_recycler;
    private ProductViewModel productViewModel;
    private WishAdapter wishAdapter;
    private List<Product> wishList;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private User login_user;
    private TextView empty;
    private ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favourite_fragment, container, false);
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("login", Context.MODE_PRIVATE);
        gson = new Gson();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setMessage("Loading ...");
        String userString = sharedPreferences.getString("login_user", null);
        if(userString != null){
            login_user = gson.fromJson(userString, User.class);
        }
        empty = view.findViewById(R.id.empty_text);
        wish_recycler = view.findViewById(R.id.wish_recyclerView);
        wishAdapter = new WishAdapter();
        wishList = new ArrayList<>();
        productViewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(ProductViewModel.class);
        productViewModel.getProductsMutableLiveData().observe(getActivity(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> productList) {
                for(int i = 0; i < productList.size(); i ++){
                    if(login_user.wishList.contains(productList.get(i).getProduct_code())){
                        wishList.add(productList.get(i));
                    }
                }
                if(wishList.size() == 0) {
                    empty.setVisibility(View.VISIBLE);
                }
                wishAdapter.setWishList((ArrayList<Product>) wishList);
                progressDialog.dismiss();
            }
        });
        wish_recycler.setAdapter(wishAdapter);


        return view;
    }
}
