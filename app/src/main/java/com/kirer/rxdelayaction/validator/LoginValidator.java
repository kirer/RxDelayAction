package com.kirer.rxdelayaction.validator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.kirer.lib.Validator;
import com.kirer.rxdelayaction.App;
import com.kirer.rxdelayaction.LoginActivity;

/**
 * @author Kirer
 * @create 2019/12/2
 * @Describe
 */
public class LoginValidator implements Validator {

    private AppCompatActivity mActivity;

    public LoginValidator(AppCompatActivity activity) {
        this.mActivity = activity;
    }

    @Override
    public boolean check() {
        return App.getInstance().isLogin();
    }

    @Override
    public void doValid() {
        mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
    }

}
