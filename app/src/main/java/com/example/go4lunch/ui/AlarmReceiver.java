package com.example.go4lunch.ui;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.go4lunch.CoreActivity;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.Workmate;
import com.example.go4lunch.repository.LunchRepository;
import com.example.go4lunch.repository.WorkmateRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    private static final boolean NOTIFICATION_DEBUG = CoreActivity.NOTIFICATION_DEBUG;

    private static final String TAG = "ALARM";

    private static final int NOTIFICATION_ID = 7; // 007

    private void sendMessage(Context context, Intent intent, Restaurant restaurant, List<String> userNames) {

        // Build String
        StringBuilder sb = new StringBuilder();
        sb.append("Rembember to go to lunch at ");
        sb.append(restaurant.getName());
        sb.append(" restaurant in : ");
        sb.append(restaurant.getAddress());
        sb.append(" with ");
        for (String s : userNames) {
            sb.append(s);
            sb.append(", ");
        }
        String notificationMessage = sb.toString();

        // Create intent
        Intent newIntent = new Intent(context, CoreActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Create pending intent
        // (it allows you to execute an action in the future)
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Create notification
        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(context, CoreActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(CoreActivity.CHANNEL_NAME)
                .setContentText(notificationMessage)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // (it allows you to specify what should happen when the user interacts with the notification)
                .setContentIntent(pendingIntent);

        // Send notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        //if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i(TAG,"ALARM NOTIFICATION SENT : " + notificationMessage);

            return;
        /*}else {
            Log.i(TAG, "Permission not authorized");
        }*/



    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"ALARM TRIGGERED");

        if(NOTIFICATION_DEBUG){
            Restaurant fakeRestaurant = new Restaurant();
            fakeRestaurant.setName("Fake Restaurant");
            fakeRestaurant.setAddress("Fake Address");
            fakeRestaurant.setId("Fake ID");
            List<String> userNames = Arrays.asList("Test1", "Test2", "Test3");
            sendMessage(context, intent, fakeRestaurant, userNames);
            return;
        }


        // Get the Workmate repository
        WorkmateRepository workmateRepository = WorkmateRepository.getInstance();

        // Get the Lunch Repository
        LunchRepository lunchRepository = LunchRepository.getInstance();

        // Get the current user
        Workmate workmate = workmateRepository.getFirebaseUserAsWorkmate();
        String workmateId = workmate.getId();

        if(workmate.getNotificationActive()){

            // Get the restaurant chosen by the current user for today's lunch
            lunchRepository.getTodayLunch(workmateId).observe(null, lunch -> {
                if (lunch != null) {
                    Restaurant restaurant = lunch.getRestaurant();

                    // Get the list of workmates who have also already chosen the restaurant for today's lunch
                    lunchRepository.getWorkmatesThatAlreadyChooseRestaurantForTodayLunchForThatRestaurant(restaurant).observe(null, workmates -> {
                        if(workmates != null) {
                            List<String> mUsersList = new ArrayList<>();
                            for (Workmate w : workmates) {
                                mUsersList.add(w.getName());
                            }
                            sendMessage(context, intent, restaurant, mUsersList);
                        }
                        Log.e(TAG, "Unable to get the list of the other workmates that are participants of that lunch");
                    });
                } else {
                    Log.e(TAG, "Current user does not have a lunch for today");
                }
            });

        } else {
            Log.i(TAG,"ALARM NOTIFICATION NOT SENT : Notification is not active");
        }
    }


}
