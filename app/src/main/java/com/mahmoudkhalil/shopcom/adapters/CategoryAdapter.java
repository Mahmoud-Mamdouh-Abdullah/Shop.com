package com.mahmoudkhalil.shopcom.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.models.Category;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private ArrayList<Category> categoriesList = new ArrayList<>();
    private onItemClickListener mListener;

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false),mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.cat_image.setImageResource(categoriesList.get(position).getCategory_url());
        holder.cat_name.setText(categoriesList.get(position).getCategory_title());
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public void setCategoriesList(ArrayList<Category> categoriesList) {
        this.categoriesList = categoriesList;
        notifyDataSetChanged();
    }

    public interface onItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        mListener = listener;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView cat_image;
        TextView cat_name;
        public CategoryViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);
            cat_image = itemView.findViewById(R.id.categoryImage);
            cat_name = itemView.findViewById(R.id.categoryName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
