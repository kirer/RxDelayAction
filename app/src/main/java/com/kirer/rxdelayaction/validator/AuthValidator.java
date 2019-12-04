package com.kirer.rxdelayaction.validator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.kirer.lib.Validator;
import com.kirer.rxdelayaction.App;
import com.kirer.rxdelayaction.AuthActivity;

/**
 * @author Kirer
 * @create 2019/12/2
 * @Describe
 */
public class AuthValidator implements Validator {

    private AppCompatActivity mActivity;

    public AuthValidator(AppCompatActivity activity) {
        this.mActivity = activity;
    }

    @Override
    public boolean check() {
        return App.getInstance().isAuth();
    }

    @Override
    public void doValid() {
        mActivity.startActivity(new Intent(mActivity, AuthActivity.class));
    }

}
