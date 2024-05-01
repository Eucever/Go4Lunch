package com.example.go4lunch.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.model.Workmate;
import com.example.go4lunch.viewmodel.DemoViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YourLunchFragment extends DialogFragment {


    @BindView(R.id.cardViewLunch)
    CardView cardViewLunch;
    @BindView(R.id.avatarLunchRestau)
    ImageView avatarRestau;

    @BindView(R.id.titleLunchRestau)
    TextView titleRestau;

    @BindView(R.id.restauLunchRatingStars)
    RatingBar restauRatingStars;

    @BindView(R.id.descLunchRestau)
    TextView restauDesc;

    @BindView(R.id.workmatesLunchRecycler)
    RecyclerView workmateRecycler;

    @BindView(R.id.noLunchText)
    TextView noLunchText;

    private WorkmateRecyclerAdapter wMateAdapter;


    private final static String TAG = "YourLunchFragment";
    private DemoViewModel mDemoViewModel;

    private FragmentManager mFragmentManager;

    public void showDialog() {
        show(mFragmentManager, TAG);
    }

    public void display(FragmentManager fragmentManager) {
        mFragmentManager = checkNotNull(fragmentManager);
        showDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_LightDialog);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_list_yourlunch, container,
                false);

        ButterKnife.bind(this, view);

        titleRestau.setVisibility(View.VISIBLE);
        restauDesc.setVisibility(View.VISIBLE);
        avatarRestau.setVisibility(View.VISIBLE);
        restauRatingStars.setVisibility(View.VISIBLE);
        cardViewLunch.setVisibility(View.VISIBLE);
        noLunchText.setVisibility(View.GONE);

        configureViewModel();

        configureListWmateRecyclerView();

        configureRestaurant();
        // Inflate the layout for this fragment
        return view;


    }

    @Override
    public void onResume(){
        super.onResume();
        titleRestau.setVisibility(View.VISIBLE);
        restauDesc.setVisibility(View.VISIBLE);
        avatarRestau.setVisibility(View.VISIBLE);
        restauRatingStars.setVisibility(View.VISIBLE);
        cardViewLunch.setVisibility(View.VISIBLE);
        noLunchText.setVisibility(View.GONE);
        if(mDemoViewModel != null){
            mDemoViewModel.refresh();
        }
    }



    private void configureRestaurant(){
        Workmate user = mDemoViewModel.getFirebaseUserAsWorkmate();
        mDemoViewModel.getTodayLunch(user.getId()).observe(this, lunch -> {
            if (lunch == null){
                titleRestau.setVisibility(View.GONE);
                restauDesc.setVisibility(View.GONE);
                avatarRestau.setVisibility(View.GONE);
                restauRatingStars.setVisibility(View.GONE);
                cardViewLunch.setVisibility(View.GONE);
                noLunchText.setVisibility(View.VISIBLE);

            }else {
                titleRestau.setText(lunch.getRestaurant().getName());
                restauDesc.setText(lunch.getRestaurant().getAddress());
                restauRatingStars.setRating(lunch.getRestaurant().getRating().floatValue());

                Glide.with(this)
                        .load(lunch.getRestaurant()
                                .getImage())
                        .into(avatarRestau);
                mDemoViewModel.getWorkmatesThatAlreadyChooseRestaurantForTodayLunchForThatRestaurant(lunch.getRestaurant()).observe(this, listwmate ->{
                    wMateAdapter.setmWorkmates(listwmate);
                });
            }

        });


    }

    private void configureViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        mDemoViewModel = new ViewModelProvider(this, viewModelFactory).get(DemoViewModel.class);
    }
    private void configureListWmateRecyclerView(){
        wMateAdapter = new WorkmateRecyclerAdapter();
        workmateRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        workmateRecycler.setAdapter(wMateAdapter);
    }

}
