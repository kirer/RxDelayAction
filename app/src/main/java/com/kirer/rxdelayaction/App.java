package com.kirer.rxdelayaction;

import android.app.Application;

/**
 * @author Kirer
 * @create 2019/12/2
 * @Describe
 */
public class App extends Application {

    private static App mApp;
    private boolean isLogin;
    private boolean isAuth;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }

    public static App getInstance() {
        return mApp;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public void setAuth(boolean auth) {
        isAuth = auth;
    }
}
