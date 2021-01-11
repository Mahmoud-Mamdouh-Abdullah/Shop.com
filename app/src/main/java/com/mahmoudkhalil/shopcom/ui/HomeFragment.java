package com.mahmoudkhalil.shopcom.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.adapters.CategoryAdapter;
import com.mahmoudkhalil.shopcom.models.Category;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private RecyclerView cat_recyclerView;
    private CategoryAdapter categoryAdapter;
    private ArrayList<Category> categoriesList;

    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);


        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            cat_recyclerView = view.findViewById(R.id.recyclerView);
            categoryAdapter = new CategoryAdapter();
            cat_recyclerView.setLayoutManager(
                    new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            );

            categoriesList = new ArrayList<>();
            categoriesList.add(new Category("1","Men Clothes",R.drawable.men_clothes));
            categoriesList.add(new Category("2","Women Clothes",R.drawable.women_clothes));
            categoriesList.add(new Category("3","Kids",R.drawable.kids_clothes));
            categoriesList.add(new Category("4","Electronics Devices",R.drawable.laptops));
            categoriesList.add(new Category("5","Jewellery",R.drawable.jewellery));
            categoriesList.add(new Category("6","Watches",R.drawable.watches));
            categoryAdapter.setCategoriesList(categoriesList);

            categoryAdapter.setOnItemClickListener(new CategoryAdapter.onItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Intent productIntent = new Intent(getActivity(), ProductsActivity.class);
                    productIntent.putExtra("category_name",categoriesList.get(position).getCategory_title());
                    startActivity(productIntent);
                    Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    getActivity().finish();
                }
            });
            cat_recyclerView.setAdapter(categoryAdapter);

        } else {
            View parentView = getActivity().findViewById(android.R.id.content);
            Snackbar.make(parentView, "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Exit", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().finish();
                        }
                    }).setActionTextColor(getResources().getColor(R.color.red)).show();
        }

        return view;
    }
}
