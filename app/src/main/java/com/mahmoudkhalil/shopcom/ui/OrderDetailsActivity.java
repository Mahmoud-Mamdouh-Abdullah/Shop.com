package com.mahmoudkhalil.shopcom.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.adapters.ProductOrderAdapter;
import com.mahmoudkhalil.shopcom.databinding.ActivityOrderDetailsBinding;
import com.mahmoudkhalil.shopcom.models.Order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class OrderDetailsActivity extends AppCompatActivity {

    private ActivityOrderDetailsBinding binding;
    private Order mOrder;
    private Gson gson;
    private ProductOrderAdapter productOrderAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);
        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gson = new Gson();
        productOrderAdapter = new ProductOrderAdapter();
        mOrder = gson.fromJson(getIntent().getStringExtra("orderObj"), Order.class);
        productOrderAdapter.setOrderList(mOrder.getProductsList());
        binding.ordersRecyclerView.setAdapter(productOrderAdapter);
        binding.orderID.setText(mOrder.getOrderID());
        binding.orderDate.setText(mOrder.getOrderDate());
        binding.total.setText(String.format("%sEGP", mOrder.getOrderTotal()));
        binding.locationText.setText(mOrder.getUserAddress());
        binding.deliveryText.setText(addToDate(mOrder.getOrderDate()));

        binding.backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderDetailsActivity.this, HomeActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });
    }

    public String addToDate(String dateStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(simpleDateFormat.parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        return simpleDateFormat.format(calendar.getTime());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(OrderDetailsActivity.this, HomeActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(OrderDetailsActivity.this, HomeActivity.class));
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}