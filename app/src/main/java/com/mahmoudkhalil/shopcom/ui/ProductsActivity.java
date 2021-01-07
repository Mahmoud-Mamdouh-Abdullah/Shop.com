package com.mahmoudkhalil.shopcom.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.mahmoudkhalil.shopcom.view_model.ProductViewModel;
import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.adapters.ProductAdapter;
import com.mahmoudkhalil.shopcom.databinding.ActivityProductsBinding;
import com.mahmoudkhalil.shopcom.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductsActivity extends AppCompatActivity {

    ActivityProductsBinding binding;
    private ProductAdapter productAdapter;
    private ArrayList<Product> myProductsList;
    private ProductViewModel productViewModel;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_products);

        productAdapter = new ProductAdapter();
        myProductsList = new ArrayList<>();
        progressDialog = new ProgressDialog(ProductsActivity.this);
        progressDialog.show();
        progressDialog.setMessage("Loading ...");
        final String category_name = Objects.requireNonNull(getIntent().getExtras()).getString("category_name");
        Objects.requireNonNull(getSupportActionBar()).setTitle(category_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        productViewModel.getProductsMutableLiveData().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> productList) {
                myProductsList.clear();
                for(int i = 0; i < productList.size(); i ++) {
                    if(productList.get(i).getCategory().equals(category_name))
                        myProductsList.add(productList.get(i));
                }
                productAdapter.setProductsList(myProductsList);
                progressDialog.dismiss();
            }
        });
        binding.productsRecyclerView.setAdapter(productAdapter);

        productAdapter.setOnItemClickListener(new ProductAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(ProductsActivity.this, ShowProductActivity.class);
                Gson gson = new Gson();
                String productObj = gson.toJson(myProductsList.get(position));
                intent.putExtra("productObj",productObj);
                intent.putExtra("category_name",category_name);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.products_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)  menuItem.getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}