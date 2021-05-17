package com.mahmoudkhalil.shopcom.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.mahmoudkhalil.shopcom.adapters.OrderAdapter;
import com.mahmoudkhalil.shopcom.models.Order;
import com.mahmoudkhalil.shopcom.models.User;
import com.mahmoudkhalil.shopcom.view_model.OrderViewModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView empty;
    private OrderViewModel orderViewModel;
    private List<Order> orderList;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private User login_user;
    private String userString;
    private OrderAdapter orderAdapter;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.orders_fragment, container, false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setMessage("Loading ...");
        sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.order_recyclerView);
        empty = view.findViewById(R.id.empty_text);
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter();
        gson = new Gson();
        userString = sharedPreferences.getString("login_user", null);
        if(userString != null) {
            login_user = gson.fromJson(userString, User.class);
        }
        orderViewModel = new ViewModelProvider(getActivity()).get(OrderViewModel.class);
        orderViewModel.getOrdersData();
        orderViewModel.getOrderMutableLiveData().observe(getActivity(), new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> orders) {
                orderList.clear();
                for(int i = 0; i < orders.size(); i ++) {
                    if(orders.get(i).getUserID().equals(login_user.getID())) {
                        orderList.add(orders.get(i));
                    }
                }
                if(orderList.size() == 0) {
                    empty.setVisibility(View.VISIBLE);
                }
                orderAdapter.setOrderList(orderList);
                progressDialog.dismiss();
            }
        });
        recyclerView.setAdapter(orderAdapter);

        orderAdapter.setOnItemClickListener(new OrderAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String orderString = gson.toJson(orderList.get(position));
                Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
                intent.putExtra("orderObj", orderString);
                startActivity(intent);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });
        return view;
    }
}
