package com.example.go4lunch.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.R;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.viewmodel.DemoViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends DialogFragment {

    private DemoViewModel mDemoViewModel;

    @BindView(R.id.notificationButton)
    ImageButton notificationButton;

    private static final String TAG = "SettingsFragment";

    private FragmentManager mFragmentManager;


    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

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

        View view = inflater.inflate(R.layout.fragment_settings, container,
                false);

        ButterKnife.bind(this, view);

        configureViewModel();

        configureNotifButton();

        // Inflate the layout for this fragment
        return view;


    }

    @Override
    public void onResume() {
        super.onResume();
    }
    private void configureViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        mDemoViewModel = new ViewModelProvider(this, viewModelFactory).get(DemoViewModel.class);
    }

    private void configureNotifButton(){
        mDemoViewModel.getNotificationActive().observe(this, arg -> {
            if(arg == null){
                Log.e("NOTIFBUTTONCONFIG", "Error arg null");
            }else if (arg){
                notificationButton.setImageResource(R.drawable.baseline_check_box_outline_checked);
            }else {
                notificationButton.setImageResource(R.drawable.baseline_check_box_outline_blank);
            }
        });

        notificationButton.setOnClickListener(view -> {
            mDemoViewModel.getNotificationActive().observe(this, arg -> {
                if(arg == null){
                    Log.e("NOTIFBUTTONCLICK", "Error arg null");
                }else if (arg){
                    mDemoViewModel.createOrUpdateWorkmate(false);
                    notificationButton.setImageResource(R.drawable.baseline_check_box_outline_blank);
                }else {
                    Log.d("NOTIFBUTTONCLICK", "Notif not activ");
                    mDemoViewModel.createOrUpdateWorkmate(true);
                    notificationButton.setImageResource(R.drawable.baseline_check_box_outline_checked);
                }
            });

        });
    }
}