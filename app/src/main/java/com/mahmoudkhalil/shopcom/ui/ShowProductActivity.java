package com.mahmoudkhalil.shopcom.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.adapters.SliderAdapter;
import com.mahmoudkhalil.shopcom.models.Product;
import com.mahmoudkhalil.shopcom.models.User;
import com.mahmoudkhalil.shopcom.repo.WishCartRepository;
import com.mahmoudkhalil.shopcom.view_model.WishCartViewModel;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShowProductActivity extends AppCompatActivity {

    @BindView(R.id.slider_view)
    SliderView sliderView;
    @BindView(R.id.product_title)
    TextView productTitle;
    @BindView(R.id.product_price)
    TextView productPrice;
    @BindView(R.id.stock)
    TextView stock;
    @BindView(R.id.VAT)
    TextView VAT;
    @BindView(R.id.vat_details)
    TextView vatDetails;
    @BindView(R.id.card1)
    CardView card1;
    @BindView(R.id.soldByText)
    TextView soldByText;
    @BindView(R.id.app_name)
    TextView appName;
    @BindView(R.id.card2)
    CardView card2;
    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.product_desc)
    TextView productDesc;
    @BindView(R.id.card3)
    CardView card3;
    @BindView(R.id.add_cart)
    Button addCart;
    @BindView(R.id.add_favourite)
    ImageButton addFavourite;
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
        setContentView(R.layout.activity_show_product);
        ButterKnife.bind(this);
        wishCartViewModel = new ViewModelProvider(this).get(WishCartViewModel.class);
        gson = new Gson();
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        productString = getIntent().getStringExtra("productObj");
        myProduct = gson.fromJson(productString, Product.class);
        userJson = sharedPreferences.getString("login_user", null);
        if (userJson != null) {
            currentUser = gson.fromJson(userJson, User.class);
        }
        if (currentUser.wishList.contains(myProduct.getProduct_code())) {
            addFavourite.setImageResource(R.drawable.ic_favorite);
        }
        if (currentUser.cartList.contains(myProduct.getProduct_code())) {
            addCart.setEnabled(false);
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
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();

        productTitle.setText(myProduct.getProduct_title());
        productPrice.setText(String.format("%s EGP", myProduct.getPrice()));
        productDesc.setText(myProduct.getDescription());
        if (Integer.parseInt(myProduct.getStock()) > 0) {
            stock.setText(getString(R.string.in_stock));
            stock.setBackgroundResource(R.drawable.in_stock_background);
            stock.setTextColor(getResources().getColor(R.color.black));
        } else {
            stock.setText(getString(R.string.out_of_stock));
            stock.setBackgroundResource(R.drawable.out_stock_background);
            stock.setTextColor(getResources().getColor(R.color.light_red));
            addCart.setEnabled(false);
        }


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, ProductsActivity.class);
                intent.putExtra("category_name", category_name);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ProductsActivity.class);
        intent.putExtra("category_name", category_name);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @OnClick({R.id.vat_details, R.id.add_cart, R.id.add_favourite})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.vat_details:
                Intent intent = new Intent(ShowProductActivity.this, VatActivity.class);
                intent.putExtra("productObj", productString);
                intent.putExtra("category_name", category_name);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.add_cart:
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
                addCart.setEnabled(false);
                break;
            case R.id.add_favourite:
                if (addFavourite.getTag().equals("outLined")) {
                    addFavourite.setImageResource(R.drawable.ic_favorite);
                    addFavourite.setTag("filled");
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
                } else {
                    addFavourite.setImageResource(R.drawable.ic_outlined_favorite);
                    addFavourite.setTag("outLined");
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
                break;
        }
    }
}