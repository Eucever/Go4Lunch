package com.example.go4lunch.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListRestaurantFragmentAdapter extends RecyclerView.Adapter<ListRestaurantFragmentAdapter.ViewHolder>{
    List<Restaurant> mRestaurants;

    public ListRestaurantFragmentAdapter (){
        mRestaurants = new ArrayList<>();
    }

    public void setmRestaurants(List<Restaurant> restaurants){
        mRestaurants = restaurants;
        //Utilise plutot le diffutil
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.avatarRestauItemList)
        public ImageView avatarRestauItemList;

        @BindView(R.id.restauItemListAddress)
        public TextView restauItemListAddress;

        @BindView(R.id.restauItemListName)
        public TextView restauItemListName;

        @BindView(R.id.restauItemListHours)
        public TextView restauItemListHours;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ListRestaurantFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_restaurants, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListRestaurantFragmentAdapter.ViewHolder holder, int position) {
        Restaurant restaurant = mRestaurants.get(position);
        holder.restauItemListName.setText(restaurant.getName());
        holder.restauItemListAddress.setText(restaurant.getAddress());
        if(restaurant.getOpeningHours()){
            holder.restauItemListHours.setText("Open now");
        }else {
            holder.restauItemListHours.setText("Closed now");
            holder.restauItemListHours.setTextColor(Color.parseColor("#A8201A"));
        }
        Glide.with(holder.avatarRestauItemList.getContext())
                .load(restaurant.getImage())
                .into(holder.avatarRestauItemList);

        holder.itemView.setOnClickListener(view -> {
            RestaurantDetailActivity.navigate(view.getContext(), mRestaurants.get(position));
        });

    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }



}
