package com.mahmoudkhalil.shopcom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.adapters.ProductOrderAdapter;
import com.mahmoudkhalil.shopcom.models.Order;
import com.mahmoudkhalil.shopcom.models.Product;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderDetailsActivity extends AppCompatActivity {


    @BindView(R.id.orderID)
    TextView orderID;
    @BindView(R.id.orders_recyclerView)
    RecyclerView ordersRecyclerView;
    @BindView(R.id.total)
    TextView total;
    @BindView(R.id.orderDate)
    TextView orderDate;
    @BindView(R.id.location_text)
    TextView locationText;
    @BindView(R.id.delivery_text)
    TextView deliveryText;
    private Order mOrder;
    private Gson gson;
    private ProductOrderAdapter productOrderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gson = new Gson();
        productOrderAdapter = new ProductOrderAdapter();
        mOrder = gson.fromJson(getIntent().getStringExtra("orderObj"), Order.class);
        productOrderAdapter.setOrderList(mOrder.getProductsList());
        ordersRecyclerView.setAdapter(productOrderAdapter);
        orderID.setText(mOrder.getOrderID());
        orderDate.setText(mOrder.getOrderDate());
        total.setText(String.format("%sEGP", mOrder.getOrderTotal()));
        locationText.setText(mOrder.getUserAddress());
        deliveryText.setText(addToDate(mOrder.getOrderDate()));

    }

    /**
     *
     * @param dateStr : the date string to add 3 days to it
     * @return the date string after adding 3 days to the original date
     */
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
        if (item.getItemId() == android.R.id.home) {
            toHomeActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        toHomeActivity();
    }

    @OnClick(R.id.backHome)
    public void onViewClicked() {
        toHomeActivity();
    }

    private void toHomeActivity() {
        Intent intent = new Intent(OrderDetailsActivity.this, HomeActivity.class);
        intent.putExtra("from", "order");
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}