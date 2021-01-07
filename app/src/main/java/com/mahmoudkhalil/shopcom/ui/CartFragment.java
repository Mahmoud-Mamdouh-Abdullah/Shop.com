package com.mahmoudkhalil.shopcom.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.adapters.CartAdapter;
import com.mahmoudkhalil.shopcom.models.Order;
import com.mahmoudkhalil.shopcom.models.Product;
import com.mahmoudkhalil.shopcom.models.User;
import com.mahmoudkhalil.shopcom.repo.OrderRepository;
import com.mahmoudkhalil.shopcom.repo.WishCartRepository;
import com.mahmoudkhalil.shopcom.view_model.OrderViewModel;
import com.mahmoudkhalil.shopcom.view_model.ProductViewModel;
import com.mahmoudkhalil.shopcom.view_model.WishCartViewModel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CartFragment extends Fragment {

    private Button calc_total, confirm_order;
    private TextView total, empty;
    private RelativeLayout relativeLayout;
    private RecyclerView cart_recycler;
    private ProductViewModel productViewModel;
    private CartAdapter cartAdapter;
    private List<Product> cartList;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private User login_user;
    private WishCartViewModel wishCartViewModel;
    private OrderViewModel orderViewModel;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private float sub_total = 0;
    private String userString;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cart_fragment, container, false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setMessage("Loading ...");
        calc_total = view.findViewById(R.id.calculate_total);
        confirm_order = view.findViewById(R.id.confirm_order);
        total = view.findViewById(R.id.sub_total);
        empty = view.findViewById(R.id.empty_text);
        relativeLayout = view.findViewById(R.id.relative);
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        wishCartViewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(WishCartViewModel.class);
        orderViewModel = new ViewModelProvider(getActivity()).get(OrderViewModel.class);
        userString = sharedPreferences.getString("login_user", null);
        if (userString != null) {
            login_user = gson.fromJson(userString, User.class);
        }
        cart_recycler = view.findViewById(R.id.cart_recyclerView);
        cartAdapter = new CartAdapter();
        cartList = new ArrayList<>();

        productViewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(ProductViewModel.class);
        productViewModel.getProductsMutableLiveData().observe(getActivity(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> productList) {
                for (int i = 0; i < productList.size(); i++) {
                    if (login_user.cartList.contains(productList.get(i).getProduct_code())) {
                        cartList.add(productList.get(i));
                    }
                }
                if (cartList.size() == 0) {
                    relativeLayout.setVisibility(View.INVISIBLE);
                }
                cartAdapter.setCartList((ArrayList<Product>) cartList);
                progressDialog.dismiss();
            }
        });
        cart_recycler.setAdapter(cartAdapter);



        cartAdapter.setOnItemClickListener(new CartAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onDeleteClick(final int position) {
                wishCartViewModel.delete_from_cart(login_user, cartList.get(position).getProduct_code());
                wishCartViewModel.setOnCartListener(new WishCartRepository.onCartListener() {
                    @Override
                    public void onCartSuccess() {
                        Toast.makeText(getActivity(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        login_user.cartList.remove(cartList.get(position).getProduct_code());
                        cartAdapter.removeAt(position);
                    }

                    @Override
                    public void onCartFailure() {
                        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                String userStr = gson.toJson(login_user);
                editor.putString("login_user", userStr);
                editor.apply();
            }
        });

        calc_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sub_total = 0;
                for (int i = 0; i < cartList.size(); i++) {
                    String priceString = cartList.get(i).getPrice();
                    priceString = priceString.replace(",", "");
                    sub_total += Float.parseFloat(priceString) * cartAdapter.qtyList.get(i);
                }
                total.setText(String.format("%s EGP", String.valueOf(sub_total)));
                confirm_order.setEnabled(true);
                confirm_order.setTextColor(getResources().getColor(R.color.white));
            }
        });

        confirm_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //When permission granted
                    fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();
                            if(location != null) {
                                try {
                                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    String address = addresses.get(0).getSubAdminArea() +
                                            "," + addresses.get(0).getLocality() + "," + addresses.get(0).getAdminArea();
                                    List<Product> confirmedList = new ArrayList<>(cartList);
                                    List<Product> orderList = new ArrayList<>(cartList);
                                    List<Integer> confirmQtyList = new ArrayList<>();
                                    for (int i = 0; i < cartList.size(); i++) {
                                        int confirmQty = Integer.parseInt(cartList.get(i).getStock()) - cartAdapter.qtyList.get(i);
                                        confirmedList.get(i).setStock(String.valueOf(confirmQty));
                                        confirmQtyList.add(i, confirmQty);
                                    }
                                    orderViewModel.confirmOrder(confirmedList);
                                    orderViewModel.setOnConfirmListener(new OrderRepository.onConfirmListener() {
                                        @Override
                                        public void onConfirmSuccess() {

                                        }

                                        @Override
                                        public void onConfirmFailed() {
                                            Toast.makeText(getActivity(), "Order Confirmed Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    for (int i = 0; i < cartList.size(); i++) {
                                        orderList.get(i).setStock(String.valueOf(cartAdapter.qtyList.get(i)));
                                    }
                                    orderViewModel.addOrder(String.valueOf(sub_total),
                                            login_user.getPhone(),
                                            getDate(),
                                            address, orderList);
                                    orderViewModel.setOnOrderListener(new OrderRepository.onOrderListener() {
                                        @Override
                                        public void onOrderSuccess(Order order) {
                                            String orderString = gson.toJson(order);
                                            Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
                                            intent.putExtra("orderObj", orderString);
                                            startActivity(intent);
                                            getActivity().finish();
                                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        }

                                        @Override
                                        public void onOrderFailed(Exception e) {
                                            Toast.makeText(getActivity(), "Something wrong happened", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    login_user.cartList.clear();
                                    userString = gson.toJson(login_user);
                                    editor.putString("login_user", userString);
                                    editor.apply();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                } else {
                    //when permission denied
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }

            }
        });
        return view;
    }

    public String getDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        return dateFormat.format(date);
    }

}
