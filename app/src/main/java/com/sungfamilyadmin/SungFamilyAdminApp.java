package com.sungfamilyadmin;

import android.app.Application;

public class SungFamilyAdminApp extends Application
{
    private String currentScreen;
    private static SungFamilyAdminApp instance;
    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
    }

    public void setCurrentScreen(String screen)
    {
        this.currentScreen = screen;
    }

    public String getCurrentScreen()
    {
        return this.currentScreen;
    }

    public static SungFamilyAdminApp getApp()
    {
        return instance;
    }
}
