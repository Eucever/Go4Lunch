package com.example.go4lunch;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.example.go4lunch.ui.AlarmReceiver;
import com.example.go4lunch.viewmodel.DemoViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CoreActivity extends AppCompatActivity {

    public AppBarConfiguration mAppBarConfiguration;

    public static final boolean NOTIFICATION_DEBUG = false;
    public static final String CHANNEL_ID = "Go4Lunch";
    public static final String CHANNEL_NAME = "LunchAlarm";


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

        checkNotificationPermission();

        configureAlarm();
    }

    private void checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    1);
        }
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

    private void configureAlarm() {

        // Create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Log.d("ConfigureAlarm", "configureAlarm: Creating notification channel");

            // Create the NotificationChannel
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, // id
                    CHANNEL_NAME, // name
                    NotificationManager.IMPORTANCE_HIGH // importance
            );

            // Register the channel with the system
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        // Get calendar instance to day
        Calendar calendar = Calendar.getInstance();

        if(!NOTIFICATION_DEBUG) {
            // Set the alarm to start at 12:00
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }

        // Create an Intent to broadcast to the AlarmReceiver
        Intent intent = new Intent(this, AlarmReceiver.class);

        // Create a PendingIntent to be triggered when the alarm goes off
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, // context
                0, // no need to request code
                intent, // intent to be triggered
                PendingIntent.FLAG_IMMUTABLE // PendingIntent flag
        );

        // Get the AlarmManager service
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Set the alarm to repeat every day
        // Warning : the alarm is not exact, it can be delayed by the system up to few minutes
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, // alarm type
                NOTIFICATION_DEBUG?System.currentTimeMillis()+10000:calendar.getTimeInMillis(), // time to start
                AlarmManager.INTERVAL_DAY, // interval
                pendingIntent // pending intent
        );

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