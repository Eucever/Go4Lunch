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
import com.example.go4lunch.model.Workmate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmateRecyclerAdapter extends RecyclerView.Adapter<WorkmateRecyclerAdapter.ViewHolder> {

    List<Workmate> mWorkmates;

    public WorkmateRecyclerAdapter(){
        mWorkmates = new ArrayList<>();
    }

    public void setmWorkmates(List<Workmate> workmates){
        mWorkmates = workmates;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.avatarWorkmate)
        public ImageView avatarWorkmate;
        @BindView(R.id.workmateJoinText)
        public TextView workmateJoinText;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.workmates_joining, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Workmate workmate = mWorkmates.get(position);
        holder.workmateJoinText.setText(workmate.getName() + " is joining !");
        Glide.with(holder.avatarWorkmate.getContext())
                .load(workmate.getAvatar())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.avatarWorkmate);

    }

    @Override
    public int getItemCount() {
        return mWorkmates.size();
    }


}
