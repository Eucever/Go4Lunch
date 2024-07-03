package com.example.go4lunch;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.model.Workmate;
import com.example.go4lunch.viewmodel.DemoViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CoreActivity extends AppCompatActivity {

    public AppBarConfiguration mAppBarConfiguration;


    @BindView(R.id.coreDrawer)
    DrawerLayout mDrawer;

    @BindView(R.id.coreNavView)
    NavigationView mNavView;

    @BindView(R.id.CoreBottomNavToolBar)
    BottomNavigationView mBottomNavigationView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    DemoViewModel mDemoViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        configureViewModel();

        configureNavigationView();

        configureProfile();

        configureAlarm();
    }
    @Override
    public void onStart(){
        super.onStart();
        Toolbar mToolbar = this.findViewById(R.id.toolbar);
        // initialiser l'icone de la toolbar pour ce fragment
        mToolbar.setNavigationIcon(R.drawable.setting_icon);
        // définir l'action au clic sur le bouton de navigation de la toolbar
        mToolbar.setNavigationOnClickListener(v -> {
            //get parent activity
            DrawerLayout dl = this.findViewById(R.id.coreDrawer);
            dl.open();
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        if(mDemoViewModel != null){
            mDemoViewModel.refresh();
        }
    }


    private void configureViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        mDemoViewModel = new ViewModelProvider(this, viewModelFactory).get(DemoViewModel.class);
    }

    private void configureNavigationView(){
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.settingsFragment, R.id.yourLunchFragment)
                .setOpenableLayout(mDrawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.coreFragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mNavView, navController);
        NavigationUI.setupWithNavController(mBottomNavigationView, navController);
    }



    private void configureProfile(){
        Workmate currentWorkmate = mDemoViewModel.getFirebaseUserAsWorkmate();
        View navView = mNavView.getHeaderView(0);
        ImageView avatar = navView.findViewById(R.id.avatarHeader);
        TextView username = navView.findViewById(R.id.usernameHeader);
        TextView email = navView.findViewById(R.id.emailHeader);
        Glide.with(avatar.getRootView())
                .load(currentWorkmate.getAvatar())
                .apply(RequestOptions.circleCropTransform())
                .into(avatar);
        username.setText(currentWorkmate.getName());
        email.setText(currentWorkmate.getMail());

    }

    private void configureAlarm(){

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.coreFragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Toast.makeText(getApplicationContext(), "MenuItem" + item.getItemId(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
}