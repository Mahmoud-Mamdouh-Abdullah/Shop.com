package com.mahmoudkhalil.shopcom.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.adapters.SliderAdapter;
import com.mahmoudkhalil.shopcom.databinding.ActivityShowProductBinding;
import com.mahmoudkhalil.shopcom.models.Product;
import com.mahmoudkhalil.shopcom.models.User;
import com.mahmoudkhalil.shopcom.repo.WishCartRepository;
import com.mahmoudkhalil.shopcom.view_model.WishCartViewModel;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;

import java.util.ArrayList;
import java.util.List;

public class ShowProductActivity extends AppCompatActivity {

    ActivityShowProductBinding binding;
    private SliderAdapter sliderAdapter;
    private Product myProduct;
    private Gson gson;
    private List<String> imagesUrls;
    private String category_name;
    private String productString;
    private User currentUser;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private WishCartViewModel wishCartViewModel;
    String userJson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_product);
        wishCartViewModel = new ViewModelProvider(this).get(WishCartViewModel.class);
        gson = new Gson();
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        productString = getIntent().getStringExtra("productObj");
        myProduct = gson.fromJson(productString, Product.class);
        userJson = sharedPreferences.getString("login_user",null);
        if(userJson != null) {
            currentUser = gson.fromJson(userJson, User.class);
        }
        if(currentUser.wishList.contains(myProduct.getProduct_code())){
            binding.addFavourite.setImageResource(R.drawable.ic_favorite);
        }
        if(currentUser.cartList.contains(myProduct.getProduct_code())){
            binding.addCart.setEnabled(false);
        }
        category_name = getIntent().getStringExtra("category_name");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(category_name);
        imagesUrls = new ArrayList<>();
        imagesUrls.add(myProduct.getPrimary_image_url());
        imagesUrls.add(myProduct.getSecondary_image_url_1());
        imagesUrls.add(myProduct.getSecondary_image_url_2());
        imagesUrls.add(myProduct.getSecondary_image_url_3());

        sliderAdapter = new SliderAdapter(imagesUrls);
        binding.sliderView.setSliderAdapter(sliderAdapter);
        binding.sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        binding.sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        binding.sliderView.startAutoCycle();

        binding.productTitle.setText(myProduct.getProduct_title());
        binding.productPrice.setText(String.format("%s EGP", myProduct.getPrice()));
        binding.productDesc.setText(myProduct.getDescription());
        if(Integer.parseInt(myProduct.getStock()) > 0) {
            binding.stock.setText(getString(R.string.in_stock));
            binding.stock.setBackgroundResource(R.drawable.in_stock_background);
            binding.stock.setTextColor(getResources().getColor(R.color.black));
        }
        else {
            binding.stock.setText(getString(R.string.out_of_stock));
            binding.stock.setBackgroundResource(R.drawable.out_stock_background);
            binding.stock.setTextColor(getResources().getColor(R.color.light_red));
            binding.addCart.setEnabled(false);
        }

        binding.vatDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowProductActivity.this, VatActivity.class);
                intent.putExtra("productObj",productString);
                intent.putExtra("category_name",category_name);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        binding.addFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.addFavourite.getTag().equals("outLined")){
                    binding.addFavourite.setImageResource(R.drawable.ic_favorite);
                    binding.addFavourite.setTag("filled");
                    wishCartViewModel.add_to_wish(currentUser, myProduct.getProduct_code());
                    wishCartViewModel.setOnAddCart(new WishCartRepository.onWishListener() {
                        @Override
                        public void onWishSuccess() {
                            Toast.makeText(ShowProductActivity.this, "Added to WishList", Toast.LENGTH_SHORT).show();
                            currentUser.wishList.add(myProduct.getProduct_code());
                        }

                        @Override
                        public void onWishFailure() {
                            Toast.makeText(ShowProductActivity.this, "failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    binding.addFavourite.setImageResource(R.drawable.ic_outlined_favorite);
                    binding.addFavourite.setTag("outLined");
                    wishCartViewModel.delete_from_wish(currentUser, myProduct.getProduct_code());
                    wishCartViewModel.setOnAddCart(new WishCartRepository.onWishListener() {
                        @Override
                        public void onWishSuccess() {
                            Toast.makeText(ShowProductActivity.this, "Deleted From WishList", Toast.LENGTH_SHORT).show();
                            currentUser.wishList.remove(myProduct.getProduct_code());
                        }

                        @Override
                        public void onWishFailure() {
                            Toast.makeText(ShowProductActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                userJson = gson.toJson(currentUser);
                editor.putString("login_user", userJson);
                editor.apply();
            }
        });

        binding.addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wishCartViewModel.add_to_cart(currentUser, myProduct.getProduct_code());
                wishCartViewModel.setOnCartListener(new WishCartRepository.onCartListener() {
                    @Override
                    public void onCartSuccess() {
                        Toast.makeText(ShowProductActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                        currentUser.cartList.add(myProduct.getProduct_code());
                    }

                    @Override
                    public void onCartFailure() {
                        Toast.makeText(ShowProductActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                userJson = gson.toJson(currentUser);
                editor.putString("login_user", userJson);
                editor.apply();
                binding.addCart.setEnabled(false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, ProductsActivity.class);
                intent.putExtra("category_name",category_name);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ProductsActivity.class);
        intent.putExtra("category_name",category_name);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}