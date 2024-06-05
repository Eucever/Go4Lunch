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
    List<RestaurantItem> mRestaurants;

    public ListRestaurantFragmentAdapter (){
        mRestaurants = new ArrayList<>();
    }

    public void setmRestaurants(List<RestaurantItem> restaurants){
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

        @BindView(R.id.restauItemWmateText)
        public TextView restauItemWmateText;

        @BindView(R.id.textDistance)
        public TextView restauiItemDistance;

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
        RestaurantItem restaurant = mRestaurants.get(position);
        holder.restauItemListName.setText(restaurant.getName());
        holder.restauItemListAddress.setText(restaurant.getAddress());
        holder.restauiItemDistance.setText((int)restaurant.getDistance() + "m");
        holder.restauItemWmateText.setText("("+restaurant.getNbParticipants()+")");
        if(restaurant.getOpeningHours()){
            holder.restauItemListHours.setText(R.string.opened_restaurant);
            holder.restauItemListHours.setTextColor(Color.parseColor("#39b800"));
        }else {
            holder.restauItemListHours.setText(R.string.closed_restaurant);
            holder.restauItemListHours.setTextColor(Color.parseColor("#A8201A"));
        }
        Glide.with(holder.avatarRestauItemList.getContext())
                .load(restaurant.getImage())
                .into(holder.avatarRestauItemList);

        holder.itemView.setOnClickListener(view -> {

            RestaurantDetailActivity.navigate(view.getContext(), restaurantItemToRestaurant(mRestaurants.get(position)));
        });

    }

    public Restaurant restaurantItemToRestaurant(RestaurantItem restauItem){
        Restaurant restau =new Restaurant();
        if (restauItem != null && restauItem.getOpeningHours() != null) {
            restau = new Restaurant(restauItem.getId(),
                    restauItem.getName(),
                    restauItem.getAddress(),
                    restauItem.getOpeningHours(),
                    restauItem.getRating(),
                    restauItem.getImage(),
                    restauItem.getTypes());
        }else if (restauItem != null && restauItem.getOpeningHours() == null){
            restau = new Restaurant(restauItem.getId(),
                    restauItem.getName(),
                    restauItem.getAddress(),
                    false,
                    restauItem.getRating(),
                    restauItem.getImage(),
                    restauItem.getTypes());
        }
        return restau;
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }



}
