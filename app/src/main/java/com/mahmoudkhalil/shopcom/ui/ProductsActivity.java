package com.mahmoudkhalil.shopcom.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.adapters.ProductAdapter;
import com.mahmoudkhalil.shopcom.models.Product;
import com.mahmoudkhalil.shopcom.view_model.ProductViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductsActivity extends AppCompatActivity {

    @BindView(R.id.products_recyclerView)
    RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private ArrayList<Product> myProductsList;
    private ProductViewModel productViewModel;
    private ProgressDialog progressDialog;
    private static final int RECOGNIZIER_RESULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        ButterKnife.bind(this);

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
                for (int i = 0; i < productList.size(); i++) {
                    if (productList.get(i).getCategory().equals(category_name))
                        myProductsList.add(productList.get(i));
                }
                productAdapter.setProductsList(myProductsList);
                progressDialog.dismiss();
            }
        });
        productsRecyclerView.setAdapter(productAdapter);

        productAdapter.setOnItemClickListener(new ProductAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(ProductsActivity.this, ShowProductActivity.class);
                Gson gson = new Gson();
                String productObj = gson.toJson(myProductsList.get(position));
                intent.putExtra("productObj", productObj);
                intent.putExtra("category_name", category_name);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.products_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
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
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
            case R.id.search_by_voice:
                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to Text");
                startActivityForResult(speechIntent, RECOGNIZIER_RESULT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RECOGNIZIER_RESULT && resultCode == RESULT_OK) {
            ArrayList<String> speech = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            productAdapter.getFilter().filter(speech.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}