package com.mahmoudkhalil.shopcom.view_model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mahmoudkhalil.shopcom.repo.ProductFirebaseRepository;
import com.mahmoudkhalil.shopcom.models.Product;

import java.util.List;

public class ProductViewModel extends ViewModel implements ProductFirebaseRepository.OnFirebaseComplete {
    private MutableLiveData<List<Product>> productsMutableLiveData = new MutableLiveData<>();
    private ProductFirebaseRepository productFirebaseRepository = new ProductFirebaseRepository(this);

    public ProductViewModel() {
        productFirebaseRepository.getProductsData();
    }

    @Override
    public void productsListDataAdded(List<Product> productList) {
        productsMutableLiveData.setValue(productList);
    }

    public MutableLiveData<List<Product>> getProductsMutableLiveData() {
        return productsMutableLiveData;
    }
}
