package com.example.go4lunch;

import android.app.Application;

public class MainApplication extends Application {
    private static Application application;

    @Override
    public void onCreate(){
        super.onCreate();
        application = this;

    }
    public static Application getApplication(){
        return application;
    }
}
