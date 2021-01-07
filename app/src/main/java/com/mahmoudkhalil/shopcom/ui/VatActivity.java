package com.mahmoudkhalil.shopcom.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.mahmoudkhalil.shopcom.R;

public class VatActivity extends AppCompatActivity {

    private String productString;
    private String category_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vat);

        getSupportActionBar().setTitle("Value Added Tax");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        productString = getIntent().getStringExtra("productObj");
        category_name = getIntent().getStringExtra("category_name");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, ShowProductActivity.class);
                intent.putExtra("productObj", productString);
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
        Intent intent = new Intent(this, ShowProductActivity.class);
        intent.putExtra("productObj", productString);
        intent.putExtra("category_name",category_name);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}