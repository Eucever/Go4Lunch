package com.example.go4lunch.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.Workmate;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListWorkmatesFragmentAdapter extends RecyclerView.Adapter<ListWorkmatesFragmentAdapter.ViewHolder>{
    List<Workmate> mWorkmates;

    public ListWorkmatesFragmentAdapter(List<Workmate> items){ mWorkmates = items;}

    public void setmWorkmates(List<Workmate> workmates){
        mWorkmates = workmates;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.avatarWmateItemList)
        public ImageView avatarWmateItemList;
        @BindView(R.id.wmateItemListText)
        public TextView wmateItemListText;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    @Override
    public ListWorkmatesFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_workmates, parent, false);
        return new ListWorkmatesFragmentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListWorkmatesFragmentAdapter.ViewHolder holder, int position) {
        Workmate workmate = mWorkmates.get(position);
        setWorkmatesLunchText(workmate, null, holder);
        Glide.with(holder.avatarWmateItemList.getContext())
                .load(workmate.getAvatar())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.avatarWmateItemList);

    }

    public void setWorkmatesLunchText(Workmate workmate,Restaurant restaurant,ListWorkmatesFragmentAdapter.ViewHolder holder){
        if (restaurant == null){
            holder.wmateItemListText.setText(workmate.getMail()+" has not chosen");
        }else {
            holder.wmateItemListText.setText(workmate.getMail()+" has chosen" + restaurant.getName());
        }
    }

    @Override
    public int getItemCount() {
        return mWorkmates.size();
    }
}
