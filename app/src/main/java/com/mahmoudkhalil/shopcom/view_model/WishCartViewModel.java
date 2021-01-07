package com.mahmoudkhalil.shopcom.view_model;

import androidx.lifecycle.ViewModel;

import com.mahmoudkhalil.shopcom.models.Product;
import com.mahmoudkhalil.shopcom.models.User;
import com.mahmoudkhalil.shopcom.repo.WishCartRepository;

import java.util.List;

public class WishCartViewModel extends ViewModel implements WishCartRepository.onWishListener, WishCartRepository.onCartListener{
    private WishCartRepository wishCartRepository = new WishCartRepository(this, this);
    private WishCartRepository.onWishListener monWishListener;
    private WishCartRepository.onCartListener onCartListener;

    public void add_to_wish(User user, String productCode) {
        wishCartRepository.add_to_wish(user,productCode);
    }

    public void delete_from_wish(User user, String productCode) {
        wishCartRepository.delete_from_wish(user, productCode);
    }

    public void add_to_cart(User user, String productCode) {
        wishCartRepository.add_to_cart(user, productCode);
    }

    public void delete_from_cart(User user, String productCode) {
        wishCartRepository.delete_from_cart(user, productCode);
    }


    public void setOnAddCart(WishCartRepository.onWishListener monWishListener) {
        this.monWishListener = monWishListener;
    }

    public void setOnCartListener(WishCartRepository.onCartListener onCartListener) {
        this.onCartListener = onCartListener;
    }


    @Override
    public void onWishSuccess() {
        monWishListener.onWishSuccess();
    }

    @Override
    public void onWishFailure() {
        monWishListener.onWishFailure();
    }


    @Override
    public void onCartSuccess() {
        onCartListener.onCartSuccess();
    }

    @Override
    public void onCartFailure() {
        onCartListener.onCartFailure();
    }

}
